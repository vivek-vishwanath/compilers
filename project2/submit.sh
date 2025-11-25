mkdir -p submit/
cp run.sh submit/
cp build.sh submit
cp -r src submit/
mkdir -p submit/custom_tests
cp private_test_cases/pow/pow.ir submit/custom_tests/custom_test.ir
cp private_test_cases/pow/0.in submit/custom_tests/custom.in
cp private_test_cases/pow/0.out submit/custom_tests/custom.out
cd submit || exit
zip -r submit.zip ./*
mv submit.zip ..