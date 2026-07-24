package com.openobserve.trace.core.propagation.ptags;

import com.openobserve.trace.core.propagation.PropagationTags;
import com.openobserve.trace.core.propagation.ptags.PTagsFactory.PTags;
import com.openobserve.trace.core.propagation.ptags.TagElement.Encoding;
import com.openobserve.trace.logger.LoggerFactory;
import com.openobserve.trace.relocate.api.RatelimitedLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.openobserve.android.trace.internal.compat.function.IntPredicate;

/** Captures configuration required for PropagationTags logic */
final class OpenObservePTagsCodec extends PTagsCodec {
  private static final RatelimitedLogger log =
      new RatelimitedLogger(LoggerFactory.getLogger(OpenObservePTagsCodec.class), 5, TimeUnit.MINUTES);
  private static final String PROPAGATION_ERROR_EXTRACT_MAX_SIZE = "extract_max_size";
  private static final String PROPAGATION_ERROR_DECODING_ERROR = "decoding_error";
  private static final char TAGS_SEPARATOR = ',';
  private static final char TAG_KEY_SEPARATOR = '=';
  private static final int MIN_ALLOWED_CHAR = 32;
  private static final int MAX_ALLOWED_CHAR = 126;

  private final int xOpenObserveTagsLimit;

  OpenObservePTagsCodec(int xOpenObserveTagsLimit) {
    this.xOpenObserveTagsLimit = xOpenObserveTagsLimit;
  }

  /**
   * Parses a header value with next eBNF:
   *
   * <pre>
   *   tagset = ( tag, { ",", tag } ) | "";
   *   tag = ( identifier - space or equal ), "=", identifier;
   *   identifier = allowed characters, { allowed characters };
   *   allowed characters = ( ? ASCII characters 32-126 ? - comma );
   *   space or equal = " " | "=";
   *   comma = ",";
   * </pre>
   *
   * All tags prefixed with `_o2.p.` are extracted from tagSet except for `_o2.p.upstream_services`.
   * TagSet that doesn't respect the format will be dropped and a warning will be logged.
   *
   * @return a PropagationTags containing only _o2.p.* tags or an error if the header value is
   *     invalid
   */
  @Override
  PropagationTags fromHeaderValue(PTagsFactory tagsFactory, String value) {
    if (value == null) {
      return tagsFactory.empty();
    }
    if (value.length() > xOpenObserveTagsLimit) {
      // Incoming x-datadog-tags value length exceeds ooTagsLimit
      // Set _o2.propagation_error:extract_max_size
      return tagsFactory.createInvalid(PROPAGATION_ERROR_EXTRACT_MAX_SIZE);
    }

    List<TagElement> tagPairs = null;
    int len = value.length();
    int tagPos = 0;
    TagValue decisionMakerTagValue = null;
    TagValue traceIdTagValue = null;
    while (tagPos < len) {
      int tagKeyEndsAt =
          validateCharsUntilSeparatorOrEnd(
              value, tagPos, TAG_KEY_SEPARATOR, OpenObservePTagsCodec::isAllowedKeyChar);
      if (tagKeyEndsAt < 0 || tagKeyEndsAt == len) {
        log.warn("Invalid datadog tags header value: '{}' at {}", value, tagPos);
        return tagsFactory.createInvalid(PROPAGATION_ERROR_DECODING_ERROR);
      }
      int tagValuePos = tagKeyEndsAt + 1;
      int tagValueEndsAt =
          validateCharsUntilSeparatorOrEnd(
              value, tagValuePos, TAGS_SEPARATOR, OpenObservePTagsCodec::isAllowedValueChar);
      if (tagValueEndsAt < 0) {
        log.warn("Invalid datadog tags header value: '{}' at {}", value, tagKeyEndsAt);
        return tagsFactory.createInvalid(PROPAGATION_ERROR_DECODING_ERROR);
      }
      TagKey tagKey = TagKey.from(Encoding.OPENOBSERVE, value, tagPos, tagKeyEndsAt);
      TagValue tagValue = TagValue.from(Encoding.OPENOBSERVE, value, tagValuePos, tagValueEndsAt);
      if (tagKey != null) {
        if (!tagKey.equals(UPSTREAM_SERVICES_DEPRECATED_TAG)) {
          if (!validateTagValue(tagKey, tagValue)) {
            log.warn(
                "Invalid datadog tags header value: '{}' invalid tag value at {}",
                value,
                tagValuePos);
            if (tagKey.equals(TRACE_ID_TAG)) {
              return tagsFactory.createInvalid(PROPAGATION_ERROR_MALFORMED_TID + tagValue);
            }
            return tagsFactory.createInvalid(PROPAGATION_ERROR_DECODING_ERROR);
          }
          if (tagKey.equals(DECISION_MAKER_TAG)) {
            decisionMakerTagValue = tagValue;
          } else if (tagKey.equals(TRACE_ID_TAG)) {
            traceIdTagValue = tagValue;
          } else {
            if (tagPairs == null) {
              // This is roughly the size of a two element linked list but can hold six
              tagPairs = new ArrayList<>(6);
            }
            tagPairs.add(tagKey);
            tagPairs.add(tagValue);
          }
        }
      }
      tagPos = tagValueEndsAt + 1;
    }
    return tagsFactory.createValid(tagPairs, decisionMakerTagValue, traceIdTagValue);
  }

  @Override
  protected int estimateHeaderSize(PTags pTags) {
    return pTags.getXOpenObserveTagsSize();
  }

  @Override
  protected int appendPrefix(StringBuilder sb, PTags ptags) {
    // Calculate the tag size here and return it. Don't do anything else since there is no prefix.
    return ptags.getXOpenObserveTagsSize();
  }

  @Override
  protected int appendTag(StringBuilder sb, TagElement key, TagElement value, int size) {
    if (size <= xOpenObserveTagsLimit) {
      if (sb.length() > 0) {
        sb.append(TAGS_SEPARATOR);
      }
      sb.append(key.forType(Encoding.OPENOBSERVE));
      sb.append(TAG_KEY_SEPARATOR);
      sb.append(value.forType(Encoding.OPENOBSERVE));
    }
    return size;
  }

  @Override
  protected int appendSuffix(StringBuilder sb, PTags ptags, int size) {
    return size;
  }

  @Override
  protected boolean isTooLarge(StringBuilder sb, int size) {
    return size > xOpenObserveTagsLimit;
  }

  @Override
  protected boolean isEmpty(StringBuilder sb, int size) {
    return sb.length() == 0;
  }

  private static int validateCharsUntilSeparatorOrEnd(
      String s, int start, char separator, IntPredicate isValid) {
    int end = s.length();
    if (start >= end) {
      return -1;
    }
    int pos = start;
    char c = s.charAt(pos);
    do {
      if (!isValid.check(c) || c == separator) {
        return -1;
      }
      pos++;
      if (pos < end) {
        c = s.charAt(pos);
        // It's not allowed to have the separator as the last character so only check
        // if there is something after the separator
        if (pos < end - 1 && c == separator) {
          break;
        }
      }
    } while (pos < end);

    return pos;
  }

  private static boolean isAllowedKeyChar(int c) {
    // space (MIN_ALLOWED_CHAR) is not allowed
    return c > MIN_ALLOWED_CHAR && c <= MAX_ALLOWED_CHAR && c != TAGS_SEPARATOR;
  }

  private static boolean isAllowedValueChar(int c) {
    return c >= MIN_ALLOWED_CHAR && c <= MAX_ALLOWED_CHAR;
  }
}
