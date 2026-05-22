package api.tests;

import api.base.BaseTest;
import api.dto_data_transfer_object.Directory;
import api.endpoints.DirectoryAPI;
import api.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

@Epic("API Tests")
@Feature("Directory Management")
@Tag("api")
@Tag("directory")
@DisplayName("Directory API Test Suite")
public class DirectoryAPITest extends BaseTest {
    private DirectoryAPI directoryAPI;
    private final List<Integer> createdDirectoryIds = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    public void setUpDirectoryAPI() {
        directoryAPI = new DirectoryAPI(token);
    }

    @AfterEach
    public void tearDownDirectories() {
        for (int dirId : createdDirectoryIds) {
            logger.debug("Cleaning up created directory ID: {}", dirId);
            directoryAPI.deleteDirectory(dirId);
        }
        createdDirectoryIds.clear();
    }

    @Test
    @Story("Create a new directory")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can create a new directory with a valid name")
    @Description("Verify that a new directory can be successfully created via a POST request to the /directories endpoint.")
    @Tag("positive")
    public void canCreateNewDirectory() {
        String dirName = "Test Dir " + Utils.generateRandomString(5);
        Directory directory = new Directory(dirName);
        logger.debug("Attempting to create directory with name: {}", dirName);

        Response createResponse = directoryAPI.createDirectory(directory);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Failed to create directory. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("Directory created successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
        createdDirectoryIds.add(id);
        Assertions.assertTrue(id > 0, "Directory creation failed: The returned ID (" + id + ") is invalid or missing.");
        logger.debug("Generated Directory ID: {}", id);

        Response getResponse = directoryAPI.getDirectory(id);
        Assertions.assertEquals(200, getResponse.statusCode(),
                "Failed to fetch the newly created directory. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Directory createdDirectory = gson.fromJson(responseBody, Directory.class);
        Assertions.assertEquals(directory.getName(), createdDirectory.getName(),
                "Data mismatch: The created directory's name ('" + createdDirectory.getName() + "') does not match the requested name ('" + directory.getName() + "').");
        logger.info("Directory data verified successfully.");
    }

    @Test
    @Story("Retrieve all directories")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can get a list of all directories")
    @Description("Verify that a list of all directories can be retrieved via a GET request to the /directories endpoint.")
    @Tag("positive")
    public void canGetAllDirectories() {
        logger.debug("Sending request to fetch all directories.");
        Response response = directoryAPI.getDirectories();

        Assertions.assertEquals(200, response.statusCode(),
                "Failed to retrieve directories list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("directories"),
                "The response did not contain a 'directories' list or it was null.");
        logger.info("Directories retrieved successfully.");
    }

    @Test
    @Story("Update a directory")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can update a directory's name")
    @Description("Verify that a directory's name can be updated via a PUT request to the /directories/{id} endpoint.")
    @Tag("positive")
    public void canUpdateDirectory() {
        String originalName = "Dir to update " + Utils.generateRandomString(5);
        Directory directory = new Directory(originalName);

        logger.debug("Creating initial directory for update test: {}", originalName);
        Response createResponse = directoryAPI.createDirectory(directory);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial directory. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdDirectoryIds.add(id);

        String updatedName = "Updated Dir " + Utils.generateRandomString(5);
        Directory updatedDirectory = new Directory(updatedName);

        logger.debug("Attempting to PUT directory ID {} with new name: {}", id, updatedName);
        Response updateResponse = directoryAPI.updateDirectory(id, updatedDirectory);
        Assertions.assertEquals(200, updateResponse.statusCode(),
                "Failed to update directory. Expected HTTP 200 OK but received: " + updateResponse.statusCode());
        logger.info("Directory updated successfully.");

        Response getResponse = directoryAPI.getDirectory(id);
        Assertions.assertEquals(200, getResponse.statusCode(),
                "Failed to fetch the updated directory. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Directory fetchedDirectory = gson.fromJson(responseBody, Directory.class);

        Assertions.assertEquals(updatedDirectory.getName(), fetchedDirectory.getName(),
                "Update failed: The directory name was not updated correctly. Expected: '" + updatedDirectory.getName() + "', but got: '" + fetchedDirectory.getName() + "'.");
        logger.info("Directory update verified successfully.");
    }

    @Test
    @Story("Delete a directory")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can delete a directory")
    @Description("Verify that a directory can be successfully deleted via a DELETE request to the /directories/{id} endpoint.")
    @Tag("positive")
    public void canDeleteDirectory() {
        Directory directory = new Directory("Dir to delete " + Utils.generateRandomString(5));

        logger.debug("Creating initial directory for deletion test.");
        Response createResponse = directoryAPI.createDirectory(directory);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial directory. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdDirectoryIds.add(id);

        logger.debug("Attempting to delete directory ID: {}", id);
        Response deleteResponse = directoryAPI.deleteDirectory(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(),
                "Failed to delete directory. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Directory deleted successfully.");
        createdDirectoryIds.remove(Integer.valueOf(id));

        Response getResponse = directoryAPI.getDirectory(id);
        Assertions.assertEquals(404, getResponse.statusCode(),
                "Deletion verification failed: The directory still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Directory absence verified successfully.");
    }
}