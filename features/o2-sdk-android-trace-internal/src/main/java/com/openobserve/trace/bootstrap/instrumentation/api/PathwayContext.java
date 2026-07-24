package com.openobserve.trace.bootstrap.instrumentation.api;

import java.io.IOException;
import java.util.LinkedHashMap;
import com.openobserve.android.trace.internal.compat.function.Consumer;

public interface PathwayContext {
  String PROPAGATION_KEY = "o2-pathway-ctx";
  String PROPAGATION_KEY_BASE64 = "o2-pathway-ctx-base64";
  String OPENOBSERVE_KEY = "_datadog";

  boolean isStarted();

  long getHash();

  void setCheckpoint(
      LinkedHashMap<String, String> sortedTags,
      Consumer<StatsPoint> pointConsumer,
      long defaultTimestamp,
      long payloadSizeBytes);

  void setCheckpoint(
      LinkedHashMap<String, String> sortedTags,
      Consumer<StatsPoint> pointConsumer,
      long defaultTimestamp);

  // The input tags should be sorted.
  void setCheckpoint(LinkedHashMap<String, String> sortedTags, Consumer<StatsPoint> pointConsumer);

  void saveStats(StatsPoint point);

  StatsPoint getSavedStats();

  byte[] encode() throws IOException;

  String strEncode() throws IOException;
}
