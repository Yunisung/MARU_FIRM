#!/bin/sh 
kill -9 `cat < firm.pid`
rm firm.pid