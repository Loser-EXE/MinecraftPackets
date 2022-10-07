package com.loserexe.auth;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import com.loserexe.pojo.minecraft.AuthMinecraft;
import com.loserexe.pojo.minecraft.PlayerProfileJson;

public class PlayerAuth {
    private final String TENANT = "consumers";
    private final String OAUTH = "/oauth2/v2.0/";
    private final String BASE_MICROSOFT_AUTH_URL = "https://login.microsoftonline.com/"+TENANT+OAUTH;
    private final String CLIENT_ID = "c5bf15ef-7ebd-4455-88e0-2366b1dc98ff";
    private final String DEVICE_AUTH_REQUEST_URL = BASE_MICROSOFT_AUTH_URL+"devicecode";
    private final String MICROSOFT_USER_AUTH_URL = BASE_MICROSOFT_AUTH_URL+"token";
    private final String XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private final String XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
	private final String AUTH_MINE_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private final String GET_MINE_PROFILE = "https://api.minecraftservices.com/minecraft/profile";
    private final String SCOPE = "Xboxlive.signin XboxLive.offline_access";
    private final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:device_code";
    private final String APPLICATION_JSON = "application/json";
    private final Header applicationUrlencodedHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    private final Header contentTypeJson = new BasicHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    private final Header acceptJson = new BasicHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
    private Header mineAuthHeader;
    private final Header[] contentAndAcceptJson = new Header[2];
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    private String MineAccessToken;

    public PlayerAuth() throws IOException, InterruptedException{
        String accessToken = authMicrosoftAccount();
        String[] XBLAuth = authXBL(accessToken);

        getMinecraftProfile(XBLAuth);

    }

    private <T> T parseJsonResponse(HttpPost httpPost, Class<T> clazz) throws IOException{
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            byte[] responseBytes = response.getEntity().getContent().readAllBytes();
            logger.debug("Got Response: " + new String(responseBytes));
            T responseJson = gson.fromJson(new String(responseBytes), clazz);
            return responseJson;
        }
    }


    private <T> T parseJsonResponseGet(HttpGet httpGet, Class<T> clazz) throws IOException{
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            byte[] responseBytes = response.getEntity().getContent().readAllBytes();
            logger.debug("Got Response: " + new String(responseBytes));
            T responseJson = gson.fromJson(new String(responseBytes), clazz);
            return responseJson;
        }
    }

    private String authMicrosoftAccount() throws IOException, InterruptedException{
        logger.debug("Started Device authorization request");
        HttpPost deviceAuthPost = new HttpPost(DEVICE_AUTH_REQUEST_URL);
        deviceAuthPost.setHeader(applicationUrlencodedHeader);

        ArrayList<NameValuePair> deviceAuthParams = new ArrayList<>();
        deviceAuthParams.add(new BasicNameValuePair("client_id", CLIENT_ID));
        deviceAuthParams.add(new BasicNameValuePair("scope", SCOPE));
        deviceAuthPost.setEntity(new UrlEncodedFormEntity(deviceAuthParams));

        DeviceAuthJson deviceAuthJson = parseJsonResponse(deviceAuthPost, DeviceAuthJson.class);

        String deviceCode = deviceAuthJson.getDeviceCode();
        int interval = deviceAuthJson.getInterval();

        System.out.println(deviceAuthJson.getMessage());

        logger.info("Authenticating user");
        HttpPost authUserPost = new HttpPost(MICROSOFT_USER_AUTH_URL);
        authUserPost.setHeader(applicationUrlencodedHeader);

        ArrayList<NameValuePair> authUserParams = new ArrayList<>();
        authUserParams.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
        authUserParams.add(new BasicNameValuePair("client_id", CLIENT_ID));
        authUserParams.add(new BasicNameValuePair("device_code", deviceCode));
        authUserPost.setEntity(new UrlEncodedFormEntity(authUserParams));

        while (true) {
            UserAuthJson authJson = parseJsonResponse(authUserPost, UserAuthJson.class);

            if (authJson.getError() != null && !authJson.getError().equals("authorization_pending")) {
                logger.error(authJson.getError()+ ": " + authJson.getErrorDescription());
                throw new IOException((authJson.getError()+ ": " + authJson.getErrorDescription()));
            }

            if (authJson.getAccessToken() != null) {
                return authJson.getAccessToken();
            }

            Thread.sleep(interval * 1000);
        }
    }

    private String[] authXBL(String microsoftToken) throws IOException, InterruptedException{
        contentAndAcceptJson[0] = contentTypeJson;
        contentAndAcceptJson[1] = acceptJson;

        logger.info("Authenticating with XBL");
        HttpPost authXBLPost = new HttpPost(XBL_AUTH_URL);
        authXBLPost.setHeaders(contentAndAcceptJson);

        String authXBLPostJson = gson.toJson(new XBLAuthJson(microsoftToken, "AuthXBL"));
        StringEntity authXBLStringEntity = new StringEntity(authXBLPostJson, ContentType.APPLICATION_JSON);
        authXBLPost.setEntity(authXBLStringEntity);

        XBLAuthJson authXBLJson = parseJsonResponse(authXBLPost, XBLAuthJson.class);

        String XBLToken = authXBLJson.getToken();
        String userHash = authXBLJson.getDisplayClaims().getXui()[0].getUserHash();

        logger.info("Authenticating with XSTS");
        HttpPost authXSTSPost = new HttpPost(XSTS_AUTH_URL);
        authXSTSPost.setHeaders(contentAndAcceptJson);

        String authXSTSPostJson = gson.toJson(new XBLAuthJson(XBLToken, "AuthXSTS"));
        StringEntity authXSTSStringEntity = new StringEntity(authXSTSPostJson, ContentType.APPLICATION_JSON);
        authXSTSPost.setEntity(authXSTSStringEntity);
        // This endpoint can respond with a error im just gonna ignore that till its a problem :)
        XBLAuthJson authXSTSJson = parseJsonResponse(authXSTSPost, XBLAuthJson.class);

        String[] XBLAuth = new String[2];
        XBLAuth[0] = authXSTSJson.getToken();
        XBLAuth[1] = userHash;

        return XBLAuth;
    }

	private void getMinecraftProfile(String[] XBLAuth) throws IOException, InterruptedException {
		logger.info("Authenticating with Minecraft");
		HttpPost httpPost = new HttpPost(AUTH_MINE_URL);
        
        String json = gson.toJson(new AuthMinecraft("XBL3.0 x="+XBLAuth[1]+";"+XBLAuth[0]));
        StringEntity stringEneity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEneity);

        AuthMinecraft responseJson = parseJsonResponse(httpPost, AuthMinecraft.class);
        MineAccessToken = responseJson.getAccessToken();

        logger.info("Fetching player information");
        mineAuthHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + MineAccessToken);        
        HttpGet httpGet = new HttpGet(GET_MINE_PROFILE);
        httpGet.addHeader(mineAuthHeader);

        PlayerProfileJson playerProfileJson = parseJsonResponseGet(httpGet, PlayerProfileJson.class);

	}
}
