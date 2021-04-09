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


readonly ACTUAL_RESULTS="$1"
readonly EXPECTED_RESULTS="$2"
readonly ACTUAL_RESULT="$3"

readonly TEST_FILE=`echo $ACTUAL_RESULT | cut -c 21-`
#readonly EXPECTED_RESULT=$EXPECTED_RESULTS/`echo $ACTUAL_RESULT | cut -c ${#ACTUAL_RESULTS}-`
readonly EXPECTED_RESULT=$EXPECTED_RESULTS/$TEST_FILE

echo "########  $TEST_FILE"
diff --unchanged-line-format="" --new-line-format="exp: %dn %L" --old-line-format="act: %dn %L"  "$ACTUAL_RESULT" "$EXPECTED_RESULT"
