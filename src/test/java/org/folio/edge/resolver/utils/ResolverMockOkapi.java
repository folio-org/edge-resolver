package org.folio.edge.resolver.utils;

import static io.vertx.core.http.HttpMethod.GET;

import java.util.List;

import org.folio.edge.core.utils.test.MockOkapi;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ResolverMockOkapi extends MockOkapi {

  public ResolverMockOkapi(int port, List<String> knownTenants) {
    super(port, knownTenants);
  }

  @Override
  protected Router defineRoutes() {
    final Router router = super.defineRoutes();

    router.route(GET, "/bl-users/by-id/:userId").handler(this::getUserById);

    return router;
  }

  public void getUserById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/bl-user-" + rc.pathParam("userId") + ".json");
  }
}
