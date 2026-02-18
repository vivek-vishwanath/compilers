#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <part>"
  exit 1
fi

PART="$1"
BENCHMARKS=(lbm mcf bc cc pr sssp tc)

for BENCHMARK in "${BENCHMARKS[@]}"; do
#  echo "Running: ./install/run_pass.sh $PART $BENCHMARK"
  ./install/run_pass.sh "$PART" "$BENCHMARK"
done
