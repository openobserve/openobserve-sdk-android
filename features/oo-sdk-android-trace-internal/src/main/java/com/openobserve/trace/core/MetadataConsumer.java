package com.openobserve.trace.core;

import com.openobserve.android.trace.internal.compat.function.Consumer;

@FunctionalInterface
public interface MetadataConsumer extends Consumer<Metadata> {

  MetadataConsumer NO_OP = (md) -> {};

  void accept(Metadata metadata);
}
