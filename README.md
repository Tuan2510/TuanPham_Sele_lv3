# Selenide Sample Framework

This repository contains a sample automation framework built with **Selenide** and **TestNG**. It demonstrates a basic setup for UI tests with data driven support, logging, and reporting via Allure and Extent Reports.

## 🗳️ Project Progress

### Outcomes
- [x] Selenide FW ([selenide.org](https://selenide.org/)): automation/test framework
- [x] Reports: HTML, Allure Report, Report Portal
- [x] Test retry: test failed ⇒ retry (1, 2)
- [x] Parallel/distributed testing
- [x] Cross browsers testing: Chrome, Edge
- [x] Selenium Grid/Shard
- [x] Test cases: VJ, Agoda
  - [x] VJ: [vj.com](https://www.vj.com/)
  - [x] Agoda: [agoda.com](https://www.agoda.com/)
- [ ] CI: Schedule test, send email notification result with summary

### User Cases
- [x] Content testing
  - [x] LeapFrog: [leapfrog.com](https://store.leapfrog.com/en-us/apps/c?p=1&platforms=197&product_list_dir=asc&product_list_order=name)
- [x] Multiple languages testing
- [x] Group tests by purposes: regression, smoke/sanity test
- [x] Source control practice: branch
- [x] Switch test environment: dev, stg (dev: agoda.com, stg: vj.com)
- [x] Wrap custom controls
- [ ] Data driven testing: test data is in excel file
- [x] Working with Shadow DOM
  - [x] books-pwakit: [books-pwakit.appspot.com](https://books-pwakit.appspot.com/)
- [ ] Compare with another FW e.g. Playwright


## Requirements

- Java 21+
- Maven 3+
- [Allure commandline](https://docs.qameta.io/allure/) available on the `PATH` (used to generate the HTML report)
- Optional: a Selenium Grid when executing in remote mode

## Installation

Clone the repository and install the Maven dependencies:

```bash
mvn -q verify -DskipTests
```

## Running Tests

Execute the sample TestNG suite with Maven:

```bash
mvn clean test
```

### Configuration

Runtime configuration is controlled through `src/main/resources/config/RunConfiguration.properties`. Key properties include:

- `retry.count` – maximum retry attempts for failed tests
- `retry.mode` – `immediate` or `afterDone`
- `browser` – browser to run tests in (e.g., `chrome`, `edge`)
- `env` – decide which environment test
- `language` – language for the test (e.g., `en`, `fr`, `de`)
- `remoteUrl` – URL of the Selenium Grid (if running remotely)

You can override any property via system properties, for example:

```bash
mvn clean test -Dretry.count=1 -Dretry.mode=afterDone -Denv=dev
```

### Reports

After a test run, reports are generated under `allure-results` and `extent-reports` folder:

- `extent-report.html` – Extent report summarizing each step
- `allure-results` – raw results used by Allure

## Project Structure

- `src/main/java` – utilities and constants
- `src/test/java` – TestNG tests, page objects and listeners
- `src/test/resources` – test data, configuration and logging setup

## Notes

This project is intended as a minimal starting point for Selenide based automation. Feel free to expand the tests and configuration to suit your needs.