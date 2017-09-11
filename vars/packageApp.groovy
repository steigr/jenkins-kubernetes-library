#!/usr/bin/groovy
def call(Map parameters = [:], body) {
	stage('packageApp') {
		echo "Packaging Application as ${env.DEPLOY_IMAGE}:${env.DEPLOY_TAG} from SCM"
		checkout scm
	  kubernetes.image().withName("${env.DEPLOY_IMAGE}:${env.DEPLOY_TAG}").build().fromPath(".")
	  kubernetes.image().withName("${env.DEPLOY_IMAGE}:${env.DEPLOY_TAG}").push().toRegistry()
	}
}