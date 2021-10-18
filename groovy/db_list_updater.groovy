pipelineJob("service-jobs/automatization/db_list_updater") {

  properties {
    logRotator(10, 10, 10, 10)
    disableConcurrentBuilds()
  }

  triggers {
    cron('H */5 * * *')
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
      scriptPath('jobs/pipeline/db_list_updater')
    }
  }
}
