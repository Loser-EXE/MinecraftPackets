package com.loserexe.auth;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loserexe.pojo.microsoft.DeviceAuthJson;
import com.loserexe.pojo.microsoft.UserAuthJson;
import com.loserexe.pojo.microsoft.XBLAuthJson;

public class PlayerAuth {
    private final String TENANT = "consumers";
    private final String OAUTH = "/oauth2/v2.0/";
    private final String BASE_MICROSOFT_AUTH_URL = "https://login.microsoftonline.com/"+TENANT+OAUTH;
    private final String CLIENT_ID = "c5bf15ef-7ebd-4455-88e0-2366b1dc98ff";
    private final String DEVICE_AUTH_REQUEST_URL = BASE_MICROSOFT_AUTH_URL+"devicecode";
    private final String MICROSOFT_USER_AUTH_URL = BASE_MICROSOFT_AUTH_URL+"token";
    private final String XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private final String SCOPE = "Xboxlive.signin XboxLive.offline_access";
    private final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:device_code";
    private final String APPLICATION_JSON = "application/json";
    private final Header applicationUrlencodedHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    private final Header contentTypeJson = new BasicHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    private final Header acceptJson = new BasicHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    private int interval;
    private String DEVICE_CODE;
    private String accessToken;

    private String XBLToken;
    private String UserHash;

    public PlayerAuth(String accessToken) throws IOException, InterruptedException{
        this.accessToken = accessToken;

        authXBL();
    }

    public PlayerAuth() throws IOException, InterruptedException{
        authXBL();
    }

    private void authDevice() throws IOException{
        logger.info("Started Device authorization request");
        HttpPost httpPost = new HttpPost(DEVICE_AUTH_REQUEST_URL);
        httpPost.setHeader(applicationUrlencodedHeader);

        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("scope", SCOPE));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            byte[] responseBytes = response.getEntity().getContent().readAllBytes();
            DeviceAuthJson responseJson = gson.fromJson(new String(responseBytes), DeviceAuthJson.class);
            DEVICE_CODE = responseJson.getDeviceCode();
            interval = responseJson.getInterval();
            System.out.println(responseJson.getMessage());
        }
    }

    private void authUser() throws IOException, InterruptedException{
        authDevice();
        logger.info("Authenticating user");
        HttpPost httpPost = new HttpPost(MICROSOFT_USER_AUTH_URL);
        httpPost.setHeader(applicationUrlencodedHeader);

        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("device_code", DEVICE_CODE));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        while (true) {
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                byte[] responseBytes = response.getEntity().getContent().readAllBytes();
                UserAuthJson responseJson = gson.fromJson(new String(responseBytes), UserAuthJson.class);

                if (responseJson.getError() != null && !responseJson.getError().equals("authorization_pending")) {
                    logger.error(responseJson.getError()+ ": " + responseJson.getErrorDescription());
                    throw new IOException((responseJson.getError()+ ": " + responseJson.getErrorDescription()));
                }

                if (responseJson.getAccessToken() != null) {
                    this.accessToken = responseJson.getAccessToken();
                    break;
                }
            }
            Thread.sleep(interval * 1000);
        }
    }

    private void authXBL() throws IOException, InterruptedException{
        if (accessToken == null) authUser();

        Header[] headers = new Header[2];
        headers[0] = contentTypeJson;
        headers[1] = acceptJson;

        HttpPost httpPost = new HttpPost(XBL_AUTH_URL);
        httpPost.setHeaders(headers);

        String json = gson.toJson(new XBLAuthJson(this.accessToken));
        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            byte[] responseBytes = response.getEntity().getContent().readAllBytes();
            XBLAuthJson responseJson = gson.fromJson(new String(responseBytes), XBLAuthJson.class);
            this.XBLToken = responseJson.getToken();
            this.UserHash = responseJson.getDisplayClaims().getXui()[0].getUserHash();
        }
    }
}
