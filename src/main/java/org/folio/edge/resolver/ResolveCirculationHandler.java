package org.folio.edge.resolver;

import static org.folio.edge.resolver.Constants.PATH_PARAM_LOAN_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_REQUEST_ID;

import org.folio.edge.core.ApiKeyHelper;
import org.folio.edge.core.security.SecureStore;
import org.folio.edge.core.utils.OkapiClientFactory;
import org.folio.edge.resolver.utils.ResolverOkapiClient;

import io.vertx.ext.web.RoutingContext;

public final class ResolveCirculationHandler extends ResolveHandler {
  public ResolveCirculationHandler(SecureStore secureStore, OkapiClientFactory ocf) {
    super(secureStore, ocf);
  }

  public ResolveCirculationHandler(SecureStore secureStore, OkapiClientFactory ocf,
      ApiKeyHelper keyHelper) {
    super(secureStore, ocf, keyHelper);
  }

  void handleResolveLoan(RoutingContext rc) {
    handleResolveUUID(rc, PATH_PARAM_LOAN_ID,
        (client, params) ->
          ((ResolverOkapiClient) client).resolveLoanId(params.get(PATH_PARAM_LOAN_ID),
              rc.request().headers(), resp -> handleProxyResponse(rc, resp),
              t -> handleProxyException(rc, t))
        );
  }

  void handleResolveRequest(RoutingContext rc) {
    handleResolveUUID(rc, PATH_PARAM_REQUEST_ID,
        (client, params) ->
          ((ResolverOkapiClient) client).resolveRequestId(params.get(PATH_PARAM_REQUEST_ID),
              rc.request().headers(), resp -> handleProxyResponse(rc, resp),
              t -> handleProxyException(rc, t))
        );
  }
}
