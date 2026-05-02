#!/bin/bash

ROOT_DIR=$(pwd)
SRC_DIR="$ROOT_DIR/src"
BUILD_DIR="$ROOT_DIR/build"

mkdir -p "$BUILD_DIR"

javac -d "$BUILD_DIR" \
"$SRC_DIR"/model/user/*.java \
"$SRC_DIR"/model/item/*.java \
"$SRC_DIR"/model/auction/*.java \
"$SRC_DIR"/factory/*.java \
"$SRC_DIR"/service/*.java \
"$SRC_DIR"/Main.java

java -cp "$BUILD_DIR" Main