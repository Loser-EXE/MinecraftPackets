package com.loserexe.http.microsoft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.loserexe.pojo.microsoft.AuthUserJson;
import com.loserexe.pojo.microsoft.DeviceAuthJson;

public class AccessToken {
    private static final String TENANT = "consumers";
    private static final String AUTH_USER_URL = "https://login.microsoftonline.com/"+TENANT+"/oauth2/v2.0/token";
    private static final String DEVICE_AUTH_URL = "https://login.microsoftonline.com/"+TENANT+"/oauth2/v2.0/devicecode";
    private static final String CLIENT_ID = "c5bf15ef-7ebd-4455-88e0-2366b1dc98ff";
    private static final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    private static final Logger logger = LogManager.getLogger(AccessToken.class.getName());
    private static final Gson gson = new Gson();
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static DeviceAuthJson deviceAuthJson;
    private static AuthUserJson authUserJson;

    public static AuthUserJson authUser() throws ClientProtocolException, IOException, InterruptedException {
        deviceAuthRequest();

        logger.info("Started User Auth...");
        HttpPost httpPost = new HttpPost(AUTH_USER_URL);
        httpPost.setHeader(header);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:device_code"));
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("device_code", deviceAuthJson.getDeviceCode()));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        while (true) {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            byte[] responseBytes = httpResponse.getEntity().getContent().readAllBytes();
            authUserJson = gson.fromJson(new String(responseBytes), AuthUserJson.class);

            if (authUserJson.getError() != null && !authUserJson.getError().equals("authorization_pending")) {
                logger.fatal(authUserJson.getError());
                throw new IOException(authUserJson.getErrorDescription());
            }

            if (authUserJson.getAccessToken() != null) {
                return authUserJson;
            }

            Thread.sleep(deviceAuthJson.getInterval() * 1000);
        }
    }

    private static void deviceAuthRequest() throws ClientProtocolException, IOException {
        logger.info("Started Device Auth Request...");
        HttpPost httpPost = new HttpPost(DEVICE_AUTH_URL);
        httpPost.setHeader(header);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("scope", "user.read openid profile"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        byte[] responseBytes = httpResponse.getEntity().getContent().readAllBytes();
        deviceAuthJson = gson.fromJson(new String(responseBytes), DeviceAuthJson.class);

        System.out.println(deviceAuthJson.getMessage());
    }
}
