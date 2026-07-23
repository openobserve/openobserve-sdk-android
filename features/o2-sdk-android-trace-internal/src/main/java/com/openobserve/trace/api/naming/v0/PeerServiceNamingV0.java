package com.openobserve.trace.api.naming.v0;

import androidx.annotation.NonNull;

import com.openobserve.trace.api.naming.NamingSchema;

import java.util.Collections;
import java.util.Map;

public class PeerServiceNamingV0 implements NamingSchema.ForPeerService {
  @Override
  public boolean supports() {
    return false;
  }

  @NonNull
  @Override
  public Map<String, Object> tags(@NonNull final Map<String, Object> unsafeTags) {
    return Collections.emptyMap();
  }
}
