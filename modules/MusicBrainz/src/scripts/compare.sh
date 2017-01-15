#!/bin/sh

readonly EXPECTED_RESULTS=target/expected-results
readonly ACTUAL_RESULTS=target/test-results

find $ACTUAL_RESULTS -name "*.mp3.n3" -exec sh src/scripts/compare-single.sh "$ACTUAL_RESULTS" "$EXPECTED_RESULTS" {} \;
