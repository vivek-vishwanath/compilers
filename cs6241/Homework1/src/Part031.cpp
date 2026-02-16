#include "Part031.h"
#include "llvm/IR/Module.h"
#include "llvm/Analysis/LoopInfo.h" // IWYU pragma: keep

using namespace llvm;

AnalysisKey Part31::Key;

Part31::Result Part31::run(Module &M, ModuleAnalysisManager &MAM) {
  // Use the FunctionAnalysisManager to get per-function analyses.
  FunctionAnalysisManager &FAM =
      MAM.getResult<FunctionAnalysisManagerModuleProxy>(M).getManager();

  Result result = {0, 0, 0, 0};

  for (Function &F : M) {
    if (F.isDeclaration()) continue;
    /////// TODO: Implement part 3.1 here. ///////
    // Get the LoopInfo for the function
  }

  return result;
}