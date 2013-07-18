#!/bin/bash

# If Spring batch tables has not been created, then run the batch with following parameter:
# ./launchInvoiceBatch.sh -Dbatch.initialize_schema=true
# 
# Pass the period end and start dates with following parameters:
# ./launchInvoiceBatch.sh -Dusage.periodStart=yyyyMMdd -Dusage.periodEnd=yyyyMMdd
# 
# See ../resources/batch.properties for more parameters. All these can be also modified by
# passing them as -D parameters.
# 
# See ../resources/log4j.properties for logging settings.
# 

if [ -n "$CLASSPATH" ]; then
    echo "*********************************************************************************"
    echo "WARN: Classpath is set! Make sure it does not interfere with the batch process."
    echo "CLASSPATH: $CLASSPATH"
    echo "*********************************************************************************"
fi

SCRIPTDIR="$( cd "$( dirname "$0" )" && pwd )"

CP=$SCRIPTDIR/../resources/

LIB=$SCRIPTDIR/../lib/*
for f in $LIB
do
CP=$CP:$f
done

# Launch with all command line parameters ($@). Note! works with -D parameters or
# parameters that can be declared before class name.
# Passing current time stamp (in milliseconds) to the batch to be used as a JobParameter. 
java -cp $CP $@ org.springframework.batch.core.launch.support.CommandLineJobRunner \
launch-context.xml jobInvoice \
time=$(($(date +%s%N)/1000000))
