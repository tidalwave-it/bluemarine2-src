#!/bin/sh

CLASSPATH=""
SEPARATOR=""

for i in lib/*.jar; do
	CLASSPATH="$CLASSPATH$SEPARATOR$i"
        SEPARATOR=":"
	done

java -cp "$CLASSPATH" $* it.tidalwave.bluemarine2.ui.impl.javafx.Main
