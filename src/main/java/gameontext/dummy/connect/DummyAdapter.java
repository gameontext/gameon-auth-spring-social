package gameontext.dummy.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

import gameontext.dummy.api.Dummy;

public class DummyAdapter implements ApiAdapter<Dummy> {

	@Override
	public boolean test(Dummy api) {
		return true;
	}

	@Override
	public void setConnectionValues(Dummy api, ConnectionValues values) {
        values.setProviderUserId(api.getUserId());
        values.setDisplayName(api.getUserName());
	}

	@Override
	public UserProfile fetchUserProfile(Dummy api) {
		return new UserProfileBuilder().setName(api.getUserName()).setUsername(api.getUserName()).setId(api.getUserId()).setEmail(api.getEmail()).build();
	}

	@Override
	public void updateStatus(Dummy api, String message) {}
}
