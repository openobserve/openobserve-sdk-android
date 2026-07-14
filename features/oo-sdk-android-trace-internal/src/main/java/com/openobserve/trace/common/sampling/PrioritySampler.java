package com.openobserve.trace.common.sampling;

import com.openobserve.trace.core.CoreSpan;

public interface PrioritySampler {
  <T extends CoreSpan<T>> void setSamplingPriority(T span);
}
