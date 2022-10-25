#!/bin/sh

cd /home/bkwinners/MARU_FIRM/bin

unset LANG

# LANG
##########################
LANG=ko_KR.eucKR
export LANG

# SET LIBRARY
for i in ../lib/*.jar; do
    CP=$CP:$i
done
CP=`echo $CP | cut -c2-`

# JVM_ARGS for VM
##########################
JVM_ARGS="-DK1_OEPN -server -DCP_CONF=../conf -Dlogback.configurationFile=../conf/logback.xml -Dfile.encoding=MS949"
JVM_ARGS="$JVM_ARGS -Xss256k -Xms128m -Xmx256m"
JVM_ARGS="$JVM_ARGS -cp $CP"


java $JVM_ARGS com.pgmate.firm.main.AutoOpen