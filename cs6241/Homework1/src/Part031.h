#include "llvm/IR/PassManager.h"
#include "llvm/Analysis/LoopInfo.h"
#include "llvm/Support/JSON.h"
#include "llvm/Support/raw_ostream.h"

using namespace llvm;

class Part31 : public AnalysisInfoMixin<Part31> {
public:
  friend AnalysisInfoMixin<Part31>;
  static AnalysisKey Key;

  struct Result {
    unsigned int totalNumLoops, totalNumOuterLoops, totalNumLoopBBs,
        totalNumExitEdges;
    /// Longest acylcic path in a loop nest (length is the number of basic
    /// blocks in the path)
    unsigned int longestPathLength;
    /// The loop that contains the longest acyclic path
    Loop *longestPathLoop;
  };

  Result run(Module &M, ModuleAnalysisManager &MAM);
};



class Part31Printer : public PassInfoMixin<Part31Printer> {
  llvm::json::Object resultToJson(const Part31::Result &R) {
    llvm::json::Object json;
    json["totalNumLoops"] = R.totalNumLoops;
    json["totalNumOuterLoops"] = R.totalNumOuterLoops;
    json["totalNumLoopBBs"] = R.totalNumLoopBBs;
    json["totalNumExitEdges"] = R.totalNumExitEdges;
    json["longestPathLength"] = R.longestPathLength;
    StringRef loopInfoStr = R.longestPathLoop
                                ? R.longestPathLoop->getHeader()->getName()
                                : "null";
    json["longestPathLoopHeader"] = loopInfoStr;
    return json;
  }

public:
  static bool isRequired() { return true; }
  
  PreservedAnalyses run(Module &M, ModuleAnalysisManager &MAM) {
    auto result = MAM.getResult<Part31>(M);
    auto jsonResult = resultToJson(result);
    std::error_code EC;
    if (!EC) {
      llvm::errs() << llvm::formatv("{0:2}", llvm::json::Value(std::move(jsonResult)));
    }
    return PreservedAnalyses::all();
  }
};
