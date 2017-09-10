#!/usr/bin/groovy
import gr.stei.Fabric8Commands;

def call(Map parameters = [:], body) {

  def defaultPort = env.DEPLOY_INGRESS_PORT
  def port = parameters.get("port",defaultPort)

  def spec = """
kind: Service
apiVersion: v1
metadata:
  name: ${env.DEPLOY_APPLICATION}
  namespace: ${env.DEPLOY_NAMESPACE}
  labels:
    k8s-app: ${env.DEPLOY_APPLICATION}
spec:
  ports:
    - protocol: TCP
      port: ${port}
      targetPort: ${port}
  selector:
    k8s-app: ${env.DEPLOY_APPLICATION}
"""
  return spec
}