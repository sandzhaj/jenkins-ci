def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
  clients.add(client)
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }
  
  pipelineJob("${config.client_name}/10-restart") {
    
    properties {
      logRotator(100, 100)
      disableConcurrentBuilds()
    }

    def target_env = []

    // Phoenix
    if ("phoenix" == config.client_name) {
      target_env = config.target_phoenix_env.plus(config.target_env_front)
    } else {
      // Client Rails5
      if ('deployment_qa_r5' in config) {
        if ("target_rails_env" in config) {
          target_env = target_env.plus(config.target_rails_env)
        } else {
          target_rails_env = ['none', 'dev', 'test', 'qa', 'release', 'production']
          target_env = target_env.plus(target_rails_env)
        }
      }
      // Client Api
      if ('deployment_qa_phoenix' in config) {
        if ("target_phoenix_env" in config) {
          target_env = target_env.plus(config.target_phoenix_env)
        } else {
          target_phoenix_env = ['none', 'test-api', 'qa-api', 'release-api', 'prod-api']
          target_env = target_env.plus(target_phoenix_env)
        }
      }
      // Client Magento
      if ('deployment_qa_mag' in config) {
        if ("target_magento_env" in config) {
          target_env = target_env.plus(config.target_magento_env)
        } else {
          target_magento_env = ['none', 'dev-catalog', 'test-catalog', 'qa-catalog', 'release-catalog', 'prod-catalog']
          target_env = target_env.plus(target_magento_env)
        }
      }
    }

    parameters {
      choiceParam {
        name('CLIENT_NAME')
        description('Select target env')
        choices(["${config.client_name}"])
      }
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
        scriptPath('jobs/pipeline/restart')
      }
    }
  }
}
