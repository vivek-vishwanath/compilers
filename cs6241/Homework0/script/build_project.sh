#! /bin/sh
pwd
# exit
if [ ! -d build ]; then
  cmake -B build -S . -DCMAKE_INSTALL_PREFIX=./install
fi
cmake --build build --target install