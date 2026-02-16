cmake_minimum_required(VERSION 3.20)
project(cs6241-hw1)

set(CMAKE_CXX_STANDARD 17)
set(LLVM_DIR "/usr/lib/llvm-21/cmake")

find_package(LLVM REQUIRED CONFIG)

message(STATUS "Found LLVM ${LLVM_PACKAGE_VERSION}")
# Ensure the version is atleast 21
if(LLVM_VERSION VERSION_LESS "21.0.0")
  message(FATAL_ERROR "LLVM version must be 21.x")
endif()

if(LLVM_VERSION VERSION_GREATER_EQUAL "23.0.0")
  message(FATAL_ERROR "LLVM version must be 21.x")
endif()

add_definitions(${LLVM_DEFINITIONS})
include_directories(${LLVM_INCLUDE_DIRS})
link_directories(${LLVM_LIBRARY_DIRS})

if(NOT LLVM_ENABLE_RTTI)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-rtti")
endif()

if(CMAKE_INSTALL_PREFIX_INITIALIZED_TO_DEFAULT)
  message(FATAL_ERROR "You must set CMAKE_INSTALL_PREFIX explicitly (e.g., with -DCMAKE_INSTALL_PREFIX=...)")
endif()


add_subdirectory(src)
add_subdirectory(script)
add_subdirectory(test_code)