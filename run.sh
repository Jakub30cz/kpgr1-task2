#!/usr/bin/env bash
# Compiles and starts the application.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf bin/main && mkdir -p bin/main
javac -d bin/main $(find src -name '*.java')
java -cp bin/main kpgr2.App
