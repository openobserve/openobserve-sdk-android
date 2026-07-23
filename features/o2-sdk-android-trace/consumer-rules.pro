-keep class com.openobserve.android.trace.GlobalOpenObserveTracer {
    public com.openobserve.android.trace.api.tracer.OpenObserveTracer getOrNull();
    public static com.openobserve.android.trace.GlobalOpenObserveTracer INSTANCE;
}
-keepclassmembernames class org.jctools.** { *; }
