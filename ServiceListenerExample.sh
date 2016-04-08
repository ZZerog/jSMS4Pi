#!/bin/bash

# Set classpath (-cp) and start a ServiceListenerExample
#
# java -cp "$(echo jSMS4Pi-*.jar)" cz.zerog.jsms4pi.example.ServiceListenerExample
#
# or
#
# java -cp ".:jSMS4Pi-1.0-SNAPSHOT.jar" cz.zerog.jsms4pi.example.ServiceListenerExample


sudo java -cp ".:$(echo jSMS4Pi-*.jar)" cz.zerog.jsms4pi.example.ServiceListenerExample $@
