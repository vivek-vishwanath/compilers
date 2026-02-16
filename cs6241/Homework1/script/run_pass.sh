#!/bin/bash

CURR=$(readlink -f "$0")
ROOT=$(dirname "$CURR")
PLUGIN="${ROOT}/libHW1.so"
PASSES=(part2 part31 part32 part33 part34 part4)
BENCHMARKS=(lbm mcf bc cc pr sssp tc)

function usage() {
  local name='run_pass.sh'
  echo "Run a pass with specified benchmark"
  echo "${name} [pass] [benchmark]"
  echo ""
  echo "Options:"
  echo "    valid pass:       ${PASSES[*]}"
  echo "    valid benchmark:  ${BENCHMARKS[*]}"
}

if [ $# -lt 2 ]; then
  usage
  exit 1
fi

PASS=$1
BENCH=$2

VALID_PASS=0
VALID_BENCH=0
for i in ${PASSES[*]}; do
  if [ $i == $PASS ]; then
    VALID_PASS=1
    break
  fi
done

if [ $VALID_PASS == 0 ]; then
  echo "Invalid pass: $PASS"
  usage
  exit 1
fi

for i in ${BENCHMARKS[*]}; do
  if [ $i == $BENCH ]; then
    VALID_BENCH=1
    break
  fi
done

if [ $VALID_BENCH == 0 ]; then
  echo "Invalid benchmark: $BENCH"
  usage
  exit 1
fi

opt -load-pass-plugin "${PLUGIN}" -passes=${PASS} -disable-output "${ROOT}/test_code/llvm_ir/${BENCH}.bc"
