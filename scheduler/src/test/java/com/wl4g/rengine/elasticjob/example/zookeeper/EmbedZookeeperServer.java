/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.rengine.elasticjob.example.zookeeper;

import static java.util.Objects.isNull;

import java.io.File;
import java.io.IOException;

import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.QuorumConfigBuilder;
import org.apache.curator.test.TestingZooKeeperServer;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link EmbedZookeeperServer}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v3.0.0
 */
@Slf4j
public final class EmbedZookeeperServer {

    // private static TestingServer testingServer;
    private static TestingZooKeeperServer testingServer;

    public synchronized static void start(final int port) {
        try {
            if (isNull(testingServer)) {
                File tmpFile = new File(String.format("target/test_zk_data/%s/", System.nanoTime()));
                // testingServer = new TestingServer(port, tmpFile);
                testingServer = new TestingZooKeeperServer(
                        new QuorumConfigBuilder(new InstanceSpec(tmpFile, port, -1, -1, true, 0)));
                testingServer.start();
            }
        } catch (Exception e) {
            log.error("Closing testing zookeeper server failure. - {}", e);
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Thread.sleep(1000L);
                    testingServer.close();
                } catch (InterruptedException | IOException e2) {
                    log.debug("Closing testing zookeeper server failure. - {}", e2.getMessage());
                }
            }));
        }
    }

}
