#!/bin/sh

readonly FILE=/tmp/artifact.zip
readonly TEST_SET=$1
readonly VERSION=$2

rm -v $FILE

cd target/test-results/ ; zip -prq $FILE $TEST_SET/*

readonly GROUP_ID=it.tidalwave.bluemarine2.testsets
readonly ARTIFACT_ID=expected-metadata-$TEST_SET

mvn install:install-file -Dfile=$FILE -DgeneratePom=true -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dpackaging=zip