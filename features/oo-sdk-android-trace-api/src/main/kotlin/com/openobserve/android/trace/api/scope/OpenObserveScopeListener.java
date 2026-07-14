/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.api.scope;

/** Hooks for scope activation */
public interface OpenObserveScopeListener {
  /**
   * Called just after a scope becomes the active scope.
   * <p>May be called multiple times. When a scope is initially created, or after a child scope is
   * deactivated.
   */
  void afterScopeActivated();

  /** Called just after a scope is closed. */
  void afterScopeClosed();
}
