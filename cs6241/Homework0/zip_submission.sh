directories_to_zip=("src" "CMakeLists.txt" "README.md" "script")

zip -r submission.zip "${directories_to_zip[@]}"
