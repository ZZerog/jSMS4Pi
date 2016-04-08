#!/bin/bash

# Set classpath (-cp) and start a FunctionalTest
#
# java -cp "$(echo jSMS4Pi-*.jar)" cz.zerog.jsms4pi.tool.FunctionalTest
#
# or
#
# java -cp ".:jSMS4Pi-1.0-SNAPSHOT.jar" cz.zerog.jsms4pi.tool.FunctionalTest
#
# more at http://jsms4pi.com


sudo java -cp ".:$(echo jSMS4Pi-*.jar)" cz.zerog.jsms4pi.tool.FunctionalTest $@
