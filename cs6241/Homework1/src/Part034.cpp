#include "Part034.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/Transforms/Utils/ModuleUtils.h"

using namespace llvm;

// Declare the external trace functions (implemented in trace_runtime.c)
FunctionCallee getOrInsertTraceFunctionEntry(Module &M) {
  LLVMContext &Ctx = M.getContext();

  // Declare: void __trace_function_entry(i8* function_name)
  Type *VoidTy = Type::getVoidTy(Ctx);
  PointerType *Int8PtrTy = PointerType::getUnqual(Ctx);

  FunctionType *FT = FunctionType::get(VoidTy, {Int8PtrTy}, false);
  return M.getOrInsertFunction("__trace_function_entry", FT);
}

FunctionCallee getOrInsertTraceFunctionExit(Module &M) {
  LLVMContext &Ctx = M.getContext();

  // Declare: void __trace_function_exit(i8* function_name)
  Type *VoidTy = Type::getVoidTy(Ctx);
  PointerType *Int8PtrTy = PointerType::getUnqual(Ctx);

  FunctionType *FT = FunctionType::get(VoidTy, {Int8PtrTy}, false);
  return M.getOrInsertFunction("__trace_function_exit", FT);
}

FunctionCallee getOrInsertTraceInit(Module &M) {
  LLVMContext &Ctx = M.getContext();

  // Declare: void __trace_init()
  Type *VoidTy = Type::getVoidTy(Ctx);
  FunctionType *FT = FunctionType::get(VoidTy, {}, false);
  return M.getOrInsertFunction("__trace_init", FT);
}

FunctionCallee getOrInsertTraceFinalize(Module &M) {
  LLVMContext &Ctx = M.getContext();

  // Declare: void __trace_finalize()
  Type *VoidTy = Type::getVoidTy(Ctx);
  FunctionType *FT = FunctionType::get(VoidTy, {}, false);
  return M.getOrInsertFunction("__trace_finalize", FT);
}

// Create a global string constant for the function name
GlobalVariable* createFunctionNameString(Module &M, StringRef FuncName) {
  LLVMContext &Ctx = M.getContext();

  Constant *StrConstant = ConstantDataArray::getString(Ctx, FuncName);

  GlobalVariable *GV = new GlobalVariable(
    M,
    StrConstant->getType(),
    true,  // isConstant
    GlobalValue::PrivateLinkage,
    StrConstant,
    ".str." + FuncName
  );

  GV->setUnnamedAddr(GlobalValue::UnnamedAddr::Global);
  return GV;
}

// Instrument a single function
void instrumentFunction(Function &F, Module &M) {
  // Skip declarations and runtime trace functions
  if (F.isDeclaration()) return;
  if (F.getName().starts_with("__trace_")) return;
  if (F.getName() == "main") return;  // Handle main specially

  LLVMContext &Ctx = M.getContext();
  IRBuilder<> Builder(Ctx);

  // Get or create the trace functions
  FunctionCallee TraceEntry = getOrInsertTraceFunctionEntry(M);
  FunctionCallee TraceExit = getOrInsertTraceFunctionExit(M);

  // Create a global string for this function's name
  GlobalVariable *FuncNameGV = createFunctionNameString(M, F.getName());

  // Insert entry trace at the beginning of the function
  BasicBlock &EntryBB = F.getEntryBlock();
  Builder.SetInsertPoint(&EntryBB, EntryBB.getFirstInsertionPt());

  // Create GEP to get pointer to the string
  Value *Zero = ConstantInt::get(Type::getInt32Ty(Ctx), 0);
  Value *FuncNamePtr = Builder.CreateInBoundsGEP(
    FuncNameGV->getValueType(),
    FuncNameGV,
    {Zero, Zero}
  );

  // Call __trace_function_entry
  Builder.CreateCall(TraceEntry, {FuncNamePtr});

  // Insert exit trace before each return instruction
  for (BasicBlock &BB : F) {
    Instruction *Terminator = BB.getTerminator();
    if (isa<ReturnInst>(Terminator)) {
      Builder.SetInsertPoint(Terminator);

      // Create GEP again for exit
      Value *FuncNamePtrExit = Builder.CreateInBoundsGEP(
        FuncNameGV->getValueType(),
        FuncNameGV,
        {Zero, Zero}
      );

      // Call __trace_function_exit
      Builder.CreateCall(TraceExit, {FuncNamePtrExit});
    }
  }
}

void createTraceInitConstructor(Module &M) {
  LLVMContext &Ctx = M.getContext();
  FunctionCallee TraceInit = getOrInsertTraceInit(M);

  FunctionType *FT = FunctionType::get(Type::getVoidTy(Ctx), {}, false);
  Function *Ctor = Function::Create(FT, GlobalValue::InternalLinkage, "__trace_init_ctor", M);

  BasicBlock *BB = BasicBlock::Create(Ctx, "entry", Ctor);
  IRBuilder<> Builder(BB);

  Builder.CreateCall(TraceInit);
  Builder.CreateRetVoid();
  appendToGlobalCtors(M, Ctor, 0);
}

void createTraceFinalizeDestructor(Module &M) {
  LLVMContext &Ctx = M.getContext();
  FunctionCallee TraceFinalize = getOrInsertTraceFinalize(M);

  FunctionType *FT = FunctionType::get(Type::getVoidTy(Ctx), {}, false);
  Function *Dtor = Function::Create(FT, GlobalValue::InternalLinkage, "__trace_finalize_dtor", M);

  BasicBlock *BB = BasicBlock::Create(Ctx, "entry", Dtor);
  IRBuilder<> Builder(BB);

  Builder.CreateCall(TraceFinalize);
  Builder.CreateRetVoid();
  appendToGlobalDtors(M, Dtor, 0);
}

// Special handling for main function
void instrumentMain(Function *Main, Module &M) {
  if (!Main || Main->isDeclaration()) return;

  LLVMContext &Ctx = M.getContext();
  IRBuilder<> Builder(Ctx);

  FunctionCallee TraceEntry = getOrInsertTraceFunctionEntry(M);
  FunctionCallee TraceExit = getOrInsertTraceFunctionExit(M);

  GlobalVariable *FuncNameGV = createFunctionNameString(M, "main");
  Value *Zero = ConstantInt::get(Type::getInt32Ty(Ctx), 0);

  // Insert entry trace at the beginning
  BasicBlock &EntryBB = Main->getEntryBlock();
  Builder.SetInsertPoint(&EntryBB, EntryBB.getFirstInsertionPt());

  Value *FuncNamePtr = Builder.CreateInBoundsGEP(
    FuncNameGV->getValueType(),
    FuncNameGV,
    {Zero, Zero}
  );

  Builder.CreateCall(TraceEntry, {FuncNamePtr});

  // Insert exit trace before returns
  for (BasicBlock &BB : *Main) {
    Instruction *Terminator = BB.getTerminator();
    if (isa<ReturnInst>(Terminator)) {
      Builder.SetInsertPoint(Terminator);

      Value *FuncNamePtrExit = Builder.CreateInBoundsGEP(
        FuncNameGV->getValueType(),
        FuncNameGV,
        {Zero, Zero}
      );

      Builder.CreateCall(TraceExit, {FuncNamePtrExit});
    }
  }
}


PreservedAnalyses Part34::run(Module &M, ModuleAnalysisManager &MAM) {
  llvm::outs() << "Part 3.4: Whole program paths - Instrumenting module\n";

  createTraceInitConstructor(M);
  createTraceFinalizeDestructor(M);
  for (Function &F : M) instrumentFunction(F, M);

  return PreservedAnalyses::none();
}