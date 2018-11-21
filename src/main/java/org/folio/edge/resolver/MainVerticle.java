package org.folio.edge.resolver;

import static io.vertx.core.http.HttpMethod.GET;
import static org.folio.edge.core.Constants.SYS_API_KEY_SOURCES;
import static org.folio.edge.core.Constants.SYS_OKAPI_URL;
import static org.folio.edge.core.Constants.SYS_REQUEST_TIMEOUT_MS;
import static org.folio.edge.resolver.Constants.PATH_PARAM_HOLDING_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_INSTANCE_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_ITEM_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_LOAN_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_REQUEST_ID;
import static org.folio.edge.resolver.Constants.PATH_PARAM_USER_ID;

import org.folio.edge.core.ApiKeyHelper;
import org.folio.edge.core.EdgeVerticle2;
import org.folio.edge.resolver.utils.ResolverOkapiClientFactory;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public final class MainVerticle extends EdgeVerticle2 {
  static {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
  }

  @Override
  public Router defineRoutes() {
    final ResolverOkapiClientFactory ocf = new ResolverOkapiClientFactory(vertx,
        config().getString(SYS_OKAPI_URL),
        config().getLong(SYS_REQUEST_TIMEOUT_MS));
    final ApiKeyHelper apiKeyHelper =
        new ApiKeyHelper(config().getString(SYS_API_KEY_SOURCES));
    final ResolveUserHandler resolveUserHandler =
        new ResolveUserHandler(secureStore, ocf, apiKeyHelper);
    final ResolveCirculationHandler resolveCirculationHandler =
        new ResolveCirculationHandler(secureStore, ocf, apiKeyHelper);
    final ResolveInventoryHandler resolveInventoryHandler =
        new ResolveInventoryHandler(secureStore, ocf, apiKeyHelper);

    final Router router = Router.router(vertx);

    // Internal/Administrative
    router.route().handler(BodyHandler.create());
    router.route(GET, "/admin/health").handler(this::handleHealthCheck);

    // Users
    router.route(GET, "/resolve/users/:" + PATH_PARAM_USER_ID)
      .handler(resolveUserHandler::handleResolveUser);

    // Circulation
    router.route(GET, "/resolve/circulation/loans/:" + PATH_PARAM_LOAN_ID)
      .handler(resolveCirculationHandler::handleResolveLoan);
    router.route(GET, "/resolve/circulation/requests/:" + PATH_PARAM_REQUEST_ID)
      .handler(resolveCirculationHandler::handleResolveRequest);

    // Inventory
    router.route(GET, "/resolve/inventory/instances/:" + PATH_PARAM_INSTANCE_ID)
      .handler(resolveInventoryHandler::handleResolveInstance);
    router.route(GET, "/resolve/inventory/holdings/:" + PATH_PARAM_HOLDING_ID)
      .handler(resolveInventoryHandler::handleResolveHolding);
    router.route(GET, "/resolve/inventory/items/:" + PATH_PARAM_ITEM_ID)
      .handler(resolveInventoryHandler::handleResolveItem);

    return router;
  }
}
