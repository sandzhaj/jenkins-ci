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

  pipelineJob("${config.client_name}/magento_github_tagger") {

    properties {
      logRotator(100, 100, 100, 100)
      disableConcurrentBuilds()
      pipelineTriggers {
        triggers {
          cron {
            spec("${config.tagger_cron_time}")
          }
        }
      }
    }

    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", '')
      stringParam('REPO_NAME', "${config.github_mag}", '')
      stringParam('SLACK_CHANNEL_NAME', "${config.client_slack_channel}", '')
      stringParam('SLACK_CHANNEL_URL', "https://hooks.slack.com/services/${config.client_slack_url}", '')
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
        scriptPath('jobs/pipeline/tagger-pipeline-magento')
      }
    }
  }
}
