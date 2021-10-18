pipelineJob("service-jobs/automatization/Pingdom Update") {

  properties {
    logRotator(10, 10, 10, 10)
    disableConcurrentBuilds()
  }

  triggers {
    cron('H 0 * * *')
  }

  parameters {
    stringParam('C_BRANCH', 'CIS-292', 'branch with data/haproxy_*.yaml config files')
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
      scriptPath('jobs/pipeline/pingdom_update')
    }
  }
}
