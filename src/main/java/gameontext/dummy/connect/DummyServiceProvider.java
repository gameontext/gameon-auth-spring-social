package gameontext.dummy.connect;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import gameontext.dummy.api.Dummy;

public class DummyServiceProvider extends AbstractOAuth2ServiceProvider<Dummy> {

    DummyServiceProvider(final String clientId, final String clientSecret, final String authUrl){
        super(new DummyOAuth2Template(clientId, clientSecret, authUrl));
    }

	@Override
	public Dummy getApi(String accessToken) {
		return new Dummy(){
            public boolean isAuthorized(){
                return true;
            }

			@Override
			public String getUserId() {
				return "fish";
			}

			@Override
			public String getEmail() {
				return "fish@wibble.com";
			}

			@Override
			public String getUserName() {
				return "Fish";
			}
        };
	}
}
