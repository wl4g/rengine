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
# If run.sh is a soft link, it is considered to be $PROJECT_HOME/run.sh, no need to call back the path.
if [ -L "`dirname $0`/run.sh" ]; then
  BASE_DIR="$(cd "`dirname $0`"; pwd)"
fi
DEFAULT_MAVEN_OPTS=${MAVEN_OPTS:-"-Xss64m -Xms1g -Xmx12g -XX:ReservedCodeCacheSize=1g -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"}
MAVEN_CLI_OPTS=${MAVEN_CLI_OPTS:-} # --no-transfer-progress
MAVEN_USERNAME=${MAVEN_USERNAME:-}
MAVEN_PASSWORD=${MAVEN_PASSWORD:-}
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-}
DOCKERHUB_TOKEN=${DOCKERHUB_TOKEN:-}
NODE_IP="$(ip a | grep -E '^[0-9]+: (em|eno|enp|ens|eth|wlp)+[0-9]' -A2 | grep inet | awk -F ' ' '{print $2}' | cut -f1 -d/ | xargs echo)"

# eg1: log "error" "Failed to xxx"
# eg2: log "xxx complete!"
function log() {
  local logLevel=" \033[33mINFO\033[0m"
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

function usages() {
    echo $"Rengine devel build tests and release & deploy chore Tools.

# for examples
export JAVA_HOME=/usr/local/jdk-11.0.10/ # Recommands
export MAVEN_OPTS='-Xss64m -Xms1g -Xmx12g -XX:ReservedCodeCacheSize=1g -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN' # Optional
# requirements (if necessary)
export MAVEN_USERNAME='myuser'
export MAVEN_PASSWORD='abc'
export MAVEN_GPG_PRIVATE_KEY='-----BEGIN PGP PRIVATE KEY BLOCK-----\n...'
export MAVEN_GPG_PASSPHRASE='abc'
export DOCKERHUB_REGISTRY='docker.io/wl4g' # eg: docker.io/wl4g(default), ghcr.io/wl4g, registry.cn-shenzhen.aliyuncs.com/wl4g, ccr.ccs.tencentyun.com/wl4g
export DOCKERHUB_USERNAME='myuser'
export DOCKERHUB_TOKEN='abc'

Usage: ./$(basename $0) [OPTIONS] [arg1] [arg2] ...
    version                                         Print maven project POM version.
    gpg-verify                                      Verifying for GPG.
    build-maven                                     Build with Maven.
    build-deploy                                    Build and deploy to Maven central.
    build-image                                     Build component images.
                        -a,--apiserver              Build image for apiserver.
                           --skip-build             Skip recompile build before building image.
                        -c,--controller             Build image for controller.
                           --skip-build             Skip recompile build before building image.
                        -j,--job                    Build image for job.
                           --skip-build             Skip recompile build before building image.
                        -e,--executor               Build image for executor.
                           --skip-build             Skip recompile build before building image.
                        -E,--executor-native        Build image for executor (native).
                           --skip-build             Skip recompile build before building image.
                        -u,--ui                     Build image for UI.
                           --skip-build             Skip recompile build before building image.
                        -d,--initdb                 Build image for initdb.
                           --skip-build             Skip recompile build before building image.
                        -A,--all                    Build image for all components (but excludes the executor-native).
                           --skip-build             Skip recompile build before building image.
    push-image                                      Push component images.
                        -a,--apiserver              Push image for apiserver.
                        -c,--controller             Push image for controller.
                        -j,--job                    Push image for job.
                        -e,--executor               Push image for executor.
                        -E,--executor-native        Push image for executor (native).
                        -u,--ui                     Push image for UI.
                        -d,--initdb                 Push image for initdb.
                        -A,--all                    Push image for all components.
    build-push                                      Build with Maven and push images for all components.
    prune-image                                     Prune unused all images. (tag=none)
    deploy-standalone                               Deploy all services with docker standalone mode.
                        -S,--status                 Display status for all services.
                        -U,--up                     Startup to all services.
                           --prune-all-volumes      Remove all data volumes before per initial deploy. Note: be careful!
                        -D,--down                   Shuwdown to all services.
                           --prune-all-volumes      Remove all data volumes after per destory deploy. Note: be careful!
"
}

function check_for_java_version() {
    # Which java to use
    export JAVA=$([ -z "$JAVA_HOME" ] && echo "java" || echo "$JAVA_HOME/bin/java")

    # the first segment of the version number, which is '1' for releases before Java 9
    # it then becomes '9', '10' etc.
    # e.g: openjdk version "11.0.10" 2021-01-19
    export JAVA_MAJOR_VERSION=$([ -z "$JAVA_MAJOR_VERSION" ] && echo $($JAVA -version 2>&1 | sed -E -n 's/.* version \"(.+)\.(.+)\.(.+)".*/\1/p') || echo "$JAVA_MAJOR_VERSION")
    export JAVA_MINOR_VERSION=$([ -z "$JAVA_MINOR_VERSION" ] && echo $($JAVA -version 2>&1 | sed -E -n 's/.* version \"(.+)\.(.+)\.(.+)".*/\2/p') || echo "$JAVA_MINOR_VERSION")
    export JAVA_PATCH_VERSION=$([ -z "$JAVA_PATCH_VERSION" ] && echo $($JAVA -version 2>&1 | sed -E -n 's/.* version \"(.+)\.(.+)\.(.+)".*/\3/p') || echo "$JAVA_PATCH_VERSION")

    if [[ ! "$JAVA_MAJOR_VERSION" -ge 11 ]] ; then
        log "No supported JAVA version, major version must >= 11"; exit 1
    else
        log "Using JAVA for $JAVA"
    fi
}

function check_for_npm_version() {
    # Which npm to use
    export NPM=$([ -z "$NODE_HOME" ] && echo "npm" || echo "$NODE_HOME/bin/npm")

    # the first segment of the version number.
    export NPM_MAJOR_VERSION=$([ -z "$NPM_MAJOR_VERSION" ] && echo $($NPM --version 2>&1 | sed -E -n 's/([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})/\1/p') || echo "$NPM_MAJOR_VERSION")
    export NPM_MINOR_VERSION=$([ -z "$NPM_MINOR_VERSION" ] && echo $($NPM --version 2>&1 | sed -E -n 's/([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})/\2/p') || echo "$NPM_MINOR_VERSION")
    export NPM_PATCH_VERSION=$([ -z "$NPM_PATCH_VERSION" ] && echo $($NPM --version 2>&1 | sed -E -n 's/([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})/\3/p') || echo "$NPM_PATCH_VERSION")

    if [[ ! "$NPM_MAJOR_VERSION" -ge 6 ]] ; then
        log "No supported NPM version, major version must >= 6"; exit 1
    else
        log "Using NPM for $NPM"
    fi
}

function print_pom_version() {
    # see:https://cloud.tencent.com/developer/article/1476991
    MAVEN_OPTS="$DEFAULT_MAVEN_OPTS -Dorg.slf4j.simpleLogger.log.org.apache.maven.plugins.help=INFO"
    #${BASE_DIR}/mvnw org.apache.maven.plugins:maven-help-plugin:3.3.0:evaluate -o -Dexpression=project.version | grep -v '[INFO]'
    POM_VERSION=$(${BASE_DIR}/mvnw -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q)
    unset MAVEN_OPTS
    echo $POM_VERSION
}

function do_build_maven() {
    check_for_java_version

    local build_opts=$1
    logDebug "Building for $build_opts ..."

    [ -z "$MAVEN_OPTS" ] && MAVEN_OPTS=$DEFAULT_MAVEN_OPTS
    $BASE_DIR/mvnw \
    $MAVEN_CLI_OPTS \
    -Dmaven.repo.local=$HOME/.m2/repository \
    -Dmaven.test.skip=true \
    -DskipTests \
    -DskipITs \
    -Dgpg.skip \
    -B \
    $build_opts
}

function do_build_npm() {
    check_for_npm_version

    local project_dir=$1
    logDebug "Building for $project_dir ..."

    cd ${project_dir}
    $NPM install && $NPM run build
    cd -
}

function do_configure_gpg() {
    log "Configuring for GPG keys ..."

    # Check for supported GPG version.
    gpg_version=$(gpg --version | head -1 | grep -iEo '(([0-9]+)\.([0-9]+)\.([0-9]+))') # eg: 2.2.19
    gpg_version_major=$(echo $gpg_version | awk -F '.' '{print $1}')
    gpg_version_minor=$(echo $gpg_version | awk -F '.' '{print $2}')
    gpg_version_revision=$(echo $gpg_version | awk -F '.' '{print $3}')
    if [[ ! ("$gpg_version_major" -ge 2 && "$gpg_version_minor" -ge 1) ]]; then
      logErr "The GPG version must >= $gpg_version_major.$gpg_version_minor.x"; exit 1
    fi

    # Check for GPG keys. (Skip if already generated)
    if [[ ! -f "$HOME/.gnupg/pubring.kbx" ]]; then
        if [[ -z "$MAVEN_GPG_PRIVATE_KEY" ]]; then
            logErr "The environment variable MAVEN_GPG_PRIVATE_KEY is missing."; exit 1
        fi

        \rm -rf ~/.gnupg/; mkdir -p ~/.gnupg/private-keys-v1.d/; chmod -R 700 ~/.gnupg/
        echo -n "$MAVEN_GPG_PRIVATE_KEY" > /tmp/private.key

        #logDebug "----- Print GPG secret key (debug) -----"
        #cat /tmp/private.key

        # FIXED:https://github.com/keybase/keybase-issues/issues/2798#issue-205008630
        #export GPG_TTY=$(tty) # Notice: github action the VM instance no tty.

        # FIXED:https://bbs.archlinux.org/viewtopic.php?pid=1691978#p1691978
        # FIXED:https://github.com/nodejs/docker-node/issues/922
        # Note that since Version 2.0 this passphrase is only used if the option --batch has also
        # been given. Since Version 2.1 the --pinentry-mode also needs to be set to loopback.
        # see:https://www.gnupg.org/documentation/manuals/gnupg/GPG-Esoteric-Options.html#index-allow_002dsecret_002dkey_002dimport
        gpg2 -v --pinentry-mode loopback --batch --secret-keyring ~/.gnupg/secring.gpg --import /tmp/private.key

        logDebug "Cleanup to /tmp/private.key ..."
        \rm -rf /tmp/private.key
        ls -al ~/.gnupg/

        logDebug "----- Imported list of GPG secret keys -----"
        gpg2 --list-keys
        gpg2 --list-secret-keys
    fi

    # Notice: Test signing should be performed first to ensure that the gpg-agent service has been 
    # pre-started (gpg-agent --homedir /root/.gnupg --use-standard-socket --daemon), otherwise
    # an error may be reported : 'gpg: signing failed: Inappropriate ioctl for device'
    if [[ -z "$MAVEN_GPG_PASSPHRASE" ]]; then
        logErr "The environment variable MAVEN_GPG_PASSPHRASE is missing."; exit 1
    fi
    logDebug "Prepare verifying the GPG signing ..."
    echo "test" | gpg2 -v --pinentry-mode loopback --passphrase $MAVEN_GPG_PASSPHRASE --clear-sign
}

function do_build_deploy() {
    mkdir -p ~/.m2/; cp ./.github/settings-security.xml ~/.m2/

    # see:https://blogs.wl4g.com/archives/56
    # see:https://central.sonatype.org/publish/requirements/gpg/#distributing-your-public-key
    # see:https://stackoverflow.com/questions/61096521/how-to-use-gpg-key-in-github-actions
    # or using:https://github.com/actions/setup-java/tree/v1.4.3#publishing-using-apache-maven
    do_configure_gpg

    do_build_maven "--settings $BASE_DIR/.github/mvn-settings.xml -T 4C deploy -Prelease"
}

function do_dl_serve_start() {
    local python3_cmd=$(which python3)
    [ -z "$python3_cmd" ] && logErr "The python3 not installed yet" && exit 1 || echo -n

    do_dl_serve_stop

    set +e
    nohup $python3_cmd -m http.server --directory ${BASE_DIR} 13337 >/dev/null 2>&1 &
    [ $? -ne 0 ] && echo "Could't to start local DL serve." && exit -1 || echo
    set -e
}

function do_dl_serve_stop() {
    local python3_cmd=$(which python3)
    [ -z "$python3_cmd" ] && logErr "The python3 not installed yet" && exit 1 || echo -n

    set +e
    ps -ef | grep $python3_cmd | grep ${BASE_DIR} | grep 13337 | cut -c 9-16 | xargs kill -9 >/dev/null 2>&1
    #[ $? -ne 0 ] && echo "Failed to stop local DL serve." || echo
    set -e
}

function do_build_image_with_springboot() {
  local app_name=$1
  local app_version=$2
  local app_mainclass=$3
  local build_file=$4
  local assets_file=$5

  log "Docker building for $app_name:$app_version ..."
  docker build --no-cache -t wl4g/rengine-${app_name}:${app_version} -f $BASE_DIR/tools/build/docker/$build_file \
    --build-arg DL_URI="http://$NODE_IP:13337/${assets_file}" \
    --build-arg APP_NAME=${app_name} \
    --build-arg APP_VERSION=${app_version} \
    --build-arg APP_MAINCLASS=${app_mainclass} .
}

function do_build_image_with_npm() {
    local app_name=$1
    local app_version=$2
    local build_file=$3
    log "Docker building for $app_name:$app_version ..."

    docker build --no-cache -t wl4g/rengine-${app_name}:${app_version} -f $build_file \
      --build-arg REPO_NAME=rengine-${app_name} \
      --build-arg BUILD_TIME=$(date +'%Y%m%dT%H%M%S') \
      --build-arg COMMIT_ID=$(git log | head -1 | awk -F ' ' '{print $2}' | cut -c 1-12) \
      ${BASE_DIR}/rengine-${app_name}
}

function do_build_initdb() {
    POM_VERSION=${POM_VERSION:-$(print_pom_version)}
    log "Building rengine initdb image for ${POM_VERSION}"

    docker build --no-cache -t wl4g/rengine-initdb:${POM_VERSION} \
        --build-arg REPO_NAME=$(print_pom_version) \
        --build-arg BUILD_TIME=$(date +'%Y%m%dT%H%M%S') \
        --build-arg COMMIT_ID=$(git log | head -1 | awk -F ' ' '{print $2}' | cut -c 1-12) \
        --build-arg APP_VERSION=${POM_VERSION} \
        -f ${BASE_DIR}/tools/build/docker/Dockerfile.initdb ${BASE_DIR}
}

function do_push_image() {
    POM_VERSION=${POM_VERSION:-$(print_pom_version)}
    local image_registry="$DOCKERHUB_REGISTRY"
    local image_name="$1"
    local image_tag="$POM_VERSION"

    if [ -z "$image_registry" ]; then
        image_registry="docker.io/wl4g"
    fi
    if [ "$(docker login >/dev/null 2>&1; echo $?)" -ne 0 ]; then
        if [[ -z "$DOCKERHUB_USERNAME" || -z "$DOCKERHUB_TOKEN" ]]; then
            logWarn "The environment variable DOCKERHUB_USERNAME or DOCKERHUB_TOKEN is missing."; exit 1
        else
            logDebug "Login to $image_registry ..."
            docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_TOKEN $image_registry
        fi
    else
        logDebug "Already login docker hub."
    fi

    logDebug "Pushing image to $image_registry/$image_name:$image_tag ..."
    docker tag wl4g/$image_name:$image_tag $image_registry/$image_name:$image_tag
    docker push $image_registry/$image_name:$image_tag
    
}

function do_prune_image() {
    local prune_images=$(docker images | grep none | awk -F ' ' '{print $3}')
    for pi in `echo $prune_images`; do
        echo "Removing image for $pi "
        docker rmi -f $pi
    done
}

function do_run_standalone() {
    local compose_args="$1"
    local prune_args="$2"
    [ -z "$compose_args" ] && logErr "docker compose args is missing." && exit 1 || echo -n

    local compose_cmd=$(which docker-compose)
    [ -z "$compose_cmd" ] && logErr "The docker-compose not installed yet" && exit 1 || echo -n

    ## Compose environment.
    echo "BASE_DIR=${BASE_DIR}" > /tmp/.env
    echo "NODE_IP=${NODE_IP}" >> /tmp/.env

    ## Compose file.
    local compose_file="${BASE_DIR}/tools/deploy/compose/docker-compose.yml"
    if [ ! -f "$compose_file" ]; then
        local remote_compose_file="https://raw.githubusercontent.com/wl4g/rengine/master/tools/deploy/compose/docker-compose.yml"
        log "Downloading compose file from $remote_compose_file ..."
        curl -k -o $compose_file $remote_compose_file
    fi

    ## Before the initial deploy prune.
    if [[ "$compose_args" == up* && "$prune_args" == "--prune-all-volumes" ]]; then
        log "Pruning a previously deployed all volumes..."
        do_prune_all_volumes_with_run_standalone $compose_cmd $compose_file
    fi

    set +e
    $compose_cmd --env-file /tmp/.env -f ${compose_file} $compose_args
    set -e

    ## After the destory prune.
    if [[ "$compose_args" == down* && "$prune_args" == "--prune-all-volumes" ]]; then
        log "Pruning deployed all volumes..."
        do_prune_all_volumes_with_run_standalone $compose_cmd $compose_file
    fi
}

function do_prune_all_volumes_with_run_standalone() {
    local compose_cmd="$1"
    local compose_file="$2"
    [ -z "$compose_cmd" ] && logErr "The docker-compose not installed yet" && exit 1 || echo -n
    [ -z "$compose_file" ] && logErr "The docker-compose yaml is missing" && exit 1 || echo -n

    ## Remove the volumes only if it is the first deploy.
    if [ -z "$($compose_cmd -f $compose_file ls | grep rengine)" ]; then
        set +e
        docker volume rm rengine_zookeeper_data 2>/dev/null
        docker volume rm rengine_kafka_data 2>/dev/null
        docker volume rm rengine_minio_data 2>/dev/null
        docker volume rm rengine_mongodb_data 2>/dev/null
        docker volume rm rengine_redis_data_0 2>/dev/null
        docker volume rm rengine_redis_data_1 2>/dev/null
        docker volume rm rengine_redis_data_2 2>/dev/null
        docker volume rm rengine_redis_data_3 2>/dev/null
        docker volume rm rengine_redis_data_4 2>/dev/null
        docker volume rm rengine_redis_data_5 2>/dev/null
        docker volume rm rengine_script_log 2>/dev/null
        docker volume rm rengine_script_rootfs 2>/dev/null
        docker volume rm rengine_script_works 2>/dev/null
        docker volume rm rengine_hbase_data 2>/dev/null
        set -e
    else
        logErr "Unable to remove data volumes, please shutdown all containers before, Or remove the arg '--prune-all-volumes'"
        exit 1
    fi
}

# --- Main. ---
case $1 in
  version)
    print_pom_version
    ;;
  gpg-verify)
    do_configure_gpg
    ;;
  build-maven)
    do_build_maven "-T 4C clean install"
    ;;
  build-deploy)
    do_build_deploy
    ;;
  build-image)
    case $2 in
      -a|--apiserver)
        ## First of all, it should be built in full to prevent the dependent modules from being updated.
        if [ "$3" != "--skip-build" ]; then
            do_build_maven "-T 4C clean install"
        fi

        ## Method 1:
        ## Build the image directly using the maven plugin. (Notice: The image size is large, there is no way, because the COPY command must be executed only once)
        #do_build_maven "install -f ${BASE_DIR}/apiserver/pom.xml -Pbuild:tar:docker"

        ## Method 2:
        ## Do not use the COPY command, open the local file service and use curl to download in the container to prevent new layers.
        do_dl_serve_start
        do_build_image_with_springboot apiserver $(print_pom_version) com.wl4g.RengineApiServer Dockerfile.springtar apiserver/target/apiserver-1.0.0-bin.tar
        do_dl_serve_stop
        ;;
      -c|--controller)
        ## First of all, it should be built in full to prevent the dependent modules from being updated.
        if [ "$3" != "--skip-build" ]; then
            do_build_maven "-T 4C clean install"
        fi

        ## Method 1:
        ## Build the image directly using the maven plugin. (Notice: The image size is large, there is no way, because the COPY command must be executed only once)
        #do_build_maven "install -f ${BASE_DIR}/controller/pom.xml -Pbuild:tar:docker"

        ## Method 2:
        ## Do not use the COPY command, open the local file service and use curl to download in the container to prevent new layers.
        do_dl_serve_start
        do_build_image_with_springboot controller $(print_pom_version) com.wl4g.RengineController Dockerfile.springtar controller/target/controller-1.0.0-bin.tar
        do_dl_serve_stop
        ;;
      -j|--job)
        if [ "$3" != "--skip-build" ]; then
            do_build_maven "-T 4C clean install"
        fi
        do_build_maven "install -f ${BASE_DIR}/job/pom.xml -Pbuild:docker"
        ;;
      -e|--executor)
        ## First of all, it should be built in full to prevent the dependent modules from being updated.
        if [ "$3" != "--skip-build" ]; then
            do_build_maven "-T 4C clean install"
        fi

        docker build --no-cache -t wl4g/rengine-executor:$(print_pom_version) -f ${BASE_DIR}/tools/build/docker/Dockerfile.quarkustar .
        ;;
      -E|--executor-native)
        ## First of all, it should be built in full to prevent the dependent modules from being updated.
        if [ "$3" != "--skip-build" ]; then
            do_build_maven "-T 4C clean install"
        fi

        if [ ! -f "${BASE_DIR}/executor/target/executor-native" ]; then
            log "Building executor native image ..."
            ${BASE_DIR}/mvnw install -f ${BASE_DIR}/executor/pom.xml \
                -Dmaven.test.skip=true \
                -DskipTests \
                -Dnative \
                -Dquarkus.native.container-build=true \
                -Dquarkus.native.container-runtime=docker
        fi

        log "Building executor native docker image ..."
        cd ${BASE_DIR}/executor
        docker build --no-cache -t wl4g/rengine-executor-native:$(print_pom_version) -f ${BASE_DIR}/tools/build/docker/Dockerfile.quarkusnative .
        cd ..
        ;;
      -u|--ui)
        ## First of all, it should be built in full to prevent the dependent modules from being updated.
        if [ "$3" != "--skip-build" ]; then
            do_build_npm "${BASE_DIR}/rengine-ui"
        fi
        do_build_image_with_npm ui $(print_pom_version) "${BASE_DIR}/rengine-ui/tools/build/docker/Dockerfile.vue"
        ;;
      -d|--initdb)
        do_build_initdb
        ;;
      -A|--all)
        POM_VERSION=${POM_VERSION:-$(print_pom_version)}
        if [ "$3" != "--skip-build" ]; then
            do_build_maven "-T 4C clean install" &
            do_build_npm "${BASE_DIR}/rengine-ui" &
            wait
        fi

        do_dl_serve_start
        do_build_image_with_springboot apiserver ${POM_VERSION} com.wl4g.RengineApiServer Dockerfile.springtar apiserver/target/apiserver-1.0.0-bin.tar &
        do_build_image_with_springboot controller ${POM_VERSION} com.wl4g.RengineController Dockerfile.springtar controller/target/controller-1.0.0-bin.tar &
        do_build_maven "install -f ${BASE_DIR}/job/pom.xml -Pbuild:docker" &
        wait
        do_dl_serve_stop

        docker build --no-cache -t wl4g/rengine-executor:${POM_VERSION} -f ${BASE_DIR}/tools/build/docker/Dockerfile.quarkustar

        ### Not enabled for now, because it usually fails due to insufficient resources on the build machine. To build a native image, you should use the '-E' option alone.
        #${BASE_DIR}/mvnw install -f ${BASE_DIR}/executor/pom.xml \
        #    -Dmaven.test.skip=true \
        #    -DskipTests \
        #    -Dnative \
        #    -Dquarkus.native.container-build=true \
        #    -Dquarkus.native.container-runtime=docker
        #
        #docker build --no-cache -t wl4g/rengine-executor-native:${POM_VERSION} -f ${BASE_DIR}/tools/build/docker/Dockerfile.quarkusnative

        do_build_image_with_npm ui $(print_pom_version) "${BASE_DIR}/rengine-ui/tools/build/docker/Dockerfile.vue"

        do_build_initdb
        ;;
      *)
        usages; exit 1
    esac
    ;;
  push-image)
    case $2 in
      -a|--apiserver)
        do_push_image "rengine-apiserver"
        ;;
      -c|--controller)
        do_push_image "rengine-controller"
        ;;
      -j|--job)
        do_push_image "rengine-job"
        ;;
      -e|--executor)
        do_push_image "rengine-executor"
        ;;
      -E|--executor-native)
        do_push_image "rengine-executor-native"
        ;;
      -u|--ui)
        do_push_image "rengine-ui"
        ;;
      -d|--initdb)
        do_push_image "rengine-initdb"
        ;;
      -A|--all)
        do_push_image "rengine-apiserver" &
        do_push_image "rengine-controller" &
        do_push_image "rengine-job" &
        do_push_image "rengine-executor" &
        do_push_image "rengine-executor-native" &
        do_push_image "rengine-ui" &
        do_push_image "rengine-initdb" &
        wait
        ;;
      *)
        usages; exit 1
    esac
    ;;
  build-push)
    POM_VERSION=${POM_VERSION:-$(print_pom_version)}
    do_build_maven "-T 4C clean install"
    do_build_deploy

    do_build_maven "install -f ${BASE_DIR}/apiserver/pom.xml -Pbuild:tar:docker" &
    do_build_maven "install -f ${BASE_DIR}/controller/pom.xml -Pbuild:tar:docker" &
    do_build_maven "install -f ${BASE_DIR}/job/pom.xml -Pbuild:docker" &
    docker build --no-cache -t wl4g/rengine-executor:${POM_VERSION} -f ${BASE_DIR}/tools/build/docker/Dockerfile.quarkustar &
    wait

    ### Not enabled for now, because it usually fails due to insufficient resources on the build machine. To build a native image, you should use the '-E' option alone.
    #${BASE_DIR}/mvnw install -f ${BASE_DIR}/executor/pom.xml \
    #    -Dmaven.test.skip=true \
    #    -DskipTests \
    #    -Dnative \
    #    -Dquarkus.native.container-build=true \
    #    -Dquarkus.native.container-runtime=docker
    #
    #docker build --no-cache -t wl4g/rengine-executor-native:${POM_VERSION} -f ${BASE_DIR}/tools/build/docker/Dockerfile.quarkusnative

    do_build_image_with_npm ui ${POM_VERSION} "${BASE_DIR}/rengine-ui/tools/build/docker/Dockerfile.vue"

    do_build_initdb

    do_push_image "rengine-apiserver"
    do_push_image "rengine-controller"
    do_push_image "rengine-job"
    do_push_image "rengine-executor"
    do_push_image "rengine-executor-native"
    do_push_image "rengine-ui"
    do_push_image "rengine-initdb"
    ;;
  prune-image)
    do_prune_image
    ;;
  deploy-standalone)
    case $2 in
      -S|--status)
        docker ps --format "table {{.ID}} {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}" | grep rengine_
        ;;
      -U|--up)
        do_run_standalone "up -d" "$3"
        ;;
      -D|--down)
        do_run_standalone "down" "$3"
        ;;
      *)
        usages; exit 1
    esac
    ;;
  *)
    usages; exit 1
    ;;
esac
