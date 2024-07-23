def setJenkinsEnvVariables(){
    def DEFAULT = "DEFAULT"
    echo 'running setTargetDeploymentVariables'
    if(params.V_JAVA_HOME != DEFAULT ){
        env.JAVA_HOME = params.V_JAVA_HOME
        env.PATH = "${env.PATH}:${env.JAVA_HOME}/bin"
    }
    if(params.V_MAVEN_HOME != DEFAULT){
        env.M2_HOME = params.V_MAVEN_HOME
        env.PATH = "${env.PATH}:${env.M2_HOME}/bin"
    }
    if(params.V_ANSIBLE_HOME != DEFAULT){
        env.ANSIBLE_HOME = params.V_ANSIBLE_HOME
        env.PATH = "${env.PATH}:${env.ANSIBLE_HOME}/bin"
    }
    echo "${env.PATH}"
}

def setTargetDeploymentVariables(){
    echo 'running setTargetDeploymentVariables'
    echo "${env.TARGET_SERVER_HOST}"
    if(params.ENVIRONMENT == "ORACLE"){
        env.TARGET_SERVER_HOST = "172.30.38.232"
        env.TARGET_SERVER_TOMCAT_HOME = ""
    }
    if(params.ENVIRONMENT == "AWS"){
        echo 'running AWS condition'
        env.TARGET_SERVER_HOST = "35.155.189.215"
        env.TARGET_SERVER_TOMCAT_HOME = "/home/ec2-user/binaries/tomcat_spanishv1"
        echo "${env.TARGET_SERVER_HOST}"
    }
}

return this