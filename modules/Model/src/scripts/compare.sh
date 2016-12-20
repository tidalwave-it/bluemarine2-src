#!/bin/sh

readonly EXPECTED_RESULTS=target/test-classes/expected-results/metadata
readonly ACTUAL_RESULTS=target/test-results

find $ACTUAL_RESULTS -name "*-dump.txt" -exec sh src/scripts/compare-single.sh "$ACTUAL_RESULTS" "$EXPECTED_RESULTS" {} \;