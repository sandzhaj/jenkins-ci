def file = Eval.me(readFileFromWorkspace('clients.config'))
def clients = []
for (client in file) {
    if ('deployment_qa_phoenix' in client) {
        clients.add(client)
    }
}
clients.each { Map config ->

  folder("${config.client_name}") {
      displayName("${config.client_name}")
      description("Folder for ${config.client_name}")
  }

  pipelineJob("${config.client_name}/1-pr-phoenix") {

    properties {
      logRotator(100, 100, 100, 100)
      disableConcurrentBuilds()
      githubProjectUrl("https://github.com/sandzhaj/${config.github_phoenix}")
      pipelineTriggers {
        triggers {
          ghprbTrigger {
            onlyTriggerPhrase(false)
            useGitHubHooks(false)
            permitAll(true)
            allowMembersOfWhitelistedOrgsAsAdmin(true)
            gitHubAuthId('value')
            adminlist('')
            whitelist('')
            orgslist('b2b2dot0')
            cron('H/2 * * * *')
            triggerPhrase('rebuild')
            onlyTriggerPhrase(false)
            useGitHubHooks(false)
            permitAll(true)
            autoCloseFailedPullRequests(false)
            displayBuildErrorsOnDownstreamBuilds(false)
            commentFilePath('')
            skipBuildPhrase('')
            blackListCommitAuthor('')
            whiteListTargetBranches {}
            blackListTargetBranches {}
            allowMembersOfWhitelistedOrgsAsAdmin(true)
            msgSuccess('')
            msgFailure('')
            commitStatusContext('Build')
            gitHubAuthId('GitCreds')
            buildDescTemplate('PR_TITLE: $title\nPR_URL: $url')
            blackListLabels('')
            whiteListLabels('')
            extensions {}
            includedRegions('')
            excludedRegions('')
          }
        }
      } 
    }

    parameters {
      stringParam('CLIENT_NAME', "${config.client_name}", 'Client Name')
      stringParam('REPO_NAME', "${config.github_rails}", 'Client github repo name')
      stringParam('CLIENT_REDIS_PORT', "${config.redis_port}", 'Redis port for pr test')
      stringParam('SAP_REDIRECT', "${config.sap_redirect}", 'Address of firewall container')
      stringParam('SAP_SERVER', "${config.sap_server}", 'SAP server ip')
      stringParam('SAP_PORT', "${config.sap_port}", 'SAP port')
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
        scriptPath('jobs/pipeline/1-pr-pipeline-r5-phoenix')
      }
    }
  }
}
