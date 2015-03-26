#!/bin/sh

BASEDIR=`dirname "$0"`

CLASSPATH=""
SEPARATOR=""

for i in $BASEDIR/lib/*.jar; do
	CLASSPATH="$CLASSPATH$SEPARATOR$i"
        SEPARATOR=":"
	done

$BASEDIR/jre/bin/java -cp "$CLASSPATH" $* it.tidalwave.bluemarine2.ui.impl.javafx.Main
