folder("service-jobs") {
    displayName("service-jobs")
    description("")
}

folder("service-jobs/docker-env") {
    displayName("docker-env")
    description("")
}

pipelineJob("service-jobs/docker-env/docker-build-dsl") {

properties {
    logRotator(2, 2)
}

parameters {
    choiceParam {
    name('DOCKER_FILE_NAME')
    description('')
        choices(["None", "Dockerfile-rspec-regression", "Dockerfile-rspec-static"])
    }
    stringParam('TAG', '1.0', '')      
}

definition {
    cpsScm {
    scm {
        gitSCM {
        userRemoteConfigs {
            userRemoteConfig {
            url('git@github.com:sandzhaj/jenkins-ci.git')
            name('')
            refspec('')
            credentialsId('CorevisCI-git')
            }
        }
        branches {
            branchSpec {
            name('*/jenkins-ci')
            }
        }
        doGenerateSubmoduleConfigurations(false)
        browser {
            gitWeb {
            repoUrl('https://github.com/sandzhaj/jenkins-ci')
            }
        }
        gitTool('git')
        }
    }
    scriptPath('jobs/pipeline/docker-build')
    }
}
}