package com.openobserve.trace.core.tagprocessor;

import com.openobserve.trace.core.DDSpanContext;

import java.util.Map;

public interface TagsPostProcessor {
  Map<String, Object> processTags(Map<String, Object> unsafeTags);

  default Map<String, Object> processTagsWithContext(
      Map<String, Object> unsafeTags, DDSpanContext spanContext) {
    return processTags(unsafeTags);
  }
}
