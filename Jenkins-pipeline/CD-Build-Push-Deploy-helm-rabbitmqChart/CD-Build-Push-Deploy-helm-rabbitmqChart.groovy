def reponame = "kubernetes-project-helm-charts"
def chartname = "rabbitmq-chart"
def git_config_email = "samer.habash1984.devops@gmail.com"
def chartVersion
def helmPackage

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
  - name: helm
    image: samer1984/helm-kubectl-alpine
    command:
    - cat
    tty: true
    volumeMounts:
    - name: kube-config
      mountPath: /root/.kube
  volumes:
  - name: kube-config
    hostPath:
      path: /home/sam/.kube/kubeconfig-to-let-jenkins-connect
"""
	}
  }
  stages {
    stage('Checkout SCM') {
      steps {
        container('helm') {
            // clone master and then checkout directory rabbutmq-chart from master branch
            sh 'git clone https://github.com/samer-habash/' + reponame
            dir(reponame) {
                sh("""
                    git checkout gh-pages
                    git checkout master ${chartname}
                """)
            }
        }
      }
    }
    stage('Package chart and commit') {
      steps {
        container('helm') {
            //package the helm and index it
            dir(reponame) {
                // create helm package and save its name to the var helm_package
                script { 
                    helm_package = sh(script: "helm package" + " " + chartname + " " + "--destination index/", returnStdout: true).split('/')[-1].trim()
                    chartVersion = helm_package.replaceAll(".tgz", "")
                }
                // assign the helm package variable to the package name
                sh("""
    				helm repo index index --url https://samer-habash.github.io/${reponame}/index/
    				rm -rf ${chartname}
                """)
            }
        }
      }
    }
    stage('Commit and Push index gh-pages') {
      steps {
        container('helm') {
            withCredentials([usernamePassword(credentialsId: 'GitHub', usernameVariable: 'Username', passwordVariable: 'Password')]) {
                dir(reponame) {
                    sh("""
                        git config --local credential.helper "!f() { echo username=\\$Username; echo password=\\$Password; }; f"
                        git config user.email ${git_config_email}
                        git add . && git commit -am "[Jenkins CI] added new helm package : ${helmPackage}"
                        git push
                    """)
                }
            }
        }
      }
    }
    stage('helm add repo and deploy') {
      steps {
        container('helm') {
            dir(reponame) {
                // helm install to fresh install, and helm upgrade to update the version of the currently installed chart
                sh("""
                    helm repo add ${chartVersion} https://samer-habash.github.io/${reponame}/index/
                    helm repo update
                """)
                script {
                    def chartExist = sh(returnStdout: true, script: "helm list --deployed --short --filter \"${chartname}\"")
                    println(chartExist)
                    // check if rabbitmq is deployed and the latest version
                    if(chartExist.contains(chartname) && chartExist.contains(chartVersion)) {
                        println("Chart" + " " + chartname + "exists and latest verison : " + chartVersion)
                    }
                    // check if rabbitmq is deployed and NOT the latest version
                    else if(chartExist.contains(chartname) && !chartExist.contains(chartVersion)) {
                        sh "helm upgrade ${chartname} ${chartVersion}/${chartname}"
                    }
                    else {
                        sh "helm install ${chartname} ${chartVersion}/${chartname}"
                    }
                }
            }
        }
      }
    }
  }
}
