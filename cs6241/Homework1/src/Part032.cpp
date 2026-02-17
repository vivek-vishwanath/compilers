#include "Part032.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/CFG.h"
#include "llvm/Analysis/LoopInfo.h"
#include <vector>
#include <map>
#include <set>
#include <chrono>
#include <ostream>

using namespace llvm;

AnalysisKey Part32::Key;

std::vector<std::vector<bool>> floydWarshall(std::vector<BasicBlock*> &blocks, std::map<BasicBlock*, int> &map) {
  int n = blocks.size();
  std::vector<std::vector<bool>> reach(n, std::vector<bool>(n, false));

  for (int i = 0; i < n; i++) {
    BasicBlock *BB = blocks[i];
    reach[i][i] = true;
    for (BasicBlock *successor : successors(BB)) {
      reach[i][map[successor]] = true;
    }
  }
  for (int k = 0; k < n; k++)
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        reach[i][j] = reach[i][j] || (reach[i][k] && reach[k][j]);

  return reach;
}

std::vector<std::set<int>> stronglyConnectedComponents(std::vector<std::vector<bool>> &reach) {
  int n = reach.size();
  std::vector<bool> visited(n, false);
  std::vector<std::set<int>> components;
  for (int i = 0; i <  n; i++) {
    if (visited[i]) continue;
    visited[i] = true;

    std::set<int> scc;
    scc.insert(i);
    for (int j = 0; j < n; j++) {
      if (!visited[j] && reach[i][j] && reach[j][i]) {
        scc.insert(j);
      }
    }
    for (int node : scc) visited[node] = true;
    components.push_back(scc);
  }
  return components;
}

std::vector<std::set<int>> filterCycles(std::vector<std::set<int>> &sccs, std::vector<BasicBlock*> &blocks) {
  std::vector<std::set<int>> filtered;
  for (std::set<int> &scc : sccs) {
    if (scc.size() == 1) {
      BasicBlock *A = blocks[*scc.begin()];
      for (BasicBlock *s : successors(A)) {
        if (s == A) {
          filtered.push_back(scc);
          break;
        }
      }
    } else filtered.push_back(scc);
  }
  return filtered;
}

std::vector<int> countEntryPoints(std::vector<std::set<int>> &sccs, std::vector<BasicBlock*> &blocks, std::map<BasicBlock*, int> &map) {

  std::vector<int> entriesPerCycle;
  for (const std::set<int> &scc : sccs) {
    int entries = 0;
    for (int i : scc) {
      BasicBlock *BB = blocks[i];
      for (BasicBlock *pred : predecessors(BB)) {
        // If predecessor is not in SCC, BB is an entry
        if (scc.find(map[pred]) == scc.end()) {
          entries++;
          outs() << entries << "\n";
          break;
        }
      }
    }
    entriesPerCycle.push_back(entries);
  }
  return entriesPerCycle;
}

void aggregate(std::vector<int> &entrypoints, Part32::Result &result) {
  for (int i : entrypoints) {
    switch (i) {
      case 1: result.singleEntryCycles++; break;
      case 2: result.twoEntryCycles++; break;
      case 3: result.threeEntryCycles++; break;
      case 4: result.fourEntryCycles++; break;
      default:
        result.multiEntryCycles++;
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
    std::map<BasicBlock*, int> map;
    int i = 0;
    for (BasicBlock &BB : F) {
      blocks.push_back(&BB);
      map[&BB] = i++;
    }
    outs() << F.getName() << "\n";
    for (BasicBlock &BB : F) {
      outs() << "\t" << map[&BB] << ": ";
      for (BasicBlock *bb : successors(&BB)) {
        outs() << map[bb] << ", ";
      }
      outs() << "\n";
    }
    auto reach = floydWarshall(blocks, map);
    auto sccs = stronglyConnectedComponents(reach);
    sccs = filterCycles(sccs, blocks);
    auto entryPoints = countEntryPoints(sccs, blocks, map);
    aggregate(entryPoints, result);
  }

  return result;
}