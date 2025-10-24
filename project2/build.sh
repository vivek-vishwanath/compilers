#!/bin/bash

# Write a script to build your optimizer in this file 
# (As required by your chosen optimizer language)

# 1. Compile Java sources
find src -name "*.java" > materials/java_sources.txt
javac -d materials/build @materials/java_sources.txt
