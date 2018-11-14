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

    // mod-users-bl
    router.route(GET, "/bl-users/by-id/:userId").handler(this::getUserById);

    // mod-circulation
    router.route(GET, "/circulation/loans/:loanId").handler(this::getLoanById);
    router.route(GET, "/circulation/requests/:requestId").handler(this::getRequestById);

    return router;
  }

  public void getUserById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/bl-user-" + rc.pathParam("userId") + ".json");
  }

  public void getLoanById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/circulation-loan-" + rc.pathParam("loanId") + ".json");
  }

  public void getRequestById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/circulation-request-" + rc.pathParam("requestId") + ".json");
  }
}
