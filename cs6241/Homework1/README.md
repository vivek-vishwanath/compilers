# CS 6241 Homework 1
> [!NOTE]
> **Question 1.3**: Find the benchmark here: [https://github.gatech.edu/CS6241/Homework1-Q1](https://github.gatech.edu/CS6241/Homework1-Q1)


There are three folders in the repo:
* **src:** source code for llvm passes. For each question in the homework, there is a corresponding "Part*.cpp" files.
* **test_code:** eight benchmarks to validate your solutions
* **script:** bash scripts to help launch llvm passes

You only need to fill in your solutions in the implementation files:

| File        | Questions                |
| ----------- | ------------------------ |
| Part020.cpp | Part 2                   |
| Part031.cpp | Part 3.1 (questions 1-5) |
| Part032.cpp | Part 3.2                 |
| Part033.cpp | Part 3.3                 |
| Part034.cpp | Part 3.4                 |
| Part040.cpp | Part 4 (bonus)           |

## Building and running the passes
This uses CMake to build the project as in Homework 0.
Invoke the following commands in the root folder of the repo:
```bash
cmake -S . -B build -DCMAKE_INSTALL_PREFIX=./install -GNinja
cmake --build build --target install
```

After successfully building the repo, the llvm passes for HW1, compiled benchmarks, and two bash scripts will be installed into the folder ``<ROOT_OF_HW1_REPO>/install``
For part 2, part 3.1, part 3.2, and part 3.3, please use the script ``run_pass.sh`` in ``<ROOT_OF_HW1_REPO>/install`` to run your solution on a selected benchmark.
For part 3.4, please use ``run_pass_and_compile.sh`` to instrument the benchmark and generate the executable.

Description of ``run_pass.sh``:

    Run a pass with specified benchmark
    run_pass.sh [pass] [benchmark]

    Options:
        valid pass:       part2 part31 part32 part33 part34 part4
        valid benchmark:  lbm mcf bc cc pr sssp tc

Descripion of ``run_pass_and_compile.sh``:

    Use a pass to transfrom the specified benchmark
    run_pass_and_compile.sh [pass] [benchmark]

    Options:
        valid pass:       part2 part31 part32 part33 part34 part4
        valid benchmark:  lbm mcf bc cc pr sssp tc

All compiled benchmarks are placed in ``<ROOT_OF_HW1_REPO>/install/test_code``, including LLVM bitcode files (``<ROOT_OF_HW1_REPO>/install/test_code/llvm_ir``) 
and the corresponding human-readable format (``<ROOT_OF_HW1_REPO>/install/test_code/human_readable``). You can use the human-readable format of LLVM IR to manually check the output of your solutions.
These files can be viewed using any text editor (e.g., vim/emacs/nano).

### Output of the passes
The output of each pass is in JSON format, printed to standard error (stderr).
This will be captured by the autograder to validate your solutions.

The output format for Parts 3.3 and 3.4 are described in the [report format](report_format.md).

## Submitting on Gradescope
Submit your source files in `src` folder only on Gradescope.
You can use `python3 script/zip_submission.py` to generate the submission zip file.

> Preferrably, use LLVM 21 or later to build and run the passes, since the autograder uses LLVM 21.
