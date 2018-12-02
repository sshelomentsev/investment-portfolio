#!/bin/bash

mvn clean package 
java -jar target/ investment-portfolio-fat.jar
