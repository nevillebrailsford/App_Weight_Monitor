#!/bin/zsh

echo "*==============================="
echo "* Run the slideshow application."
echo "*==============================="

echo "*================================="
echo "* Set up the required directories."
echo "*================================="

ROOT_DIR=/Users/nevil/Projects/WeightMonitor.app
JAR_NAME=WeightMonitorApp.jar
DATA_DIR=/Users/nevil/OneDrive/Projects/data

echo "*============================================="
echo "* Invoke the program - may take a few moments."
echo "*============================================="

java -jar ${ROOT_DIR}/${JAR_NAME}  --name=weight.monitor.application --dir=${DATA_DIR}