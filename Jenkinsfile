#!groovy

@Library(["gcp-workflowlib@master"]) _

buildAgent = "gcp-agent-${new Date().getTime()}"

pipeline {
    agent {
        kubernetes {
            label buildAgent
            defaultContainer 'fga-cli'
            yaml """
apiVersion: v1
kind: Pod
spec:
  securityContext:
    runAsUser: 1000
  containers:
    - name: fga-cli
      image: artifactory.globaldevtools.bbva.com:443/gl-gcp-docker-local/gcp/arch/bbva-fga-cli-ng:latest
      command:
        - cat
      tty: true
      resources:
        requests:
          cpu: 1
          memory: 2048Mi
        limits:
          cpu: 1
          memory: 2048Mi
  imagePullSecrets:
    - name: registrypullsecret
"""
        }
    }

    options {
        ansiColor('xterm')
        timeout(time: 60, unit: 'MINUTES')
    }

    environment {
        UUAA = ''
        SAMUEL_PROJECT_NAME = 'BBVA_PE_GCP_GOB_DICCIONARIO_DATOS'
        CLI_MODE = 'jenkins'
        BOT_GCP_READER_USER = credentials('bot_gcp_reader_vdc_user')
        BOT_GCP_READER_PASSWORD = credentials('bot_gcp_reader_vdc_password')
        NO_PROXY = "172.20.0.0/16,10.60.0.0/16,169.254.169.254,.igrupobbva,.jenkins,.internal,localhost,127.0.0.1,127.20.0.1,central-jenkins-cache.s3.eu-west-1.amazonaws.com,central-jenkins-cache.s3.amazonaws.com,.eu-west-1.amazonaws.com,jenkins.globaldevtools.bbva.com,globaldevtools.bbva.com"
        HTTPS_PROXY = "http://proxy.cloud.local:8080"
        HTTP_PROXY = "http://proxy.cloud.local:8080"
        https_proxy = "http://proxy.cloud.local:8080"
        http_proxy = "http://proxy.cloud.local:8080"
        no_proxy = "172.20.0.0/16,10.60.0.0/16,169.254.169.254,.igrupobbva,.jenkins,.internal,localhost,127.0.0.1,127.20.0.1,central-jenkins-cache.s3.eu-west-1.amazonaws.com,central-jenkins-cache.s3.amazonaws.com,.eu-west-1.amazonaws.com,jenkins.globaldevtools.bbva.com,globaldevtools.bbva.com"
    }

    stages {
        stage ('Samuel setup') {
            steps {
                script {
                    gcpsamuel.prepare()
                }
            }
        }

        stage ('Test & Coverage') {
            steps {
                library 'sonar@lts'
                script {
                    def statusCode = null

                    sonar([
                            waitForQualityGate: true
                    ]) {
                        withCredentials([file(credentialsId: 'bot_gcp_maven_settings', variable: 'GCP_MAVEN_SETTINGS')]) {
                            statusCode = sh returnStatus: true, script: '''
                                mvn -B -V -U -s $GCP_MAVEN_SETTINGS clean verify sonar:sonar
                            '''
                        }
                    }

                    // if (statusCode != 0) {
                    //     error 'Error executing test and coverage analysis'
                    // }
                }
            }
        }

        stage ('Wait for Cloud Build') {
            steps {
                script {
                    try {
                        gcphelper.waitForCloudBuildExecution(true)

                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error "Error: ${e.getMessage()}"
                    }
                }
            }
        }

        stage ('Tag code') {
            when {
                branch 'master'
            }

            steps {
                script {
                    gcphelper.createTag()
                }
            }
        }
    }

    post {
        always {
            echo "We have been through the entire pipeline"
        }
        changed {
            echo "There have been some changes from the last build"
        }
        success {
            echo "Build successful"
        }
        failure {
            echo "There have been some errors"
        }
        unstable {
            echo "Unstable"
        }
        aborted {
            echo "Aborted"
            script {
                gcphelper.delete_notifications(true)
            }
        }
    }
}