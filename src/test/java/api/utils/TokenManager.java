package api.utils;

import api.endpoints.LoginAPI;
import io.restassured.response.Response;

public class TokenManager {
    private static String token;
    private static long expiryTime;

    /**
     * Gets a valid token. If the token is null or expired, it fetches a new one.
     * @return A valid bearer token
     */
    public static synchronized String getToken() {
        long currentTime = System.currentTimeMillis() / 1000L;
        
        // Check if token is null or expires in less than 60 seconds
        if (token == null || currentTime >= (expiryTime - 60)) {
            System.out.println("Token is missing or expired. Fetching a new one...");
            LoginAPI loginAPI = new LoginAPI();
            Response response = loginAPI.login();
            
            if (response.statusCode() == 200) {
                token = response.jsonPath().getString("token");
                expiryTime = response.jsonPath().getLong("expires");
            } else {
                throw new RuntimeException("Failed to fetch token. Status code: " + response.statusCode());
            }
        } else {
            System.out.println("Using cached token.");
        }
        
        return token;
    }
}
