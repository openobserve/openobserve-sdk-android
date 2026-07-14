/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.trace.appsec.api.blocking;

public enum BlockingContentType {
  /**
   * Automatically choose between HTML and JSON, depending on the value of the <code>Accept</code>
   * header. If the preference value is the same, {@link #JSON} will be preferred.
   */
  AUTO,
  /** An HTTP response. */
  HTML,
  /** A JSON response. */
  JSON,
  /** No body in the response */
  NONE,
}
