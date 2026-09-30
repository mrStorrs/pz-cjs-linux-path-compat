#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ZOMBIE_BUDDY_JAR="${ZOMBIE_BUDDY_JAR:-/home/cjstorrs/games/Project Zomboid Linux 42.20.0/game/projectzomboid/ZombieBuddy.jar}"
BUILD_DIR="${ROOT_DIR}/.build"
OUT_JAR="${ROOT_DIR}/42.21/media/java/cjsLinuxPathCompat.jar"

if [[ ! -f "${ZOMBIE_BUDDY_JAR}" ]]; then
    echo "ZombieBuddy jar not found: ${ZOMBIE_BUDDY_JAR}" >&2
    exit 1
fi

rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}/classes" "$(dirname "${OUT_JAR}")"

mapfile -t JAVA_SOURCES < <(
    find "${ROOT_DIR}/src/stubs/java" "${ROOT_DIR}/src/main/java" -name '*.java' | sort
)

javac --release 17 \
    -classpath "${ZOMBIE_BUDDY_JAR}" \
    -d "${BUILD_DIR}/classes" \
    "${JAVA_SOURCES[@]}"

rm -rf "${BUILD_DIR}/classes/zombie"
jar --create --file "${OUT_JAR}" -C "${BUILD_DIR}/classes" com
python3 -m zipfile -t "${OUT_JAR}" >/dev/null
echo "Built ${OUT_JAR}"
