#include "Part040.h"
#include <llvm/IR/Module.h>


using namespace llvm;

AnalysisKey Part4::Key;

Part4::Result Part4::run(Module &M, ModuleAnalysisManager &MAM) {
  Result result;

  /////// TODO: Implement part 4 here. ///////
  // Example: iterate over each function
  for (auto &F: M) {
    if (F.isDeclaration()) continue;
    // Add analysis logic for each function in the module
  }
  // Other logic
  return result;
}

