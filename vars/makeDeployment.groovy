#!/usr/bin/groovy

def call(Map parameters = [:], body) {

  def defaultPort = env.DEPLOY_INGRESS_PORT
  def port = parameters.get("port",defaultPort)

	 def spec = """
kind: Deployment
apiVersion: extensions/v1beta1
metadata:
  name: ${env.DEPLOY_APPLICATION}
  namespace: ${env.DEPLOY_NAMESPACE}
  labels:
    k8s-app: ${env.DEPLOY_APPLICATION}
spec:
  replicas: 1
  selector:
    matchLabels:
      k8s-app: ${env.DEPLOY_APPLICATION}
  template:
    metadata:
      labels:
        k8s-app: ${env.DEPLOY_APPLICATION}
    spec:
      containers:
        - name: ${env.DEPLOY_APPLICATION}
          image: ${env.DEPLOY_IMAGE}:${env.DEPLOY_TAG}
          imagePullPolicy: Always 
          ports:
            - containerPort: ${port}
              protocol: TCP
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
"""
  return spec
}