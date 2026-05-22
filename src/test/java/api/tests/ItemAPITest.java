package api.tests;

import api.base.BaseTest;
import api.dto_data_transfer_object.Item;
import api.endpoints.ItemAPI;
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
@Feature("Item Management")
@Tag("api")
@Tag("item")
@DisplayName("Item API Test Suite")
public class ItemAPITest extends BaseTest {
    private ItemAPI itemAPI;
    private final List<Integer> createdItemIds = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    public void setUpItemAPI() {
        itemAPI = new ItemAPI(token);
    }

    @AfterEach
    public void tearDownItems() {
        for (int itemId : createdItemIds) {
            logger.debug("Cleaning up created item ID: {}", itemId);
            itemAPI.deleteItem(itemId);
        }
        createdItemIds.clear();
    }

    @Test
    @Story("Create a new item")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can create a new item")
    @Description("Verify that a new item can be successfully created via a POST request to the /items endpoint.")
    @Tag("positive")
    public void canCreateNewItem() {
        Item item = new Item(Utils.generateRandomString(5), "BGN", "kg", 25.50f);
        logger.debug("Attempting to create item with name: {}", item.getName());

        Response itemResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, itemResponse.statusCode(),
                "Failed to create item. Expected HTTP 201 Created but received: " + itemResponse.statusCode());
        logger.info("Item created successfully with status 201.");

        int id = itemResponse.jsonPath().getInt("id");
        createdItemIds.add(id);
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
    }

    @Test
    @Story("Retrieve all items")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can get a list of all items")
    @Description("Verify that a list of all items can be retrieved via a GET request to the /items endpoint.")
    @Tag("positive")
    public void canGetAllItems() {
        logger.debug("Sending request to fetch all items.");
        Response response = itemAPI.getItems();

        Assertions.assertEquals(200, response.statusCode(),
                "Failed to retrieve items list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("items"),
                "The response did not contain an 'items' list or it was null.");
        logger.info("Items retrieved successfully.");
    }

    @Test
    @Story("Delete an item")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can delete an item")
    @Description("Verify that an item can be successfully deleted via a DELETE request to the /items/{id} endpoint.")
    @Tag("positive")
    public void canDeleteItem() {
        Item item = new Item("Item to delete", "BGN", "pcs", 10.0f);
        logger.debug("Creating initial item for deletion test.");

        Response createResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial item. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdItemIds.add(id);

        logger.debug("Attempting to delete item ID: {}", id);
        Response deleteResponse = itemAPI.deleteItem(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(),
                "Failed to delete item. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Item deleted successfully.");
        createdItemIds.remove(Integer.valueOf(id));

        Response getResponse = itemAPI.getItem(id);
        Assertions.assertEquals(404, getResponse.statusCode(),
                "Deletion verification failed: The item still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Item absence verified successfully.");
    }

    @Test
    @Story("Update an item")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can update an item")
    @Description("Verify that an item's information can be updated via a PUT request to the /items/{id} endpoint.")
    @Tag("positive")
    public void canUpdateItem() {
        Item item = new Item("Item to update", "BGN", "pcs", 10.0f);
        logger.debug("Creating initial item for update test.");

        Response createResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial item. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdItemIds.add(id);

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
    }

    @Test
    @Story("Partially update an item")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can partially update an item")
    @Description("Verify that an item's information can be partially updated via a PATCH request to the /items/{id} endpoint.")
    @Tag("positive")
    public void canPartiallyUpdateItem() {
        Item item = new Item("Item to partially update", "BGN", "pcs", 10.0f);
        logger.debug("Creating initial item for partial update test.");

        Response createResponse = itemAPI.createItem(item);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial item. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdItemIds.add(id);

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
    }
}