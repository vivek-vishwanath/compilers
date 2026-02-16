#!/usr/bin/env python3
# Generated through Claude 4.5
import zipfile
import os
from pathlib import Path

def main():
    items_to_zip = ['src', 'script', 'CMakeLists.txt']
    output_filename = 'submission.zip'
    
    with zipfile.ZipFile(output_filename, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for item in items_to_zip:
            item_path = Path(item)
            
            if item_path.is_file():
                # Add single file
                zipf.write(item, item)
                print(f"Added file: {item}")
            elif item_path.is_dir():
                # Add directory recursively
                for root, dirs, files in os.walk(item):
                    for file in files:
                        file_path = os.path.join(root, file)
                        arcname = file_path
                        zipf.write(file_path, arcname)
                        print(f"Added: {file_path}")
            else:
                print(f"Warning: {item} not found, skipping...")
    
    print(f"\n✓ Created {output_filename}")

if __name__ == '__main__':
    main()
