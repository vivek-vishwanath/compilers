#!/bin/bash

# Write a script to build your optimizer in this file 
# (As required by your chosen optimizer language)

cd materials
mkdir -p build
find src -name "*.kt" -o -name "*.java" > sources.txt
kotlinc -d build @sources.txt
