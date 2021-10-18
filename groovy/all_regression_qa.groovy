def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
  for (client in file) {
        if ('deployment_qa_r5' in client) {
            clients.addAll(client)

        }
    }
clients.each { Map config ->
  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }
for (target in config.path_to_tests_qa) {
  pipelineJob("${config.client_name}/6-regression-${target.keySet()[0]}-${target.values()[0]}") {

    properties {
      logRotator(100, 100, 100, 100)
      disableConcurrentBuilds()
    }

    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", 'Client Name')
      stringParam('TARGET_ENV', "${target.keySet()[0]}", 'Taget env for tests')
      stringParam('PATH_TO_TESTS', "${target.values()[0]}", 'Location of test in regression repo')
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
        lightweight(lightweight = true)
        scriptPath('jobs/pipeline/6-regression-qa-pipeline')
      }
    }
  }
}
}