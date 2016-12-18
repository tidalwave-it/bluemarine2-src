#!/bin/sh

# Hudson uses java.io.File API to remve and archive files in the workspace,
# so it fails with file names containing diacritics. We have to manually remove them.

if [ -z ${WORKSPACE+x} ]; then 
    echo "WORKSPACE is not set."
else 
    find $WORKSPACE -name "Music" -exec rm -rfv {} \;
fi
