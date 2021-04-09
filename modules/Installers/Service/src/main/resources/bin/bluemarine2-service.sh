#!/bin/sh
#
# *********************************************************************************************************************
#
# blueMarine II: Semantic Media Centre
# http://tidalwave.it/projects/bluemarine2
#
# Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
#
# *********************************************************************************************************************
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
# the License. You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
# an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations under the License.
#
# *********************************************************************************************************************
#
# git clone https://bitbucket.org/tidalwave/bluemarine2-src
# git clone https://github.com/tidalwave-it/bluemarine2-src
#
# *********************************************************************************************************************
#


BASE_DIR="$(dirname -- "$(readlink -f -- "$0")")"
JAR_FILE=$BASE_DIR/../lib/it-tidalwave-bluemarine2-headlessservice-${project.version}.jar

WORKSPACE=/var/lib/bluemarine2-service
LOG_FOLDER=/var/log/bluemarine2-service

mkdir -p "$LOG_FOLDER"

exec java -DblueMarine2.workspace=$WORKSPACE \
          -DblueMarine2.logFolder=$LOG_FOLDER \
          -DblueMarine2.logConfigOverride=$WORKSPACE/config/logback-override.xml \
          -jar $JAR_FILE
