package org.folio.edge.resolver;

import java.util.Collections;

import org.folio.edge.core.Constants;
import org.folio.edge.core.utils.ApiKeyUtils;
import org.folio.edge.core.utils.test.TestUtils;
import org.folio.edge.resolver.utils.ResolverMockOkapi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {
  private static final Logger logger;

  static {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    logger = LoggerFactory.getLogger(MainVerticleTest.class);
  }

  private static final String apiKey = "Z1luMHVGdjNMZl9kaWt1X2Rpa3U=";

  private static Vertx vertx;
  private static ResolverMockOkapi mockOkapi;

  @BeforeClass
  public static void setUpBeforeClass(final TestContext context) throws Exception {
    final int serverPort = TestUtils.getPort();
    final int okapiPort = TestUtils.getPort();

    //    System.setProperty(Constants.SYS_PORT, String.valueOf(okapiPort));

    mockOkapi = Mockito.spy(new ResolverMockOkapi(okapiPort,
        Collections.singletonList(ApiKeyUtils.parseApiKey(apiKey).tenantId)));
    mockOkapi.start(context);

    final JsonObject conf = new JsonObject()
        .put(Constants.SYS_PORT, serverPort)
        .put(Constants.SYS_OKAPI_URL, "http://localhost:" + okapiPort)
        .put(Constants.SYS_SECURE_STORE_PROP_FILE, "src/test/resources/secure_store_props/ephemeral.properties");
    final DeploymentOptions opt = new DeploymentOptions().setConfig(conf);

    vertx = Vertx.vertx();
    vertx.deployVerticle(MainVerticle.class.getName(), opt, context.asyncAssertSuccess());

    RestAssured.port = serverPort;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    logger.info("Resolver Egde API Test Setup Done using port " + serverPort);
  }

  @AfterClass
  public static void tearDownAfterClass(final TestContext context) throws Exception {
    logger.info("Shutting down server");

    vertx.close(context.asyncAssertSuccess(res -> mockOkapi.close()));
  }

  @Test
  public final void testAdminHealth(final TestContext context) {
    logger.info("=== Test the health check endpoint ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/admin/health")
        .then()
          .contentType(ContentType.TEXT)
          .statusCode(200)
          .extract()
            .response();

    context.assertEquals("\"OK\"", resp.body().asString());
  }

  @Test
  public final void testResolveUserId(final TestContext context) {
    logger.info("=== Test the resolve user ID endpoint ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/users/8b9b8859-6e28-4702-8f5c-cbbac094bff0?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.JSON)
          .statusCode(200)
          .extract()
            .response();

    final JsonObject jo = new JsonObject(resp.body().asString());
    final JsonObject user = jo.getJsonObject("user");

    context.assertEquals("test", user.getString("username"));
  }

  @Test
  public final void testResolveUserIdBadUUID(final TestContext context) {
    logger.info("=== Test the resolve user ID endpoint with an invalid UUID ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/users/12345?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.TEXT)
          .statusCode(400)
          .extract()
            .response();

    context.assertEquals("Invalid userId format, must be UUID: 12345",
        resp.body().asString());
  }

  @Test
  public final void testResolveLoanId(final TestContext context) {
    logger.info("=== Test the resolve loan ID endpoint ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/circulation/loans/b7a40894-218e-4151-8383-01efc98ae248?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.JSON)
          .statusCode(200)
          .extract()
            .response();

    final JsonObject jo = new JsonObject(resp.body().asString());
    final JsonObject item = jo.getJsonObject("item");

    context.assertEquals("Example Book", item.getString("title"));
  }

  @Test
  public final void testResolveLoanIdBadUUID(final TestContext context) {
    logger.info("=== Test the resolve loan ID endpoint with an invalid UUID ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/circulation/loans/12345?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.TEXT)
          .statusCode(400)
          .extract()
            .response();

    context.assertEquals("Invalid loanId format, must be UUID: 12345",
        resp.body().asString());
  }

  @Test
  public final void testResolveRequestId(final TestContext context) {
    logger.info("=== Test the resolve request ID endpoint ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/circulation/requests/a69db0e3-30fa-485c-a910-1396095e3964?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.JSON)
          .statusCode(200)
          .extract()
            .response();

    final JsonObject jo = new JsonObject(resp.body().asString());
    final JsonObject item = jo.getJsonObject("item");

    context.assertEquals("Example Book", item.getString("title"));
  }

  @Test
  public final void testResolveRequestIdBadUUID(final TestContext context) {
    logger.info("=== Test the resolve request ID endpoint with an invalid UUID ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/circulation/requests/12345?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.TEXT)
          .statusCode(400)
          .extract()
            .response();

    context.assertEquals("Invalid requestId format, must be UUID: 12345",
        resp.body().asString());
  }

  @Test
  public final void testResolveInstanceId(final TestContext context) {
    logger.info("=== Test the resolve instance ID endpoint ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/inventory/instances/54b52ddb-4477-496c-9a3e-cb282d16a89a?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.JSON)
          .statusCode(200)
          .extract()
            .response();

    final JsonObject instance = new JsonObject(resp.body().asString());

    context.assertEquals("Bridget Jones's Baby: the diaries", instance.getString("title"));
  }

  @Test
  public final void testResolveInstanceIdBadUUID(final TestContext context) {
    logger.info("=== Test the resolve instance ID endpoint with an invalid UUID ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/inventory/instances/12345?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.TEXT)
          .statusCode(400)
          .extract()
            .response();

    context.assertEquals("Invalid instanceId format, must be UUID: 12345",
        resp.body().asString());
  }

  @Test
  public final void testResolveHoldingId(final TestContext context) {
    logger.info("=== Test the resolve holding ID endpoint ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/inventory/holdings/6e5283bf-0ca5-43c6-bdcd-6c925b197694?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.JSON)
          .statusCode(200)
          .extract()
            .response();

    final JsonObject holding = new JsonObject(resp.body().asString());

    context.assertEquals("PR6056.I4588 B749 2016", holding.getString("callNumber"));
  }

  @Test
  public final void testResolveHoldingIdBadUUID(final TestContext context) {
    logger.info("=== Test the resolve holding ID endpoint with an invalid UUID ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/inventory/holdings/12345?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.TEXT)
          .statusCode(400)
          .extract()
            .response();

    context.assertEquals("Invalid holdingId format, must be UUID: 12345",
        resp.body().asString());
  }

  @Test
  public final void testResolveItemId(final TestContext context) {
    logger.info("=== Test the resolve item ID endpoint ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/inventory/items/2031073f-3924-40eb-9816-7ad34f8ec203?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.JSON)
          .statusCode(200)
          .extract()
            .response();

    final JsonObject item = new JsonObject(resp.body().asString());

    context.assertEquals("Bridget Jones's Baby: the diaries", item.getString("title"));
  }

  @Test
  public final void testResolveItemIdBadUUID(final TestContext context) {
    logger.info("=== Test the resolve item ID endpoint with an invalid UUID ===");

    final Response resp = RestAssured
        .given()
        .when()
          .get("/resolve/inventory/items/12345?apiKey=" + apiKey)
        .then()
          .contentType(ContentType.TEXT)
          .statusCode(400)
          .extract()
            .response();

    context.assertEquals("Invalid itemId format, must be UUID: 12345",
        resp.body().asString());
  }
}
