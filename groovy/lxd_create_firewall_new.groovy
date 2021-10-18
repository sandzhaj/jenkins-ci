folder("lxd-manager") {} 

freeStyleJob("lxd-manager/1_create-firewall-new_dsl") {

  properties {
    logRotator(100, 100)

  }

  parameters {
    stringParam('CONTAINER_NAME', 'none', 'NOTE: digits in name are not allowed. Name shoul be like \'tca-firewall\'.')
    choiceParam('VLAN_ID', ['none', '1101', '1102', '1103', '1104', '1105', '1106', '1107', '1108', '1109', '1110', '1111', '1112', '1113', '1114', '1115', '1116', '1117', '1118', '1119', '1120', '1121', '1122', '1123', '1124', '1125', '1126', '1127', '1128', '1129', '1130', '1131', '1132', '1133', '1134', '1135','1136','1137','1138','1139','1140'])
    choiceParam('TEMPLATE_IMAGE', ['QA-Baseline-CentOS7-v02', 'none'], 'Select the base container image')
    choiceParam('LXD_HOST', ['None', 'lxd-qa-server-001','lxd-qa-server-002','lxd-qa-server-003','lxd-qa-server-004','lxd-qa-server-005','lxd-qa-server-006','lxd-qa-server-007','lxd-qa-server-008','lxd-qa-server-009','lxd-qa-server-010','lxd-qa-server-011','lxd-qa-server-012','lxd-qa-server-013','lxd-qa-server-014'], 'Select the lxd host for the new container')
    stringParam('EXTERNAL_IP', "172.16.100.555", 'Please select ip from available 172.16.100.0/24 network')
  }

  wrappers {
      buildName('${ENV,var="CONTAINER_NAME"}-${ENV,var="VLAN_ID"}-${ENV,var="TEMPLATE_IMAGE"}-${ENV,var="LXD_HOST"}')
  }

  label('lxd-deployer')

  steps {
      shell('''SETTINGS_FILE='firewall.cfg'

echo "CONTAINER_NAME=\"$CONTAINER_NAME\"" > $SETTINGS_FILE
echo "VLAN_ID=\"$VLAN_ID\"" >> $SETTINGS_FILE
echo "TEMPLATE_IMAGE=\"$TEMPLATE_IMAGE\"" >> $SETTINGS_FILE
echo "LXD_HOST=\"$LXD_HOST\"" >> $SETTINGS_FILE
echo "EXTERNAL_IP=\"$EXTERNAL_IP\""  >> $SETTINGS_FILE''')
  }

  publishers {
    publishOverSsh {
      server('lxd-cluster') {
        transferSet {
            sourceFiles('firewall.cfg')
            remoteDirectory('tmp/')
            execCommand('''cat ~/tmp/firewall.cfg
create_firewall_new.sh ~/tmp/firewall.cfg''')
            patternSeparator('[, ]+')
            execTimeout(0)
            execInPty(true) 
        }
        verbose(true)                            
      }
    }
  }
}
