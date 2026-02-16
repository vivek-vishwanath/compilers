#include "Part033.h"
#include "llvm/IR/Analysis.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/Module.h"
#include <chrono>
#include <llvm/IR/BasicBlock.h>

using namespace llvm;

void Part33::printInfo() {
  errs() << "Part 3.3 Analysis Results:\n";
  errs() << "  Average Time per Trial (microseconds): " << avgTime << "\n";
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

PreservedAnalyses Part33::run(Module &M, ModuleAnalysisManager &MAM) {
  // Use the FunctionAnalysisManager to get per-function analyses.
  FunctionAnalysisManager &FAM =
      MAM.getResult<FunctionAnalysisManagerModuleProxy>(M).getManager();

  unsigned int trials = 0;
  auto start = std::chrono::high_resolution_clock::now();
  for (; trials < 100000; trials++) {
    auto [BB1, BB2] = getRandomBBPair(M);
    ///// TODO: Implement part 3.3 here. /////
  }

  auto stop = std::chrono::high_resolution_clock::now();
  auto duration =
      std::chrono::duration_cast<std::chrono::microseconds>(stop - start);
  avgTime = duration.count() / (double)trials;

  printInfo();
  return PreservedAnalyses::all();
}