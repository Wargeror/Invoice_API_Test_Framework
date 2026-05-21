package api.tests;

import api.dto_data_transfer_object.File;
import api.endpoints.FileAPI;
import api.utils.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Tag("api")
@Tag("file")
@DisplayName("File API Test")
public class FileAPITest {
    private static final Logger logger = LogManager.getLogger(FileAPITest.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final boolean SAVE_DOWNLOADED_FILE = false;

    private java.io.File createTempFile() throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("test_file_", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("This is a temporary test file content.");
        }
        logger.debug("Created temporary file for upload: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    @Test
    @Tag("positive")
    @DisplayName("Can upload a new file")
    public void canUploadNewFile() throws IOException {
        logger.info("Starting test: canUploadNewFile");
        String token = TokenManager.getToken();
        FileAPI fileAPI = new FileAPI(token);

        java.io.File uploadFile = createTempFile();

        logger.debug("Attempting to upload file: {}", uploadFile.getName());
        Response createResponse = fileAPI.uploadFile(uploadFile);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Failed to upload file. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("File uploaded successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
        Assertions.assertTrue(id > 0, "File upload failed: The returned ID (" + id + ") is invalid or missing.");
        logger.debug("Generated File ID: {}", id);

        Response getResponse = fileAPI.getFile(id);
        Assertions.assertEquals(200, getResponse.statusCode(), 
            "Failed to fetch the newly uploaded file details. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        File fetchedFile = gson.fromJson(responseBody, File.class);
        Assertions.assertNotNull(fetchedFile.getFilename(), "Data mismatch: The uploaded file's name was null in the response.");
        logger.info("File data verified successfully. Filename: {}", fetchedFile.getFilename());

        // Cleanup
        logger.debug("Cleaning up uploaded file ID: {}", id);
        Response deleteResponse = fileAPI.deleteFile(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test file. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can get all files")
    public void canGetAllFiles() {
        logger.info("Starting test: canGetAllFiles");
        String token = TokenManager.getToken();
        FileAPI fileAPI = new FileAPI(token);

        logger.debug("Sending request to fetch all files.");
        Response response = fileAPI.getFiles();

        Assertions.assertEquals(200, response.statusCode(), 
            "Failed to retrieve files list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("files"), 
            "The response did not contain a 'files' list or it was null.");
        logger.info("Files retrieved successfully. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can delete file")
    public void canDeleteFile() throws IOException {
        logger.info("Starting test: canDeleteFile");
        String token = TokenManager.getToken();
        FileAPI fileAPI = new FileAPI(token);

        java.io.File uploadFile = createTempFile();

        logger.debug("Uploading initial file for deletion test.");
        Response createResponse = fileAPI.uploadFile(uploadFile);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not upload initial file. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        logger.debug("Attempting to delete file ID: {}", id);
        Response deleteResponse = fileAPI.deleteFile(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Failed to delete file. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("File deleted successfully.");

        Response getResponse = fileAPI.getFile(id);
        Assertions.assertEquals(404, getResponse.statusCode(), 
            "Deletion verification failed: The file still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("File absence verified successfully. Test finished.");
    }
    
    @Test
    @Tag("positive")
    @DisplayName("Can download file")
    public void canDownloadFile() throws IOException {
        logger.info("Starting test: canDownloadFile");
        String token = TokenManager.getToken();
        FileAPI fileAPI = new FileAPI(token);

        java.io.File uploadFile = createTempFile();

        logger.debug("Uploading initial file for download test.");
        Response createResponse = fileAPI.uploadFile(uploadFile);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not upload initial file. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        String filename = createResponse.jsonPath().getString("filename");

        logger.debug("Attempting to download file ID: {}", id);
        Response downloadResponse = fileAPI.downloadFile(id);
        Assertions.assertEquals(200, downloadResponse.statusCode(), 
            "Failed to download file. Expected HTTP 200 OK but received: " + downloadResponse.statusCode());
        logger.info("File downloaded successfully.");
        
        byte[] fileData = downloadResponse.getBody().asByteArray();
        Assertions.assertTrue(fileData.length > 0, "Download failed: The downloaded file data is unexpectedly empty (0 bytes).");
        logger.debug("Downloaded file size: {} bytes.", fileData.length);

        if (SAVE_DOWNLOADED_FILE) {
            java.nio.file.Path downloadPath = Paths.get("downloads");
            if (!Files.exists(downloadPath)) {
                Files.createDirectories(downloadPath);
            }
            Files.write(downloadPath.resolve(filename), fileData);
            logger.info("Downloaded file actively saved to disk at: {}", downloadPath.resolve(filename));
        }

        // Cleanup
        logger.debug("Cleaning up uploaded file ID: {}", id);
        Response deleteResponse = fileAPI.deleteFile(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test file. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }
}
