#!/bin/bash
echo "Starting Appium..."
appium --address 127.0.0.1 --port 4723
echo "Appium started on 127.0.0.1:4723"

 Save the file and give it executable permissions:
 chmod +x startAppium.sh

 Now you can start Appium by running the script:
 ./startAppium.sh

 You should see the following output:
 Starting Appium...