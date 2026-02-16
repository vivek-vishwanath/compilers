PARENT_DIR="private_test_cases"

./build.sh

for dir in "$PARENT_DIR"/*/; do
    # Remove trailing slash
    dir="${dir%/}"
    
    # Run your command using "$dir" as argument
    ./test.sh "$dir"
done
