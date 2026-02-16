#include "Part034.h"
#include <llvm/IR/Analysis.h>

using namespace llvm;

PreservedAnalyses Part34::run(Module &M, ModuleAnalysisManager &MAM) {
  llvm::outs() << "Part 3.4: Whole program paths\n";

  //// TODO: Implement your pass for part 3.4 here. ////
  // You can create new functions as needed.

  return PreservedAnalyses::none();
}

