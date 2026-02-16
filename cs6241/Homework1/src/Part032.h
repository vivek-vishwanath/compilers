#include "llvm/IR/PassManager.h"
#include "llvm/Support/JSON.h"
#include "llvm/Support/raw_ostream.h"
#include <llvm/IR/Analysis.h>

using namespace llvm;

class Part32 : public AnalysisInfoMixin<Part32> {
public:
  friend AnalysisInfoMixin<Part32>;
  static AnalysisKey Key;

  /// Cycle counts across all functions in the module
  struct Result {
    unsigned int singleEntryCycles,
        twoEntryCycles, threeEntryCycles,
        fourEntryCycles, multiEntryCycles,
        totalCycles;
  };

  Result run(Module &M, ModuleAnalysisManager &MAM);
};


class Part32Printer : public PassInfoMixin<Part32Printer> {
public:
  static bool isRequired() { return true; }
  PreservedAnalyses run(Module &M, ModuleAnalysisManager &MAM) {
    auto result = MAM.getResult<Part32>(M);

    llvm::json::Object json;
    json["singleEntryCycles"] = result.singleEntryCycles;
    json["twoEntryCycles"] = result.twoEntryCycles;
    json["threeEntryCycles"] = result.threeEntryCycles;
    json["fourEntryCycles"] = result.fourEntryCycles;
    json["multiEntryCycles"] = result.multiEntryCycles;
    json["totalCycles"] = result.totalCycles;

    std::error_code EC;
    if (!EC) {
      llvm::errs() << llvm::formatv("{0:2}", llvm::json::Value(std::move(json)));
    }
    return PreservedAnalyses::all();
  }
};