# Needed to make sure we don't remove any test code
-dontshrink
#-dontoptimize
#-keepattributes *Annotation*

# Required for some Kotlin-jvm implementation using reflection
-keepnames class kotlin.jvm.** { *; }

# Required because we need access to OpenObserve.stop() by reflection
-keepnames class com.openobserve.android.OpenObserve {
    *;
}

# Required because we need access to GlobalOpenObserveTracer getOrNull method to reset it through reflection
-keepnames class com.openobserve.android.trace.GlobalOpenObserveTracer {
    public com.openobserve.android.trace.api.tracer.OpenObserveTracer getOrNull();
    public static com.openobserve.android.trace.GlobalOpenObserveTracer INSTANCE;
}

# Required because we need access to GlobalRumMonitor reset method to reset it through reflection
-keepnames class com.openobserve.android.rum.GlobalRumMonitor {
    private void reset();
}

# Required because we need access to RumContext fields by reflection
-keepnames class com.openobserve.android.rum.internal.domain.RumContext {
    *;
}

# Required because we need access to telemetry methods by reflection
-keepnames class com.openobserve.android.rum.internal.monitor.OpenObserveRumMonitor {
    *;
}

-dontwarn kotlin.Experimental$Level
-dontwarn kotlin.Experimental
