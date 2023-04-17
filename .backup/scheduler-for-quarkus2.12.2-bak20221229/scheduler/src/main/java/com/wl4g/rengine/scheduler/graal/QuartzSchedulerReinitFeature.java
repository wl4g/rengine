package com.wl4g.rengine.scheduler.graal;
//package com.wl4g.rengine.scheduler.graal;
//
//import org.graalvm.nativeimage.ImageSingletons;
//import org.graalvm.nativeimage.hosted.Feature;
//import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;
//
///**
// * {@link QuartzSchedulerReinitFeature}
// * 
// * Usages: -Dquarkus.native.additional-build-args="--features=com.wl4g.rengine.scheduler.graal.QuartzSchedulerReinitFeature"
// * 
// * @author James Wong
// * @date 2022-09-25
// * @since v1.0.0
// * @see io.quarkus.runtime.graal.ResourcesFeature
// * @see see:https://foivos.zakkak.net/tutorials/working-with-randoms-native-images/
// */
//public class QuartzSchedulerReinitFeature implements Feature {
//
//    @Override
//    public void afterRegistration(AfterRegistrationAccess access) {
//        RuntimeClassInitializationSupport rci = ImageSingletons.lookup(RuntimeClassInitializationSupport.class);
//        rci.initializeAtBuildTime(java.rmi.server.ObjID.class, "Needs to be optimized");
//        rci.rerunInitialization(java.rmi.server.ObjID.class, "Contains Random instance");
//
//        rci.initializeAtBuildTime("sun.rmi.transport.LiveRef", "Needs to be optimized");
//        rci.rerunInitialization("sun.rmi.transport.LiveRef", "Contains Random instance");
//    }
//
//}