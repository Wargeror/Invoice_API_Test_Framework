package api.tests;

import api.base.BaseTest;
import api.dto_data_transfer_object.File;
import api.endpoints.FileAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Epic("API Tests")
@Feature("File Management")
@Tag("api")
@Tag("file")
@DisplayName("File API Test Suite")
public class FileAPITest extends BaseTest {
    private FileAPI fileAPI;
    private final List<Integer> createdFileIds = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final boolean SAVE_DOWNLOADED_FILE = false;

    @BeforeEach
    public void setUpFileAPI() {
        fileAPI = new FileAPI(token);
    }

    @AfterEach
    public void tearDownFiles() {
        for (int fileId : createdFileIds) {
            logger.debug("Cleaning up created file ID: {}", fileId);
            fileAPI.deleteFile(fileId);
        }
        createdFileIds.clear();
    }

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
    @Story("Upload a new file")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can upload a new file")
    @Description("Verify that a new file can be successfully uploaded via a POST request to the /files endpoint.")
    @Tag("positive")
    public void canUploadNewFile() throws IOException {
        java.io.File uploadFile = createTempFile();

        logger.debug("Attempting to upload file: {}", uploadFile.getName());
        Response createResponse = fileAPI.uploadFile(uploadFile);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Failed to upload file. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("File uploaded successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
        createdFileIds.add(id);
        Assertions.assertTrue(id > 0, "File upload failed: The returned ID (" + id + ") is invalid or missing.");
        logger.debug("Generated File ID: {}", id);

        Response getResponse = fileAPI.getFile(id);
        Assertions.assertEquals(200, getResponse.statusCode(),
                "Failed to fetch the newly uploaded file details. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        File fetchedFile = gson.fromJson(responseBody, File.class);
        Assertions.assertNotNull(fetchedFile.getFilename(), "Data mismatch: The uploaded file's name was null in the response.");
        logger.info("File data verified successfully. Filename: {}", fetchedFile.getFilename());
    }

    @Test
    @Story("Retrieve all files")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can get a list of all files")
    @Description("Verify that a list of all files can be retrieved via a GET request to the /files endpoint.")
    @Tag("positive")
    public void canGetAllFiles() {
        logger.debug("Sending request to fetch all files.");
        Response response = fileAPI.getFiles();

        Assertions.assertEquals(200, response.statusCode(),
                "Failed to retrieve files list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("files"),
                "The response did not contain a 'files' list or it was null.");
        logger.info("Files retrieved successfully.");
    }

    @Test
    @Story("Delete a file")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can delete a file")
    @Description("Verify that a file can be successfully deleted via a DELETE request to the /files/{id} endpoint.")
    @Tag("positive")
    public void canDeleteFile() throws IOException {
        java.io.File uploadFile = createTempFile();

        logger.debug("Uploading initial file for deletion test.");
        Response createResponse = fileAPI.uploadFile(uploadFile);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not upload initial file. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdFileIds.add(id);

        logger.debug("Attempting to delete file ID: {}", id);
        Response deleteResponse = fileAPI.deleteFile(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(),
                "Failed to delete file. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("File deleted successfully.");
        createdFileIds.remove(Integer.valueOf(id));

        Response getResponse = fileAPI.getFile(id);
        Assertions.assertEquals(404, getResponse.statusCode(),
                "Deletion verification failed: The file still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("File absence verified successfully.");
    }

    @Test
    @Story("Download a file")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can download a file")
    @Description("Verify that a file can be successfully downloaded via a GET request to the /files/{id}/download endpoint.")
    @Tag("positive")
    public void canDownloadFile() throws IOException {
        java.io.File uploadFile = createTempFile();

        logger.debug("Uploading initial file for download test.");
        Response createResponse = fileAPI.uploadFile(uploadFile);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not upload initial file. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdFileIds.add(id);
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
    }
}