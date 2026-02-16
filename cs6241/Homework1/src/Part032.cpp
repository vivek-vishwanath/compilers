#include "Part032.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/CFG.h"
#include "llvm/Analysis/LoopInfo.h"
#include <vector>
#include <map>
#include <set>
#include <chrono>

using namespace llvm;

AnalysisKey Part32::Key;

// Helper to get all basic blocks in a function
std::vector<BasicBlock*> getAllBasicBlocks(Function &F) {
  std::vector<BasicBlock*> blocks;
  for (BasicBlock &BB : F) {
    blocks.push_back(&BB);
  }
  return blocks;
}

// Floyd-Warshall for transitive closure
// Returns reachability matrix: reach[i][j] = true if there's a path from i to j
std::vector<std::vector<bool>> computeTransitiveClosure(
    const std::vector<BasicBlock*> &blocks,
    const std::map<BasicBlock*, int> &blockIndex) {

  int n = blocks.size();
  std::vector<std::vector<bool>> reach(n, std::vector<bool>(n, false));

  // Initialize direct edges
  for (int i = 0; i < n; i++) {
    BasicBlock *BB = blocks[i];
    reach[i][i] = true; // Self-reachable

    for (BasicBlock *Succ : successors(BB)) {
      auto it = blockIndex.find(Succ);
      if (it != blockIndex.end()) {
        int j = it->second;
        reach[i][j] = true;
      }
    }
  }

  // Floyd-Warshall algorithm
  for (int k = 0; k < n; k++) {
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        reach[i][j] = reach[i][j] || (reach[i][k] && reach[k][j]);
      }
    }
  }

  return reach;
}

// Detect all cycles and classify by number of entries
struct Cycle {
  std::set<BasicBlock*> blocks;
  std::set<BasicBlock*> entries;
};

std::vector<Cycle> detectCycles(
    const std::vector<BasicBlock*> &blocks,
    const std::map<BasicBlock*, int> &blockIndex,
    const std::vector<std::vector<bool>> &reach) {

  int n = blocks.size();
  std::vector<Cycle> cycles;
  std::set<std::set<BasicBlock*>> uniqueCycles;

  // Find all cycles: look for i->j path and j->i edge
  for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
      if (i == j) continue;

      // Check if there's a path from i to j
      if (!reach[i][j]) continue;

      // Check if there's a direct edge from j to i
      bool hasBackEdge = false;
      for (BasicBlock *Succ : successors(blocks[j])) {
        if (Succ == blocks[i]) {
          hasBackEdge = true;
          break;
        }
      }

      if (hasBackEdge) {
        // Found a cycle, now find all blocks in the cycle
        std::set<BasicBlock*> cycleBlocks;

        // BFS to find all blocks on paths from i to j
        std::set<BasicBlock*> visited;
        std::vector<BasicBlock*> worklist = {blocks[i]};
        visited.insert(blocks[i]);

        while (!worklist.empty()) {
          BasicBlock *current = worklist.back();
          worklist.pop_back();

          cycleBlocks.insert(current);

          if (current == blocks[j]) continue;

          for (BasicBlock *Succ : successors(current)) {
            auto it = blockIndex.find(Succ);
            if (it == blockIndex.end()) continue;

            int succIdx = it->second;
            if (reach[succIdx][j] && visited.insert(Succ).second) {
              worklist.push_back(Succ);
            }
          }
        }

        // Add j to cycle as well
        cycleBlocks.insert(blocks[j]);

        // Check if this cycle is already found
        if (uniqueCycles.insert(cycleBlocks).second) {
          Cycle cycle;
          cycle.blocks = cycleBlocks;
          cycles.push_back(cycle);
        }
      }
    }
  }

  return cycles;
}

// Count entry points for each cycle
void countCycleEntries(
    std::vector<Cycle> &cycles,
    const std::map<BasicBlock*, int> &blockIndex) {

  for (Cycle &cycle : cycles) {
    // An entry is a block in the cycle with a predecessor outside the cycle
    for (BasicBlock *BB : cycle.blocks) {
      for (BasicBlock *Pred : predecessors(BB)) {
        if (cycle.blocks.find(Pred) == cycle.blocks.end()) {
          // Predecessor is outside the cycle, so BB is an entry
          cycle.entries.insert(BB);
          break;
        }
      }
    }

    // If no entries found, it's an unreachable cycle or the whole function
    // Mark one arbitrary block as entry
    if (cycle.entries.empty() && !cycle.blocks.empty()) {
      cycle.entries.insert(*cycle.blocks.begin());
    }
  }
}

Part32::Result Part32::run(Module &M, ModuleAnalysisManager &MAM) {
  FunctionAnalysisManager &FAM =
      MAM.getResult<FunctionAnalysisManagerModuleProxy>(M).getManager();

  Result result = {0, 0, 0, 0, 0, 0};

  for (Function &F : M) {
    if (F.isDeclaration()) continue;

    // Get all basic blocks

    std::vector<BasicBlock*> blocks;

    std::map<BasicBlock*, int> blockIndex;
    int i = 0;
    for (BasicBlock &BB : F) {
      blocks.push_back(&BB);
      blockIndex[&BB] = i++;
    }
    if (blockIndex.empty()) continue;

    // Compute transitive closure using Floyd-Warshall
    std::vector<std::vector<bool>> reach = computeTransitiveClosure(blocks, blockIndex);
    // Detect cycles
    std::vector<Cycle> cycles = detectCycles(blocks, blockIndex, reach);
    // Count entries for each cycle
    countCycleEntries(cycles, blockIndex);

    // Classify cycles by number of entries
    for (const Cycle &cycle : cycles) {
      int numEntries = cycle.entries.size();

      result.totalCycles++;

      if (numEntries == 1) {
        result.singleEntryCycles++;
      } else if (numEntries == 2) {
        result.twoEntryCycles++;
      } else if (numEntries == 3) {
        result.threeEntryCycles++;
      } else if (numEntries == 4) {
        result.fourEntryCycles++;
      } else {
        result.multiEntryCycles++;
      }
    }
  }

  return result;
}