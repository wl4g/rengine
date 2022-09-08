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

# Current directory.
CURR_DIR="$(cd "`dirname "$0"`"/.; pwd)"

# Reference external env definition.
VAR_PATH=$CURR_DIR"/*-env.sh"
. $VAR_PATH

# Execution start.
CTRL_PATH=$CURR_DIR"/*-ctrl.sh"
$CTRL_PATH start
