#!/bin/sh

readonly ACTUAL_RESULTS=$1
readonly EXPECTED_RESULTS=$2
readonly ACTUAL_RESULT=$3

readonly TEST_FILE=`echo $ACTUAL_RESULT | cut -c 21-`
#readonly EXPECTED_RESULT=$EXPECTED_RESULTS/`echo $ACTUAL_RESULT | cut -c ${#ACTUAL_RESULTS}-`
readonly EXPECTED_RESULT=$EXPECTED_RESULTS/$TEST_FILE

# echo "########  $TEST_FILE"
diff --unchanged-line-format="" --new-line-format="exp: %dn %L" --old-line-format="act: %dn %L"  "$ACTUAL_RESULT" "$EXPECTED_RESULT"
