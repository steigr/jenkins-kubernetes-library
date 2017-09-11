#!/usr/bin/groovy

def call(Map parameters = [:], body) {
	def defaultEnvironment = "staging"
	def environment = parameters.get("environment", defaultEnvironment)

	if(parameters.get("ingress",false)) {
		stage('Deploy Ingress') {
			echo "Deploy Ingress"
			kubernetesApply(file: makeIngress(parameters),environment: environment)
		}
	}

	if(parameters.get("service",false)) {
		stage('Deploy Service') {
			echo "Deploy Service"
			kubernetesApply(file: makeService(parameters),environment: environment)
		}
	}

	if(parameters.get("deployment",false)) {
		stage('Applying Deployment') {
			echo "Applying Deployment"
			kubernetesApply(file: makeDeployment(parameters),environment: environment)
		}
	}
}