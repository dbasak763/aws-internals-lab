#!/bin/sh
set -eu
cd "$(dirname "$0")"
if ! command -v javac >/dev/null 2>&1; then
  echo "Install JDK 17 or later before running this app." >&2
  exit 1
fi
mkdir -p build
javac --add-modules jdk.httpserver -d build src/main/java/lab/Main.java
exec java --add-modules jdk.httpserver -cp build lab.Main
