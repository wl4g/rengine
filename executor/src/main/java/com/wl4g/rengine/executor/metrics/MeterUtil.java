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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.metrics;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.CDI;

import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsTag;

import io.micrometer.core.instrument.Timer;
import lombok.CustomLog;

/**
 * {@link MeterUtil}
 * 
 * @author James Wong
 * @version 2022-12-28
 * @since v1.0.0
 */
@CustomLog
public class MeterUtil {

    static ExecutorMeterService meterService;

    public static void counter(
            final MetricsName metricsName,
            final String dataSourceName,
            final DataSourceType dsType,
            final String methodName) {
        try {
            getMeterService()
                    .counter(metricsName.getName(), metricsName.getHelp(), MetricsTag.DS_TYPE, dsType.name(), MetricsTag.DS_NAME,
                            dataSourceName, MetricsTag.METHOD_NAME, methodName)
                    .increment();
        } catch (Throwable e) {
            log.error(
                    format("Unable to counter meter for metricsName: '%s', dataSourceName: '%s', dsType: '%s', methodName: '%s'",
                            metricsName, dataSourceName, dsType, methodName),
                    e);
        }
    }

    public static <T> T timer(
            final MetricsName metricsName,
            final String dataSourceName,
            final DataSourceType dsType,
            final String methodName,
            final Callable<T> func) {
        final Timer timer = getMeterService().timer(metricsName.getName(), metricsName.getHelp(),
                ExecutorMeterService.DEFAULT_PERCENTILES, MetricsTag.DS_TYPE, dsType.name(), MetricsTag.DS_NAME, dataSourceName,
                MetricsTag.METHOD_NAME, methodName);
        return timer.record(() -> {
            try {
                return func.call();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public static void counter(final MetricsName metricsName, final Class<?> sdkType, final String methodName) {
        try {
            getMeterService()
                    .counter(metricsName.getName(), metricsName.getHelp(), MetricsTag.SDK_TYPE, sdkType.getSimpleName(),
                            MetricsTag.METHOD_NAME, methodName)
                    .increment();
        } catch (Throwable e) {
            log.error(format("Unable to counter meter for metricsName: '%s', sdkType: '%s', methodName: '%s'", metricsName,
                    sdkType.getSimpleName(), methodName), e);
        }
    }

    public static <T> T timer(
            final MetricsName metricsName,
            final Class<?> sdkType,
            final String methodName,
            final Callable<T> func) {
        final Timer timer = getMeterService().timer(metricsName.getName(), metricsName.getHelp(),
                ExecutorMeterService.DEFAULT_PERCENTILES, MetricsTag.SDK_TYPE, sdkType.getSimpleName(), MetricsTag.METHOD_NAME,
                methodName);
        return timer.record(() -> {
            try {
                return func.call();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    static ExecutorMeterService getMeterService() {
        if (isNull(meterService)) {
            synchronized (MeterUtil.class) {
                if (isNull(meterService)) {
                    meterService = CDI.current().select(ExecutorMeterService.class).get();
                }
            }
        }
        return meterService;
    }

}
