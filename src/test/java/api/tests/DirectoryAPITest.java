package api.tests;

import api.dto_data_transfer_object.Directory;
import api.endpoints.DirectoryAPI;
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

@Tag("api")
@Tag("directory")
@DisplayName("Directory API Test")
public class DirectoryAPITest {
    private static final Logger logger = LogManager.getLogger(DirectoryAPITest.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    @Tag("positive")
    @DisplayName("Can create new directory")
    public void canCreateNewDirectory() {
        logger.info("Starting test: canCreateNewDirectory");
        Utils utils = new Utils();
        String token = TokenManager.getToken();
        DirectoryAPI directoryAPI = new DirectoryAPI(token);

        String dirName = "Test Dir " + utils.generateRandomString(5);
        Directory directory = new Directory(dirName);
        logger.debug("Attempting to create directory with name: {}", dirName);

        Response createResponse = directoryAPI.createDirectory(directory);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Failed to create directory. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("Directory created successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
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

        // Cleanup
        logger.debug("Cleaning up created directory ID: {}", id);
        Response deleteResponse = directoryAPI.deleteDirectory(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test directory. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can get all directories")
    public void canGetAllDirectories() {
        logger.info("Starting test: canGetAllDirectories");
        String token = TokenManager.getToken();
        DirectoryAPI directoryAPI = new DirectoryAPI(token);

        logger.debug("Sending request to fetch all directories.");
        Response response = directoryAPI.getDirectories();

        Assertions.assertEquals(200, response.statusCode(), 
            "Failed to retrieve directories list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("directories"), 
            "The response did not contain a 'directories' list or it was null.");
        logger.info("Directories retrieved successfully. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can update directory")
    public void canUpdateDirectory() {
        logger.info("Starting test: canUpdateDirectory");
        Utils utils = new Utils();
        String token = TokenManager.getToken();
        DirectoryAPI directoryAPI = new DirectoryAPI(token);

        String originalName = "Dir to update " + utils.generateRandomString(5);
        Directory directory = new Directory(originalName);
        
        logger.debug("Creating initial directory for update test: {}", originalName);
        Response createResponse = directoryAPI.createDirectory(directory);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial directory. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        String updatedName = "Updated Dir " + utils.generateRandomString(5);
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

        // Cleanup
        logger.debug("Cleaning up created directory ID: {}", id);
        Response deleteResponse = directoryAPI.deleteDirectory(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test directory. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can delete directory")
    public void canDeleteDirectory() {
        logger.info("Starting test: canDeleteDirectory");
        Utils utils = new Utils();
        String token = TokenManager.getToken();
        DirectoryAPI directoryAPI = new DirectoryAPI(token);

        Directory directory = new Directory("Dir to delete " + utils.generateRandomString(5));
        
        logger.debug("Creating initial directory for deletion test.");
        Response createResponse = directoryAPI.createDirectory(directory);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial directory. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        logger.debug("Attempting to delete directory ID: {}", id);
        Response deleteResponse = directoryAPI.deleteDirectory(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Failed to delete directory. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Directory deleted successfully.");

        Response getResponse = directoryAPI.getDirectory(id);
        Assertions.assertEquals(404, getResponse.statusCode(), 
            "Deletion verification failed: The directory still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Directory absence verified successfully. Test finished.");
    }
}
