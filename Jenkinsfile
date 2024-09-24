#!groovy

@Library(["gcp-workflowlib@master"]) _

buildAgent = "gcp-agent-${new Date().getTime()}"

pipeline {
    agent {
        kubernetes {
            label buildAgent
            defaultContainer 'gcloud-sdk'
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    name: ${buildAgent}
spec:
  securityContext:
    runAsUser: 1000
  containers:
    - name: gcloud-sdk
      image: google/cloud-sdk:alpine
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

    - name: maven
      image: maven:3.8.6-openjdk-18
      command:
        - cat
      tty: true
      resources:
        requests:
          cpu: 1
          memory: 768Mi
        limits:
          cpu: 1
          memory: 768Mi
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
        SAMUEL_PROJECT_NAME = 'Pipeline Java Back GAE'
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
                container(name: 'maven', shell: '/bin/bash') {
                    library 'sonar@lts'
                    script {
                        def statusCode = null

                        sonar([
                            sonarInstanceName: 'sonar-community-pro',
                            waitForQualityGate: true
                        ]) {
                            withCredentials([
                                    string(credentialsId: 'gcp-global-mvn-repository-server-vdc', variable: 'GCP_MAVEN_SERVER'),
                                    string(credentialsId: 'gcp-global-mvn-repository-mirror-vdc', variable: 'GCP_MAVEN_MIRROR'),
                                    usernamePassword(credentialsId: 'proxyng-user-pass', usernameVariable: 'PROXYNG_USER', passwordVariable: 'PROXYNG_PASS')
                            ]) {
                                statusCode = sh returnStatus: true, script: '''
                                    echo "
                                        <settings>
                                          <localRepository>~/.m2/repository</localRepository>
                                          <proxies>
                                            <proxy>
                                              <id>proxyng</id>
                                              <active>true</active>
                                              <protocol>https</protocol>
                                              <host>proxy.cloud.local</host>
                                              <port>8080</port>
                                              <username>$PROXYNG_USER</username>
                                              <password>$PROXYNG_PASS</password>
                                              <nonProxyHosts></nonProxyHosts>
                                            </proxy>
                                          </proxies>

                                          <servers>
                                            <server>
                                              $GCP_MAVEN_SERVER
                                            </server>
                                          </servers>
                                          <mirrors>
                                            <mirror>
                                              $GCP_MAVEN_MIRROR
                                            </mirror>
                                          </mirrors>
                                        </settings>" > mvn-settings.xml
                                    mvn -B -V -U -s mvn-settings.xml clean verify sonar:sonar
                                '''
                            }
                        }

                        if (statusCode != 0) {
                            error 'Error executing test and coverage analysis'
                        }
                    }
                }
            }
        }

        stage ('Wait for Cloud Build') {
           when {
               anyOf {
                   branch 'develop'
                   branch 'master'
               }
           }
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