package api.tests;

import api.dto_data_transfer_object.Client;
import api.endpoints.ClientAPI;
import api.utils.TokenManager;
import api.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Tag("api")
@Tag("client")
@DisplayName("Client API Test")
public class ClientAPITest {
    private static final Logger logger = LogManager.getLogger(ClientAPITest.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    @Tag("positive")
    @DisplayName("Can create new client")
    public void canCreateNewClient() {
        logger.info("Starting test: canCreateNewClient");
        Utils utils = new Utils();
        String token = TokenManager.getToken();
        ClientAPI clientAPI = new ClientAPI(token);

        String clientName = "Test Client " + utils.randomNumeric(5);
        Client client = new Client(clientName);
        logger.debug("Attempting to create client with name: {}", clientName);

        Response createResponse = clientAPI.createClient(client);
        
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Failed to create client. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("Client created successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
        Assertions.assertTrue(id > 0, "Client creation failed: The returned ID (" + id + ") is invalid or missing.");
        logger.debug("Generated Client ID: {}", id);

        Response getResponse = clientAPI.getClient(id);
        Assertions.assertEquals(200, getResponse.statusCode(), 
            "Failed to fetch the newly created client. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Client createdClient = gson.fromJson(responseBody, Client.class);
        Assertions.assertEquals(client.getName(), createdClient.getName(), 
            "Data mismatch: The created client's name ('" + createdClient.getName() + "') does not match the requested name ('" + client.getName() + "').");
        logger.info("Client data verified successfully.");

        // Cleanup
        logger.debug("Cleaning up created client ID: {}", id);
        Response deleteResponse = clientAPI.deleteClient(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test client. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can get all clients")
    public void canGetAllClients() {
        logger.info("Starting test: canGetAllClients");
        String token = TokenManager.getToken();
        ClientAPI clientAPI = new ClientAPI(token);

        logger.debug("Sending request to fetch all clients.");
        Response response = clientAPI.getClients();

        Assertions.assertEquals(200, response.statusCode(), 
            "Failed to retrieve clients list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("clients"), 
            "The response did not contain a 'clients' list or it was null.");
        logger.info("Clients retrieved successfully. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can partially update client")
    public void canPartiallyUpdateClient() {
        logger.info("Starting test: canPartiallyUpdateClient");
        Utils utils = new Utils();
        String token = TokenManager.getToken();
        ClientAPI clientAPI = new ClientAPI(token);

        String clientName = "Test Client " + utils.randomNumeric(5);
        Client client = new Client(clientName);
        client.setTown("Sofia");

        logger.debug("Creating initial client for update test: {}", clientName);
        Response createResponse = clientAPI.createClient(client);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial client. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        Map<String, Object> fields = new HashMap<>();
        fields.put("town", "Plovdiv");

        logger.debug("Attempting to PATCH client ID {} with new town: Plovdiv", id);
        Response updateResponse = clientAPI.patchClient(id, fields);
        Assertions.assertEquals(204, updateResponse.statusCode(), 
            "Failed to update client. Expected HTTP 204 No Content but received: " + updateResponse.statusCode());
        logger.info("Client updated successfully.");

        Response getResponse = clientAPI.getClient(id);
        Assertions.assertEquals(200, getResponse.statusCode(), 
            "Failed to fetch the updated client. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Client fetchedClient = gson.fromJson(responseBody, Client.class);

        Assertions.assertEquals("Plovdiv", fetchedClient.getTown(), 
            "Partial update failed: The town was not updated to 'Plovdiv'. Current value is: '" + fetchedClient.getTown() + "'.");
        Assertions.assertEquals(client.getName(), fetchedClient.getName(), 
            "Partial update failed: The client name was unexpectedly altered during the town update.");
        logger.info("Client partial update verified successfully.");

        // Cleanup
        logger.debug("Cleaning up created client ID: {}", id);
        Response deleteResponse = clientAPI.deleteClient(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test client. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can delete client")
    public void canDeleteClient() {
        logger.info("Starting test: canDeleteClient");
        Utils utils = new Utils();
        String token = TokenManager.getToken();
        ClientAPI clientAPI = new ClientAPI(token);

        Client client = new Client("Client to delete " + utils.randomNumeric(5));
        logger.debug("Creating initial client for deletion test.");
        Response createResponse = clientAPI.createClient(client);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial client. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        logger.debug("Attempting to delete client ID: {}", id);
        Response deleteResponse = clientAPI.deleteClient(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Failed to delete client. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Client deleted successfully.");

        Response getResponse = clientAPI.getClient(id);
        Assertions.assertEquals(404, getResponse.statusCode(), 
            "Deletion verification failed: The client still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Client absence verified successfully. Test finished.");
    }
}
