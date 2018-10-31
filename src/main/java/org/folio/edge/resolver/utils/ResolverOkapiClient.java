package org.folio.edge.resolver.utils;

import org.folio.edge.core.utils.OkapiClient;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;

public final class ResolverOkapiClient extends OkapiClient {
  public ResolverOkapiClient(OkapiClient client) {
    super(client);
  }

  ResolverOkapiClient(Vertx vertx, String okapiURL, String tenant,
      long timeout) {
    super(vertx, okapiURL, tenant, timeout);
  }

  public void resolveUserId(String userId, MultiMap headers,
      Handler<HttpClientResponse> responseHandler,
      Handler<Throwable> exceptionHandler) {
    get(okapiURL + "/bl-users/by-id/" + userId, tenant,
        combineHeadersWithDefaults(headers), responseHandler,
        exceptionHandler);
  }
}
