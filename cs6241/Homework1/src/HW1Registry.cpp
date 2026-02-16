#include "Part020.h"
#include "Part031.h"
#include "Part032.h"
#include "Part033.h"
#include "Part034.h"
#include "Part040.h"
#include "llvm/Passes/PassBuilder.h"
#include "llvm/Passes/PassPlugin.h"

using namespace llvm;

extern "C" LLVM_ATTRIBUTE_WEAK ::llvm::PassPluginLibraryInfo
llvmGetPassPluginInfo() {
  return {LLVM_PLUGIN_API_VERSION, "HW1Driver", LLVM_VERSION_STRING,
          [](PassBuilder &PB) {
            // Register analyses
            PB.registerAnalysisRegistrationCallback(
                [](ModuleAnalysisManager &MAM) {
                  MAM.registerPass([] { return Part2(); });
                  MAM.registerPass([] { return Part31(); });
                  MAM.registerPass([] { return Part32(); });
                  MAM.registerPass([] { return Part4(); });
                });

            // Register all passes
            PB.registerPipelineParsingCallback(
                [](StringRef Name, ModulePassManager &MPM,
                   ArrayRef<PassBuilder::PipelineElement>) {
                  if (Name == "part2") {
                    MPM.addPass(Part2Printer());
                    return true;
                  } else if (Name == "part31") {
                    MPM.addPass(Part31Printer());
                    return true;
                  } else if (Name == "part32") {
                    MPM.addPass(Part32Printer());
                    return true;
                  } else if (Name == "part33") {
                    MPM.addPass(Part33());
                    return true;
                  } else if (Name == "part34") {
                    MPM.addPass(Part34());
                    return true;
                  } else if (Name == "part4") {
                    MPM.addPass(Part4Printer());
                    return true;
                  }
                  return false;
                });
          }};
}