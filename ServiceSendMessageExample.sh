#!/bin/bash

# Set classpath (-cp) and start a ServiceSendMessageExample
#
# java -cp "$(echo jSMS4Pi-*.jar)" cz.zerog.jsms4pi.example.ServiceSendMessageExample
#
# or
#
# java -cp ".:jSMS4Pi-1.0-SNAPSHOT.jar" cz.zerog.jsms4pi.example.ServiceSendMessageExample


sudo java -cp ".:$(echo jSMS4Pi-*.jar)" cz.zerog.jsms4pi.example.ServiceSendMessageExample $@
