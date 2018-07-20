package gameontext.dummy.connect;

import java.util.Map;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;


public class DummyOAuth2Template extends OAuth2Template {

    //TODO: this might work as just localhost and port..
    private static final String authUrl = System.getenv("FRONT_END_AUTH_URL");
    private static final String authUri = authUrl+"/dummy/fake/auth";
    private static final String tokenUri = authUrl+"/dummy/fake/token";

	private String redirectUri;

	public DummyOAuth2Template(String clientId, String clientSecret) {
		this(clientId, clientSecret, null);
	}

	public DummyOAuth2Template(String clientId, String clientSecret,String redirectUri) {
		super(clientId, clientSecret, authUri, tokenUri);
		setUseParametersForClientAuthentication(true);
		this.redirectUri = redirectUri;
	}

	@Override
	protected AccessGrant createAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn, Map<String, Object> response) {
		return super.createAccessGrant(accessToken, scope, refreshToken, expiresIn, response);
	}


	@Override
	public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters parameters) {
		if (redirectUri != null) parameters.setRedirectUri(redirectUri);
		return super.buildAuthenticateUrl(grantType, parameters);
	}

	@Override
	public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters parameters) {
		if (redirectUri != null) parameters.setRedirectUri(redirectUri);
		return super.buildAuthorizeUrl(grantType, parameters);
	}

	@Override
	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
		return super.exchangeForAccess(authorizationCode, this.redirectUri != null ? this.redirectUri : redirectUri, additionalParameters);
	}

}
