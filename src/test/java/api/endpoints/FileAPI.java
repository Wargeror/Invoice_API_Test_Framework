package api.endpoints;

import api.dto_data_transfer_object.Credentials;
import api.dto_data_transfer_object.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class FileAPI {
    private static final String BASE_PATH = "v3";
    private static final String RESOURCE_PATH = "files";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String token;
    private Credentials credentials;

    public FileAPI(String token) {
        this.token = token;
        this.credentials = new Credentials();
    }

    /**
     * Upload a new file
     * @param file java.io.File to upload
     * @return Response
     */
    public Response uploadFile(java.io.File file) {
        return RestAssured.given()
                .contentType("multipart/form-data")
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .multiPart("file", file)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .when()
                .post(RESOURCE_PATH)
                .prettyPeek();
    }

    /**
     * Get a file by id
     * @param id
     * @return Response
     */
    public Response getFile(int id) {
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
     * Get all files
     * @return Response
     */
    public Response getFiles() {
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
     * Delete a file by id
     * @param id
     * @return Response
     */
    public Response deleteFile(int id) {
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
     * Download a file by id
     * @param id
     * @return Response
     */
    public Response downloadFile(int id) {
        return RestAssured.given()
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .when()
                .get(RESOURCE_PATH + "/" + id + "/download")
                .prettyPeek();
    }
}
