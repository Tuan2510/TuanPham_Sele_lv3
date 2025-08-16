pipeline {
    agent any

    parameters {
        choice(name: 'SUITE', choices: ['Agoda_Suite', 'Book_Suite', 'LeapFrog_Suite', 'VietJet_Suite'], description: 'Select the test suite')
        choice(name: 'BROWSER', choices: ['chrome', 'edge'], description: 'Select the browser')
        choice(name: 'ENV', choices: ['dev', 'book', 'leapfrog', 'stg'], description: 'Select the environment')
        choice(name: 'GROUP', choices: ['AGRegression', 'BookRegression','LeapFrogTest', 'VJRegression'], description: 'Select the TestNG group')
        choice(name: 'PARALLEL_MODE', choices: ['methods', 'tests'], description: 'TestNG parallel mode')
        choice(name: 'LANGUAGE', choices: ['en-us', 'vi-vn'], description: 'Select the language')
        string(name: 'GRID_URL', defaultValue: '', description: 'Selenium Grid URL (leave empty to run local)')
        string(name: 'MAX_RETRY', defaultValue: '0', description: 'Max retry count')
        string(name: 'EMAIL_RECIPIENT', defaultValue: '', description: 'Email recipients separated by comma')
    }

    environment {
        TOTAL_TESTS = ''
        PASSED_TESTS = ''
        FAILED_TESTS = ''
        SKIPPED_TESTS = ''
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test with Maven') {
            steps {
                script {
                    // Define the mapping of suite names to their XML files
                    def suiteMap = [
                        'VietJet_Suite': 'src/test/resources/suites/VietJetTestSuite.xml',
                        'Agoda_Suite'  : 'src/test/resources/suites/AgodaTestSuite.xml',
                        'LeapFrog_Suite': 'src/test/resources/suites/LeapFrogTest.xml',
                        'Book_Suite'   : 'src/test/resources/suites/BookTestSuite.xml'
                    ]
                    def suiteFile = suiteMap[params.SUITE]

                    // Remove previous results
                    if (isUnix()) {
                        sh '''
                            rm -rf allure-results
                            rm -rf extent-reports
                        '''
                    } else {
                        bat '''
                            if exist allure-results rmdir /s /q allure-results
                            if exist extent-reports rmdir /s /q extent-reports
                        '''
                    }

                    // Run Maven command with parameters
                    def mvnCmd = "mvn clean test " +
                        "-DsuiteXmlFile=${suiteFile} " +
                        "-Dbrowser=${params.BROWSER} " +
                        "-Denv=${params.ENV} " +
                        "-Dgroup=${params.GROUP} " +
                        "-Dparallel=${params.PARALLEL_MODE} " +
                        "-Dlanguage=${params.LANGUAGE} " +
                        "-DgridUrl=${params.GRID_URL} " +
                        "-DmaxRetry=${params.MAX_RETRY}"

                    if (isUnix()) {
                        sh mvnCmd
                    } else {
                        bat mvnCmd
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                def total = 'N/A'
                def passed = 'N/A'
                def failed = 'N/A'
                def skipped = 'N/A'
                def logAttributes
                logAttributes = { node ->
                    node.attributes().each { key, value ->
                        println "${key} = ${value}"
                    }
                    node.children().each { child ->
                        logAttributes(child)
                    }
                }

                if (fileExists('allure-results')) {
                    try {
                        def allureCommand = 'allure generate allure-results --clean --single-file -o allure-report'
                        if (isUnix()) {
                            sh allureCommand
                        } else {
                            bat allureCommand
                        }
                    } catch (Exception e) {
                        echo "Allure report generation failed: ${e.message}"
                    }
                } else {
                    echo 'No Allure results found.'
                }

                def reportPath = isUnix()
                    ? 'target/surefire-reports/testng-results.xml'
                    : 'target\\surefire-reports\\testng-results.xml'

                if (fileExists(reportPath)) {
                    def content = readFile(reportPath)
                    def xml = new XmlSlurper().parseText(content)

                    logAttributes(xml)

                    total = xml.attributes().get('total').toString()
                    passed = xml.attributes().get('passed').toString()
                    failed = xml.attributes().get('failed').toString()
                    skipped = xml.attributes().get('skipped').toString()

                    echo "Parsed values → total=${total}, passed=${passed}, failed=${failed}, skipped=${skipped}"
                } else {
                    echo '⚠️ testng-results.xml not found.'
                }

                emailext(
                    from: '"[no-reply] Selenide CI" <jenkinscisele3@gmail.com>',
                    subject: "[Automation Report] ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                    body: """<!DOCTYPE html>
                            <html>
                            <head><style>body { font-family: Arial } td, th { padding: 5px; }</style></head>
                            <body>
                            <p>Hi Team,</p>
                            <p>The automated test execution has been completed. Below is the summary report:</p>
                            <table border='1'>
                            <tr><th>Job Name</th><td>${env.JOB_NAME}</td></tr>
                            <tr><th>Build Number</th><td>${env.BUILD_NUMBER}</td></tr>
                            <tr><th>Total Test Case</th><td>${total}</td></tr>
                            <tr><th>Passed</th><td style='color:green'>${passed}</td></tr>
                            <tr><th>Failed</th><td style='color:red'>${failed}</td></tr>
                            <tr><th>Skipped</th><td>${skipped}</td></tr>
                            </table>
                            <p>Please check the attachment for more detailed reports.</p>
                            <p>Best regards,<br/>Selenide CI</p>
                            </body>
                            </html>""",
                    mimeType: 'text/html',
                    attachLog: false,
                    attachmentsPattern: 'allure-report/index.html',
                    to: params.EMAIL_RECIPIENT
                )
            }
        }
    }
}
