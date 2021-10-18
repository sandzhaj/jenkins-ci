folder("lxd-manager") {} 

freeStyleJob("lxd-manager/4_move_container_dsl") {

  properties {
    logRotator(100, 100)

  }

  parameters {
    choiceParam('DST_HOST', ['None', 'lxd-qa-server-001','lxd-qa-server-002','lxd-qa-server-003','lxd-qa-server-004','lxd-qa-server-005','lxd-qa-server-006','lxd-qa-server-007','lxd-qa-server-008','lxd-qa-server-009','lxd-qa-server-010','lxd-qa-server-011','lxd-qa-server-012','lxd-qa-server-013','lxd-qa-server-014'], 'Select the lxd host for the new container')
    stringParam('CONT_NAME', "None", 'Select the container name')
  }

  label('lxd-deployer')

  publishers {
    publishOverSsh {
      server('lxd-cluster') {
        transferSet {
            execCommand('move_container.sh $DST_HOST $CONT_NAME')
            patternSeparator('[, ]+')
            execTimeout(0)
        }                            
      }
    }
  }
}
