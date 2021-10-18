folder("lxd-manager") {} 

freeStyleJob("lxd-manager/0_list_stats_dsl") {

  properties {
    logRotator(100, 100)

  }

  label('lxd-deployer')

  publishers {
    publishOverSsh {
      server('lxd-cluster') {
        transferSet {
            execCommand('lxd_stats.sh')
            patternSeparator('[, ]+')
            execTimeout(120000)
        }
        verbose(true)                            
      }
    }
  }
}
