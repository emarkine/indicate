#!/bin/bash  

date=`/bin/date +%Y-%m-%d`
#exec &>> log/$date.${@: -1}.log
exec &>> log/${@: -1}.log
java -jar dist/indicate.jar $@
