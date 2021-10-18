def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_prod_mag' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }
  
  pipelineJob("${config.client_name}/9-magento_prod_task") {
    
    properties {
      logRotator(100, 100)
      disableConcurrentBuilds()
    }

    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", 'Client name')
      choiceParam {
        name('CLIENT_ENV')
        description('Select target env')
        choices(['prod-catalog'])
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
