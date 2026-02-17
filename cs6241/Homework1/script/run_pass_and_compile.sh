#!/bin/bash

CURR=$(readlink -f "$0")
ROOT=$(dirname "$CURR")
DEF="${ROOT}/def"
PLUGIN="${ROOT}/libHW1.so"
PASSES=(part2 part31 part32 part33 part34 part4)
C_BENCHMARKS=(lbm mcf jtest)
CXX_BENCHMARKS=(bc cc pr sssp tc)

function usage() {
  local name='run_pass_and_compile.sh'
  echo "Use a pass to transform the specified benchmark"
  echo "${name} [pass] [benchmark]"
  echo ""
  echo "Options:"
  echo "    valid pass:       ${PASSES[*]}"
  echo "    valid benchmark:  ${C_BENCHMARKS[*]} ${CXX_BENCHMARKS[*]}"
}

. "${DEF}"

if [ $# -lt 2 ]; then
  usage
  exit 1
fi

PASS=$1
BENCH=$2
OUTPUT="${BENCH}-transformed.bc"
EXE="${BENCH}-transformed.exe"

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

for i in ${C_BENCHMARKS[*]}; do
  if [ $i == $BENCH ]; then
    VALID_BENCH=1
    COMPLIER='clang'
    DESC_INPUT="Input data is placed in ${CMAKE_SRC}/test_code/${BENCH}/data"
    break
  fi
done

for i in ${CXX_BENCHMARKS[*]}; do
  if [ $i == $BENCH ]; then
    VALID_BENCH=1
    COMPLIER='clang++'
    DESC_INPUT="Specify the input using the '-g' option. Run '${EXE} -h' for more details"
    break
  fi
done

if [ $VALID_BENCH == 0 ]; then
  echo "Invalid benchmark: $BENCH"
  usage
  exit 1
fi

opt -load-pass-plugin "${PLUGIN}" -passes=${PASS} "${ROOT}/test_code/llvm_ir/${BENCH}.bc" -o ${OUTPUT}
${COMPLIER} ${OUTPUT} -lm -o ${EXE}
echo ""
echo ""
echo "======================================================================================="
echo "Transformed LLVM IR: ${OUTPUT}"
echo "Transformed executable: ${EXE}"
echo ${DESC_INPUT}
