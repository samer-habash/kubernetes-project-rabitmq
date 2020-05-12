podTemplate(cloud: 'kubernetes-project', label: 'kubernetes-project-pod-label', 
			containers: [containerTemplate(args: 'cat', command: '/bin/sh -c', image: 'docker:latest', name: 'docker', ttyEnabled: true, workingDir: '/home/jenkins/agent')], 
			volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')])
{
    stage('CI build/push docker images') {
    	parallel (
    	    'build/push producer' : {
          		node('kubernetes-project-pod-label') {
        			container('docker') {
        			    // git clone sub-directory producer
        				sh("""
        					apk add --no-cache subversion
        					svn co https://github.com/samer-habash/kubernetes-project-rabitmq/trunk/app/producer/ . --depth files
                    	""")
                    	// docker build image producer
                        sh("""
        					docker build -t jenkins-producer:latest .
        					docker tag jenkins-producer:latest samer1984/jenkins-producer:latest
                    	""")
                    	// push producer image to docker Hub
                    	withDockerRegistry([ credentialsId: "DockerhubCreds", url: "" ]) {
                    	    sh 'docker push samer1984/jenkins-producer:latest'
        			    }
        		    }
          		}
    	    },
    	    'build/push consumer' : {
        		node('kubernetes-project-pod-label') {
        			container('docker') {
        			    // git clone sub-directory producer
            			sh("""
            				apk add --no-cache subversion
            				svn co https://github.com/samer-habash/kubernetes-project-rabitmq/trunk/app/consumer/ . --depth files
            			""")
            			// docker build image consumer
                        sh("""
        					docker build -t jenkins-consumer:latest .
        					docker tag jenkins-producer:latest samer1984/jenkins-consumer:latest
                    	""")
                    	// push consumer image to docker Hub
                    	withDockerRegistry([ credentialsId: "DockerhubCreds", url: "" ]) {
                    	    sh 'docker push samer1984/jenkins-consumer:latest'
        			    }
        		    }
          		}
            }
        )
    }
}

