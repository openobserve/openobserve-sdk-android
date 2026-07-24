package com.openobserve.trace.core.propagation;

import static com.openobserve.trace.api.ConfigDefaults.DEFAULT_TRACE_X_OPENOBSERVE_TAGS_MAX_LENGTH;

import com.openobserve.trace.api.Config;
import com.openobserve.trace.core.propagation.ptags.PTagsFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates logic related to the Service Propagation including:
 *
 * <pre>
 *   - parsing and validation the x-datadog-tags header value
 *   - dropping non _o2.p.* tags
 *   - error handling and propagation
 *   - concurrent updates to the sampling priority
 *   - producing the x-datadog-tags header value
 *   - producing meta tags to be sent to the agent
 * </pre>
 */
public abstract class PropagationTags {

  public static Factory factory(Config config) {
    return factory(config.getxOpenObserveTagsMaxLength());
  }

  public static Factory factory(int ooTagsLimit) {
    return new PTagsFactory(ooTagsLimit);
  }

  public static Factory factory() {
    return factory(DEFAULT_TRACE_X_OPENOBSERVE_TAGS_MAX_LENGTH);
  }

  public enum HeaderType {
    OPENOBSERVE,
    W3C;

    private static final int numValues = HeaderType.values().length;

    public static int getNumValues() {
      return numValues;
    }
  }

  public interface Factory {
    PropagationTags empty();

    PropagationTags fromHeaderValue(HeaderType headerType, String value);
  }

  /**
   * Updates the trace-level sampling priority decision if it hasn't already been made and _o2.p.dm
   * tag doesn't exist. Called on the root span context.
   */
  public abstract void updateTraceSamplingPriority(int samplingPriority, int samplingMechanism);

  public abstract int getSamplingPriority();

  public abstract void updateTraceOrigin(CharSequence origin);

  public abstract CharSequence getOrigin();

  public abstract long getTraceIdHighOrderBits();

  public abstract void updateTraceIdHighOrderBits(long highOrderBits);

  public abstract String getRumSessionId();

  public abstract void updateRumSessionId(String sessionId);

  /**
   * Gets the original <a href="https://www.w3.org/TR/trace-context/#tracestate-header">W3C
   * tracestate header</a> value.
   *
   * @return The original W3C tracestate header value.
   */
  public abstract String getW3CTracestate();

  /**
   * Stores the original <a href="https://www.w3.org/TR/trace-context/#tracestate-header">W3C
   * tracestate header</a> value.
   *
   * @param tracestate The original W3C tracestate header value.
   */
  public abstract void updateW3CTracestate(String tracestate);

  /**
   * Constructs a header value that includes valid propagated _o2.p.* tags and possibly a new
   * sampling decision tag _o2.p.dm based on the current state. Returns null if the value length
   * exceeds a configured limit or empty.
   */
  public abstract String headerValue(HeaderType headerType);

  /**
   * Fills a provided tagMap with valid propagated _o2.p.* tags and possibly a new sampling decision
   * tags _o2.p.dm (root span only) based on the current state, or sets only an error tag if the
   * header value exceeds a configured limit.
   */
  public abstract void fillTagMap(Map<String, String> tagMap);

  public HashMap<String, String> createTagMap() {
    HashMap<String, String> result = new HashMap<>();
    fillTagMap(result);
    return result;
  }
}
