pipeline {
    agent any

    triggers {
        // Run tests once a day, Monday to Friday, around 12:00 (noon).
        cron('H 12 * * 1-5')
    }
    
    parameters {
        choice(name: 'SUITE', choices: ['Agoda_Suite', 'Book_Suite', 'LeapFrog_Suite', 'VietJet_Suite'], description: 'Select the test suite')
        choice(name: 'BROWSER', choices: ['chrome', 'edge'], description: 'Select the browser')
        choice(name: 'ENV', choices: ['dev', 'book', 'leapfrog', 'stg'], description: 'Select the environment')
        choice(name: 'GROUP', choices: ['AGRegression', 'BookRegression','LeapFrogTest', 'VJRegression'], description: 'Select the TestNG group')
        choice(name: 'PARALLEL_MODE', choices: ['methods', 'tests'], description: 'TestNG parallel mode')
        choice(name: 'LANGUAGE', choices: ['en-us', 'vi-vn'], description: 'Select the language')
        choice(name: 'GRID_URL', choices: ['local', 'grid'], description: 'Select run mode')
        string(name: 'MAX_RETRY', defaultValue: '0', description: 'Max retry count')
        string(name: 'EMAIL_RECIPIENT', defaultValue: '', description: 'Email recipients separated by comma')
        booleanParam(name: 'ARCHIVE_REPORTS', defaultValue: true, description: 'Generate and archive Allure report')
    }

    environment {
        TOTAL_TESTS = ''
        PASSED_TESTS = ''
        FAILED_TESTS = ''
        SKIPPED_TESTS = ''
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build and Test with Maven') {
            steps {
                script {
                    // Map suite -> xml
                    def suiteMap = [
                        'VietJet_Suite' : 'src/test/resources/suites/VietJetTestSuite.xml',
                        'Agoda_Suite'   : 'src/test/resources/suites/AgodaTestSuite.xml',
                        'LeapFrog_Suite': 'src/test/resources/suites/LeapFrogTest.xml',
                        'Book_Suite'    : 'src/test/resources/suites/BookTestSuite.xml'
                    ]
                    def suiteFile = suiteMap[params.SUITE]

                    // Clean old reports
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

                    // Maven cmd
                    def remoteFlag = params.GRID_URL == 'grid' ? '-Dselenide.remote="http://localhost:4444" ' : ''
                    def mvnCmd = "mvn clean test " +
                        "-DsuiteXmlFile=${suiteFile} " +
                        "-Dbrowser=${params.BROWSER} " +
                        "-Denv=${params.ENV} " +
                        "-Dgroup=${params.GROUP} " +
                        "-Dparallel=${params.PARALLEL_MODE} " +
                        "-Dlanguage=${params.LANGUAGE} " +
                        remoteFlag +
                        "-DmaxRetry=${params.MAX_RETRY}"

                    isUnix() ? sh(mvnCmd) : bat(mvnCmd)
                }
            }
        }

        stage('Check & Archive Artifacts') {
              when { expression { return params.ARCHIVE_REPORTS } }
              steps {
                  script {
                      // regenerate Allure if allure-results exist and report file missing
                      if (fileExists('allure-results') && !fileExists('allure-report/index.html')) {
                        echo 'Allure report missing, generating single-file report...'
                        try {
                          def cmd = 'allure generate allure-results --clean --single-file -o allure-report'
                          isUnix() ? sh(cmd) : bat(cmd)
                        } catch (e) {
                          echo "⚠️ Allure report generation failed: ${e.message}"
                        }
                      }

                      // Check presence
                      def hasAllureIndex = fileExists('allure-report/index.html')
                      if (hasAllureIndex) {
                        // Archive so BUILD_URL/artifact/... works
                        archiveArtifacts artifacts: 'allure-report/index.html', allowEmptyArchive: false
                        env.ALLURE_LINK = "${env.BUILD_URL}artifact/allure-report/index.html"
                        env.ALLURE_ARCHIVED = 'true'
                        echo "Allure archived at: ${env.ALLURE_LINK}"
                      } else {
                        env.ALLURE_ARCHIVED = 'false'
                        echo '⚠️ allure-report/index.html not found; skipping archive.'
                      }
                  }
              }
            }
    }

    post {
        always {
            script {
                def total = '0'
                def passed = '0'
                def failed = '0'
                def skipped = '0'

                // Generate Allure HTML (single file)
                if (fileExists('allure-results')) {
                    try {
                        def allureCommand = 'allure generate allure-results --clean --single-file -o allure-report'
                        isUnix() ? sh(allureCommand) : bat(allureCommand)
                    } catch (Exception e) {
                        echo "Allure report generation failed: ${e.message}"
                    }
                } else {
                    echo 'No Allure results found.'
                }

                // Parse TestNG using a regex to avoid sandbox issues with XmlSlurper
                def reportPath = isUnix()
                    ? 'target/surefire-reports/testng-results.xml'
                    : 'target\\surefire-reports\\testng-results.xml'

                if (fileExists(reportPath)) {
                    def content = readFile(reportPath)
                    // Matches attributes on the <testng-results ...> root element
                    def m = content =~ /<testng-results\b[^>]*\btotal="(\d+)"[^>]*\bpassed="(\d+)"[^>]*\bfailed="(\d+)"[^>]*\bskipped="(\d+)"/
                    if (m.find()) {
                        total   = m.group(1)
                        passed  = m.group(2)
                        failed  = m.group(3)
                        skipped = m.group(4)
                    } else {
                        echo '⚠️ Unable to parse totals from testng-results.xml'
                    }
                } else {
                    echo '⚠️ testng-results.xml not found.'
                }

                // Compute % safely
                def toInt = { String s -> (s?.isInteger() ? s.toInteger() : 0) }
                int ti = toInt(total)
                int pi = toInt(passed)
                int fi = toInt(failed)
                int si = toInt(skipped)

                def pct = { int part, int whole -> whole > 0 ? String.format('%.1f%%', (part * 100.0) / whole) : '0.0%' }
                def passPct = pct(pi, ti)
                def failPct = pct(fi, ti)
                def skipPct = pct(si, ti)

                // Build param table
                def isGrid = params.GRID_URL == 'grid'
                def runMode = isGrid ? 'Grid' : 'Local'
                def paramMap = [
                    'Suite'         : params.SUITE,
                    'Browser'       : params.BROWSER,
                    'Environment'   : params.ENV,
                    'Group'         : params.GROUP,
                    'Parallel Mode' : params.PARALLEL_MODE,
                    'Language'      : params.LANGUAGE,
                    'Run Mode'      : runMode,
                    'Grid URL'      : (isGrid ? 'http://localhost:4444' : '(local)'),
                    'Max Retry'     : params.MAX_RETRY
                ]
                def paramHeaders = paramMap.keySet().collect { "<th>${it}</th>" }.join('')
                def paramValues  = paramMap.values().collect { "<td>${it}</td>" }.join('')

                // Allure link (artifact path)
                def allureLink = "${env.BUILD_URL}artifact/allure-report/index.html"

                emailext(
                    from: '"[no-reply] Selenide CI" <jenkinscisele3@gmail.com>',
                    subject: "[Automation Report] ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                    body: """<!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                                <style>
                                    body { font-family: Arial, Helvetica, sans-serif; font-size: 14px; }
                                    table { border-collapse: collapse; }
                                    td, th { padding: 6px 8px; text-align: center; border: 1px solid #ccc; }
                                    th { background-color: #f2f2f2; }
                                    .green { color: green; font-weight: bold; }
                                    .red { color: red; font-weight: bold; }
                                    .muted { color: #666; }
                                </style>
                            </head>
                            <body>
                            <p>Hi Team,</p>
                            <p>The automated test execution has been completed. Below is the summary report:</p>

                            <h3>Build Parameters</h3>
                            <table>
                                <tr>${paramHeaders}</tr>
                                <tr>${paramValues}</tr>
                            </table>

                            <br/>

                            <h3>Execution Summary</h3>
                            <table>
                                <tr>
                                    <th>Job Name</th>
                                    <th>Build Number</th>
                                    <th>Total</th>
                                    <th>Passed</th>
                                    <th>Failed</th>
                                    <th>Skipped</th>
                                    <th>Pass %</th>
                                    <th>Fail %</th>
                                    <th>Skip %</th>
                                </tr>
                                <tr>
                                    <td>${env.JOB_NAME}</td>
                                    <td>${env.BUILD_NUMBER}</td>
                                    <td>${ti}</td>
                                    <td class='green'>${pi}</td>
                                    <td class='red'>${fi}</td>
                                    <td>${si}</td>
                                    <td class='green'>${passPct}</td>
                                    <td class='red'>${failPct}</td>
                                    <td>${skipPct}</td>
                                </tr>
                            </table>

                            <p>
                                <strong>Allure Report:</strong>
                                <a href="${allureLink}">${allureLink}</a>
                                <span class="muted">(requires Jenkins access)</span>
                            </p>

                            <p>Please check the attachment for more detailed reports.</p>
                            <p>Best regards,<br/>Tuan Pham</p>
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
