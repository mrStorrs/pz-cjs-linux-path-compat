#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ZOMBIE_BUDDY_JAR="${ZOMBIE_BUDDY_JAR:-/home/cjstorrs/games/Project Zomboid Linux 42.20.0/game/projectzomboid/ZombieBuddy.jar}"
BUILD_DIR="${ROOT_DIR}/.build/test"

if [[ ! -f "${ZOMBIE_BUDDY_JAR}" ]]; then
    echo "ZombieBuddy jar not found: ${ZOMBIE_BUDDY_JAR}" >&2
    exit 1
fi

rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}"

mapfile -t JAVA_SOURCES < <(
    find \
        "${ROOT_DIR}/src/stubs/java" \
        "${ROOT_DIR}/src/main/java" \
        "${ROOT_DIR}/src/test/java" \
        -name '*.java' |
        sort
)

javac --release 17 \
    -classpath "${ZOMBIE_BUDDY_JAR}" \
    -d "${BUILD_DIR}" \
    "${JAVA_SOURCES[@]}"

java -ea -classpath "${BUILD_DIR}:${ZOMBIE_BUDDY_JAR}" \
    com.cjstorrs.cjslinuxpathcompat.LinuxPathCompatTest
