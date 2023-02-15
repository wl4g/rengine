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

BASE_DIR="$(cd "`dirname $0`"/../..; pwd)"
DEFAULT_MAVEN_OPTS=${MAVEN_OPTS:-"-Xss64m -Xms1g -Xmx12g -XX:ReservedCodeCacheSize=1g -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"}
MAVEN_CLI_OPTS=${MAVEN_CLI_OPTS:-"--settings .github/mvn-settings.xml"} # --no-transfer-progress
MAVEN_USERNAME=${MAVEN_USERNAME:-}
MAVEN_PASSWORD=${MAVEN_PASSWORD:-}
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-}
DOCKERHUB_TOKEN=${DOCKERHUB_TOKEN:-}

# eg1: log "error" "Failed to xxx"
# eg2: log "xxx complete!"
function log() {
  local logLevel="\033[33mINFO\033[0m "
  local logContent=$1
  if [[ $# > 1 ]]; then
    logLevel=$1
    logContent=$2
  fi
  local logMsg="[$logLevel] $(date '+%Y-%m-%d %H:%M:%S') - $logContent"
  echo -e "$logMsg"
  echo -e "$logMsg" >> /tmp/run-builder.log
}

function logDebug() {
  log "\033[37mDEBUG\033[0m" "$@"
}

function logWarn() {
  log "\033[33mWARN \033[0m" "$@"
}

function logErr() {
  log "\033[31mERROR\033[0m" "$@"
}

function usages(){
    echo $"
# for examples
export JAVA_VERSION=11 # Optional
export MAVEN_OPTS='-Xss64m -Xms1g -Xmx12g -XX:ReservedCodeCacheSize=1g -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN' # Optional
export MAVEN_USERNAME='myuser'
export MAVEN_PASSWORD='abc'
export DOCKERHUB_USERNAME='myuser'
export DOCKERHUB_TOKEN='abc'

Usage: ./$(basename $0) [OPTIONS] [arg1] [arg2] ...
    version                             Print maven project POM version.
    build-maven                         Build with Maven.
    build-deploy                        Build and deploy to Maven central.
    build-image                         Build component images.
                -a,--apiserver          Build image for apiserver.
                -c,--controller         Build image for controller.
                -e,--executor           Build image for executor.
                -E,--executor-native    Build image for executor (native).
                -A,--all                Build image for all components.
    push-image                          Push component images.
                -a,--apiserver          Push image for apiserver.
                -c,--controller         Push image for controller.
                -e,--executor           Push image for executor.
                -E,--executor-native    Push image for executor (native).
                -A,--all                Push image for all components.
    all                                 Build with Maven and push images for all components.
"
}

function print_pom_version() {
    ## see:https://cloud.tencent.com/developer/article/1476991
    MAVEN_OPTS="$DEFAULT_MAVEN_OPTS -Dorg.slf4j.simpleLogger.log.org.apache.maven.plugins.help=INFO"
    #./mvnw org.apache.maven.plugins:maven-help-plugin:3.3.0:evaluate -o -Dexpression=project.version | grep -v '[INFO]'
    POM_VERSION=$(./mvnw -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q)
    unset MAVEN_OPTS
    echo $POM_VERSION
}

function do_build_maven() {
    local build_opts=$1
    logDebug "Building for $build_opts ..."
    [ -z "$MAVEN_OPTS" ] && MAVEN_OPTS=$DEFAULT_MAVEN_OPTS
    $BASE_DIR/mvnw $MAVEN_CLI_OPTS \
    -Dmaven.repo.local=$HOME/.m2/repository \
    -DskipTests \
    -DskipITs \
    -Dgpg.skip \
    -B \
    $build_opts
}

function do_push_image() {
    local image_name="$1"
    local image_tag="$2"
    local image_registry="$3"
    if [[ -z "$DOCKERHUB_USERNAME" || -z "$DOCKERHUB_TOKEN" ]]; then
      logWarn "The environment variable DOCKERHUB_USERNAME or DOCKERHUB_TOKEN is missing."
      exit 1
    fi
    if [ -z "$image_registry" ]; then
      image_registry="docker.io"
    fi
    logDebug "Login to $image_registry ..."
    docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_TOKEN $image_registry
    logDebug "Pushing image for $image_registry/$image_name:$image_tag ..."
    docker tag wl4g/$image_name:$image_tag $image_registry/wl4g/$image_name:$image_tag
    docker push $image_registry/wl4g/$image_name:$image_tag
}

function check_maven_env() {
    if [[ -z "$MAVEN_USERNAME" || -z "$MAVEN_PASSWORD" ]]; then
      logWarn "The environment variable MAVEN_USERNAME or MAVEN_PASSWORD is missing."
      exit 1
    fi
}

# --- Main. ---
case $1 in
  version)
    print_pom_version
    ;;
  build-maven)
    do_build_maven "-T 4C clean install"
    ;;
  build-deploy)
    check_maven_env
    do_build_maven "-T 4C deploy -Prelease"
    ;;
  build-image)
    case $2 in
      -a|--apiserver)
        do_build_maven "package -f apiserver/pom.xml -Pbuild:tar:docker"
        ;;
      -c|--controller)
        do_build_maven "package -f controller/pom.xml -Pbuild:tar:docker"
        ;;
      -e|--executor)
        do_build_maven "package -f executor/pom.xml -Pbuild:tar:docker"
        ;;
      -E|--executor-native)
        do_build_maven "package -f executor/pom.xml -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=docker"
        ;;
      -A,--all)
        do_build_maven "package -f apiserver/pom.xml -Pbuild:tar:docker"
        do_build_maven "package -f controller/pom.xml -Pbuild:tar:docker"
        do_build_maven "package -f executor/pom.xml -Pbuild:tar:docker"
        do_build_maven "package -f executor/pom.xml -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=docker"
        ;;
      *)
        usages; exit 1
    esac
    ;;
  push-image)
    POM_VERSION=${POM_VERSION:-$(print_pom_version)}
    case $2 in
      -a|--apiserver)
        do_push_image "rengine-apiserver" "$POM_VERSION" "$3"
        ;;
      -c|--controller)
        do_push_image "rengine-controller" "$POM_VERSION" "$3"
        ;;
      -e|--executor)
        do_push_image "rengine-executor" "$POM_VERSION" "$3"
        ;;
      -E|--executor-native)
        do_push_image "rengine-executor-native" "$POM_VERSION" "$3"
        ;;
      -A|--all)
        do_push_image "rengine-apiserver" "$POM_VERSION" "$3"
        do_push_image "rengine-controller" "$POM_VERSION" "$3"
        do_push_image "rengine-executor" "$POM_VERSION" "$3"
        do_push_image "rengine-executor-native" "$POM_VERSION" "$3"
        ;;
      *)
        usages; exit 1
    esac
    ;;
  all)
    do_build_maven "package -f apiserver/pom.xml -Pbuild:tar:docker" &
    do_build_maven "package -f controller/pom.xml -Pbuild:tar:docker" &
    do_build_maven "package -f executor/pom.xml -Pbuild:tar:docker" &
    wait
    do_build_maven "package -f executor/pom.xml -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=docker"
    POM_VERSION=${POM_VERSION:-$(print_pom_version)}
    do_push_image "rengine-apiserver" "$POM_VERSION"
    do_push_image "rengine-controller" "$POM_VERSION"
    do_push_image "rengine-executor" "$POM_VERSION"
    do_push_image "rengine-executor-native" "$POM_VERSION"
    ;;
  *)
    usages; exit 1
    ;;
esac