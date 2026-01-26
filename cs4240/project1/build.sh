#!/bin/bash

# Write a script to build your optimizer in this file 
# (As required by your chosen optimizer language)

cd materials
mkdir -p build

# 1. Compile Java sources
find src -name "*.java" > java_sources.txt
javac -d build @java_sources.txt