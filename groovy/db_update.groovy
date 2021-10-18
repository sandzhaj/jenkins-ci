import groovy.json.JsonSlurper
def jsonSlurper = new JsonSlurper()
data = jsonSlurper.parseText(readFileFromWorkspace('db.json'))

folder("service-jobs/database-maintenance") {
    displayName("database-maintenance")
}

pipelineJob("service-jobs/database-maintenance/db_update") {

  properties {
    logRotator(10, 10, 10, 10)
    disableConcurrentBuilds()
  }
 
  def choice_list = data['mariadb'] + data['mysql']

  parameters {
    choiceParam{
      name('SOURCE_DATABASE')
      description('Select source database. We will take the latest backup for it.')
      choices(data['default'] + choice_list.sort())
    }
    choiceParam{
      name('TARGET_DATABASE')
      description('Select database you want to update.')
      choices(data['default'] + choice_list.sort())
    }
    booleanParam('UPDATE_SOURCE', false, 'Update source backup before pushing it to the target database')
    booleanParam('BACKUP_TARGET', false, 'Create backup for target database before updating')
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
      scriptPath('jobs/pipeline/db_update')
    }
  }
}

