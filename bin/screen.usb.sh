#!/bin/bash  
echo screen usb start
getty tty
screen /dev/ttyACM0 
#cat /dev/ttyACM0
echo screen usb stop
