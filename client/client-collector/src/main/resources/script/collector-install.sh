#!/bin/bash
# Copyright (c) 2017 ~ 2025, the original authors individual Inc,
# All rights reserved. Contact us James Wong <jameswong1376@gmail.com>
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
set -e

#
# ===== Global define =====
#

# Depending on scripts.
BASE_DIR="$(cd "`dirname "$0"`"/.; pwd)"
VAR_PATH=$BASE_DIR"/*-env.sh"
. $VAR_PATH

# SC control script.
SC_CTL_PATH="/bin/${APP_NAME}-ctl"

# Service file name.
SERVICE_FILE_NAME="${APP_NAME}.service"

#
# ===== Check installed =====
#

OLD_SERVICE_PATH="/etc/init.d/${SERVICE_FILE_NAME}"
# Ubuntu15+ CentOS7+(/lib -> /usr/lib) compatible systemd directory.
OLD_SYSTEMD_PATH="/lib/systemd/system/${SERVICE_FILE_NAME}"

if [ -f "$OLD_SERVICE_PATH" ] || [ -f "$OLD_SYSTEMD_PATH" ]; then
  while true
  do
    read -t 10 -p "Service '${SERVICE_FILE_NAME}' has been installed, is it override installtion? (y|yes|n|no)" cover
    if [ -n "$(echo $cover|egrep -i 'Y|YES')" ]; then # Ignore case
      break;
    elif [ -n "$(echo $cover|egrep -i 'N|NO')" ]; then
      exit 0;
    else
      echo "Please reenter it!"
    fi
  done
fi

#
# ===== Check user and group =====
#

if [ "$DEFAULT_APP_GROUP" != "root" ]; then
  if [ -z "$(grep "^$DEFAULT_APP_GROUP" /etc/group)" ]; then
    groupadd $DEFAULT_APP_GROUP
  fi
fi

if [ "$APP_GROUP" != "root" ]; then
  if [ -z "$(grep "^$APP_GROUP" /etc/group)" ]; then
    groupadd $APP_GROUP
  fi
fi

if [ "$APP_USER" != "root" ]; then
  if [ -z "$(grep "^$APP_USER" /etc/passwd)" ]; then
    useradd -g $APP_GROUP $APP_USER
  fi
fi

#
# ===== Create useful dependence directory. =====
#

# JVM hsperfdata directory.
if [ ! -x "$DEFAULT_JVM_HSPERFDATA_HOME" ]; then
  mkdir -p "$DEFAULT_JVM_HSPERFDATA_HOME"
fi

#
# ===== Granting directory =====
#

gpasswd -a $APP_USER $DEFAULT_APP_GROUP
#chown -R $APP_USER:$APP_GROUP $APP_USER_HOME
chown -R $APP_USER:$APP_GROUP $PARENT_APP_HOME

chown -R $APP_USER:$APP_GROUP $DATA_DIR
chown -R $APP_USER:$APP_GROUP $LOG_DIR
chown -R $APP_USER:$DEFAULT_APP_GROUP $DEFAULT_JVM_HSPERFDATA_HOME

#chmod -R 740 $APP_USER_HOME/.bash*
chmod -R 750 $PARENT_APP_HOME

chmod -R 750 $DATA_DIR
chmod -R 750 $LOG_DIR
chmod -R 750 $DEFAULT_JVM_HSPERFDATA_HOME

#
# ===== Generate SysV/init.d(e.g. If CentOS 6) service =====
#

# Execution command
EXEC_CMD="$JAVA -server $DEBUG_OPTS $HEAP_OPTS $JVM_PERFORMANCE_OPTS $GC_LOG_OPTS $JMX_OPTS $JAVA_OPTS -cp $APP_CLASSPATH $MAIN_CLASS $APP_OPTS"

SERVICE_FILE=$BASE_DIR/$SERVICE_FILE_NAME
cat<<EOF>$SERVICE_FILE
#!/bin/bash
# chkconfig: - 85 15
# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,
# All rights reserved. Contact us 983708408@qq.com
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Version v2.0

# Source networking configuration.
if [ -f /etc/sysconfig/network ]; then
  . /etc/sysconfig/network

  # Check that networking is up.
  [ "\$NETWORKING" = "no" ] && exit 0
fi

# Check that networking is up.
[ "\$NETWORKING" = "no" ] && exit 0

# Load the user environment, for example, get the secret key of decrypting database password.
. /etc/profile
. /etc/bashrc
. $APP_USER_HOME/.bash_profile
. $APP_USER_HOME/.bashrc

# Make sure that when using absolute paths (e.g. /sbin/service XX start) bugs in the environment
# envs of the current system user are not retrieved.
if [ "\$USER" == "root" ]; then
  . "/root/.bash_profile"
  . "/root/.bashrc"
fi

# Get the execution command arg1.
COMMAND=\$1

#
# Definition function
#
# Start function.
function start(){
  PIDS=\$(getPids) # Get current process code.
  if [ -z "\$PIDS" ]; then
    nohup $EXEC_CMD > $OUT_FILE 2>&1 < /dev/null &

    echo -n "$APP_NAME starting ..."
    while true
    do
      PIDS=\$(getPids)
      if [ "\$PIDS" == "" ]; then
        echo -n ".";
        sleep 0.8;
      else
        echo \$PIDS >"${DATA_DIR}/${APP_NAME}.pid"
        break;
      fi
    done
    echo -e "\nStarted on "\$PIDS
  else
    echo "Server is running "\$PIDS
  fi
}

# Stop function.
function stop(){
  PIDS=\$(getPids) # Get current process code.
  if [ -z "\$PIDS" ]; then
    echo "No $APP_NAME running!"
  else
    echo -n "$APP_NAME stopping by \$PIDS ..."
    kill -s TERM \$PIDS
    while true
    do
      PIDS=\$(getPids)
      if [ "\$PIDS" == "" ]; then
        rm -f ${DATA_DIR}/${APP_NAME}.pid
        break;
      else
        echo -n ".";
        sleep 0.8;
      fi
    done
    echo -e "\nStop successfully."
  fi
}

# Status function.
function status(){
  ps -ef | grep -v grep | grep $APP_HOME
}

# Shell run conosle function.
function console(){
  if [ -z "\$SHELL_PORT_OPTS" ]; then
    # Startup argument -Dservname and -Dservpoint are both selected. When both exist, only -Dservpoint takes effect. (e.g.: -Dservpoint=127.0.0.1:60120 -Dxdebug)
    # More see: 
    # https://github.com/wl4g/xcloud-shell/blob/master/README.md
    # https://gitee.com/wl4g/xcloud-shell/blob/master/README_CN.md
    SHELL_PORT_OPTS=""
  fi
  EXEC_CMD_SHELL="$JAVA -Dprompt=$APP_NAME -Dservname=$APP_NAME $SHELL_PORT_OPTS -cp .:$APP_HOME/$LIB_DIR_NAME/* com.wl4g.ShellBootstrap"
  exec \$EXEC_CMD_SHELL
}

# Got pids.
function getPids(){
  PIDS=\$(ps ax | grep java | grep -i $APP_HOME | grep -v grep | awk '{print \$1}')
  echo \$PIDS # Output execution result value.
  return 0 # Return the execution result code.
}

# Executive operations.
case \$COMMAND in
  status)
    status
    ;;
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart)
    stop
    start
    ;;
  shell)
    console
    ;;
    *)
  echo \$"Usage:{start|stop|restart|status|shell}"
  exit 2
esac
EOF
chmod 750 $SERVICE_FILE && chown -R $APP_USER:$APP_GROUP $SERVICE_FILE && mv $SERVICE_FILE /etc/init.d/

#
# ===== Generate systemd(e.g. If CentOS 7 systemctl) service =====
#

if [ -f "/bin/systemctl" ]; then
  # Systemd service filename.
  SERVICE_FILE=$BASE_DIR/$SERVICE_FILE_NAME
  # Application environment.
  APP_ENV=". /etc/bashrc && . /etc/profile &&"
  if [ "$APP_USER" == 'root' ]; then
    APP_ENV=${APP_ENV}" . /root/.bash_profile && . /root/.bashrc"
  else
    APP_ENV=${APP_ENV}" . /home/$APP_USER/.bash_profile && . /home/$APP_USER/.bashrc"
  fi

cat<<EOF>$SERVICE_FILE
# See:http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-commands.html
[Unit]
Description=$APP_NAME - lightweight high availability application server based on JVM
After=network.target remote-fs.target nss-lookup.target

[Service]
Type=simple
PIDFile=$DATA_DIR/$APP_NAME.pid
ExecStartPre=/bin/rm -f $DATA_DIR/$APP_NAME.pid
ExecStart=/bin/bash -c "$APP_ENV && exec $EXEC_CMD > $OUT_FILE 2>&1"
ExecStartPost=/bin/bash -c "/bin/echo \$MAINPID >$DATA_DIR/$APP_NAME.pid"
ExecReload=/bin/kill -s HUP \$MAINPID

# Will it cause 'Restart=on-abnormal' to be invalid?
#ExecStop=/bin/kill -s TERM \$MAINPID
#StandardOutput=null
StandardError=journal
LimitNOFILE=1048576
LimitNPROC=1048576
LimitCORE=infinity
TimeoutStartSec=5
Restart=on-abnormal
KillMode=process
#KillSignal=SIGQUIT
#PrivateTmp=true # Will move the JVM default hsperfdata file
User=$APP_USER
Group=$APP_GROUP
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOF
  # Install to the system D directory.
  chmod 750 $SERVICE_FILE && chown -R $APP_USER:$APP_GROUP $SERVICE_FILE && mv $SERVICE_FILE /lib/systemd/system/
  # Make the system D service effective.
  systemctl daemon-reload
fi

#
# ===== Setup the system to start automatically =====
#

# Set to /etc/rc.local when systemctl is not supported, otherwise use systemctl first.
if [ ! -f "/bin/systemctl" ]; then
  if [ -z "$(cat /etc/rc.local|grep $APP_NAME)" ]; then
    echo "sudo -u $APP_USER /usr/sbin/service $APP_NAME start" >> /etc/rc.local
    # To try to ensure that it can start automatically (e.g. In CentOS 7 '/etc/rc.local' privileges was demoted)
    chmod +x /etc/rc.local
  fi
else
  /bin/systemctl enable $SERVICE_FILE_NAME
fi

#
# ===== Generate short service =====
#

cat<<EOF>$SC_CTL_PATH
#!/bin/bash
# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,
# All rights reserved. Contact us 983708408@qq.com
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Version v2.0

# Note: The process initiated by the ordinary shell scripts (e.g. 'service xx start'), 'systemctl restart xx' cannot terminate it, 
# and at the same time, 'systemctl start xx' can't be killed by 'kill -9 xx', because we specified the 
# 'Restart=on-abnormal' automatic restart policy when installing the systemd service.
if [ -f "/bin/systemctl" ] && [ -n "\$(echo \$1|egrep 'start|stop|restart|status')" ]; then
  /bin/systemctl \$* ${APP_NAME}.service
else
  /sbin/service ${APP_NAME}.service \$*
fi
EOF

#
# ===== Grant short service script =====
#

chmod 750 $SC_CTL_PATH
chown -R root:$DEFAULT_APP_GROUP $SC_CTL_PATH

echo "Installed $APP_NAME service successfully."

