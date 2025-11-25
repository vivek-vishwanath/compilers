#!/bin/bash
# Usage: ./test.sh <folder>
# Example: ./test.sh public_test_cases/prime/


FOLDER="${1%/}"   # strip trailing slash

# --- Step 1: Find the unique IR file ---
IR_FILE_COUNT=$(find "$FOLDER" -maxdepth 1 -type f -name "*.ir" | wc -l)
if [[ "$IR_FILE_COUNT" -ne 1 ]]; then
    echo "Error: Expected exactly one .ir file in $FOLDER, found $IR_FILE_COUNT"
    exit 1
fi

IR_FILE=$(find "$FOLDER" -maxdepth 1 -type f -name "*.ir" | head -n 1)
BASE_NAME="${IR_FILE%.ir}"   # removes suffix
S_FILE="${BASE_NAME}.s"      # just append .s

# --- Step 1: Run the .ir/.s pair once ---
if [[ ! -f "$IR_FILE" ]]; then
    echo "Error: Missing file $IR_FILE"
    exit 1
fi

echo ">>> Running initial build"
echo "./run.sh $IR_FILE $S_FILE"
./run.sh "$IR_FILE" "$S_FILE" -o

# --- Step 2: Run tests inside mips-interpreter ---
echo "Entering mips-interpreter/"
cd mips-interpreter

# Loop through all test input/output pairs (0.in, 1.in, ...)
for in_file in ../"$FOLDER"/*.in; do
    num=$(basename "$in_file" .in)
    out_file="../$FOLDER/${num}.out"

    if [[ -f "$out_file" ]]; then
        echo
        echo "=== Test $num ==="
        diff <(./run.sh --in "../$FOLDER/${num}.in" "../${BASE_NAME}.s") "../$FOLDER/${num}.out" -w
    else
        echo "Warning: No matching .out file for $in_file, skipping."
    fi
done
