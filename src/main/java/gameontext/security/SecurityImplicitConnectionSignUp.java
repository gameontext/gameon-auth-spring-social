package gameontext.security;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

public class SecurityImplicitConnectionSignUp implements ConnectionSignUp {

  public SecurityImplicitConnectionSignUp() {
  }

  @Override
  public String execute(Connection<?> connection) {
    String providerUserId = connection.getKey().getProviderUserId();
    return providerUserId;
  }

}