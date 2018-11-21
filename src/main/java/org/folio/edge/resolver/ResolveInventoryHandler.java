package org.folio.edge.resolver;

import static org.folio.edge.resolver.Constants.PATH_PARAM_HOLDING_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_INSTANCE_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_ITEM_ID;

import org.folio.edge.core.ApiKeyHelper;
import org.folio.edge.core.security.SecureStore;
import org.folio.edge.core.utils.OkapiClientFactory;
import org.folio.edge.resolver.utils.ResolverOkapiClient;

import io.vertx.ext.web.RoutingContext;

public final class ResolveInventoryHandler extends ResolveHandler {
  public ResolveInventoryHandler(SecureStore secureStore, OkapiClientFactory ocf) {
    super(secureStore, ocf);
  }

  public ResolveInventoryHandler(SecureStore secureStore, OkapiClientFactory ocf,
      ApiKeyHelper keyHelper) {
    super(secureStore, ocf, keyHelper);
  }

  void handleResolveInstance(RoutingContext rc) {
    handleResolveUUID(rc, PATH_PARAM_INSTANCE_ID,
        (client, params) ->
          ((ResolverOkapiClient) client).resolveInstanceId(params.get(PATH_PARAM_INSTANCE_ID),
              rc.request().headers(), resp -> handleProxyResponse(rc, resp),
              t -> handleProxyException(rc, t))
        );
  }

  void handleResolveHolding(RoutingContext rc) {
    handleResolveUUID(rc, PATH_PARAM_HOLDING_ID,
        (client, params) ->
          ((ResolverOkapiClient) client).resolveHoldingId(params.get(PATH_PARAM_HOLDING_ID),
              rc.request().headers(), resp -> handleProxyResponse(rc, resp),
              t -> handleProxyException(rc, t))
        );
  }

  void handleResolveItem(RoutingContext rc) {
    handleResolveUUID(rc, PATH_PARAM_ITEM_ID,
        (client, params) ->
          ((ResolverOkapiClient) client).resolveItemId(params.get(PATH_PARAM_ITEM_ID),
              rc.request().headers(), resp -> handleProxyResponse(rc, resp),
              t -> handleProxyException(rc, t))
        );
  }
}
