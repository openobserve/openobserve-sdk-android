package com.openobserve.trace.core.scopemanager;

import com.openobserve.trace.api.Stateful;
import com.openobserve.trace.bootstrap.instrumentation.api.AgentSpan;
import com.openobserve.trace.logger.Logger;
import com.openobserve.trace.relocate.api.RatelimitedLogger;

final class ContinuingScope extends ContinuableScope {
  /** Continuation that created this scope. */
  private final AbstractContinuation continuation;

  ContinuingScope(
          final ContinuableScopeManager scopeManager,
          final AgentSpan span,
          final byte source,
          final boolean isAsyncPropagating,
          final AbstractContinuation continuation,
          final Stateful scopeState,
          final Logger logger,
          final RatelimitedLogger ratelimitedLogger) {
    super(scopeManager, span, source, isAsyncPropagating, scopeState, logger, ratelimitedLogger);
    this.continuation = continuation;
  }

  @Override
  void cleanup(final ScopeStack scopeStack) {
    super.cleanup(scopeStack);

    continuation.cancelFromContinuedScopeClose();
  }
}
