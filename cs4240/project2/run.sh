#!/bin/bash

# Write a script to run your optimizer in this file
# This script should take 2 command line arguments: a path to
# the input ir file and a path where the output ir file should
# be created.

#!/bin/bash

# Usage:
# ./run.sh path/to/file.ir         => uses --naive
# ./run.sh path/to/file.ir -o      => uses --chaitin

IR_FILE="$1"

# Output file is always DIR/out.s
OUT_FILE="$2"

# Determine mode
if [[ "$3" == "-o" || "$3" == "--opt" ]]; then
    MODE="--chaitin"
else
    MODE="--naive"
fi

# Run the Java program
java -cp materials/build backend.Generator "$IR_FILE" "$OUT_FILE" "$MODE"
