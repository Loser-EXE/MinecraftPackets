package com.loserexe.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.text.AbstractDocument.Content;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import com.loserexe.pojo.minecraft.ClientAuthJson;
import com.loserexe.pojo.minecraft.PlayerCertificatesJson;
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
	private final String GET_PLAYER_CERTIF = "https://api.minecraftservices.com/player/certificates";
    private final String AUTH_CLIENT = "https://sessionserver.mojang.com/session/minecraft/join";
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
    private final String HOME = System.getProperty("user.home");
    private final String PATH = HOME + "\\AppData\\local\\mpm";
    private String mineAccessToken;

	private UserAuthJson userAuthJson;
	private XBLAuthJson xblAuthJson;
	private PlayerProfileJson playerProfileJson;

    public PlayerAuth() throws IOException, InterruptedException{
        UserAuthJson userAuth = loadCache("userAuth", UserAuthJson.class);
        userAuthJson = authMicrosoftAccount(userAuth);

        xblAuthJson = authXBL(userAuthJson.getAccessToken());
		playerProfileJson = getMinecraftProfile(xblAuthJson);
    }
    
    public <T> T loadCache(String name, Class<T> clazz) throws FileNotFoundException {
        try {
            String data = "";
            String filePath = PATH + "\\cache\\" + name + ".json";
            File file = new File(filePath);
            Scanner fileReader = new Scanner(file); 
            while (fileReader.hasNextLine()) {
                data += fileReader.nextLine();
            }
            fileReader.close();

            return gson.fromJson(data, clazz); 
        } catch (Exception e) {
            logger.warn("Failed to load userAuth");
            return null;
        }
    }

    public void cacheData(String name, String data) throws IOException {
        String path = PATH + "\\cache";
        String filePath = path + "\\" + name + ".json";

        try {
            File directory = new File(path);
            if (directory.mkdirs()) {
                logger.info("Created directory: " + directory.getPath());
            }  

            File file = new File(filePath);

            if (file.createNewFile()) {
                logger.info("Created file: " + file.getName());
            } else {
                logger.warn("Overriding file: " + file.getName());
                FileWriter fileWriter = new FileWriter(file, false);
                PrintWriter printWriter = new PrintWriter(fileWriter, false);
                printWriter.flush();
                printWriter.close();
                fileWriter.close();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
            logger.info("Successfully wrote to file: " + file.getName());

        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
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

    private UserAuthJson authMicrosoftAccount(UserAuthJson userAuth) throws IOException, InterruptedException{
        // ToDo: Catch Failed refresh token attempt
        int interval = 0;
        String deviceCode = "";
        if (userAuth == null) {
            logger.debug("Started Device authorization request");
            HttpPost deviceAuthPost = new HttpPost(DEVICE_AUTH_REQUEST_URL);
            deviceAuthPost.setHeader(applicationUrlencodedHeader);

            ArrayList<NameValuePair> deviceAuthParams = new ArrayList<>();
            deviceAuthParams.add(new BasicNameValuePair("client_id", CLIENT_ID));
            deviceAuthParams.add(new BasicNameValuePair("scope", SCOPE));
            deviceAuthPost.setEntity(new UrlEncodedFormEntity(deviceAuthParams));

            DeviceAuthJson deviceAuthJson = parseJsonResponse(deviceAuthPost, DeviceAuthJson.class);

            deviceCode = deviceAuthJson.getDeviceCode();
            interval = deviceAuthJson.getInterval();

            System.out.println(deviceAuthJson.getMessage());
        }

        logger.info("Authenticating user");
        HttpPost authUserPost = new HttpPost(MICROSOFT_USER_AUTH_URL);
        authUserPost.setHeader(applicationUrlencodedHeader);

        ArrayList<NameValuePair> authUserParams = new ArrayList<>();
        authUserParams.add(new BasicNameValuePair("client_id", CLIENT_ID));
        if (userAuth != null) {
            logger.info("Refreshing token");
            authUserParams.add(new BasicNameValuePair("scope", SCOPE));
            authUserParams.add(new BasicNameValuePair("refresh_token", userAuth.getRefreshToken()));
            authUserParams.add(new BasicNameValuePair("grant_type", "refresh_token"));
        } else {
            logger.info("Obtaning new token");
            authUserParams.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
            authUserParams.add(new BasicNameValuePair("device_code", deviceCode));
        }
        authUserPost.setEntity(new UrlEncodedFormEntity(authUserParams));

        while (true) {
            UserAuthJson authJson = parseJsonResponse(authUserPost, UserAuthJson.class);

            if (authJson.getError() != null && !authJson.getError().equals("authorization_pending")) {
                logger.error(authJson.getError()+ ": " + authJson.getErrorDescription());
                throw new IOException((authJson.getError()+ ": " + authJson.getErrorDescription()));
            }

            if (authJson.getAccessToken() != null) {
                String data = gson.toJson(authJson);
                cacheData("userAuth", data);
                return authJson;
            }

            Thread.sleep(interval * 1000);
        }
    }

    private XBLAuthJson authXBL(String microsoftToken) throws IOException, InterruptedException{
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

        logger.info("Authenticating with XSTS");
        HttpPost authXSTSPost = new HttpPost(XSTS_AUTH_URL);
        authXSTSPost.setHeaders(contentAndAcceptJson);

        String authXSTSPostJson = gson.toJson(new XBLAuthJson(XBLToken, "AuthXSTS"));
        StringEntity authXSTSStringEntity = new StringEntity(authXSTSPostJson, ContentType.APPLICATION_JSON);
        authXSTSPost.setEntity(authXSTSStringEntity);
        // This endpoint can respond with a error im just gonna ignore that till its a problem :)
        XBLAuthJson authXSTSJson = parseJsonResponse(authXSTSPost, XBLAuthJson.class);

        return authXSTSJson;
    }

	private PlayerProfileJson getMinecraftProfile(XBLAuthJson XBLAuth) throws IOException, InterruptedException {
		logger.info("Authenticating with Minecraft");
		HttpPost httpPost = new HttpPost(AUTH_MINE_URL);

		String userHash = XBLAuth.getDisplayClaims().getXui()[0].getUserHash();
		String token = XBLAuth.getToken();
        
		String json = gson.toJson(new AuthMinecraft("XBL3.0 x="+userHash+";"+token));
        StringEntity stringEneity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEneity);

        AuthMinecraft responseJson = parseJsonResponse(httpPost, AuthMinecraft.class);
        mineAccessToken = responseJson.getAccessToken();

        logger.info("Fetching player information");
        mineAuthHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + mineAccessToken);        
        HttpGet httpGet = new HttpGet(GET_MINE_PROFILE);
        httpGet.addHeader(mineAuthHeader);

        PlayerProfileJson playerProfileJson = parseJsonResponseGet(httpGet, PlayerProfileJson.class);
		return playerProfileJson;

	}

	public PlayerCertificatesJson getPlayerCertificates() throws IOException, InterruptedException {
		logger.info("Fetching player certificates");
		HttpPost httpPost = new HttpPost(GET_PLAYER_CERTIF);

		httpPost.addHeader(mineAuthHeader);

		PlayerCertificatesJson playerCertificatesJson = parseJsonResponse(httpPost, PlayerCertificatesJson.class);	
		return playerCertificatesJson;

	}

    public void authClient(String hash) throws ClientProtocolException, IOException {
        logger.info("Authenticating client");
        HttpPost httpPost = new HttpPost(AUTH_CLIENT);
        httpPost.addHeader(contentTypeJson);

        ClientAuthJson clientAuthJson = new ClientAuthJson(mineAccessToken, playerProfileJson.getUuid().replace("-", ""), hash);
        String json = gson.toJson(clientAuthJson);
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        
        CloseableHttpResponse response = httpClient.execute(httpPost);

        if(response.getStatusLine().getStatusCode() == 204) {
            logger.info("Successfully authenticated client");
            return;
        }
        
        byte[] responseBytes = response.getEntity().getContent().readAllBytes();
        ClientAuthJson clientAuth = gson.fromJson(new String(responseBytes), ClientAuthJson.class);
        throw new IOException(clientAuth.getError());
    }

	public UserAuthJson getUserAuthJson() {
		return this.userAuthJson;
	}

	public XBLAuthJson getXBLAuthJson() {
		return this.xblAuthJson;
	}

	public PlayerProfileJson getPlayerProfileJson() {
		return this.playerProfileJson;
	}
}
