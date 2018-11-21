package org.folio.edge.resolver;

import java.util.Map;
import java.util.UUID;

import org.folio.edge.core.ApiKeyHelper;
import org.folio.edge.core.Handler;
import org.folio.edge.core.security.SecureStore;
import org.folio.edge.core.utils.OkapiClient;
import org.folio.edge.core.utils.OkapiClientFactory;

import io.vertx.ext.web.RoutingContext;

public class ResolveHandler extends Handler {

  public ResolveHandler(SecureStore secureStore, OkapiClientFactory ocf) {
    super(secureStore, ocf);
  }

  public ResolveHandler(SecureStore secureStore, OkapiClientFactory ocf,
      ApiKeyHelper keyHelper) {
    super(secureStore, ocf, keyHelper);
  }

  protected void handleResolveUUID(RoutingContext rc, String pathParam,
      TwoParamVoidFunction<OkapiClient, Map<String, String>> action) {
    final String id = rc.pathParam(pathParam);

    try {
      UUID.fromString(id);
    } catch (Exception e) {
      badRequest(rc, "Invalid " + pathParam + " format, must be UUID: " + id);
      return;
    }

    handleCommon(rc, new String[] {pathParam}, new String[] {}, action);
  }
}