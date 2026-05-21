package api.endpoints;

import api.dto_data_transfer_object.Credentials;
import api.dto_data_transfer_object.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class ClientAPI {
    // Base path for the API
    private static final String BASE_PATH = "v3";
    // Resource path for the API
    private static final String RESOURCE_PATH = "clients";
    // Gson instance for serialization and deserialization
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // Token for authentication
    private String token;
    // Credentials for authentication
    private Credentials credentials;


    public ClientAPI(String token) {
        this.token = token;
        this.credentials = new Credentials();
    }

    /**
     * Create a new client
     * @param client
     * @return
     */
    public Response createClient(Client client) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .body(gson.toJson(client))
                .when()
                .post(RESOURCE_PATH)
                .prettyPeek();
    }

    /**
     * Get a client by id
     * @param id
     * @return
     */
    public Response getClient(int id) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .when()
                .get(RESOURCE_PATH + "/" + id)
                .prettyPeek();
    }

    /**
     * Get all clients
     * @return
     */
    public Response getClients() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .when()
                .get(RESOURCE_PATH)
                .prettyPeek();
    }

    /**
     * Delete a client by id
     * @param id
     * @return
     */
    public Response deleteClient(int id) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .when()
                .delete(RESOURCE_PATH + "/" + id)
                .prettyPeek();
    }

    /**
     * Patch a client by id
     * @param id
     * @param fields
     * @return
     */
    public Response patchClient(int id, Map<String, Object> fields) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .body(gson.toJson(fields))
                .when()
                .patch(RESOURCE_PATH + "/" + id)
                .prettyPeek();
    }
}
