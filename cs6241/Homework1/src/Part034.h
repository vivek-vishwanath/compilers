#include "llvm/IR/Function.h"
#include "llvm/IR/PassManager.h"

using namespace llvm;

class Part34 : public PassInfoMixin<Part34> {
public:
  static bool isRequired() { return true; }

  PreservedAnalyses run(Module &, ModuleAnalysisManager &);
};
