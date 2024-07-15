package avengers.nexus.gauth.service;


import avengers.nexus.auth.service.AuthenticateService;
import gauth.GAuth;
import gauth.GAuthToken;
import gauth.GAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GauthService implements AuthenticateService {
    @Value("${gauth.client-id}")
    private String clientId;
    @Value("${gauth.client-secret}")
    private String clientSecret;
    @Value("${gauth.redirect-uri}")
    private String redirectUri;

    private final GAuth gAuth;

    public String getAccessToken(String authorizationCode) {
        System.out.println("clientId: " + clientId);
        System.out.println("clientSecret: " + clientSecret);
        System.out.println("redirectUri: " + redirectUri);
        GAuthToken token = gAuth.generateToken(
                authorizationCode,
                clientId,
                clientSecret,
                redirectUri
        );
        return token.getAccessToken();
    }
    public GAuthUserInfo getUserInfoByCode(String accessCode) {
        String accessToken = getAccessToken(accessCode);
        return getUserInfo(accessToken);
    }
    public GAuthUserInfo getUserInfo(String accessToken) {
        return gAuth.getUserInfo(accessToken);
    }
}
