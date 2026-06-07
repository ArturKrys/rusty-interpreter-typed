#!/bin/sh

# Directories
GRAMMAR=grammar
SRC=src
GEN=gen
BIN=bin

# --- Clean ---
rm -rf "$BIN"
rm -rf "$GEN"
mkdir -p "$BIN" "$GEN/parser"

# --- Generate parser from grammar ---
javacc -OUTPUT_DIRECTORY="$GEN/parser" "$GRAMMAR/ParserR1T.v0.jj"

# --- Compile everything together ---
javac -d "$BIN" $(find "$SRC" "$GEN" -name "*.java")

echo "Done. Run with: ./r1tint"
