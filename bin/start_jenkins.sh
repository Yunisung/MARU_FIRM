#!/bin/sh
unset LANG

export JAVA_PATH=/usr/local/jdk1.8.0_271/bin
export MARU_PATH=/home/bkwinners/MARU_FIRM

# LANG
##########################
LANG=ko_KR.utf8
export LANG

# SET LIBRARY
for i in $MARU_PATH/lib/*.jar; do
    CP=$CP:$i
done
CP=`echo $CP | cut -c2-`

# JVM_ARGS for VM
##########################
JVM_ARGS="-DMARU_FIRM -server -DCP_CONF=$MARU_PATH/conf -Dlogback.configurationFile=$MARU_PATH/conf/logback.xml -Dfile.encoding=EUC-KR -Dhttps.protocols=TLSv1.3,TLSv1.2 -Djdk.tls.client.protocols=TLSv1.3,TLSv1.2"
JVM_ARGS="$JVM_ARGS -Xss256k -Xms128m -Xmx256m"
JVM_ARGS="$JVM_ARGS -cp $CP"

nohup $JAVA_PATH/java $JVM_ARGS com.pgmate.firm.server.Server > /dev/null 2>&1 &
echo $!>firm.pid
