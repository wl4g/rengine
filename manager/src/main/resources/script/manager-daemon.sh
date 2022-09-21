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

# Source function library.
. /etc/rc.d/init.d/functions

# Source networking configuration.
. /etc/sysconfig/network

# Check that networking is up.
[ "$NETWORKING" = "no" ] && exit 0

# Check java env env.
if [ -z "${JAVA_HOME}" ]; then
  echo "Please perfect the Java environment env `JAVA_HOME`."
  exit 1
fi

# Current directory.
BASE_DIR="$(cd "`dirname "$0"`"/; pwd)"

# Reference external env definition.
VAR_PATH=$BASE_DIR"/*-env.sh"
. $VAR_PATH

# Get the execution command, last arguments.
COMMAND="${!#}"

# Execution command
EXEC_CMD="$JAVA -server $DEBUG_OPTS $HEAP_OPTS $JVM_PERFORMANCE_OPTS $GC_LOG_OPTS $JMX_OPTS $JAVA_OPTS -cp $APP_CLASSPATH $MAIN_CLASS $APP_OPTS"

#
# Definition function
#
# Start function.
function start(){
  PIDS=$(getPids) # Get current process code.
  if [ -z "$PIDS" ]; then
    if [ "x$DAEMON_MODE" = "xtrue" ]; then
      nohup $EXEC_CMD > "$OUT_FILE" 2>&1 < /dev/null &
    else
      exec $EXEC_CMD
    fi

    echo -n "$APP_NAME starting ..."
    while true
    do
      PIDS=$(getPids)
      if [ "$PIDS" == "" ]; then
        echo -n ".";
        sleep 0.8;
      else
        /usr/bin/echo \$PIDS >"${DATA_DIR}/${APP_NAME}.pid"
        break;
      fi
    done
    echo -e "\nStarted on "$PIDS
  else
    echo "Server is running "$PIDS
  fi
}

# Stop function.
function stop(){
  PIDS=$(getPids)
  if [ -z "$PIDS" ]; then
    echo "Not process $MAIN_JAR_NAME stop."
  else
    echo -n "Stopping $MAIN_JAR_NAME $PIDS ..."
    kill -s TERM $PIDS
    while true
    do
      PIDS=$(getPids)
      if [ "$PIDS" == "" ]; then
        /usr/bin/rm -f ${DATA_DIR}/${APP_NAME}.pid
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
    # Startup argument -Dservpids and -Dservpoint are both selected. When both exist, only -Dservpoint takes effect. (e.g.: -Dservpoint=127.0.0.1:60120 -Ddebug)
    # More see: https://github.com/wl4g/super-devops/blob/master/super-devops-shell/super-devops-shell-cli/README_EN.md
    SHELL_PORT_OPTS=""
  fi
  EXEC_CMD_SHELL="$JAVA -Dprompt=$MAIN_JAR_NAME -Dservpids=\$(getPids) $SHELL_PORT_OPTS -cp .:$APP_HOME/$LIB_DIR_NAME/* com.wl4g.ShellBootstrap"
  exec \$EXEC_CMD_SHELL
}

# Get pids.
function getPids(){
  PIDS=$(ps ax | grep java | grep -i $APP_HOME | grep -v grep | awk '{print $1}')
  echo $PIDS # Output execution result value.
  return 0 # Return the execution result code.
}

# Executive operations.
case $COMMAND in
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
  echo $"Usage:{start|stop|restart|status|shell}"
  exit 2
esac

