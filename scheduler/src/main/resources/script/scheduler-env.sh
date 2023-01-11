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

# Load the user environment, e.g. get the secret key of decrypting database password.
if [ -f "/etc/profile" ]; then # e.g. CentOS.x, Ubuntu.x
  . /etc/profile && echo -n '' || echo -n ''
fi
if [ -f "/etc/bashrc" ]; then # e.g. CentOS.x
  . /etc/bashrc && echo -n '' || echo -n ''
fi
if [ -f "/etc/bash.bashrc" ]; then # e.g. Ubuntu.x
  . /etc/bash.bashrc && echo -n '' || echo -n ''
fi
if [ -f "$HOME/.bashrc" ]; then # e.g. CentOS.x, Ubuntu.x
  . ~/.bashrc && echo -n '' || echo -n ''
fi
if [ -f "$HOME/.bash_profile" ]; then # e.g. CentOS.x
  . ~/.bash_profile && echo -n '' || echo -n ''
fi
if [ -f "$HOME/.profile" ]; then # e.g. Ubuntu.x
  . ~/.profile && echo -n '' || echo -n ''
fi

# Current directory.
BASE_DIR="$(cd "`dirname "$0"`"/..; pwd)"

#
# --- Global define ---
#

# Note that the application name must be the same as the boot jar file name. (Short name is recommended)
APP_NAME="${APP_NAME:-rengine-scheduler}"
APP_VERSION="${APP_VERSION:-1.0.0}"
APP_PROFILE="${APP_PROFILE:-pro}"
MAIN_CLASS="${MAIN_CLASS:-com.wl4g.RengineScheduler}"

# Need to enable the willcard classpath? (Enable: "1", otherwise not enabled),
# When enabled, the generated startup script will contain wildcard loads e.g. "java -cp /usr/local/myapp1/lib/*"
ENABLE_WILDCARD_CLASSPATH="${ENABLE_WILDCARD_CLASSPATH:1}"

# Running in daemon mode?
DAEMON_MODE="${DAEMON_MODE:-true}"

# App home and config directory define.
APP_HOME=$BASE_DIR
PARENT_APP_HOME=$(dirname "$APP_HOME")

CONF_HOME="${APP_HOME}/conf"

# JVM define.
DEFAULT_JVM_HSPERFDATA_HOME="/tmp/hsperfdata_${APP_USER}"

# App user and group define.
APP_USER=$APP_NAME
APP_GROUP=$APP_USER
DEFAULT_APP_GROUP="apps"
APP_USER_HOME="/home/${APP_USER}"

# Logs,lib and data directory define.
LIB_DIR_NAME="lib"
DATA_DIR="/mnt/disk1/${APP_NAME}"
LOG_DIR="/mnt/disk1/log/${APP_NAME}"

# Standard out and err logfile.
#STDOUT_FILE="/dev/null"
STDOUT_FILE="${LOG_DIR}/${APP_NAME}.stdout"
STDERR_FILE="${LOG_DIR}/${APP_NAME}.stderr"

# Note that "java -cp" is in order. See: https://www.jianshu.com/p/23e0517d76f7
# and https://docs.oracle.com/javase/8/docs/technotes/tools/unix/classpath.html
APP_CLASSPATH=.:$CONF_HOME

# Correction path. (e.g. "//" to "/")
# For soft link security.(e.g. "/opt/apps/myapp1/" In ln commands, paths ending with "/" are generally dangerous.)
DATA_DIR=${DATA_DIR//\/\//\/}
LOG_DIR=${LOG_DIR//\/\//\/}
APP_CLASSPATH=${APP_CLASSPATH//\/\//\/}

#
# --- Runtime define ---
#

# Debugger port, If the configuration is empty, it will not be enabled. (The default is usually 8000)
DEBUG_PORT=""

# JMX port, If the configuration is empty, it will not be enabled. (The default random port)
JMX_PORT=""

# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi
# the first segment of the version number, which is '1' for releases before Java 9
# it then becomes '9', '10' etc.
# e.g: openjdk version "11.0.10" 2021-01-19
JAVA_MAJOR_VERSION=$($JAVA -version 2>&1 | sed -E -n 's/.* version \"(.+)\.(.+)\.(.+)".*/\1/p')
JAVA_MINOR_VERSION=$($JAVA -version 2>&1 | sed -E -n 's/.* version \"(.+)\.(.+)\.(.+)".*/\2/p')
JAVA_PATCH_VERSION=$($JAVA -version 2>&1 | sed -E -n 's/.* version \"(.+)\.(.+)\.(.+)".*/\3/p')

# Debug options
if [ -n "$DEBUG_PORT" ]; then
  DEBUG_OPTS="-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=$DEBUG_PORT,suspend=n"
fi

#
# --- JVM jmx options. ---
#

if [ "$JMX_PORT" ]; then
  JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=$JMX_PORT"
fi

#
# --- JVM memory options. ---
#

if [ -z "$HEAP_OPTS" ]; then
  HEAP_OPTS="-Xms256M -Xmx1G"
fi

# JVM performance options
if [ -z "$JVM_PERFORMANCE_OPTS" ]; then
  # The `-server` parameter indicates that the current JVM is activated in server or client mode (only the old 32 bit JDK is supported),
  # and is not supported in the current 64 bit JDK. 
  JVM_PERFORMANCE_OPTS="$JVM_PERFORMANCE_OPTS -Djava.awt.headless=true -XX:MaxDirectMemorySize=1G -XX:NativeMemoryTracking=off"

  if [[ "$JAVA_MAJOR_VERSION" -eq "1" ]] ; then
    if [[ "$JAVA_MINOR_VERSION" -le "8" ]] ; then # e.g: jdk1.7.x
      JVM_PERFORMANCE_OPTS="$JVM_PERFORMANCE_OPTS -XX:PermSize=256m -XX:MaxPermSize=512m"
    else
      JVM_PERFORMANCE_OPTS="$JVM_PERFORMANCE_OPTS -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m"
    fi
  fi

  # The -XX:HeapDumpOnOutOfMemoryError parameter generates a snapshot under the XX:HeapDumpPath when a memory overflow occurs in JVM,
  # and the default path is user.dir.
  JVM_PERFORMANCE_OPTS="$JVM_PERFORMANCE_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}/jvm_dump.hprof"

  # The OnOutOfMemoryError parameter allows the user to specify a callback action when the oom appears, such as the knowledge of the mail.
  #JVM_PERFORMANCE_OPTS="$JVM_PERFORMANCE_OPTS -XX:OnOutOfMemoryError=`sh $APP_HOME/example-notification.sh`"
fi

#
# --- Generic JVM options. ---
#

if [ -z "$JAVA_OPTS" ]; then
  # Add JAVA_OPTS for debugger.
  JAVA_OPTS="-Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"

  # http://netty.io/wiki/reference-counted-objects.html
  # DISABLED - disables leak detection completely. Not recommended. SIMPLE - tells if there is a leak or 
  # not for 1% of buffers. Default. ADVANCED - tells where the leaked buffer was accessed for 1% of buffers.
  # PARANOID - Same with ADVANCED except that it's for every single buffer. Useful for automated testing phase.
  # You could fail the build if the build output contains 'LEAK:'.
  #JAVA_OPTS="$JAVA_OPTS -Dio.netty.leakDetection.level=ADVANCED"
fi

#
# --- JVM GC options. ---
#

if [ -z "$GC_LOG_OPTS" ]; then # Enable default loggc args
  GC_LOG_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC"
  GC_LOG_FILE_NAME="${APP_NAME}-gc.log"
  if [[ "$JAVA_MAJOR_VERSION" -ge "9" ]] ; then
    GC_LOG_OPTS="$GC_LOG_OPTS -Xlog:gc*:file=$LOG_DIR/$GC_LOG_FILE_NAME:time,tags:filecount=100,filesize=102400"
  else
    GC_LOG_OPTS="$GC_LOG_OPTS -Xloggc:${LOG_DIR}/${GC_LOG_FILE_NAME} -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps \
-XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
  fi
fi

#
# --- APP classpath configuration. ---
#

# Repair classpath order (reordering) to solve jar packet conflicts.
#
# The jar package path that needs to be repaired is defined as an array: "CLASSPATH_ORDERED_ARR", the fix rule is that the location of the I
# element and the i+1 element is interchanged, so the length of the array CLASSPATH_ORDERED_ARR must be an even number. e.g. CLASSPATH_ORDERED_ARR=(aaa.jar 
# bbb.jar ccc.jar ddd.jar) It means changing the location of aaa.jar and bbb.jar, and changing the location between ccc.jar and ddd.jar. 
CLASSPATH_ORDERED_ARR=(
#  "phoenix-client-4.7.0-Hbase-1.1.jar" "logback-classic-1.1.11.jar"
#  "phoenix-client-4.7.0-Hbase-1.1.jar" "tomcat-embed-core-8.5.31.jar"
#  "phoenix-client-4.7.0-Hbase-1.1.jar" "jackson-core-2.8.10.jar"
#  "phoenix-client-4.7.0-Hbase-1.1.jar" "jackson-databind-2.8.11.1.jar"
#  "phoenix-client-4.7.0-Hbase-1.1.jar" "jackson-annotations-2.8.0.jar"
)
CLASSPATH_ORDERED_LEN=${#CLASSPATH_ORDERED_ARR[@]}

# Generate classpath.
#
# There is a problem when using shell to set a class path, that is, when there is a jar conflict, you need to adjust the order of
# the "CLASSPATH" path yourself, or you can use the "Maven jar plug-in" and "Maven assembler plug-in". 
# See: https://yq.aliyun.com/articles/604026?spm=a2c4e.11155435.0.0.32d633123UyO88
# Why do you want to use "sort -r" ? 
#
# The problem of the order of "java -cp" loading. See: https://www.jianshu.com/p/23e0517d76f7
# You also use the way "sort -k 1" and so on to generate the desired order.

LIB_DIR="$BASE_DIR/$LIB_DIR_NAME"
if [ -d "$LIB_DIR" ]; then
  for file in `ls -a "$LIB_DIR"/* | sort`; do
    filename=$(basename $file)
    # Using wildcard classpath
    if [[ $ENABLE_WILDCARD_CLASSPATH == "1" ]]; then
      # Does it exist in a "$CLASSPATH_ORDERED_ARR" ?
      IS_EXIST=$(echo "${CLASSPATH_ORDERED_ARR[@]}" | grep -wq "$filename" && echo "Y" || echo "N")
      # echo $IS_EXIST" filename: "$filename

      if [ "$IS_EXIST" == "Y" ]; then
        APP_CLASSPATH="$APP_CLASSPATH":"$file"
      fi
    else
      APP_CLASSPATH="$APP_CLASSPATH":"$file"
    fi
  done
fi

# Add wildcard classpath. 
if [[ $ENABLE_WILDCARD_CLASSPATH == "1" ]]; then
  APP_CLASSPATH="${APP_CLASSPATH}:${APP_HOME}/${LIB_DIR_NAME}/*"
fi

# Modify the loading order of classpath.
if [ $CLASSPATH_ORDERED_LEN -gt 1 ]; then
  echo "Repair classpath..."
  TMP_STR="__tmp_string_"

  # Check repair array string legality.
  if [ $(($CLASSPATH_ORDERED_LEN % 2)) != 0 ]; then
    echo "The length of the classpath repair path array must be even!"
    exit 0;
  fi

  # App classpath order repair.
  for((i=0;i<${#CLASSPATH_ORDERED_ARR[@]}-1;i++)) do
    APP_CLASSPATH=${APP_CLASSPATH/${CLASSPATH_ORDERED_ARR[i]}/$TMP_STR}
    APP_CLASSPATH=${APP_CLASSPATH/${CLASSPATH_ORDERED_ARR[i+1]}/${CLASSPATH_ORDERED_ARR[i]}}
    APP_CLASSPATH=${APP_CLASSPATH/$TMP_STR/${CLASSPATH_ORDERED_ARR[i+1]}}
  done;
fi

#
# --- APP options settings. ---
#
if [ -z "$APP_OPTS" ]; then
  # Add the core options arguments it supports according to different application types.
  if [ -n "$(ls ${BASE_DIR}/lib/* 2>&1|grep -E ${BASE_DIR}/lib/spring-boot)" ]; then # The spring-boot App?
    APP_OPTS="$APP_OPTS --spring.application.name=${APP_NAME}"
    APP_OPTS="$APP_OPTS --spring.profiles.active=${APP_PROFILE}"
    APP_OPTS="$APP_OPTS --server.tomcat.basedir=${DATA_DIR}"
    APP_OPTS="$APP_OPTS --logging.file.name=${LOG_DIR}/${APP_NAME}_${APP_PROFILE}.log"
  elif [ -n "$(ls ${BASE_DIR}/lib/* 2>&1|grep -E ${BASE_DIR}/lib/quarkus-core)" ]; then # The quarkus App?
    APP_OPTS="$APP_OPTS -Dquarkus.application.name=${APP_NAME}"
    APP_OPTS="$APP_OPTS -Dquarkus.log.file.path=${LOG_DIR}/${APP_NAME}_${APP_PROFILE}.log"
  fi
fi
