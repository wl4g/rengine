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
MAVEN_CLI_OPTS=${MAVEN_CLI_OPTS:-} # --no-transfer-progress
MAVEN_USERNAME=${MAVEN_USERNAME:-}
MAVEN_PASSWORD=${MAVEN_PASSWORD:-}
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-}
DOCKERHUB_TOKEN=${DOCKERHUB_TOKEN:-}

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

function usages(){
    echo $"
# for examples
export JAVA_HOME=/usr/local/jdk-11.0.10/ # Recommands
export MAVEN_OPTS='-Xss64m -Xms1g -Xmx12g -XX:ReservedCodeCacheSize=1g -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN' # Optional
# Required (If needed)
export MAVEN_USERNAME='myuser'
export MAVEN_PASSWORD='abc'
export MAVEN_GPG_PRIVATE_KEY='-----BEGIN PGP PRIVATE KEY BLOCK-----\n...'
export MAVEN_GPG_PASSPHRASE='abc'
export DOCKERHUB_USERNAME='myuser'
export DOCKERHUB_TOKEN='abc'

Usage: ./$(basename $0) [OPTIONS] [arg1] [arg2] ...
    version                             Print maven project POM version.
    gpg-verify                          Verifying for GPG.
    build-maven                         Build with Maven.
    build-deploy                        Build and deploy to Maven central.
    build-image                         Build component images.
                -a,--apiserver          Build image for apiserver.
                -c,--controller         Build image for controller.
                -j,--job                Build image for job.
                -e,--executor           Build image for executor.
                -E,--executor-native    Build image for executor (native).
                -A,--all                Build image for all components.
    push-image                          Push component images.
                -a,--apiserver          Push image for apiserver.
                -c,--controller         Push image for controller.
                -j,--job                Push image for job.
                -e,--executor           Push image for executor.
                -E,--executor-native    Push image for executor (native).
                -A,--all                Push image for all components.
    all                                 Build with Maven and push images for all components.
"
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

function do_push_image() {
    local image_name="$1"
    local image_tag="$2"
    local image_registry="$3"
    if [[ -z "$DOCKERHUB_USERNAME" || -z "$DOCKERHUB_TOKEN" ]]; then
        logWarn "The environment variable DOCKERHUB_USERNAME or DOCKERHUB_TOKEN is missing."; exit 1
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
        do_build_maven "-T 4C clean install"
        do_build_maven "package -f apiserver/pom.xml -Pbuild:tar:docker"
        ;;
      -c|--controller)
        do_build_maven "-T 4C clean install"
        do_build_maven "package -f controller/pom.xml -Pbuild:tar:docker"
        ;;
      -j|--job)
        do_build_maven "-T 4C clean install"
        do_build_maven "package -f job/pom.xml -Pbuild:jar:docker"
        ;;
      -e|--executor)
        do_build_maven "-T 4C clean install"
        do_build_maven "package -f executor/pom.xml -Pbuild:tar:docker"
        ;;
      -E|--executor-native)
        do_build_maven "-T 4C clean install"
        do_build_maven "package -f executor/pom.xml -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=docker"
        ;;
      -A|--all)
        do_build_maven "-T 4C clean install"
        do_build_maven "package -f apiserver/pom.xml -Pbuild:tar:docker"
        do_build_maven "package -f controller/pom.xml -Pbuild:tar:docker"
        do_build_maven "package -f job/pom.xml -Pbuild:jar:docker"
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
      -j|--job)
        do_push_image "rengine-job" "$POM_VERSION" "$3"
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
        do_push_image "rengine-job" "$POM_VERSION" "$3"
        do_push_image "rengine-executor" "$POM_VERSION" "$3"
        do_push_image "rengine-executor-native" "$POM_VERSION" "$3"
        ;;
      *)
        usages; exit 1
    esac
    ;;
  all)
    do_build_maven "-T 4C clean install"
    do_build_maven "package -f apiserver/pom.xml -Pbuild:tar:docker" &
    do_build_maven "package -f controller/pom.xml -Pbuild:tar:docker" &
    do_build_maven "package -f job/pom.xml -Pbuild:jar:docker" &
    do_build_maven "package -f executor/pom.xml -Pbuild:tar:docker" &
    wait
    do_build_maven "package -f executor/pom.xml -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=docker"

    do_build_deploy

    POM_VERSION=${POM_VERSION:-$(print_pom_version)}
    do_push_image "rengine-apiserver" "$POM_VERSION"
    do_push_image "rengine-controller" "$POM_VERSION"
    do_push_image "rengine-job" "$POM_VERSION"
    do_push_image "rengine-executor" "$POM_VERSION"
    do_push_image "rengine-executor-native" "$POM_VERSION"
    ;;
  *)
    usages; exit 1
    ;;
esac
