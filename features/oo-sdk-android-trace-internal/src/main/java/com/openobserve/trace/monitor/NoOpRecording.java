/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.trace.monitor;

public class NoOpRecording extends Recording {

  public static final Recording NO_OP = new NoOpRecording();

  @Override
  public Recording start() {
    return this;
  }

  @Override
  public void reset() {}

  @Override
  public void stop() {}

  @Override
  public void flush() {}
}
