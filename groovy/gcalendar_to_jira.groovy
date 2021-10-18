pipelineJob("service-jobs/automatization/gCalendar-to-Jira") {

  properties {
    logRotator(10, 10, 10, 10)
    disableConcurrentBuilds()
  }

  triggers {
    cron('H */5 * * *')
  }

  parameters {
    stringParam('G_CALENDAR', 'c_citdrh7et77vufihfhas3r93ds@group.calendar.google.com', 'Calendar id')
    stringParam('G_MAX_RESULTS', "100", 'Max items, while requesting event list')
    choiceParam('J_PROJECT', ['CIS','ISC'], 'Default project key if not specified')
    choiceParam('J_ASSIGNEE', ['','Mark','Andrei','Justin'], 'Default assignee if not specified')
    choiceParam('J_PRIORITY', ['Medium','Lowest','Low','High','Highest'], 'Default priority if not specified')
    stringParam('J_TYPE', 'Task', 'Default issue type if not specified')
    stringParam('J_LABELS', '', 'Default labels if not specified')
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
      scriptPath('jobs/pipeline/gcalendar-to-jira')
    }
  }
}
