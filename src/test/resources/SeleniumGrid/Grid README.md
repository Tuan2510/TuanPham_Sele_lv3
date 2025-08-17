# Selenium Grid Configuration
## This file provides instructions for setting up and running a Selenium Grid with custom configurations.

Grid configuration file `GridConfiguration.toml` is located in `src/test/resources/grid`. 
Update this file to customize your Selenium Grid setup, such as specifying the hub IP, browser versions, and other settings.

To run Selenium Grid, you need to have the Selenium Server JAR file.


### To run Selenium Grid in standalone mode, you need to have the Selenium Server JAR file.
java -jar "path\to\selenium-server-4.21.0.jar" standalone --config "path\to\GridConfiguration.toml"

### To run Selenium Grid in hub and node mode, you need to have the Selenium Server JAR file.
java -jar "path\to\selenium-server-4.21.0.jar" hub --config "path\to\GridConfiguration.toml"
java -jar "path\to\selenium-server-4.21.0.jar" node --config "path\to\GridConfiguration.toml"
