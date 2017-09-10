#!/usr/bin/groovy
import gr.stei.Fabric8Commands;

def call(Map parameters = [:], body) {
  def flow = new Fabric8Commands()
  def cloud = flow.getCloudConfig()
  
  def defaultNamespace = "jenkins"
  def namespace = parameters.get("namespace", defaultNamespace)

  def defaultApplication = "app"
  def application = parameters.get("application", defaultApplication)

  def defaultLabel = 'deployer'
  def label = parameters.get("label",defaultLabel)

  def defaultTag = "${env.BRANCH_NAME}.${env.BUILD_NUMBER}"
  def tag = parameters.get("tag", defaultTag)

  def defaultRegistry = "127.0.0.1"
  def registry = parameters.get("registry",defaultRegistry)

  def defaultRegistryPort = "5000"
  def registryPort = parameters.get("registryPort",defaultRegistryPort)

  def defaultIngressHostname = "example.com"
  def ingressHostname = parameters.get("ingressHostname",defaultIngressHostname)

  def defaultIngressPort = 8080
  def ingressPort = parameters.get("ingressPort",defaultIngressPort)

  def defaultIngressPath = "/"
  def ingressPath = parameters.get("ingressPath",defaultIngressPath)

  def image = "127.0.0.1:5000/${namespace}/${application}"

  podTemplate(cloud: cloud, label: label, serviceAccount: 'jenkins', namespace: namespace,
    containers: [
      containerTemplate(
        name: 'registry-proxy',
        image: 'youfu/socat-minimal',
        command: "/usr/bin/socat",
        args: "tcp4-listen:5000,reuseaddr,reuseport,bind=127.0.0.1,fork tcp4-connect:${registry}:${registryPort}"),
      containerTemplate(
        name: 'docker-engine',
        image: "docker.io/library/docker:dind",
        privileged: true,
        command: "/usr/local/bin/dockerd-entrypoint.sh",
        args: '--storage-driver=overlay2 --data-root=/home/jenkins/docker'),
      containerTemplate(
        name: 'jnlp',
        image: 'jenkinsci/jnlp-slave:alpine',
        args: '${computer.jnlpmac} ${computer.name}',
        envVars: [
          envVar(key: "DOCKER_HOST", value: "tcp://127.0.0.1:2375/"),
          envVar(key: "DEPLOY_REGISTRY", value: registry),
          envVar(key: "DEPLOY_REGISTRY_PORT", value: registryPort),
          envVar(key: "DEPLOY_INGRESS_HOSTNAME", value: ingressHostname),
          envVar(key: "DEPLOY_INGRESS_PORT", value: ingressPort.toString()),
          envVar(key: "DEPLOY_INGRESS_PATH", value: ingressPath),
          envVar(key: "DEPLOY_LABEL", value: label),
          envVar(key: "DEPLOY_TAG", value: tag),
          envVar(key: "DEPLOY_IMAGE", value: image),
          envVar(key: "DEPLOY_APPLICATION", value: application),
          envVar(key: "DEPLOY_NAMESPACE", value: namespace)])],
    volumes: [
      persistentVolumeClaim(claimName: 'workspace-home', mountPath: '/home/jenkins'),
      emptyDirVolume(mountPath: '/var/run', memory: true)]
  ) {
    node(label) {
      body()
    }
  }
}