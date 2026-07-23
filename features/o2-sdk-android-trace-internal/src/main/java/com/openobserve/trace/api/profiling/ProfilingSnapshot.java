package com.openobserve.trace.api.profiling;

public interface ProfilingSnapshot extends ObservableType {
  enum Kind {
    PERIODIC,
    ON_SHUTDOWN
  }
}
