def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_prod_r5' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }

  pipelineJob("${config.client_name}/0-prod-deploy-new") {

    properties {
      logRotator(100, 100, 100, 100)
      disableConcurrentBuilds()

      pipelineTriggers {
        triggers {
          upstream {
            upstreamProjects("${config.client_name}/github_tagger")
          }
        }
      }
    }

    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", 'Client name')
      stringParam('CLIENT_ENV', "production", 'Taget env for deployment')
      stringParam('BRANCH_TO_DEPLOY', "master", 'Branch for deployment')
      stringParam('YOUR_COMMENT', "prod support deploy", 'Your deploy comment')
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
        scriptPath('jobs/pipeline/0-prod-deploy-pipeline')
      }
    }
  }
}
