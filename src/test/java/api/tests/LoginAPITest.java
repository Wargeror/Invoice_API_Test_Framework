package api.tests;

import api.dto_data_transfer_object.Credentials;
import api.endpoints.LoginAPI;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("api")
@Tag("login")
@DisplayName("Login API Test")
public class LoginAPITest {
    private static final Logger logger = LogManager.getLogger(LoginAPITest.class);

    @Test
    @Tag("positive")
    @DisplayName("Can login with valid username/password")
    public void canLoginWithValidCredentials() {
        logger.info("Starting test: canLoginWithValidCredentials");
        LoginAPI loginAPI = new LoginAPI();
        
        logger.debug("Attempting to login with valid credentials.");
        Response response = loginAPI.login();
        
        Assertions.assertEquals(200, response.statusCode(), 
            "Login failed: Expected HTTP 200 OK but received: " + response.statusCode());
        logger.info("Login successful with status 200.");

        String token = response.jsonPath().getString("token");
        String expiresString = response.jsonPath().getString("expires_string");
        
        Assertions.assertFalse(token == null || token.isEmpty(), 
            "Authentication failed: The 'token' field in the response is empty or missing.");
        Assertions.assertFalse(expiresString == null || expiresString.isEmpty(), 
            "Authentication verification failed: The 'expires_string' field is empty or missing.");
        
        logger.info("Token and expiry data verified successfully. Test finished.");
    }

    @Test
    @Tag("negative")
    @DisplayName("Cant login with invalid username/password")
    public void cantLoginWithInvalidPassword() {
        logger.info("Starting test: cantLoginWithInvalidPassword");
        Credentials credentials = new Credentials();
        LoginAPI loginAPI = new LoginAPI();
        
        logger.debug("Attempting to login with invalid password for user: {}", credentials.getEmail());
        Response response = loginAPI.manualLogin(credentials.getEmail(), "wrong-pass", credentials.getDomain());
        
        Assertions.assertEquals(401, response.statusCode(), 
            "Security vulnerability: Login with incorrect password should have failed with HTTP 401 Unauthorized, but received: " + response.statusCode());
        logger.info("Login correctly rejected with status 401.");

        String errorMsg = response.jsonPath().getString("error");
        Assertions.assertEquals("Wrong username or password", errorMsg, 
            "Unexpected error message: Expected 'Wrong username or password' but got '" + errorMsg + "'.");
        logger.info("Error message verified successfully. Test finished.");
    }

    @Test
    @Tag("negative")
    @DisplayName("Cant login for non-existing company")
    public void cantLoginForNonExistingCompany() {
        logger.info("Starting test: cantLoginForNonExistingCompany");
        Credentials credentials = new Credentials();
        LoginAPI loginAPI = new LoginAPI();
        
        String badDomain = "not-existing-company-2026";
        logger.debug("Attempting to login for non-existing domain: {}", badDomain);
        Response response = loginAPI.manualLogin(credentials.getEmail(), credentials.getPassword(), badDomain);
        
        Assertions.assertEquals(401, response.statusCode(), 
            "Security vulnerability: Login for a non-existent company should have failed with HTTP 401 Unauthorized, but received: " + response.statusCode());
        logger.info("Login correctly rejected with status 401.");

        String errorMsg = response.jsonPath().getString("error");
        Assertions.assertEquals("Firm not found", errorMsg, 
            "Unexpected error message: Expected 'Firm not found' but got '" + errorMsg + "'.");
        logger.info("Error message verified successfully. Test finished.");
    }

    @Test
    @Tag("negative")
    @DisplayName("Cant login with blank email")
    public void cantLoginWithBlankEmail() {
        logger.info("Starting test: cantLoginWithBlankEmail");
        Credentials credentials = new Credentials();
        LoginAPI loginAPI = new LoginAPI();
        
        logger.debug("Attempting to login with a null email parameter.");
        Response response = loginAPI.manualLogin(null, credentials.getPassword(), credentials.getDomain());
        
        Assertions.assertEquals(400, response.statusCode(), 
            "Validation failure: Login request missing an email should have returned HTTP 400 Bad Request, but received: " + response.statusCode());
        logger.info("Login request correctly rejected with status 400.");

        String errorMsg = response.jsonPath().getString("error");
        Assertions.assertEquals("POST argument `email` is missing", errorMsg, 
            "Unexpected error message: Expected 'POST argument `email` is missing' but got '" + errorMsg + "'.");
        logger.info("Error message verified successfully. Test finished.");
    }
}
