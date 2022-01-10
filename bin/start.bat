
d:
cd D:\Develop\workspace\MARU_FIRM\bin

java -DCP_CONF=../conf -Dlogback.configurationFile=../conf/logback.xml -Dfile.encoding=MS949 -Xms128m -Xmx512m -Xss128k -XX:+AggressiveOpts -XX:+UseParallelGC -XX:+UseBiasedLocking -XX:NewSize=64m -cp ../lib/*;../classes com.pgmate.firm.server.Server
pause