# CS6241 Homework 0 (ungraded): Setting up LLVM

For homework assignments in this course, you'll need to get LLVM and CMake up and running on your machine. We're rolling with LLVM 21 this semester. 

## Getting started with LLVM
You've got three paths to glory here. May your choice be the most enthralling.

### Option 1. Use VSCode Dev Containers (Recommended)
Hit the ground running with a Docker container with all dependencies ready.

1. Follow the instructions [here](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) to install VSCode, Docker, and the Dev Containers extension for VSCode.
2. Clone or download this repository to your local machine and open it in VSCode.
3. Reopen the folder in a container with "Dev Containers: Reopen in Container" command from the Command Palette (Ctrl+Shift+P).
> You can click on the bottom-left corner button (which looks like `><`) and select "Reopen in Container" from there as well.
4. Wait for the container to start. Verify that LLVM and CMake are installed by opening a terminal in VSCode (Ctrl+`) and running:
   ```bash
   clang --version
   cmake --version
   ```

If you are on Windows, you can also use WSL2 with the above method. Just make sure Docker is set up to work with WSL2. See [here for more details](https://code.visualstudio.com/docs/devcontainers/containers#_open-a-wsl-2-folder-in-a-container-on-windows).

### Option 2. Install LLVM Locally
If you prefer working bare-metal on your local machine, installing LLVM 21 and CMake is straightforward.

Grab LLVM via your package manager, or build it from source.

> Install `CMake` if you don't have it already. [Stackoverflow has good instructions](https://askubuntu.com/questions/355565/how-do-i-install-the-latest-version-of-cmake-from-the-command-line).

#### Option 2.1 Install LLVM 21 (Recommended)
Follow the steps [here](https://apt.llvm.org/) to install LLVM 21 on your system.

```bash
wget https://apt.llvm.org/llvm.sh
chmod +x llvm.sh
sudo ./llvm.sh 21
```

See [instructions for Windows here](https://llvm.org/docs/GettingStartedVS.html).

Fair warning: I'm both reluctant and inexperienced in debugging Windows installation issues, so consider using WSL2 or a Linux/Mac machine if possible, thanks!

#### Option 2.2 Build from Source
Go the LLVM developer route and build it yourself. Follow the instructions [here](https://llvm.org/docs/GettingStarted.html) to build LLVM from source.

> Tip: You won't need multiple targets for this course. Stick to your host target with the `-DLLVM_TARGETS_TO_BUILD` option to only build required stuff. The example below is for x86_64 hosts.

Here is a minimal example:
```bash
git clone --depth 1 https://github.com/llvm/llvm-project.git
cd llvm-project
cmake -S llvm -B build -GNinja \
  -DLLVM_ENABLE_PROJECTS="clang" \
  -DLLVM_TARGETS_TO_BUILD="X86" \
  -DCMAKE_BUILD_TYPE=Release
cmake --build build --target install
# Verify
clang --version
opt --version
```

### Option 3. Use a virtual machine
If you really want the old-school tread. See the previous course offering's instructions [here.](VM%20Setup.md) 

> [!CAUTION]
> The VM ships with LLVM 15, so you'll need to bump it up to 21.
> So don't do this since you'll have to install LLVM 21 anyway; just use 1 or 2 above. I am keeping this here for archival purposes.

## Running the homework
Once you've got LLVM and CMake locked and loaded, you're ready to build and run LLVM passes.

Clone this repository to your local machine and navigate to its root directory.

```bash
cmake -B build -S . -GNinja -DCMAKE_INSTALL_PREFIX=./install
cmake --build build --target install
```

This will build the LLVM pass and analysis in `src` and place the compiled libraries in `install/lib`.

Run the pass with `opt`:

```bash
opt -load-pass-plugin ./install/lib/libHW0.so -passes='counter-pass' -o /dev/null test/test.ll
```

You should see 
```
Function main has 0 basic blocks.
```

Correct the analysis logic.
```diff
  CounterAnalysis::Result CounterAnalysis::run(Function &F, 
    FunctionAnalysisManager&) {
-    return Result {0};
+    return Result {static_cast<int>(F.size())};
  }
```
Rebuild and rerun the pass.
```bash
cmake --build build --target install
opt -load-pass-plugin ./install/lib/libHW0.so -passes='counter-pass' -o /dev/null test/test.ll
```
You should see 
```
Function main has 4 basic blocks.
```

> Now is a good time to get into LLVM.
>
> Start here with the official guides: https://llvm.org/docs/
>
> Bookmark this: https://llvm.org/docs/ProgrammersManual.html
> 
> Want the full story? Check out the origins: https://llvm.org/pubs/2002-12-LattnerMSThesis.pdf
>

## Submitting on GradeScope
Once you've completed the homework, run `python3 zip_submission.py` and upload the generated `submission.zip` file to test-drive the compiler on GradeScope.

> `zip_submission.sh` also does the same thing.


