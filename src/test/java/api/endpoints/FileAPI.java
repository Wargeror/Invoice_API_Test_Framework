package api.endpoints;

import api.base.BaseAPI;
import api.base.Endpoint;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class FileAPI extends BaseAPI {

    public FileAPI(String token) {
        super(token);
    }

    public Response uploadFile(java.io.File file) {
        return RestAssured.given()
                .contentType("multipart/form-data")
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .multiPart("file", file)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath("v3")
                .when()
                .post(Endpoint.FILES.getPath())
                .prettyPeek();
    }

    public Response getFile(int id) {
        return get(Endpoint.FILES.getPath(), id);
    }

    public Response getFiles() {
        return getAll(Endpoint.FILES.getPath());
    }

    public Response deleteFile(int id) {
        return delete(Endpoint.FILES.getPath(), id);
    }
    
    public Response downloadFile(int id) {
        return getRequestSpec()
                .when()
                .get(Endpoint.FILES.getPath() + "/" + id + "/download")
                .prettyPeek();
    }
}
