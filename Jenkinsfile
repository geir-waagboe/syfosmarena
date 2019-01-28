#!/usr/bin/env groovy

pipeline {
    agent any

     environment {
           APPLICATION_NAME = 'syfosmarena'
           DOCKER_SLUG = 'syfo'
       }

     stages {
         stage('initialize') {
             steps {
                 script {
                     init action: 'default'
                     sh './gradlew clean'
                     applicationVersionGradle = sh(script: './gradlew -q printVersion', returnStdout: true).trim()
                     if (!applicationVersionGradle.endsWith('-SNAPSHOT')) {
                         env.DEPLOY_TO = 'production'
                     }
                     env.APPLICATION_VERSION = "${applicationVersionGradle}-${env.COMMIT_HASH_SHORT}"
                     init action: 'updateStatus'
                 }
             }
         }
        stage('build') {
            steps {
                sh './gradlew build -x test'
            }
        }
        stage('run tests (unit & intergration)') {
            steps {
                sh './gradlew test'
            }
        }
        stage('create uber jar') {
            steps {
                sh './gradlew shadowJar'
            }
        }
         stage('deploy to preprod') {
             steps {
                     dockerUtils action: 'createPushImage'
                     deployApp action: 'kubectlDeploy', cluster: 'preprod-fss'
                     env.FASIT_ENVIRONMENT = 'q1'
                     slackStatus status: 'deploying'

                 }
             }
         stage('deploy to production') {
             when { environment name: 'DEPLOY_TO', value: 'production' }

             steps {
                     deployApp action: 'kubectlDeploy', cluster: 'prod-fss', file: 'naiserator-prod.yaml'
                     env.FASIT_ENVIRONMENT = 'p'
                     slackStatus status: 'deploying'
                 }
             }
        }
        post {
            always {
                postProcess action: 'always'
            }
            success {
                postProcess action: 'success'
            }
            failure {
                postProcess action: 'failure'
            }
        }
}
