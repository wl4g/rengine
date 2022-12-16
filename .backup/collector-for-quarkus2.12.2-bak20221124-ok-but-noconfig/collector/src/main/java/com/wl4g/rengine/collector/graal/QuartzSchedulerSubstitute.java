package com.wl4g.rengine.collector.graal;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.sql.ResultSet;

import org.quartz.core.RemotableQuartzScheduler;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

// see:https://foivos.zakkak.net/tutorials/working-with-randoms-native-images/
// see:https://github1s.com/quarkusio/quarkus/blob/2.12.2.Final/extensions/quartz/runtime/src/main/java/io/quarkus/quartz/runtime/graal/QuartzSubstitutions.java#L10
@TargetClass(className = "org.quartz.core.QuartzScheduler")
final class QuartzSchedulerSubstitute {

    @Substitute
    private void bind() throws RemoteException {
    }

    @Substitute
    private void unBind() throws RemoteException {
    }

    @Substitute
    private void registerJMX() throws Exception {
    }

    @Substitute
    private void unregisterJMX() throws Exception {
    }
}

@TargetClass(className = "org.quartz.impl.RemoteScheduler")
final class Target_org_quartz_impl_RemoteScheduler {
    @Substitute
    protected RemotableQuartzScheduler getRemoteScheduler() {
        return null;
    }
}

@TargetClass(StdJDBCDelegate.class)
final class Target_org_quartz_impl_jdbc_jobstore_StdJDBCDelegate {

    /**
     * Activate the usage of {@link java.util.Properties} to avoid Object
     * serialization which is not supported by GraalVM - see
     * https://github.com/oracle/graal/issues/460
     *
     * @return true
     */
    @Substitute
    protected boolean canUseProperties() {
        return true;
    }

    @Substitute
    protected ByteArrayOutputStream serializeObject(Object obj) {
        // should not reach here
        throw new IllegalStateException("Object serialization not supported.");
    }

    @Substitute
    protected Object getObjectFromBlob(ResultSet rs, String colName) {
        // should not reach here
        throw new IllegalStateException("Object serialization not supported.");
    }
}

final class QuartzSubstitutions {
}
