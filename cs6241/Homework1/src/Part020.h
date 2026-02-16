#include "llvm/IR/Function.h"
#include "llvm/IR/PassManager.h"
#include "llvm/Support/JSON.h"
#include "llvm/Support/raw_ostream.h"

using namespace llvm;

class Part2 : public AnalysisInfoMixin<Part2> {
public:
  friend AnalysisInfoMixin<Part2>;
  static AnalysisKey Key;

  struct Result {
    struct Stats {
      double avg;
      int min, max;
    };
    struct TerminatorInstStats {
      Stats switchInstStats, branchInstStats, otherInstStats;
    } terminatorInstStats;
    Stats basicBlockStats, cfgEdgeStats;
    struct DominatorStats {
      /// These are the average number of dominators per basic block in a
      /// function.
      /// So, `dominatorNum.avg` is the average over functions of the
      /// average number of dominators per basic block
      double avgDominatorNum;
      Function *maxDominatorFunc;
      Function *minDominatorFunc;
    } dominatorBlockStats;
  };

  Result run(Module &M, ModuleAnalysisManager &MAM);
};

class Part2Printer : public PassInfoMixin<Part2Printer> {
private:
  llvm::json::Object statsToJson(const Part2::Result::Stats &S) {
    return llvm::json::Object{{"avg", S.avg}, {"min", S.min}, {"max", S.max}};
  }

  void printResultToJson(const Part2::Result &R) {
    llvm::json::Object TermStats;
    TermStats["switchInstStats"] =
        statsToJson(R.terminatorInstStats.switchInstStats);
    TermStats["branchInstStats"] =
        statsToJson(R.terminatorInstStats.branchInstStats);
    TermStats["otherInstStats"] =
        statsToJson(R.terminatorInstStats.otherInstStats);

    llvm::json::Object Root;
    Root["terminatorInstStats"] = std::move(TermStats);
    Root["basicBlockStats"] = statsToJson(R.basicBlockStats);
    Root["cfgEdgeStats"] = statsToJson(R.cfgEdgeStats);
    llvm::json::Object DomStats;
    DomStats["avgDominatorNum"] = R.dominatorBlockStats.avgDominatorNum;
    DomStats["maxDominatorFunc"] =
        R.dominatorBlockStats.maxDominatorFunc
            ? R.dominatorBlockStats.maxDominatorFunc->getName()
            : "null";
    DomStats["minDominatorFunc"] =
        R.dominatorBlockStats.minDominatorFunc
            ? R.dominatorBlockStats.minDominatorFunc->getName()
            : "null";
    Root["dominatorBlockStats"] = std::move(DomStats);

    std::error_code EC;
    if (!EC) {
      llvm::errs() << llvm::formatv("{0:2}",
                                    llvm::json::Value(std::move(Root)));
    }
  }

public:
  static bool isRequired() { return true; }

  PreservedAnalyses run(Module &M, ModuleAnalysisManager &MAM) {
    auto result = MAM.getResult<Part2>(M);
    // Print the results in JSON format
    printResultToJson(result);
    return PreservedAnalyses::all();
  };
};
