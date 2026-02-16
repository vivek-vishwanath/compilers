#include "llvm/IR/Analysis.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/IR/PassManager.h"
#include "llvm/Support/JSON.h"
#include "llvm/Support/raw_ostream.h"

using namespace llvm;

class Part33 : public PassInfoMixin<Part33> {
private:
  double avgTime, avgDef, avgReachable;
  void printInfo();

  // Example function to implement reachability:
  // bool isReachable(BasicBlock *src, BasicBlock *dest, FunctionAnalysisManager &FAM);

public:
  PreservedAnalyses run(Module &, ModuleAnalysisManager &);
  static bool isRequired() { return true; }
};

