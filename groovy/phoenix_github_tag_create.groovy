def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_prod_phoenix' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }

  pipelineJob("${config.client_name}/phoenix_back_github_tagger") {

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
    if ('send_release_notes' in config) {
      send_release_notes=true
    } else {
      send_release_notes=false
    }
    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", '')
      stringParam('REPO_NAME', "${config.github_phoenix}", '')
      stringParam('SLACK_CHANNEL_NAME', "${config.client_slack_channel}", '')
      stringParam('SLACK_CHANNEL_URL', "https://hooks.slack.com/services/${config.client_slack_url}", '')
      booleanParam('SEND_RELEASE_NOTES', send_release_notes, '')
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
        scriptPath('jobs/pipeline/tagger-pipeline-phoenix-back')
      }
    }
  }
}
