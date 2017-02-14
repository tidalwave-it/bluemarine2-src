#!/bin/sh

BASE_DIR="$(dirname -- "$(readlink -f -- "$0")")"
JAR_FILE=$BASE_DIR/../lib/it-tidalwave-bluemarine2-headlessservice-${project.version}.jar

WORKSPACE=/var/lib/bluemarine2-service
LOG_FOLDER=/var/log/bluemarine2-service

mkdir -p "$LOG_FOLDER"

exec java -DblueMarine2.workspace=$WORKSPACE \
          -DblueMarine2.logFolder=$LOG_FOLDER \
          -DblueMarine2.logConfigOverride=$WORKSPACE/config/logback-override.xml \
          -jar $JAR_FILE
