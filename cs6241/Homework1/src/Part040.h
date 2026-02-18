#include "llvm/ADT/DenseMap.h"
#include "llvm/ADT/DenseSet.h"
#include "llvm/ADT/StringRef.h"
#include "llvm/IR/PassManager.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/JSON.h"
#include "llvm/Support/raw_ostream.h"

using namespace llvm;

class Part4 : public AnalysisInfoMixin<Part4> {
public:
  friend AnalysisInfoMixin<Part4>;
  static AnalysisKey Key;

  struct Result {
    llvm::DenseMap<Function *, DenseSet<Function *>> localAlways;
    llvm::DenseSet<Function *> alwaysExecuted;
  };

  Result run(Module &M, ModuleAnalysisManager &MAM);
};


class Part4Printer : public PassInfoMixin<Part4Printer> {
private:
void serializeExecutionResults(const Part4::Result &R) {
    llvm::json::Object Root;

    llvm::json::Object LocalMap;
    for (auto const& [Func, Set] : R.localAlways) {
        llvm::json::Array FunctionSet;
        for (Function *F : Set) {
            FunctionSet.push_back(F->getName().str());
        }
        LocalMap[Func->getName().str()] = std::move(FunctionSet);
    }
    Root["locallyAlwaysExecuted"] = std::move(LocalMap);

    llvm::json::Array MainSet;
    for (Function *F : R.alwaysExecuted) {
        MainSet.push_back(F->getName().str());
    }
    Root["alwaysExecutedFromMain"] = std::move(MainSet);

    std::error_code EC;
    if (!EC) {
        errs() << llvm::formatv("{0:2}", llvm::json::Value(std::move(Root)));
    }
}
public:
  static bool isRequired() { return true; }
  PreservedAnalyses run(Module &M, ModuleAnalysisManager &MAM) {
    auto result = MAM.getResult<Part4>(M);

    serializeExecutionResults(result);
    return PreservedAnalyses::all();
  }
};

