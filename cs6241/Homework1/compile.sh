cmake -S . -B build -DCMAKE_INSTALL_PREFIX=./install -GNinja
cmake --build build --target install
