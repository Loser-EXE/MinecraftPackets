package com.loserexe;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.jupiter.api.Test;

import com.loserexe.http.microsoft.AccessToken;

public class AccessTokenTest {
    @Test
    public void getAccessCode() throws ClientProtocolException, IOException, InterruptedException {
        String accessToken = AccessToken.authUser().getAccessToken();

        System.out.println(accessToken);
    }
}
