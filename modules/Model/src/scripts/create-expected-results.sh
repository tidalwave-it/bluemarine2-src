#!/bin/bash

function usage()
  {
    echo "Usage: create-expected-results test_set_name version"
  }

readonly TEST_SET=$1
readonly VERSION=$2

readonly FILE=$PWD/target/artifact.zip
readonly TEST_RESULTS=target/test-results

if [ ! -d "$TEST_RESULTS/$TEST_SET" ] ; then
    readonly TEST_SETS=`cd $TEST_RESULTS; echo *`
    echo "Invalid test set name, pick one in:"
    echo ""

    for i in  $TEST_SETS; do
        echo "            $i"
        done

    echo ""
    usage
    exit 1
    fi

rm -vf $FILE

cd $TEST_RESULTS ; zip -prq $FILE $TEST_SET/*

readonly GROUP_ID=it.tidalwave.bluemarine2.testsets
readonly ARTIFACT_ID=expected-metadata-$TEST_SET

mvn install:install-file -Dfile=$FILE -DgeneratePom=true -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dpackaging=zip