#!/bin/bash  
echo screen usb start
sudo minicom -D /dev/ttyACM1 -b 9600 -C aaa.txt
#getty tty
#screen /dev/ttyACM0 
#cat /dev/ttyACM0
echo screen usb stop
