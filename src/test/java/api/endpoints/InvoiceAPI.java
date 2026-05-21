package api.endpoints;

import api.dto_data_transfer_object.Credentials;
import api.dto_data_transfer_object.Invoice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;


public class InvoiceAPI {
    private static final String BASE_PATH = "v3";
    private static final String RESOURCE_PATH = "invoices";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String token;
    private Credentials credentials;

    public InvoiceAPI(String token) {
        this.token = token;
        this.credentials = new Credentials();
    }

    /**
     * Create a new invoice
     * @param invoice
     * @return Response
     */
    public Response createInvoice(Invoice invoice) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .body(gson.toJson(invoice))
                .when()
                .post(RESOURCE_PATH)
                .prettyPeek();
    }

    /**
     * Get an invoice by id
     * @param id
     * @return Response
     */
    public Response getInvoice(int id) {
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
     * Get all invoices
     * @return Response
     */
    public Response getInvoices() {
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
     * Delete an invoice by id
     * @param id
     * @return Response
     */
    public Response deleteInvoice(int id) {
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
     * Update an invoice
     * @param id
     * @param invoice
     * @return Response
     */
    public Response updateInvoice(int id, Invoice invoice) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("User-Agent", "Mozilla")
                .header("Authorization", "Bearer " + token)
                .log().all()
                .baseUri(credentials.getBaseUri())
                .basePath(BASE_PATH)
                .body(gson.toJson(invoice))
                .when()
                .put(RESOURCE_PATH + "/" + id)
                .prettyPeek();
    }

    /**
     * Patch an invoice
     * @param id
     * @param fields
     * @return Response
     */
    public Response patchInvoice(int id, Map<String, Object> fields) {
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
