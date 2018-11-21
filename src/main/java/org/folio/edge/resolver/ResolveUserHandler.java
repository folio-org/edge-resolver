package org.folio.edge.resolver;

import static org.folio.edge.resolver.Constants.PATH_PARAM_USER_ID;

import org.folio.edge.core.ApiKeyHelper;
import org.folio.edge.core.security.SecureStore;
import org.folio.edge.core.utils.OkapiClientFactory;
import org.folio.edge.resolver.utils.ResolverOkapiClient;

import io.vertx.ext.web.RoutingContext;

public final class ResolveUserHandler extends ResolveHandler {
  public ResolveUserHandler(SecureStore secureStore, OkapiClientFactory ocf) {
    super(secureStore, ocf);
  }

  public ResolveUserHandler(SecureStore secureStore, OkapiClientFactory ocf,
      ApiKeyHelper keyHelper) {
    super(secureStore, ocf, keyHelper);
  }

  void handleResolveUser(RoutingContext rc) {
    handleResolveUUID(rc, PATH_PARAM_USER_ID,
        (client, params) ->
          ((ResolverOkapiClient) client).resolveUserId(params.get(PATH_PARAM_USER_ID),
              rc.request().headers(), resp -> handleProxyResponse(rc, resp),
              t -> handleProxyException(rc, t))
        );
  }
}
