package api.base;

import api.dto_data_transfer_object.Credentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/**
 * Base class for all API endpoint classes.
 * Contains common RestAssured configuration and generic request methods.
 */
public abstract class BaseAPI {
    protected final String token;
    protected final Credentials credentials;
    protected final Gson gson;

    public BaseAPI(String token) {
        this.token = token;
        this.credentials = new Credentials();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Creates the base RequestSpecification with shared configurations
     * (auth, headers, logging).
     * @return A pre-configured RequestSpecification.
     */
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .baseUri(credentials.getBaseUri())
                .basePath("v3")
                .log().all();
    }

    /**
     * Performs a POST request.
     * @param resourcePath The specific path for the resource (e.g., "clients", "items").
     * @param body The DTO object to be sent as the JSON body.
     * @return The RestAssured Response object.
     */
    protected Response post(String resourcePath, Object body) {
        return getRequestSpec()
                .body(gson.toJson(body))
                .when()
                .post(resourcePath)
                .prettyPeek();
    }

    /**
     * Performs a GET request for a single resource by its ID.
     * @param resourcePath The specific path for the resource.
     * @param id The ID of the resource to fetch.
     * @return The RestAssured Response object.
     */
    protected Response get(String resourcePath, int id) {
        return getRequestSpec()
                .when()
                .get(resourcePath + "/" + id)
                .prettyPeek();
    }

    /**
     * Performs a GET request to retrieve all resources.
     * @param resourcePath The specific path for the resource.
     * @return The RestAssured Response object.
     */
    protected Response getAll(String resourcePath) {
        return getRequestSpec()
                .when()
                .get(resourcePath)
                .prettyPeek();
    }

    /**
     * Performs a PUT request to fully update a resource.
     * @param resourcePath The specific path for the resource.
     * @param id The ID of the resource to update.
     * @param body The DTO object representing the full update.
     * @return The RestAssured Response object.
     */
    protected Response put(String resourcePath, int id, Object body) {
        return getRequestSpec()
                .body(gson.toJson(body))
                .when()
                .put(resourcePath + "/" + id)
                .prettyPeek();
    }

    /**
     * Performs a PATCH request to partially update a resource.
     * @param resourcePath The specific path for the resource.
     * @param id The ID of the resource to update.
     * @param fields A map of fields to be updated.
     * @return The RestAssured Response object.
     */
    protected Response patch(String resourcePath, int id, Map<String, Object> fields) {
        return getRequestSpec()
                .body(gson.toJson(fields))
                .when()
                .patch(resourcePath + "/" + id)
                .prettyPeek();
    }

    /**
     * Performs a DELETE request to remove a resource.
     * @param resourcePath The specific path for the resource.
     * @param id The ID of the resource to delete.
     * @return The RestAssured Response object.
     */
    protected Response delete(String resourcePath, int id) {
        return getRequestSpec()
                .when()
                .delete(resourcePath + "/" + id)
                .prettyPeek();
    }
}
