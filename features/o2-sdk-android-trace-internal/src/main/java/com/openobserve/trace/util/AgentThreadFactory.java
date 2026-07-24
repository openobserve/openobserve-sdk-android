package com.openobserve.trace.util;

import com.openobserve.trace.logger.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/** A {@link ThreadFactory} implementation that starts all agent {@link Thread}s as daemons. */
public final class AgentThreadFactory implements ThreadFactory {
  public static final ThreadGroup AGENT_THREAD_GROUP = new ThreadGroup("o2-trace-java");

  public static final long THREAD_JOIN_TIMOUT_MS = 800;

  // known agent threads
  public enum AgentThread {
    TASK_SCHEDULER("o2-task-scheduler"),

    TRACE_STARTUP("o2-agent-startup-datadog-tracer"),
    TRACE_MONITOR("o2-trace-monitor"),
    TRACE_PROCESSOR("o2-trace-processor"),
    SPAN_SAMPLING_PROCESSOR("o2-span-sampling-processor"),
    TRACE_CASSANDRA_ASYNC_SESSION("o2-cassandra-session-executor"),

    METRICS_AGGREGATOR("o2-metrics-aggregator"),
    STATSD_CLIENT("o2-statsd-client"),

    JMX_STARTUP("o2-agent-startup-jmxfetch"),
    JMX_COLLECTOR("o2-jmx-collector"),

    PROFILER_STARTUP("o2-agent-startup-datadog-profiler"),
    PROFILER_RECORDING_SCHEDULER("o2-profiler-recording-scheduler"),
    PROFILER_HTTP_DISPATCHER("o2-profiler-http-dispatcher"),

    APPSEC_HTTP_DISPATCHER("o2-appsec-http-dispatcher"),

    TELEMETRY("o2-telemetry"),

    FLEET_MANAGEMENT_POLLER("o2-fleet-management-poller"),
    REMOTE_CONFIG("o2-remote-config"),

    TRACER_FLARE("o2-tracer-flare"),

    CWS_TLS("o2-cws-tls"),

    PROCESS_SUPERVISOR("o2-process-supervisor"),

    DEBUGGER_HTTP_DISPATCHER("o2-debugger-upload-http-dispatcher");

    public final String threadName;

    AgentThread(final String threadName) {
      this.threadName = threadName;
    }
  }

  private final AgentThread agentThread;

  /**
   * Constructs a new agent {@code ThreadFactory}.
   *
   * @param agentThread the agent thread created by this factory.
   */
  public AgentThreadFactory(final AgentThread agentThread) {
    this.agentThread = agentThread;
  }

  @Override
  public Thread newThread(final Runnable runnable) {
    return newAgentThread(agentThread, runnable);
  }

  /**
   * Constructs a new agent {@code Thread} as a daemon with a null ContextClassLoader.
   *
   * @param agentThread the agent thread to create.
   * @param runnable work to run on the new thread.
   */
  public static Thread newAgentThread(final AgentThread agentThread, final Runnable runnable) {
    return newAgentThread(agentThread, null, runnable, true);
  }

  public static Thread newAgentThread(
      final AgentThread agentThread, final Runnable runnable, boolean daemon) {
    return newAgentThread(agentThread, null, runnable, daemon);
  }

  public static Thread newAgentThread(
      final AgentThread agentThread,
      final String nameSuffix,
      final Runnable runnable,
      boolean daemon) {
    final String threadName =
        nameSuffix != null ? agentThread.threadName + nameSuffix : agentThread.threadName;
    final Thread thread = new Thread(AGENT_THREAD_GROUP, runnable, threadName);
    thread.setDaemon(daemon);
    thread.setContextClassLoader(null);
    thread.setUncaughtExceptionHandler(
        new Thread.UncaughtExceptionHandler() {
          @Override
          public void uncaughtException(final Thread thread, final Throwable e) {
            LoggerFactory.getLogger(runnable.getClass())
                .error("Uncaught exception {} in {}", e, agentThread.threadName, e);
          }
        });
    return thread;
  }
}
