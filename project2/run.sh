#!/bin/bash

# Write a script to run your optimizer in this file 
# This script should take 2 command line arguments: a path to 
# the input ir file and a path where the output ir file should
# be created.

java -cp materials/build backend.Generator $1 $2