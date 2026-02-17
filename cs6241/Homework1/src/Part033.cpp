#include "Part033.h"
#include "llvm/IR/Analysis.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/CFG.h"
#include "llvm/IR/Dominators.h"
#include "llvm/Analysis/PostDominators.h"
#include "llvm/Analysis/LoopInfo.h"
#include <chrono>
#include <queue>
#include <llvm/IR/BasicBlock.h>

using namespace llvm;

void Part33::printInfo() {
  errs() << "Part 3.3 Analysis Results:\n";
  errs() << "  Average Time per Trial (microseconds): " << avgTime << "\n";
  errs() << "  Average # of Pairs of Reachable Blocks: " << avgReachable << "\n";
  errs() << "  Average # of Defs that Reach another Random Block: " << avgDef << "\n";
  /// TODO: Print your other statistics.
}

std::pair<BasicBlock*, BasicBlock*> getRandomBBPair(Module &M) {
  // select a random function from the module
  auto funcIter = M.begin();
  std::advance(funcIter, rand() % M.size());
  Function *F = &*funcIter;
  while (F->isDeclaration()) {
    funcIter = M.begin();
    std::advance(funcIter, rand() % M.size());
    F = &*funcIter;
  }

  auto bbIter1 = F->begin();
  std::advance(bbIter1, rand() % F->size());
  auto bbIter2 = F->begin();
  std::advance(bbIter2, rand() % F->size());
  // Check for valid iterators
  if (bbIter1 == F->end() || bbIter2 == F->end()) {
    llvm::errs() << "Error: Could not select random basic blocks.\n";
    return {nullptr, nullptr};
  }
  return {&*bbIter1, &*bbIter2};
}

bool bfs(BasicBlock *A, BasicBlock *B) {
  if (A == B) return true;
  
  SmallPtrSet<BasicBlock*, 32> visited;
  std::queue<BasicBlock*> workList;
  
  workList.push(A);
  visited.insert(A);
  
  while (!workList.empty()) {
    BasicBlock *Current = workList.front();
    workList.pop();
    
    for (BasicBlock *successor : successors(Current)) {
      if (successor == B) return true;
      if (visited.insert(successor).second) {
        workList.push(successor);
      }
    }
  }
  
  return false;
}

bool reachable(BasicBlock *A, BasicBlock *B, FunctionAnalysisManager &FAM, int *totalDefs) {
  for (const llvm::Instruction &I : *A) {
    if (!I.getType()->isVoidTy()) (*totalDefs)++;
  }
  if (A == B) return true;
  Function *F = A->getParent();
  if (F != B->getParent()) return false;
  DominatorTree &DT = FAM.getResult<DominatorTreeAnalysis>(*F);
  PostDominatorTree &PDT = FAM.getResult<PostDominatorTreeAnalysis>(*F);
  if (DT.dominates(A, B) || PDT.dominates(B, A)) return true;
  LoopInfo &LI = FAM.getResult<LoopAnalysis>(*F);
  Loop *LA = LI.getLoopFor(A);
  Loop *LB = LI.getLoopFor(B);
  if (LA == LB && LA) return true;
  return bfs(A, B);
}

PreservedAnalyses Part33::run(Module &M, ModuleAnalysisManager &MAM) {
  // Use the FunctionAnalysisManager to get per-function analyses.
  FunctionAnalysisManager &FAM =
      MAM.getResult<FunctionAnalysisManagerModuleProxy>(M).getManager();

  unsigned int trials = 0;
  auto start = std::chrono::high_resolution_clock::now();
  int n = 0;
  int totalDefs = 0;
  for (; trials < 1000000; trials++) {
    int defs = 0;
    auto [BB1, BB2] = getRandomBBPair(M);
    if (reachable(BB1, BB2, FAM, &defs)) {
      n++;
      totalDefs += defs;
    }
  }
  auto stop = std::chrono::high_resolution_clock::now();
  auto duration =
      std::chrono::duration_cast<std::chrono::microseconds>(stop - start);
  avgTime = duration.count() / (double)trials;
  avgReachable = n / (double) trials;
  avgDef = totalDefs / (double) trials;

  printInfo();
  return PreservedAnalyses::all();
}