#--------------------------------------------
# standalone version
java -jar "path\to\selenium-server-4.21.0.jar" standalone --config "path\to\GridConfiguration.toml"

#--------------------------------------------
# hub&node version
java -jar "path\to\selenium-server-4.21.0.jar" hub --config "path\to\GridConfiguration.toml"
java -jar "path\to\selenium-server-4.21.0.jar" node --config "path\to\GridConfiguration.toml"

#--------------------------------------------
#toml configuration file
[server]
max-threads = 20
session-retry-interval = 3

[sessionqueue]
session-request-timeout = 500

[node]
selenium-manager = false
detect-drivers = false
drivers = ["chrome", "firefox"]
max-sessions = 10
hub = "http://localhost:4444"

[[node.driver-configuration]]
display-name = "Chrome"
max-sessions = 10
stereotype = "{\"browserName\": \"chrome\"}"
webdriver-executable = 'path\to\chromedriver.exe'

[[node.driver-configuration]]
display-name = "Firefox"
max-sessions = 5
stereotype = "{\"browserName\": \"firefox\"}"
webdriver-executable = 'path\to\geckodriver.exe'