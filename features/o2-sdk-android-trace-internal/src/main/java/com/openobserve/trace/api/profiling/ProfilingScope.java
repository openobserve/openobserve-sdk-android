package com.openobserve.trace.api.profiling;

public interface ProfilingScope extends AutoCloseable, ProfilingContext {

  ProfilingScope NO_OP = () -> {};

  @Override
  void close();
}
