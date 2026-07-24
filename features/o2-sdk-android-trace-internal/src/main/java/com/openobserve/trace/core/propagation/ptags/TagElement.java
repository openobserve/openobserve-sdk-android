package com.openobserve.trace.core.propagation.ptags;

abstract class TagElement implements CharSequence {
  public enum Encoding {
    OPENOBSERVE("_o2.p."),
    W3C("t.");

    private final String prefix;

    Encoding(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }

    public int getPrefixLength() {
      return prefix.length();
    }

    private static final Encoding[] cachedValues;
    private static final int numValues;

    static {
      cachedValues = values();
      numValues = cachedValues.length;
    }

    static Encoding[] getCachedValues() {
      return cachedValues;
    }

    static int getNumValues() {
      return numValues;
    }
  }

  abstract CharSequence forType(Encoding encoding);
}
