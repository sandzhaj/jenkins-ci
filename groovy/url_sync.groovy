pipelineJob("service-jobs/automatization/urlList to gSpreadsheet") {

  properties {
    logRotator(10, 10, 10, 10)
    disableConcurrentBuilds()
  }

  triggers {
    cron('H 0 * * *')
  }

  parameters {
    stringParam('G_SPREADSHEET_ID', '1N-R4KuyIjLUQcfWVLE7EcONenOEfpEkFJ8-foJf0wd8', 'Spreadsheet id')
    stringParam('J_PROJECT', 'CIS', 'Default project key for jira tickets for sandzhaj domains and if project key is not specified in haproxy config')
    stringParam('J_TYPE', 'Problem', 'Issue type for SSL renewal issues')
    stringParam('SSL_DAYS_LIMIT', '30', 'How many days before the expiry of the certificate to create an issue')
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
      scriptPath('jobs/pipeline/url_sync')
    }
  }
}
