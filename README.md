# LoRaParkApplication

This repository contains the code for the LoRaPark Android Application. This is an context-aware application that uses a rule based context description to exectue actions.
The context description can be based on different values, but the main gaol is the usage of the sensor data of the [LoRaPark](https://lorapark.de/) in Ulm. 

In order to retrieve the current data a middleware is necessary, that fetches the data from the LoRaPark and provides it to the Android clients.
This server can be found [here](https://github.com/oli-f/LoRaParkServer).

The complete documentation with the design process and the archtecture decisions can be found [here](https://github.com/frozzenshooter/LoRaParkAppDocumentation)
