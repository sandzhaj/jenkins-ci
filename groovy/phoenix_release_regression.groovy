def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('phoenix_check_release' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }
  
  pipelineJob("${config.client_name}/phoenix_regression_tests") {
    
    properties {
      logRotator(100, 100)
      disableConcurrentBuilds()
    }
      
    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", 'Client name')
      choiceParam {
        name('PATH_TO_TESTS')
        description('Select client folder')
        choices(['phoenix_gem_qa'])
      }
      choiceParam {
        name('TARGET_ENV')
        description('Select env. Forget which env is related to each client? Check here: https://drive.google.com/file/d/1KfBaIz07n82iCm2C5fbHVkIckWNzq5fv/view')
        choices(["release"])
      }
      stringParam('BRANCH', 'release/1.10.0', '')

    }

    triggers {
      cron('H 15 * * *')
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
        scriptPath('jobs/pipeline/phoenix_release_regression')
      }
    }
  }
}
