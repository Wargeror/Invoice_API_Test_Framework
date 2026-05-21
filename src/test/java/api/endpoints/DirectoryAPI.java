package api.endpoints;

import api.dto_data_transfer_object.Credentials;
import api.dto_data_transfer_object.Directory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;


public class DirectoryAPI {
    private static final String BASE_PATH = "v3";
    private static final String RESOURCE_PATH = "directories";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String token;
    private Credentials credentials;

    public DirectoryAPI(String token) {
        this.token = token;
        this.credentials = new Credentials();
    }

    /**
     * Create a new directory
     * @param directory
     * @return Response
     */
    public Response createDirectory(Directory directory) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .body(gson.toJson(directory))
                .when()
                .post(RESOURCE_PATH)
                .prettyPeek();
    }

    /**
     * Get a directory by id
     * @param id
     * @return Response
     */
    public Response getDirectory(int id) {
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
     * Get all directories
     * @return Response
     */
    public Response getDirectories() {
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
     * Delete a directory by id
     * @param id
     * @return Response
     */
    public Response deleteDirectory(int id) {
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
     * Update a directory
     * @param id
     * @param directory
     * @return Response
     */
    public Response updateDirectory(int id, Directory directory) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .body(gson.toJson(directory))
                .when()
                .put(RESOURCE_PATH + "/" + id)
                .prettyPeek();
    }
}
