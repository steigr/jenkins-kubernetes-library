#!/usr/bin/groovy
import gr.stei.Fabric8Commands;

def call(Map parameters = [:], body) {

	def defaultHostname = env.DEPLOY_INGRESS_HOSTNAME
	def hostname = parameters.get("hostname",defaultHostname)

  def defaultPort = env.DEPLOY_INGRESS_PORT
  def port = parameters.get("port",defaultPort)

  def defaultPath = env.DEPLOY_INGRESS_PATH
  def path = parameters.get("path",defaultPath)

	def spec = """
kind: Ingress
apiVersion: extensions/v1beta1
metadata:
  annotations:
    kubernetes.io/tls-acme: 'true'
  name: ${env.DEPLOY_APPLICATION}
  namespace: ${env.DEPLOY_NAMESPACE}
  labels:
    k8s-app: ${env.DEPLOY_APPLICATION}
spec:
  tls:
    - hosts:
        - ${hostname}
      secretName: ${hostname}-tls
  rules:
    - host: ${hostname}
      http:
        paths:
          - path: ${path}
            backend:
              serviceName: ${env.DEPLOY_APPLICATION}
              servicePort: ${port}
"""
  return spec
}