#include "llvm/Passes/PassBuilder.h"
#include "llvm/Passes/PassPlugin.h"

using namespace llvm;

class CounterAnalysis
    : public AnalysisInfoMixin<CounterAnalysis> {
  friend AnalysisInfoMixin<CounterAnalysis>;
  static AnalysisKey Key;

public:
  /// Provide the result typedef for this analysis pass.
  struct Result {
    int totalBasicBlocks = 0;
  };

  Result run(Function &F, FunctionAnalysisManager &);
};
