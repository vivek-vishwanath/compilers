#include "Part020.h"

using namespace llvm;

AnalysisKey Part2::Key;


Part2::Result Part2::run(Module &M, ModuleAnalysisManager &MAM) {
  // Use the FunctionAnalysisManager to get per-function analyses.
  FunctionAnalysisManager &FAM =
      MAM.getResult<FunctionAnalysisManagerModuleProxy>(M).getManager();
  

  /////////// TODO: Implement part 2 here. ////////////


  // Iterate over each function in the module
  // for (Function &F : M) ...

  
  return Result();
};
