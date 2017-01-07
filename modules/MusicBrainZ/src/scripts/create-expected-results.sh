#!/bin/bash

set -e

GOAL=install:install-file

if [ "$1" == "--deploy" ] ; then
    GOAL=gpg:sign-and-deploy-file
    shift
    fi

readonly TEST_SET=$1
readonly VERSION=$2
readonly GROUP_ID=it.tidalwave.bluemarine2.testsets

function usage()
  {
    echo "Usage: create-expected-results [--deploy] test_set_name version"
  }

function createCache()
  {
    readonly ARTIFACT_ID=expected-metadata-musicbrainz-cddb-$TEST_SET
    readonly FILE=$PWD/target/$ARTIFACT_ID.zip
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

    (cd $TEST_RESULTS ; zip -prq $FILE $TEST_SET/*)

    cat << EOF > $PWD/target/thepom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>it.tidalwave.bluemarine2</groupId>
        <artifactId>bluemarine2</artifactId>
        <version>1.0-ALPHA-15</version>
        <relativePath/>
    </parent>

    <groupId>$GROUP_ID</groupId>
    <artifactId>$ARTIFACT_ID</artifactId>
    <version>$VERSION</version>
    <packaging>zip</packaging>

</project>
EOF

    mvn $GOAL \
        -Dfile=$FILE \
        -DpomFile=$PWD/target/thepom.xml \
        -DcreateChecksum=true \
        -DgroupId=$GROUP_ID \
        -DartifactId=$ARTIFACT_ID \
        -Dversion=$VERSION \
        -Dpackaging=zip \
        -Dtft.maven-gpg-plugin.version=1.6 \
        -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2 \
        -DrepositoryId=staging-oss.sonatype.org
  }



createCache
