package api.tests;

import api.dto_data_transfer_object.Item;
import api.endpoints.ItemAPI;
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
@Tag("item")
@DisplayName("Item API Test")
public class ItemAPITest {
    private static final Logger logger = LogManager.getLogger(ItemAPITest.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    @Tag("positive")
    @DisplayName("Can create new item")
    public void canCreateNewItem() {
        logger.info("Starting test: canCreateNewItem");
        Utils utils = new Utils();
        String token = TokenManager.getToken();
        ItemAPI itemAPI = new ItemAPI(token);

        Item item = new Item(utils.generateRandomString(5), "BGN", "kg", 25.50f);
        logger.debug("Attempting to create item with name: {}", item.getName());

        Response itemResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, itemResponse.statusCode(), 
            "Failed to create item. Expected HTTP 201 Created but received: " + itemResponse.statusCode());
        logger.info("Item created successfully with status 201.");

        int id = itemResponse.jsonPath().getInt("id");
        Assertions.assertTrue(id > 0, "Item creation failed: The returned ID (" + id + ") is invalid or missing.");
        logger.debug("Generated Item ID: {}", id);

        Response getResponse = itemAPI.getItem(id);
        Assertions.assertEquals(200, getResponse.statusCode(), 
            "Failed to fetch the newly created item. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Item createItem = gson.fromJson(responseBody, Item.class);
        Assertions.assertEquals(item.getName(), createItem.getName(), 
            "Data mismatch: The created item's name ('" + createItem.getName() + "') does not match the requested name ('" + item.getName() + "').");
        Assertions.assertEquals(item.getCurrency(), createItem.getCurrency(), 
            "Data mismatch: The created item's currency ('" + createItem.getCurrency() + "') does not match the requested currency ('" + item.getCurrency() + "').");
        logger.info("Item data verified successfully.");

        // Cleanup
        logger.debug("Cleaning up created item ID: {}", id);
        Response deleteResponse = itemAPI.deleteItem(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test item. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can get all items")
    public void canGetAllItems() {
        logger.info("Starting test: canGetAllItems");
        String token = TokenManager.getToken();
        ItemAPI itemAPI = new ItemAPI(token);

        logger.debug("Sending request to fetch all items.");
        Response response = itemAPI.getItems();

        Assertions.assertEquals(200, response.statusCode(), 
            "Failed to retrieve items list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("items"), 
            "The response did not contain an 'items' list or it was null.");
        logger.info("Items retrieved successfully. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can delete item")
    public void canDeleteItem() {
        logger.info("Starting test: canDeleteItem");
        String token = TokenManager.getToken();
        ItemAPI itemAPI = new ItemAPI(token);

        Item item = new Item("Item to delete", "BGN", "pcs", 10.0f);
        logger.debug("Creating initial item for deletion test.");
        
        Response createResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial item. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        logger.debug("Attempting to delete item ID: {}", id);
        Response deleteResponse = itemAPI.deleteItem(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Failed to delete item. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Item deleted successfully.");

        Response getResponse = itemAPI.getItem(id);
        Assertions.assertEquals(404, getResponse.statusCode(), 
            "Deletion verification failed: The item still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Item absence verified successfully. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can update item")
    public void canUpdateItem() {
        logger.info("Starting test: canUpdateItem");
        String token = TokenManager.getToken();
        ItemAPI itemAPI = new ItemAPI(token);

        Item item = new Item("Item to update", "BGN", "pcs", 10.0f);
        logger.debug("Creating initial item for update test.");
        
        Response createResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial item. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        Item updatedItem = new Item("Updated Item", "EUR", "kg", 20.0f);
        logger.debug("Attempting to PUT item ID {} with new data.", id);
        
        Response updateResponse = itemAPI.updateItem(id, updatedItem);
        Assertions.assertEquals(200, updateResponse.statusCode(), 
            "Failed to update item. Expected HTTP 200 OK but received: " + updateResponse.statusCode());
        logger.info("Item updated successfully.");

        Response getResponse = itemAPI.getItem(id);
        Assertions.assertEquals(200, getResponse.statusCode(), 
            "Failed to fetch the updated item. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Item fetchedItem = gson.fromJson(responseBody, Item.class);

        Assertions.assertEquals(updatedItem.getName(), fetchedItem.getName(), 
            "Update failed: Item name does not match updated value.");
        Assertions.assertEquals(updatedItem.getCurrency(), fetchedItem.getCurrency(), 
            "Update failed: Item currency does not match updated value.");
        Assertions.assertEquals(updatedItem.getQuantityUnit(), fetchedItem.getQuantityUnit(), 
            "Update failed: Item quantity unit does not match updated value.");
        Assertions.assertEquals(updatedItem.getPrice(), fetchedItem.getPrice(), 
            "Update failed: Item price does not match updated value.");
        logger.info("Item full update verified successfully.");

        // Cleanup
        logger.debug("Cleaning up created item ID: {}", id);
        Response deleteResponse = itemAPI.deleteItem(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test item. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can partially update item")
    public void canPartiallyUpdateItem() {
        logger.info("Starting test: canPartiallyUpdateItem");
        String token = TokenManager.getToken();
        ItemAPI itemAPI = new ItemAPI(token);

        Item item = new Item("Item to partially update", "BGN", "pcs", 10.0f);
        logger.debug("Creating initial item for partial update test.");
        
        Response createResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial item. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        Map<String, Object> fields = new HashMap<>();
        fields.put("currency", "EUR");

        logger.debug("Attempting to PATCH item ID {} with new currency: EUR", id);
        Response updateResponse = itemAPI.patchItem(id, fields);
        Assertions.assertEquals(204, updateResponse.statusCode(), 
            "Failed to partially update item. Expected HTTP 204 No Content but received: " + updateResponse.statusCode());
        logger.info("Item patched successfully.");

        Response getResponse = itemAPI.getItem(id);
        Assertions.assertEquals(200, getResponse.statusCode(), 
            "Failed to fetch the partially updated item. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Item fetchedItem = gson.fromJson(responseBody, Item.class);

        Assertions.assertEquals("EUR", fetchedItem.getCurrency(), 
            "Partial update failed: The currency was not updated to 'EUR'. Current value is: '" + fetchedItem.getCurrency() + "'.");
        Assertions.assertEquals(item.getName(), fetchedItem.getName(), 
            "Partial update failed: The item name was unexpectedly altered.");
        Assertions.assertEquals(item.getQuantityUnit(), fetchedItem.getQuantityUnit(), 
            "Partial update failed: The quantity unit was unexpectedly altered.");
        Assertions.assertEquals(item.getPrice(), fetchedItem.getPrice(), 
            "Partial update failed: The price was unexpectedly altered.");
        logger.info("Item partial update verified successfully.");

        // Cleanup
        logger.debug("Cleaning up created item ID: {}", id);
        Response deleteResponse = itemAPI.deleteItem(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test item. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }
}
