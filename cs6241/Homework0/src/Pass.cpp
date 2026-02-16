#include "Analyses.h"
#include "llvm/IR/Analysis.h"
#include <llvm/Passes/PassPlugin.h>

using namespace llvm;

class CounterPass : public PassInfoMixin<CounterPass> {
public:
  static bool isRequired() { return true; }

  PreservedAnalyses run(Function &F, FunctionAnalysisManager &FAM) {
    auto &CountResult = FAM.getResult<CounterAnalysis>(F);
    llvm::errs() << "Function " << F.getName()
                  << " has " << CountResult.totalBasicBlocks
                  << " basic blocks.\n";
    return PreservedAnalyses::all();
  }
};


extern "C" LLVM_ATTRIBUTE_WEAK ::llvm::PassPluginLibraryInfo
llvmGetPassPluginInfo() {
  return {LLVM_PLUGIN_API_VERSION, "CS6241HW0", LLVM_VERSION_STRING,
          [](PassBuilder &PB) {
            PB.registerAnalysisRegistrationCallback(
                [](FunctionAnalysisManager &FAM) {
                  FAM.registerPass([] { return CounterAnalysis(); });
                });

            PB.registerPipelineParsingCallback(
                [](StringRef Name, FunctionPassManager &FPM,
                   ArrayRef<PassBuilder::PipelineElement>) {
                  if (Name == "counter-pass") {
                    FPM.addPass(CounterPass());
                    return true;
                  }
                  return false;
                });
          }};
}