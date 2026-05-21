package api.endpoints;

import api.dto_data_transfer_object.Credentials;
import api.utils.Input;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class LoginAPI {
    private static final String BASE_PATH = "v3";
    private static final String RESOURCE_PATH = "login/token";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Create new GSON instance
    private Credentials credentials;

    public LoginAPI() {
        this.credentials = new Credentials();
    }

    /**
     *
     * @return Response
     */
    public Response login() {
        return RestAssured.given()
                .contentType(ContentType.JSON) //sets content-type header
                .accept(ContentType.JSON) //sets accept header
                .headers("User-Agent", "Mozilla")
                .log().all() //logs all details of the request
                .baseUri(credentials.getBaseUri()) //sets base uri
                .basePath(BASE_PATH) //sets base path
                .body(gson.toJson(credentials)) //This will be transformed into valid json body
                .when()
                .post(RESOURCE_PATH) //sets resource path of the request
                .prettyPeek(); //prints the response in a nice way
    }
    
    /**
     *
     * @param username email
     * @param password password
     * @param domain domain
     * @return Response
     */
    public Response manualLogin(String username, String password, String domain) {
        Credentials manualCredentials = new Credentials(username, password, domain);
        Input input = new Input();
        return RestAssured.given()
                .contentType(ContentType.JSON) //sets content-type header
                .accept(ContentType.JSON) //sets accept header
                .headers("User-Agent", "Mozilla")
                .log().all() //logs all details of the request
                .baseUri(input.getProperty("base_uri")) //sets base uri
                .basePath(BASE_PATH) //sets base path
                .body(gson.toJson(manualCredentials)) //This will be transformed into valid json body
                .when()
                .post(RESOURCE_PATH) //sets resource path of the request
                .prettyPeek(); //prints the response in a nice way
    }

    /**
     * Obtains valid bearer token for a specific user
     * @return token as string
     */
    public String obtainToken() {
        Response response = login();
        return response.jsonPath().getString("token"); //This will extract the value of the token field from the response
    }
}
