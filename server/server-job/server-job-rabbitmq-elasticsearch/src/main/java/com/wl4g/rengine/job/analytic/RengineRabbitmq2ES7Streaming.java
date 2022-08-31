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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job.analytic;

import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;

import com.wl4g.rengine.job.analytic.core.elasticsearch7.RengineES7StreamingSupport;

/**
 * {@link RengineRabbitmq2ES7Streaming}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-02 v3.0.0
 * @since v3.0.0
 */
public class RengineRabbitmq2ES7Streaming extends RengineES7StreamingSupport {

    public static void main(String[] args) throws Exception {
        new RengineRabbitmq2ES7Streaming().parse(args).run();
    }

    @Override
    protected <T, S extends SourceSplit, E> Source<T, S, E> createSource() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
