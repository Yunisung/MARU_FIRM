#!/bin/sh

cd /home/bkwinners/MARU_FIRM/bin

pid_file="firm.pid"

now=$(date +"%Y-%m-%d:%H:%M:%S");

value=`cat $pid_file`

if [ -f "$pid_file" ] 
then

	if ps -p $value > /dev/null
		then
   		echo "$now : $pid_file $value is running"
	else
   		echo "$now : $pid_file $value was stop "
		./start.sh
	fi
else
	echo "$now : $pid_file is not exist"
	./start.sh
fi
