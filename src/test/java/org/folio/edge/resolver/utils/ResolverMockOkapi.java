package org.folio.edge.resolver.utils;

import static io.vertx.core.http.HttpMethod.GET;
import static org.folio.edge.resolver.Constants.PATH_PARAM_HOLDING_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_INSTANCE_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_ITEM_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_LOAN_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_REQUEST_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_USER_ID;

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
    router.route(GET, "/bl-users/by-id/:" + PATH_PARAM_USER_ID).handler(this::getUserById);

    // mod-circulation
    router.route(GET, "/circulation/loans/:" + PATH_PARAM_LOAN_ID).handler(this::getLoanById);
    router.route(GET, "/circulation/requests/:" + PATH_PARAM_REQUEST_ID).handler(this::getRequestById);

    // mod-inventory
    router.route(GET, "/inventory/instances/:" + PATH_PARAM_INSTANCE_ID).handler(this::getInstanceById);
    router.route(GET, "/inventory/items/:" + PATH_PARAM_ITEM_ID).handler(this::getItemById);

    // mod-inventory-storage
    router.route(GET, "/holdings-storage/holdings/:" + PATH_PARAM_HOLDING_ID).handler(this::getHoldingById);

    return router;
  }

  public void getUserById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/bl-user-" + rc.pathParam(PATH_PARAM_USER_ID) + ".json");
  }

  public void getLoanById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/circulation-loan-" + rc.pathParam(PATH_PARAM_LOAN_ID) + ".json");
  }

  public void getRequestById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/circulation-request-" + rc.pathParam(PATH_PARAM_REQUEST_ID) + ".json");
  }

  public void getInstanceById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/inventory-instance-" + rc.pathParam(PATH_PARAM_INSTANCE_ID) + ".json");
  }

  public void getItemById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/inventory-item-" + rc.pathParam(PATH_PARAM_ITEM_ID) + ".json");
  }

  public void getHoldingById(RoutingContext rc) {
    rc.response().setStatusCode(200).sendFile("mock_data/inventory-holding-" + rc.pathParam(PATH_PARAM_HOLDING_ID) + ".json");
  }
}
