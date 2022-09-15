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
package com.wl4g.rengine.job.analytic.core.elasticsearch7;

import org.apache.commons.cli.ParseException;
import org.apache.flink.streaming.api.datastream.DataStreamSource;

import com.wl4g.rengine.job.analytic.core.RengineFlinkStreamingBase;
import com.wl4g.rengine.job.analytic.core.model.RengineEventAnalyticalModel;

import lombok.Getter;

/**
 * {@link RengineES7StreamingSupport}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v3.0.0
 */
@Getter
public abstract class RengineES7StreamingSupport extends RengineFlinkStreamingBase {

    protected RengineES7StreamingSupport() {
        super();
        // Sink options.
        // TODO
    }

    @Override
    protected RengineFlinkStreamingBase parse(String[] args) throws ParseException {
        super.parse(args);
        // Sink options.
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    protected RengineFlinkStreamingBase customStream(DataStreamSource<RengineEventAnalyticalModel> dataStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
