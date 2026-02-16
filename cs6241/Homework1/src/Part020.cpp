#include "Part020.h"
#include "llvm/IR/Module.h"
#include "llvm/Analysis/CFG.h"
#include "llvm/IR/Dominators.h"
#include "llvm/IR/Instructions.h"
#include <limits>

using namespace llvm;

AnalysisKey Part2::Key;

Part2::Result Part2::run(Module &M, ModuleAnalysisManager &MAM) {
  // Use the FunctionAnalysisManager to get per-function analyses.
  FunctionAnalysisManager &FAM =
      MAM.getResult<FunctionAnalysisManagerModuleProxy>(M).getManager();

  Result result;

  // Initialize stats with sentinel values
  result.basicBlockStats = {0.0, std::numeric_limits<int>::max(), std::numeric_limits<int>::min()};
  result.cfgEdgeStats = {0.0, std::numeric_limits<int>::max(), std::numeric_limits<int>::min()};
  result.terminatorInstStats.switchInstStats = {0.0, std::numeric_limits<int>::max(), std::numeric_limits<int>::min()};
  result.terminatorInstStats.branchInstStats = {0.0, std::numeric_limits<int>::max(), std::numeric_limits<int>::min()};
  result.terminatorInstStats.otherInstStats = {0.0, std::numeric_limits<int>::max(), std::numeric_limits<int>::min()};
  result.dominatorBlockStats = {0.0, nullptr, nullptr};

  int totalBBs = 0;
  int totalEdges = 0;
  int numFunctions = 0;
  int numFunctionsWithSwitch = 0;
  int numFunctionsWithBranch = 0;
  int numFunctionsWithOther = 0;
  int totalSwitchesPerFunc = 0;
  int totalBranchesPerFunc = 0;
  int totalOthersPerFunc = 0;

  double totalAvgDominators = 0.0;
  double maxAvgDominators = std::numeric_limits<double>::lowest();
  double minAvgDominators = std::numeric_limits<double>::max();

  // Iterate over each function in the module
  for (Function &F : M) {
    if (F.isDeclaration())
      continue;

    numFunctions++;

    int bbCount = 0;
    for (BasicBlock &BB : F) {
      bbCount++;
    }

    totalBBs += bbCount;
    if (bbCount < result.basicBlockStats.min)
      result.basicBlockStats.min = bbCount;
    if (bbCount > result.basicBlockStats.max)
      result.basicBlockStats.max = bbCount;

    int edgeCount = 0;
    int switchCountInFunc = 0;
    int branchCountInFunc = 0;
    int otherCountInFunc = 0;

    for (BasicBlock &BB : F) {
      Instruction *terminator = BB.getTerminator();
      if (!terminator)
        continue;

      int numSuccessors = terminator->getNumSuccessors();
      edgeCount += numSuccessors;

      // Categorize terminator instructions
      if (isa<SwitchInst>(terminator)) {
        switchCountInFunc++;
      } else if (isa<BranchInst>(terminator)) {
        branchCountInFunc++;
      } else {
        otherCountInFunc++;
      }
    }

    // Update per-function counts
    if (switchCountInFunc > 0) {
      numFunctionsWithSwitch++;
      totalSwitchesPerFunc += switchCountInFunc;
      if (switchCountInFunc < result.terminatorInstStats.switchInstStats.min)
        result.terminatorInstStats.switchInstStats.min = switchCountInFunc;
      if (switchCountInFunc > result.terminatorInstStats.switchInstStats.max)
        result.terminatorInstStats.switchInstStats.max = switchCountInFunc;
    }

    if (branchCountInFunc > 0) {
      numFunctionsWithBranch++;
      totalBranchesPerFunc += branchCountInFunc;
      if (branchCountInFunc < result.terminatorInstStats.branchInstStats.min)
        result.terminatorInstStats.branchInstStats.min = branchCountInFunc;
      if (branchCountInFunc > result.terminatorInstStats.branchInstStats.max)
        result.terminatorInstStats.branchInstStats.max = branchCountInFunc;
    }

    if (otherCountInFunc > 0) {
      numFunctionsWithOther++;
      totalOthersPerFunc += otherCountInFunc;
      if (otherCountInFunc < result.terminatorInstStats.otherInstStats.min)
        result.terminatorInstStats.otherInstStats.min = otherCountInFunc;
      if (otherCountInFunc > result.terminatorInstStats.otherInstStats.max)
        result.terminatorInstStats.otherInstStats.max = otherCountInFunc;
    }

    // Update CFG edge stats
    totalEdges += edgeCount;
    if (edgeCount < result.cfgEdgeStats.min)
      result.cfgEdgeStats.min = edgeCount;
    if (edgeCount > result.cfgEdgeStats.max)
      result.cfgEdgeStats.max = edgeCount;

    // Get dominator tree for this function
    DominatorTree &DT = FAM.getResult<DominatorTreeAnalysis>(F);

    // Calculate average number of dominators per basic block in this function
    int totalDominators = 0;
    int numBBsInFunction = 0;
    for (BasicBlock &BB : F) {
      numBBsInFunction++;
      DomTreeNode *Node = DT.getNode(&BB);
      if (Node) {
        // Count dominators by walking up the dominator tree
        int dominatorCount = 0;
        DomTreeNode *Current = Node;
        while (Current) {
          dominatorCount++;
          Current = Current->getIDom();
        }
        totalDominators += dominatorCount;
      }
    }

    double avgDominatorsInFunc = numBBsInFunction > 0
        ? static_cast<double>(totalDominators) / numBBsInFunction
        : 0.0;

    totalAvgDominators += avgDominatorsInFunc;

    // Track functions with max/min average dominators
    if (avgDominatorsInFunc > maxAvgDominators) {
      maxAvgDominators = avgDominatorsInFunc;
      result.dominatorBlockStats.maxDominatorFunc = &F;
    }
    if (avgDominatorsInFunc < minAvgDominators) {
      minAvgDominators = avgDominatorsInFunc;
      result.dominatorBlockStats.minDominatorFunc = &F;
    }
  }

  // Compute final averages
  if (numFunctions > 0) {
    result.basicBlockStats.avg = static_cast<double>(totalBBs) / numFunctions;
    result.cfgEdgeStats.avg = static_cast<double>(totalEdges) / numFunctions;
    result.dominatorBlockStats.avgDominatorNum = totalAvgDominators / numFunctions;
  }

  if (numFunctionsWithSwitch > 0) {
    result.terminatorInstStats.switchInstStats.avg =
        static_cast<double>(totalSwitchesPerFunc) / numFunctionsWithSwitch;
  } else {
    result.terminatorInstStats.switchInstStats.min = 0;
    result.terminatorInstStats.switchInstStats.max = 0;
  }

  if (numFunctionsWithBranch > 0) {
    result.terminatorInstStats.branchInstStats.avg =
        static_cast<double>(totalBranchesPerFunc) / numFunctionsWithBranch;
  } else {
    result.terminatorInstStats.branchInstStats.min = 0;
    result.terminatorInstStats.branchInstStats.max = 0;
  }

  if (numFunctionsWithOther > 0) {
    result.terminatorInstStats.otherInstStats.avg =
        static_cast<double>(totalOthersPerFunc) / numFunctionsWithOther;
  } else {
    result.terminatorInstStats.otherInstStats.min = 0;
    result.terminatorInstStats.otherInstStats.max = 0;
  }

  // Handle edge cases for empty modules
  if (numFunctions == 0) {
    result.basicBlockStats.min = 0;
    result.basicBlockStats.max = 0;
    result.cfgEdgeStats.min = 0;
    result.cfgEdgeStats.max = 0;
  }

  return result;
}