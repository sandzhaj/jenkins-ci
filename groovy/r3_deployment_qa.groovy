def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_qa_r3' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }

  pipelineJob("${config.client_name}/0-qa-deploy-new") {

    properties {
      logRotator(100, 100, 100, 100)
      disableConcurrentBuilds()
    }

    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", 'Client name')
      choiceParam {
        name('CLIENT_ENV')
        description('Select target env')
        choices(['none', 'dev', 'test', 'qa', 'release'])
      }
      stringParam('BRANCH_TO_DEPLOY', "master", 'Branch for deployment')
      stringParam('YOUR_COMMENT', "qa support deploy", 'Your deploy comment')
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
        scriptPath('jobs/pipeline/0-qa-deploy-pipeline')
      }
    }
  }
}
