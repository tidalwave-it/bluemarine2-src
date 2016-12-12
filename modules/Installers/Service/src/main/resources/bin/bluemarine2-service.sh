#!/bin/sh

BASE_DIR=$(dirname "$0")
JAR_FILE=$BASE_DIR/../lib/it-tidalwave-bluemarine2-service-${project.version}.jar
WORKSPACE=/var/lib/bluemarine2
LOG_FOLDER=/var/log

exec java -DblueMarine2.workspace=$WORKSPACE -Dit.tidalwave.bluemarine2.logFolder=$LOG_FOLDER -jar $JAR_FILE
