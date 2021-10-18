def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('phoenix_check_release' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  if ("phoenix-release" == config.client_name) {
    // jobs = ["front-clients","front", "back"]
    jobs = ["front", "back"]
    gem = true
  } else {
    jobs = ["back"]
    gem = false
  }

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }

  for (job in jobs) {
    freeStyleJob("${config.client_name}/phoenix-check-release-${job}") {

      properties {
        logRotator(100, 100, 100, 100)
        disableConcurrentBuilds()
      }

      // Define vars
      if (job == "back") {
        if ("phoenix-release" == config.client_name) {
          trigger_job = '../phoenix/0-phoenix-deploy-qa'
        } else {
          trigger_job = '0-phoenix-deploy-qa'
        }
        git_repo = config.github_phoenix
        trigger_env = 'release-api'
      } else {
        git_repo = "sandzhaj-react"
        trigger_job = '../phoenix/0-phoenix-deploy-qa-FRONTEND'
        if (job == "front") {
          trigger_env = 'release-front1'
        } else if (job == "front-clients") {
          trigger_env = 'release-front-clients'
        }
      }

      // Define git SCM
      scm {
        gitSCM {
          userRemoteConfigs {
            userRemoteConfig {
              url("git@github.com:sandzhaj/${git_repo}.git")
              name('')
              refspec('')
              credentialsId('CorevisCI-git')
            }
          }
          branches {
            branchSpec {
              name('*/release/*')
            }
          }
          doGenerateSubmoduleConfigurations(false)
          browser {
            gitWeb {
              repoUrl("https://github.com/sandzhaj/${git_repo}")
            }
          }
          gitTool('git')
        }
      }

      // Job trigger
      triggers {
        scm(config.tagger_cron_time)
      }

      // Trigget another job
      steps {
        shell('echo BRANCH_TO_DEPLOY=${GIT_COMMIT:0:10} > build.properties')
        downstreamParameterized {
            trigger(trigger_job) {
                parameters {
                    predefinedProps([CLIENT_ENV: trigger_env, RAKE_DB_REBUILD: true])
                    fileBuildParameters {
                      propertiesFile('build.properties')
                      encoding('')
                      failTriggerOnMissing(false)
                      useMatrixChild(false)
                      combinationFilter('')
                      onlyExactRuns(false)
                      textParamValueOnNewLine(false)
                      }
                }
            }
        }
      }

    }
  }
}
