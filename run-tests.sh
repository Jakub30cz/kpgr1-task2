#!/usr/bin/env bash
# Compiles sources and tests and runs the JUnit 5 suite.
set -euo pipefail
cd "$(dirname "$0")"
JUNIT_VERSION=1.11.3
JAR=".cache/junit-platform-console-standalone-$JUNIT_VERSION.jar"
if [ ! -f "$JAR" ]; then
  mkdir -p .cache
  curl -fsSL -o "$JAR" "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$JUNIT_VERSION/junit-platform-console-standalone-$JUNIT_VERSION.jar"
fi
rm -rf bin && mkdir -p bin/main bin/test
SRC=$(find src -name '*.java')
[ -n "$SRC" ] && javac -d bin/main $SRC
TESTS=$(find test -name '*.java')
if [ -z "$TESTS" ]; then echo "No tests yet."; exit 0; fi
javac -d bin/test -cp "bin/main:$JAR" $TESTS
java -jar "$JAR" execute --class-path "bin/main:bin/test" --scan-class-path --disable-banner --details=summary
