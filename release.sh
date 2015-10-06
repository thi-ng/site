#!/bin/bash

# brew install htmlcompressor
# npm install clean-css -g

readonly RES=resources/public
readonly BUILD=build

readonly TARGET="s3://thi.ng"

rm -rf $BUILD
mkdir -p $BUILD/css $BUILD/fonts $BUILD/img $BUILD/js/compiled

if [[ $1 == "-c" ]]
then
    lein with-profile prod do clean, cljsbuild once min
else
    echo "skipping compilation..."
fi

cp $RES/js/compiled/app.js $BUILD/js/compiled/
cp -R $RES/img/ $BUILD/img
cp -R $RES/fonts/ $BUILD/fonts

htmlcompressor --remove-surrounding-spaces max -o $BUILD/index.html $RES/index.html
cleancss -o $BUILD/css/style.css $RES/css/style.css

s3cmd -P -v sync $BUILD/ $TARGET/

echo "done"
