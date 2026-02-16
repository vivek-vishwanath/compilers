#include "Analyses.h"
#include <llvm/IR/PassManager.h>

using namespace llvm;

AnalysisKey CounterAnalysis::Key;

CounterAnalysis::Result CounterAnalysis::run(Function &F, 
  FunctionAnalysisManager&) {
    return Result {static_cast<int>(F.size())};
}


