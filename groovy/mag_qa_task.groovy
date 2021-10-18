def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_qa_mag' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }
  
  pipelineJob("${config.client_name}/9-magento_qa_task") {
    
    properties {
      logRotator(100, 100)
      disableConcurrentBuilds()
    }

    if ("target_magento_env" in config) {
      target_env = config.target_magento_env
    } else {
      target_env = ['none', 'dev-catalog', 'test-catalog', 'qa-catalog', 'release-catalog']
    }

    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", 'Client name')
      choiceParam {
        name('CLIENT_ENV')
        description('Select target env')
        choices(target_env)
      }
      choiceParam {
        name('CLIENT_SLACK_URL')
        description('used for notifications')
        choices(["https://hooks.slack.com/services/${config.client_slack_url}"])
      }
      choiceParam {
        name('CLIENT_SLACK_CHANNEL')
        description('')
        choices(["#${config.client_slack_channel}"])
      }
      stringParam('COMMENT', 'magento_task', '')
      stringParam('TASK_TO_RUN', 'sandzhaj:import', '')
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
        scriptPath('jobs/pipeline/magento_task')
      }
    }
  }
}
