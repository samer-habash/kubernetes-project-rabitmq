pipeline {
    agent {
        kubernetes {
        cloud 'kubernetes-cluster1'
        label 'kubernetes-project-pod-label'
        defaultContainer 'jnlp'
        yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  role: CI
spec:
  containers:
  - name: docker
    image: docker:latest
    command:
    - cat
    tty: true
    volumeMounts:
    - name: docker-socket
      mountPath: /var/run/docker.sock
  volumes:
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
"""
	}
  }
  stages {
    stage('Checkout SCM') {
      steps {
        container('docker') {
            // I can use git checkout and then go the specified producer directory and build the image, but my repo is for the whole project so I used svn to clone the producer directory only.
            // checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'GitHub', url: 'https://github.com/samer-habash/kubernetes-project-rabitmq']]])
            sh """
                apk add --no-cache subversion
                svn co https://github.com/samer-habash/kubernetes-project-rabitmq/trunk/app/consumer/ . --depth files
            """
        }
      }
    }
    stage('Build Image') {
      steps {
        container('docker') {
            sh """
                docker build -t jenkins-consumer:latest .
                docker tag jenkins-consumer:latest samer1984/jenkins-consumer:latest
            """
        }
      }
    }
    stage('Push Image') {
      steps {
        container('docker') {
            withDockerRegistry([ credentialsId: "DockerhubCreds", url: "" ]) {
                sh 'docker push samer1984/jenkins-consumer:latest'
            }
        }
      }
    }
  }
}
