#include "Part031.h"
#include "llvm/IR/Module.h"
#include "llvm/Analysis/LoopInfo.h" // IWYU pragma: keep
#include "llvm/ADT/SmallVector.h"
#include "llvm/IR/CFG.h"
#include <set>
#include <map>
#include <algorithm>

using namespace llvm;

AnalysisKey Part31::Key;

void countLoops(Loop *L, std::vector<Loop*> &AllLoops) {
    AllLoops.push_back(L);
    for (Loop *SubLoop : L->getSubLoops()) {
        countLoops(SubLoop, AllLoops);
    }
}

int longestPathDFS(BasicBlock *BB, std::set<BasicBlock*> &visited, Loop *L) {
    if (visited.count(BB)) {
        return 0; // Cycle detected
    }
    visited.insert(BB);

    int maxPath = 0;

    // Try all successors
    for (BasicBlock *successor : successors(BB)) {
        if (L->contains(successor)) {
            int pathLength = longestPathDFS(successor, visited, L);
            maxPath = std::max(maxPath, pathLength);
        }
    }

    visited.erase(BB);

    // Current node + longest path from successors
    return 1 + maxPath;
}

// Find longest acyclic path in a loop
int findLongestAcyclicPath(Loop *L) {
    int longestPath = 0;

    // Try starting from each basic block in the loop
    for (BasicBlock *BB : L->blocks()) {
        std::set<BasicBlock*> visited;
        int pathLength = longestPathDFS(BB, visited, L);
        longestPath = std::max(longestPath, pathLength);
    }

    return longestPath;
}

// Get the outermost loop containing this loop
Loop* getOutermostLoop(Loop *L) {
    Loop *outermost = L;
    while (outermost->getParentLoop() != nullptr) {
        outermost = outermost->getParentLoop();
    }
    return outermost;
}

Part31::Result Part31::run(Module &M, ModuleAnalysisManager &MAM) {
  // Use the FunctionAnalysisManager to get per-function analyses.
  FunctionAnalysisManager &FAM =
      MAM.getResult<FunctionAnalysisManagerModuleProxy>(M).getManager();

    Result result = {0, 0, 0, 0};
    int maxAcyclicPathLength = 0;
    Loop *maxPathOutermostLoop = nullptr;

    for (Function &F : M) {
        if (F.isDeclaration()) continue;

        LoopInfo &LI = FAM.getResult<LoopAnalysis>(F);
        std::vector<Loop*> AllLoops;
        for (Loop *L : LI) {
            countLoops(L, AllLoops);
            result.totalNumOuterLoops++;
        }

        for (Loop *L : AllLoops) {
            SmallVector<std::pair<BasicBlock*, BasicBlock*>, 8> ExitEdges;
            L->getExitEdges(ExitEdges);
            result.totalNumExitEdges += ExitEdges.size();
        }

        for (Loop *L : LI) {
            int pathLength = findLongestAcyclicPath(L);

            if (pathLength > maxAcyclicPathLength) {
                maxAcyclicPathLength = pathLength;
                Loop *outermost = getOutermostLoop(L);
                maxPathOutermostLoop = outermost;
            }
        }
        // Use a set to track unique basic blocks
        std::set<BasicBlock*> UniqueBlocks;
        for (Loop *L : AllLoops) {
            for (BasicBlock *BB : L->blocks()) {
                UniqueBlocks.insert(BB);
            }
        }
        result.totalNumLoopBBs += UniqueBlocks.size();
        result.totalNumLoops += AllLoops.size();
    }
    result.longestPathLength = maxAcyclicPathLength;
    result.longestPathLoop = maxPathOutermostLoop;

    return result;
}