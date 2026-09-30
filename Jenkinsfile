pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    parameters {
        choice(name: 'ENV', choices: ['stage', 'dev', 'prod'], description: 'AEM environment to test')
        choice(name: 'SUITE', choices: ['regression', 'smoke'], description: 'Test suite to run')
    }

    triggers {
        // Every weekday night at around 2 AM
        cron('H 2 * * 1-5')
    }

    stages {
        stage('Run API tests') {
            steps {
                withCredentials([usernamePassword(credentialsId: "aem-${params.ENV}-credentials",
                                                  usernameVariable: 'AEM_USERNAME',
                                                  passwordVariable: 'AEM_PASSWORD')]) {
                    // Windows agent. On a Linux agent use sh instead of bat.
                    bat "mvn clean test -Denv=${params.ENV} -Dsuite=${params.SUITE} -Dmaven.test.failure.ignore=true"
                }
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            publishHTML(target: [
                reportName           : 'Extent Report',
                reportDir            : 'target/extent-report',
                reportFiles          : 'index.html',
                keepAll              : true,
                alwaysLinkToLastBuild: true,
                allowMissing         : true
            ])
            archiveArtifacts artifacts: 'target/api-log.txt', allowEmptyArchive: true
        }
    }
}
