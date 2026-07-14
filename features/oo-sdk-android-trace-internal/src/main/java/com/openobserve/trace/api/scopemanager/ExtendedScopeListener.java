package com.openobserve.trace.api.scopemanager;

import com.openobserve.trace.api.DDTraceId;
import com.openobserve.trace.api.TraceConfig;

public interface ExtendedScopeListener extends ScopeListener {
  void afterScopeActivated(
      DDTraceId traceId, long localRootSpanId, long spanId, TraceConfig traceConfig);

  /** Called just after a scope is closed. */
  @Override
  void afterScopeClosed();
}
