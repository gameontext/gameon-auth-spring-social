
package gameontext.dummy.connect;

import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;

import gameontext.dummy.api.Dummy;

public class DummyConnectionFactory extends OAuth2ConnectionFactory<Dummy> {

  public DummyConnectionFactory(final String clientId, final String clientSecret, final String authUrl) {
    super("dummy", new DummyServiceProvider(clientId, clientSecret, authUrl),
      new DummyAdapter());
  }

  @Override
  protected String extractProviderUserId(final AccessGrant accessGrant) {
    final Dummy api = ((DummyServiceProvider) getServiceProvider()).getApi(accessGrant.getAccessToken());
    final UserProfile userProfile = getApiAdapter().fetchUserProfile(api);
    return userProfile.getUsername();
  }
}
