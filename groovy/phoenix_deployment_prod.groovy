def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_prod_phoenix' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  if ("phoenix" == config.client_name) {
    jobs = ["deploy-prod-FRONTEND", "deploy-prod"]
    gem = true
  } else {
    jobs = ["deploy-prod"]
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

        if (job != 'deploy-prod-FRONTEND') {
          pipelineTriggers {
            triggers {
              upstream {
                upstreamProjects("${config.client_name}/phoenix_back_github_tagger")
              }
            }
          }
        }
      }

      if (gem == true) {
        if (job == "deploy-prod-FRONTEND") {
          capistrano_branch = "phoenix-fron-deploy"
          target_env = ['prod-front-clients', 'prod-front-gem']
        } else {
          capistrano_branch = "phoenix-back-gem"
          target_env = ['prod-api']
        }
      } else {
        target_env = ['prod-api']
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
          choices("https://hooks.slack.com/services/${config.client_slack_url}")
        }
        choiceParam {
          name('CLIENT_SLACK_CHANNEL')
          description('used for notifications')
          choices("#${config.client_slack_channel}")
        }
        choiceParam {
          name('CLIENT_GITHUB')
          description("client's repository")
          choices([config.github_phoenix])
        }
        stringParam('BRANCH_TO_DEPLOY', "master", 'Branch for deployment')
        choiceParam {
          name('CAP_BRANCH')
          description('Capistrano branch')
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
          scriptPath("jobs/pipeline/0-prod-deploy-pipeline-phoenix")
        }
      }
    }
  }
}
