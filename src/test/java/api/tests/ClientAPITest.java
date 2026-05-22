package api.tests;

import api.base.BaseTest;
import api.dto_data_transfer_object.Client;
import api.endpoints.ClientAPI;
import api.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("API Tests")
@Feature("Client Management")
@Tag("api")
@Tag("client")
@DisplayName("Client API Test Suite")
public class ClientAPITest extends BaseTest {
    private ClientAPI clientAPI;
    private final List<Integer> createdClientIds = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    public void setUpClientAPI() {
        clientAPI = new ClientAPI(token);
    }

    @AfterEach
    public void tearDownClients() {
        for (int clientId : createdClientIds) {
            logger.debug("Cleaning up created client ID: {}", clientId);
            clientAPI.deleteClient(clientId);
        }
        createdClientIds.clear();
    }

    private Client createDefaultClient() {
        String clientName = "Test Client " + Utils.randomNumeric(5);
        Client client = new Client(clientName);
        client.setBulstat(Utils.generateValidBulstat9());
        client.setMol(Utils.generateRandomString(10));
        client.setIsRegVat(false);
        return client;
    }

    @Test
    @Story("Create a new client")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can create a new client with valid data")
    @Description("Verify that a new client can be successfully created via a POST request to the /clients endpoint.")
    @Tag("positive")
    public void canCreateNewClient() {
        Client client = createDefaultClient();
        logger.debug("Attempting to create client with name: {}", client.getName());

        Response createResponse = clientAPI.createClient(client);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Failed to create client. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("Client created successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
        createdClientIds.add(id); // Add to cleanup list
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
    }

    @Test
    @Story("Retrieve all clients")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can get a list of all clients")
    @Description("Verify that a list of all clients can be retrieved via a GET request to the /clients endpoint.")
    @Tag("positive")
    public void canGetAllClients() {
        logger.debug("Sending request to fetch all clients.");
        Response response = clientAPI.getClients();

        Assertions.assertEquals(200, response.statusCode(),
                "Failed to retrieve clients list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("clients"),
                "The response did not contain a 'clients' list or it was null.");
        logger.info("Clients retrieved successfully.");
    }

    @Test
    @Story("Partially update a client")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can partially update a client's information")
    @Description("Verify that a client's information can be partially updated via a PATCH request to the /clients/{id} endpoint.")
    @Tag("positive")
    public void canPartiallyUpdateClient() {
        Client client = createDefaultClient();
        client.setTown("Sofia");

        logger.debug("Creating initial client for update test: {}", client.getName());
        Response createResponse = clientAPI.createClient(client);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial client. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdClientIds.add(id);

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
    }

    @Test
    @Story("Delete a client")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can delete a client")
    @Description("Verify that a client can be successfully deleted via a DELETE request to the /clients/{id} endpoint.")
    @Tag("positive")
    public void canDeleteClient() {
        Client client = createDefaultClient();
        logger.debug("Creating initial client for deletion test.");
        Response createResponse = clientAPI.createClient(client);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial client. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdClientIds.add(id);

        logger.debug("Attempting to delete client ID: {}", id);
        Response deleteResponse = clientAPI.deleteClient(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(),
                "Failed to delete client. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Client deleted successfully.");
        createdClientIds.remove(Integer.valueOf(id)); // Remove from cleanup list as it's already deleted

        Response getResponse = clientAPI.getClient(id);
        Assertions.assertEquals(404, getResponse.statusCode(),
                "Deletion verification failed: The client still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Client absence verified successfully.");
    }
}