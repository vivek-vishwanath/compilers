#include "Part032.h"
#include "llvm/IR/Module.h"

using namespace llvm;

AnalysisKey Part32::Key;

Part32::Result Part32::run(Module &M, ModuleAnalysisManager &MAM) {
  Result R = {0, 0, 0, 0, 0, 0};

  // Iterate over each function in the module
  for (Function &F : M) {
    //// TODO: Implement part 32 here. ////
  }

  return R;
};