package api.endpoints;

import api.dto_data_transfer_object.Credentials;
import api.dto_data_transfer_object.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class ItemAPI {
    private static final String BASE_PATH = "v3";
    private static final String RESOURCE_PATH = "items";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Create new GSON instance
    private String token;
    private Credentials credentials;

    public ItemAPI (String token){
        this.token = token;
        this.credentials = new Credentials();
    }

    /**
     *
     * @param item
     * @return Response
     */
    //POST operation
    public Response createItem(Item item) {
        return RestAssured.given()
                .contentType(ContentType.JSON) //sets content-type header
                .accept(ContentType.JSON) //sets accept header
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                //.auth().oauth2(token) //Set bearer token as authorization header (Authorization | Bearer token)
                .log().all() //logs all details of the request
                .baseUri(credentials.getBaseUri()) //sets base uri
                .basePath(BASE_PATH) //sets base path
                .body(gson.toJson(item)) //This will be transformed into valid json body
                .when()
                .post(RESOURCE_PATH) //sets resource path of the request to be POST (verb)
                .prettyPeek(); //prints the response in a nice way
    }

    //GET operation
    public Response getItem(int id) {
        return RestAssured.given()
                .contentType(ContentType.JSON) //sets content-type header
                .accept(ContentType.JSON) //sets accept header
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                //.auth().oauth2(token) //Set bearer token as authorization header (Authorization | Bearer token)
                .log().all() //logs all details of the request
                .baseUri(credentials.getBaseUri()) //sets base uri
                .basePath(BASE_PATH) //sets base path
                .when()
                .get(RESOURCE_PATH + "/" + id) //sets resource path of the request to be GET (verb)
                .prettyPeek(); //prints the response in a nice way
    }

    //GET operation
    public Response getItems() {
        return RestAssured.given()
                .contentType(ContentType.JSON) //sets content-type header
                .accept(ContentType.JSON) //sets accept header
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                //.auth().oauth2(token) //Set bearer token as authorization header (Authorization | Bearer token)
                .log().all() //logs all details of the request
                .baseUri(credentials.getBaseUri()) //sets base uri
                .basePath(BASE_PATH) //sets base path
                .when()
                .get(RESOURCE_PATH) //sets resource path of the request to be GET (verb)
                .prettyPeek(); //prints the response in a nice way
    }

    //DELETE operation
    public Response deleteItem(int id){
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

    //PUT operation
    public Response updateItem(int id, Item updateItem){
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .body(gson.toJson(updateItem))
                .when()
                .put(RESOURCE_PATH + "/" + id)
                .prettyPeek();
    }

    //PATCH operation
    public Response patchItem(int id, Map<String, Object> fields){
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