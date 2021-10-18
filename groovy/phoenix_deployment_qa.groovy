def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_qa_phoenix' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  if ("phoenix" == config.client_name) {
    jobs = ["deploy-qa-FRONTEND", "deploy-qa"]
    gem = true
  } else {
    jobs = ["deploy-qa"]
    gem = false
  }

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }

  for (job in jobs) {
    pipelineJob("${config.client_name}/0-phoenix-${job}") {

      properties {
        logRotator(100, 100, 100, 100)
        disableConcurrentBuilds()
      }
      
      if ("target_phoenix_env" in config) {
        target_env = config.target_phoenix_env
      } else {
        target_env = ['none', 'test-api', 'qa-api', 'release-api', 'prod-api']
      }
      branch_name = "develop"

      if (gem == true) {
        if (job == "deploy-qa-FRONTEND") {
          capistrano_branch = "phoenix-fron-deploy"
          target_env = config.target_env_front
          branch_name = "release/0.10.0"
        } else {
          capistrano_branch = "phoenix-back-gem"
        }
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
          description('Select target env')
          choices(["#${config.client_slack_channel}"])
        }
        stringParam('BRANCH_TO_DEPLOY', "${branch_name}", 'Branch for deployment')
        choiceParam {
          name('CAP_BRANCH')
          description('Capistrano branch')
          choices(capistrano_branch)
        }
        if (job != "deploy-qa-FRONTEND") {
          booleanParam('RAKE_DB_REBUILD', false, 'check to run db:rebuild')
        }
        booleanParam('TRIGGER_REGRESSION', false, '')
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
          scriptPath("jobs/pipeline/0-qa-deploy-pipeline-phoenix")
        }
      }
    }
  }
}
