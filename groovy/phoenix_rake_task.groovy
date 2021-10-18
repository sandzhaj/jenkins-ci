def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_qa_phoenix' in client || 'deployment_prod_phoenix' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }
  
  pipelineJob("${config.client_name}/9-phoenix_rake_task") {
    
    properties {
      logRotator(100, 100)
      disableConcurrentBuilds()
    }

    if ("target_phoenix_env" in config) {
      target_env = config.target_phoenix_env
    } else {
      target_env = ['none', 'test-api', 'qa-api', 'release-api', 'prod-api']
    }
      
    if ("phoenix" == config.client_name) {
      capistrano_branch = "phoenix-back-gem"
    } else {
      capistrano_branch = "phoenix-back"
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
      stringParam('COMMENT', 'rake_task', '')
      stringParam('TASK_TO_RUN', 'db:migrate', '')
      choiceParam {
        name('CAP_BRANCH')
        description('')
        choices(capistrano_branch)
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
        scriptPath('jobs/pipeline/rake_task')
      }
    }
  }
}
