package com.github.MartinFlores751;

import com.github.MartinFlores751.jpop.JMoeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class MoeRestClient {
    private static final Logger logger = LoggerFactory.getLogger(JMoeClient.class);
    private static final String API_URL_BASE = "https://listen.moe/api";
    private static String authToken;

    /**
     * Connects to the specified endpoint to then send and receive data
     *
     * @param apiEndpoint Specifies the target end point to send data to
     * @return Connection to specified endpoint with mandatory headers
     * @throws IOException From URL and connection creation
     */
    private static HttpsURLConnection createConnection(String apiEndpoint) throws IOException {
        // Create connection to specified endpoint
        HttpsURLConnection serverConnection = (HttpsURLConnection) new URL(API_URL_BASE + apiEndpoint).openConnection();

        // Set required headers specified by https://docs.listen.moe/api/
        serverConnection.setRequestProperty("Content-Type", "application/json");
        serverConnection.setRequestProperty("Accept", "application/vnd.listen.v4+json");

        return serverConnection;
    }

    /**
     * Logs user in with provided data
     *
     * @param userName User's username for ListenMoe
     * @param password User's password for ListenMoe
     * @throws IOException From creating connection to server endpoint
     */
    public static void loginUser(String userName, String password) throws IOException {
        // Create connection to login endpoint and set method to POST
        HttpsURLConnection loginConnection = createConnection("/login");
        loginConnection.setRequestMethod("POST");

        // Create Login JSON obj
        JsonObject loginJson = Json.createObjectBuilder()
                .add("username", userName)
                .add("password", password)
                .build();

        // Enable body output
        loginConnection.setDoOutput(true);

        // Get stream to create body
        OutputStream outStream = loginConnection.getOutputStream();

        // Write data to body, flush to ensure writing is finished, and close stream
        outStream.write(loginJson.toString().getBytes());
        outStream.flush();
        outStream.close();

        // Check server response code
        int responseCode = loginConnection.getResponseCode();

        // Get response body
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(loginConnection.getInputStream()));

        // Get data as a JsonObject
        JsonObject responseJson = Json.createReader(responseReader).readObject();

        // Log response message
        logger.debug(responseJson.getString("message"));

        switch (responseCode) {
            case HttpsURLConnection.HTTP_OK:
                // Determine if 2FA is needed!
                boolean twoFactorAuthNeeded = responseJson.getBoolean("mfa", false);
                if (twoFactorAuthNeeded) {
                    // Get 2FA input
                } else {
                    // Get auth token
                    authToken = responseJson.getString("token");
                }
                break;
            case HttpsURLConnection.HTTP_BAD_REQUEST:
                // Invalid Body
                logger.warn("Bad password!");
                break;
            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                // Bad Password
                break;
            case HttpsURLConnection.HTTP_FORBIDDEN:
                // Deactivated Account
                break;
        }
    }
}
