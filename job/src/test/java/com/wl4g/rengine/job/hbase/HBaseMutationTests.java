/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job.hbase;

import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static com.wl4g.infra.common.lang.StringUtils2.getBytes;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.NamespaceExistException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.junit.Test;

/**
 * {@link HBaseMutationTests}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @date 2022-06-08 v3.0.0
 * @since v1.0.0
 */
public class HBaseMutationTests {

    @Test
    public void testNativeHBasePutMutations() throws Exception {
        String dateTime = DateFormatUtils.format(currentTimeMillis(), "SSSssmmHHddMMyy");
        Put put = new Put(getBytes(dateTime + "," + "user"));
        put.addColumn(getBytes("f1"), getBytes("name"), getBytes("mary"));
        put.addColumn(getBytes("f1"), getBytes("age"), getBytes("18"));
        put.addColumn(getBytes("f1"), getBytes("sex"), getBytes("woman"));

        String hbaseZkAddrs = "127.0.0.1:2181";
        Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.CLIENT_ZOOKEEPER_QUORUM, hbaseZkAddrs, getClass().getSimpleName());
        conf.set(HConstants.ZOOKEEPER_QUORUM, hbaseZkAddrs, getClass().getSimpleName());
        conf.set("hbase.defaults.for.version.skip", "false");

        System.out.println(format("[TEST] Connecting to HBase for %s ...", hbaseZkAddrs));
        try (Connection conn = ConnectionFactory.createConnection(conf);) {

            // Create test table.
            String hTableNamespace = "testdb";
            String hTableName = "t_test";

            Admin admin = conn.getAdmin();
            try {
                admin.createNamespace(NamespaceDescriptor.create(hTableNamespace).build());
            } catch (NamespaceExistException e) {
                System.out.println(format("[TEST] Found existing htable namespace for '%s'", hTableNamespace));
            }
            TableName table = TableName.valueOf(hTableNamespace, hTableName);
            if (!admin.tableExists(table)) {
                TableDescriptor tabDesc = TableDescriptorBuilder.newBuilder(table)
                        .setColumnFamily(ColumnFamilyDescriptorBuilder.of("f1"))
                        // .setCompactionEnabled(true)
                        // .setMergeEnabled(true)
                        // .setSplitEnabled(true)
                        .build();
                admin.createTable(tabDesc);
            } else {
                System.out.println(format("[TEST] Found that HTable: %s already existing.", table));
            }

            // Write test data to table.
            BufferedMutatorParams params = new BufferedMutatorParams(table);
            params.writeBufferSize(1);
            BufferedMutator mutator = conn.getBufferedMutator(params);
            mutator.mutate(singletonList(put));

            System.out.println("[TEST] flushing ...");
            mutator.flush();
            System.out.println("[TEST] closing ...");
            mutator.close();
        }

    }

}
