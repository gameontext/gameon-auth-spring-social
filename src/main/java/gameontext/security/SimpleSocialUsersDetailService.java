package gameontext.security;

import java.util.Collections;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

public class SimpleSocialUsersDetailService
    implements SocialUserDetailsService {

  public SimpleSocialUsersDetailService() {
  }

  @Override
  public SocialUserDetails loadUserByUserId(String userId)
      throws UsernameNotFoundException {
    return new SocialUser(userId, "", Collections.emptyList());
  }

}
