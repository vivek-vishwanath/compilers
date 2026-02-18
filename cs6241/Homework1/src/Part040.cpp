#include "Part040.h"

#include "llvm/IR/Module.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/IR/Instructions.h"
#include "llvm/Analysis/CFG.h"
#include <map>
#include <set>
#include <queue>

using namespace llvm;

std::map<Function *, std::set<Function *>> localAlways;
std::map<Function *, std::set<Function *>> nonLocalAlways;
std::map<Function *, std::set<Function *>> alwaysExecuted;

std::set<Function *> get_direct_callees(BasicBlock &BB) {
  std::set<Function *> callees;
  for (auto &I : BB) {
    if (CallBase *CB = dyn_cast<CallBase>(&I)) {
      Function *callee = CB->getCalledFunction();
      if (callee && !callee->isDeclaration())
        callees.insert(callee);
    }
  }
  return callees;
}

void computeLocalAlways(Function &F) {
  std::map<BasicBlock *, std::set<Function *>> alwaysCall;
  std::set<BasicBlock *> visited;
  std::vector<BasicBlock *> postOrder;
  std::set<BasicBlock *> seen;

  std::function<void(BasicBlock *)> dfs = [&](BasicBlock *BB) {
    if (!seen.insert(BB).second) return;
    for (auto *successor : successors(BB))
      dfs(successor);
    postOrder.push_back(BB);
  };
  dfs(&F.getEntryBlock());

  bool changed = true;
  while (changed) {
    changed = false;
    for (auto BB : postOrder) {
      std::set<Function *> newSet = get_direct_callees(*BB);

      auto succesors = successors(BB);
      if (succesors.begin() != succesors.end()) {
        bool first_succesor = true;
        std::set<Function *> intersection;
        for (BasicBlock *succ : succesors) {
          if (visited.count(succ) == 0) {
            continue;
          }
          if (first_succesor) {
            intersection = alwaysCall[succ];
            first_succesor = false;
          } else {
            std::set<Function *> tmp;
            for (auto *fn : intersection)
              if (alwaysCall[succ].count(fn))tmp.insert(fn);
            intersection = tmp;
          }
        }
        for (auto *fn : intersection)
          newSet.insert(fn);
      }
      visited.insert(BB);
      if (newSet != alwaysCall[BB]) {
        alwaysCall[BB] = newSet;
        changed = true;
      }
    }
  }

  localAlways[&F] = alwaysCall[&F.getEntryBlock()];
}

void computeNonLocalAlways(Function &F) {
  std::map<BasicBlock *, std::set<Function *>> mustTrans;
  std::set<BasicBlock *> visited;

  std::vector<BasicBlock *> postOrder;
  std::set<BasicBlock *> seen;
  std::function<void(BasicBlock *)> dfs = [&](BasicBlock *BB) {
    if (!seen.insert(BB).second) return;
    for (auto *succ : successors(BB)) dfs(succ);
    postOrder.push_back(BB);
  };
  dfs(&F.getEntryBlock());

  bool changed = true;
  while (changed) {
    changed = false;
    for (auto it = postOrder.begin(); it != postOrder.end(); ++it) {
      BasicBlock *BB = *it;
      std::set<Function *> newSet = get_direct_callees(*BB);
      std::set<Function *> expanded = newSet;
      for (auto *callee : newSet)
        for (auto *transCallee : alwaysExecuted[callee])
          expanded.insert(transCallee);
      auto succesors = successors(BB);
      if (succesors.begin() != succesors.end()) {
        bool firstSucc = true;
        std::set<Function *> interSection;
        for (BasicBlock *succ : succesors) {
          if (!visited.count(succ)) continue;
          if (firstSucc) {
            interSection = mustTrans[succ];
            firstSucc = false;
          } else {
            std::set<Function *> tmp;
            for (auto *fn : interSection)
              if (mustTrans[succ].count(fn))
                tmp.insert(fn);
            interSection = tmp;
          }
        }
        for (auto *fn : interSection)
          expanded.insert(fn);
      }

      visited.insert(BB);
      if (expanded != mustTrans[BB]) {
        mustTrans[BB] = expanded;
        changed = true;
      }
    }
  }

  std::set<Function *> entrySet = mustTrans[&F.getEntryBlock()];
  for (auto *fn : localAlways[&F])
    entrySet.erase(fn);
  nonLocalAlways[&F] = entrySet;
}


AnalysisKey Part4::Key;

Part4::Result Part4::run(Module &M, ModuleAnalysisManager &MAM) {
  Result result;

  for (auto &F : M) {
    if (F.isDeclaration()) continue;
    localAlways[&F].clear();
    nonLocalAlways[&F].clear();
    alwaysExecuted[&F].clear();
  }

  for (auto &F : M) {
    if (F.isDeclaration()) continue;
    computeLocalAlways(F);
  }

  bool changed = true;
  while (changed) {
    changed = false;

    for (auto &F : M) {
      if (F.isDeclaration()) continue;
      std::set<Function *> oldAlways = alwaysExecuted[&F];
      computeNonLocalAlways(F);

      alwaysExecuted[&F] = localAlways[&F];
      for (auto *fn : nonLocalAlways[&F])
        alwaysExecuted[&F].insert(fn);

      if (alwaysExecuted[&F] != oldAlways)
        changed = true;
    }
  }

  Function *mainFn = M.getFunction("main");
  for (auto &[fn, calleeSet] : localAlways) {
    result.localAlways[fn].insert(calleeSet.begin(), calleeSet.end());
  }
  result.alwaysExecuted.insert(alwaysExecuted[mainFn].begin(), alwaysExecuted[mainFn].end());
  // outs() << "alwaysExecuted size: " << result.alwaysExecuted.size() << "\n";
  return result;
}


