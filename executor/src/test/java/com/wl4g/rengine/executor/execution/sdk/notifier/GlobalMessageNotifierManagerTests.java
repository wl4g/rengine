/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
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
package com.wl4g.rengine.executor.execution.sdk.notifier;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
//import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.utility.DockerImageName;

import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.notification.dingtalk.DingtalkMessageNotifier;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.MsgKeyType;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.SampleActionCard6Param;
import com.wl4g.infra.common.notification.email.internal.EmailSenderAPI;
import com.wl4g.infra.common.serialize.JacksonUtils;
import com.wl4g.rengine.executor.meter.TestDefaultMeterSetup;
import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;
import com.wl4g.rengine.executor.util.TestDefaultRedisSetup;

/**
 * {@link GlobalMessageNotifierManagerTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v1.0.0
 */
public class GlobalMessageNotifierManagerTests {

    // see:https://www.testcontainers.org/modules/databases/mongodb/#usage-example
    static MongoDBContainer mongoDBContainer;
    static GlobalMessageNotifierManager globalMessageNotifierManager;

    // @BeforeClass // invalid?
    public static void setup() throws Exception {
        if (isNull(globalMessageNotifierManager)) {
            synchronized (GlobalMessageNotifierManagerTests.class) {
                if (isNull(globalMessageNotifierManager)) {
                    TestDefaultMeterSetup.setup();
                    globalMessageNotifierManager = new GlobalMessageNotifierManager();
                    globalMessageNotifierManager.config = TestDefaultBaseSetup.createExecutionConfig();
                    globalMessageNotifierManager.mongoRepository = TestDefaultBaseSetup.createMongoRepository();
                    globalMessageNotifierManager.redisDS = TestDefaultRedisSetup.buildRedisDataSourceDefault();
                    globalMessageNotifierManager.notifiers = asList(new EmailScriptMessageNotifier(),
                            new DingtalkScriptMessageNotifier());
                    globalMessageNotifierManager.init();
                }
            }
        }
    }

    @Test
    @RepeatedTest(2)
    public void testDingtalkNotifierSend() throws Exception {
        setup();

        final ScriptMessageNotifier dingtalkNotifier = globalMessageNotifierManager.obtain(NotifierKind.DINGTALK);
        System.out.println("dingtalkNotifier : " + dingtalkNotifier);

        // @formatter:off
        final MsgKeyType msgKey = MsgKeyType.sampleActionCard6;
        final SampleActionCard6Param msgParam = SampleActionCard6Param.builder()
                .title("（故障演练）异常告警") 
                .text("- 告警时间: 2023-01-01 01:01:01\n- 持续时间: 10m\n- 应用服务: mqttcollect\n- 集群环境: production\n- 节点 IP: 10.0.0.112\n- 节点 CPU(10s): 200%\n- 节点 Free Mem(5m): 10%\n- 节点 InNet(1m): 1234mbps\n- 节点 OutNet(1m): 1234mbps\n- 节点 IOPS(1m): 512/1501\n- 节点 Free Disks: 99GB/250GB\n- 诊断信息: <font color='#ff0000' size=3>send_kafka_fail_rate > 30%</font>\n- **[更多指标](http://grafana.example.com/123)**")
                .buttonTitle1("Restart Now")
                .buttonUrl1("https://qq.com")
                .buttonTitle2("Cancel")
                .buttonUrl2("https://qq.com")
                .build();
        System.out.println(msgParam);
        // @formatter:on

        // @formatter:off
        //final MsgKeyType msgKey = MsgKeyType.sampleActionCard5;
        //final SampleActionCard5Param msgParam = SampleActionCard5Param.builder()
        //        .title("（故障演练）异常告警")
        //        .text("- 告警时间: 2023-01-01 01:01:01\n- 持续时间: 10m\n- 应用服务: mqttcollect\n- 集群环境: production\n- 节点 IP: 10.0.0.112\n- 节点 CPU(10s): 200%\n- 节点 Free Mem(5m): 10%\n- 节点 InNet(1m): 1234mbps\n- 节点 OutNet(1m): 1234mbps\n- 节点 IOPS(1m): 512/1501\n- 节点 Free Disks: 99GB/250GB\n- 诊断信息: <font color='#ff0000' size=3>send_kafka_fail_rate > 30%</font>\n- **[更多指标](http://grafana.example.com/123)**")
        //        .actionTitle1("链接1")
        //        .actionURL1("https://qq.com")
        //        .actionTitle2("链接2")
        //        .actionURL2("https://qq.com")
        //        .actionTitle3("链接3")
        //        .actionURL3("https://qq.com")
        //        .actionTitle4("链接4")
        //        .actionURL4("https://qq.com")
        //        .actionTitle5("链接5")
        //        .actionURL5("https://qq.com")
        //        .build();
        //System.out.println(msgParam);
        // @formatter:on

        final Map<String, Object> parameter = new HashMap<>();
        parameter.put(DingtalkMessageNotifier.KEY_MSG_KEY, msgKey.name());
        parameter.put(DingtalkMessageNotifier.KEY_MSG_PARAM, toJSONString(msgParam));
        parameter.put(DingtalkMessageNotifier.KEY_OPEN_CONVERSATION_ID, "cidG+niQ3Ny\\/NwUc5KE7mANUQ==");
        parameter.put(DingtalkMessageNotifier.KEY_ROBOT_CODE, "dingbhyrzjxx6qjhjcdr");

        final Object result = dingtalkNotifier.send(parameter);
        System.out.println(toJSONString(result));
    }

    @Test
    public void testDingtalkNotifierCreateScenesGroupV2() throws Exception {
        setup();

        final DingtalkScriptMessageNotifier dingtalkNotifier = (DingtalkScriptMessageNotifier) globalMessageNotifierManager
                .obtain(NotifierKind.DINGTALK);
        System.out.println("dingtalkNotifier : " + dingtalkNotifier);

        final Map<String, Object> parameter = JacksonUtils.convertBean(CreateSceneGroupV2.builder()
                .title("测试群-01")
                .templateId("4ba6847f-b9b0-42ca-96ea-22c4ed8a3fbd")
                .ownerUserId("6165471647114842627")
                .userIds("6165471647114842627")
                .subadminIds("6165471647114842627")
                .build(), JacksonUtils.MAP_OBJECT_TYPE_REF);
        System.out.println("parameter : " + parameter);

        final Object result = dingtalkNotifier.createSceneGroupV2(parameter);

        System.out.println(toJSONString(result));
    }

    @Test
    @RepeatedTest(2)
    public void testEmailNotifierSend() throws Exception {
        setup();

        final ScriptMessageNotifier emailNotifier = globalMessageNotifierManager.obtain(NotifierKind.EMAIL);
        System.out.println("emailNotifier : " + emailNotifier);

        final Map<String, Object> parameter = new HashMap<>();
        parameter.put(EmailSenderAPI.KEY_MAIL_TYPE, EmailSenderAPI.VALUE_MAIL_MIME);
        parameter.put(EmailSenderAPI.KEY_MAIL_SUBJECT, "Testing Sender");
        parameter.put(EmailScriptMessageNotifier.KEY_MAIL_TO_USERS, "983708408@qq.com");
        parameter.put(EmailScriptMessageNotifier.KEY_MAIL_MSG,
                "This testing <b>MIME<b> message!!!</br><font color=red>It's is red font.</font>");

        final Object result = emailNotifier.send(parameter);
        System.out.println(toJSONString(result));
    }

}
