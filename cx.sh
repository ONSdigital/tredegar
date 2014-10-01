#!/bin/bash
export PROJECT_DIR=`pwd`

# Build the CX project:
cd src/main/scss &&
grunt build &&
cd $PROJECT_DIR &&

# Replace old files with new files:
#rm -rf src/main/resources/files/ui &&
cp -r src/main/scss/ui src/main/resources/files/ &&
rm src/main/resources/files/ui/config.json &&
rm -rf src/main/resources/files/ui/scss
