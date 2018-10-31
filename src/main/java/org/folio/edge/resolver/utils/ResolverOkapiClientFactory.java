package org.folio.edge.resolver.utils;

import org.folio.edge.core.utils.OkapiClientFactory;

import io.vertx.core.Vertx;

public final class ResolverOkapiClientFactory extends OkapiClientFactory {
  public ResolverOkapiClientFactory(Vertx vertx, String okapiURL,
      long reqTimeoutMs) {
    super(vertx, okapiURL, reqTimeoutMs);
  }

  @Override
  public ResolverOkapiClient getOkapiClient(String tenant) {
    return new ResolverOkapiClient(vertx, okapiURL, tenant, reqTimeoutMs);
  }
}
