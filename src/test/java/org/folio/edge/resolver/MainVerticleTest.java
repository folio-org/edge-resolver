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

    context.assertEquals("Invalid user ID format, must be UUID: 12345",
        resp.body().asString());
  }
}
