package gameontext.config;

import gameontext.dummy.connect.DummyConnectionFactory;
import gameontext.security.SecurityImplicitConnectionSignUp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {
    @Value("${GAMEON_MODE}")
    private String mode;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        //facebook and twitter seem to register themselves, and google, and github do not.

        //connectionFactoryConfigurer.addConnectionFactory(new FacebookConnectionFactory(
        //    environment.getProperty("spring.social.facebook.appId"),
        //    environment.getProperty("spring.social.facebook.appSecret")));
        //connectionFactoryConfigurer.addConnectionFactory(new TwitterConnectionFactory(
        //    environment.getProperty("spring.social.twitter.appId"),
        //    environment.getProperty("spring.social.twitter.appSecret")));
        connectionFactoryConfigurer.addConnectionFactory(new GoogleConnectionFactory(
            environment.getProperty("spring.social.google.appId"),
            environment.getProperty("spring.social.google.appSecret")));
        connectionFactoryConfigurer.addConnectionFactory(new GitHubConnectionFactory(
            environment.getProperty("spring.social.github.appId"),
            environment.getProperty("spring.social.github.appSecret")));

        if(mode.equalsIgnoreCase("development")){
            connectionFactoryConfigurer.addConnectionFactory(new DummyConnectionFactory(
                "dummy",
                "secret"));
        }
  }

  @Override
  public UsersConnectionRepository getUsersConnectionRepository(
      ConnectionFactoryLocator connectionFactoryLocator) {
    InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
    repository.setConnectionSignUp(new SecurityImplicitConnectionSignUp());
    return repository;
  }

  @Override
  public UserIdSource getUserIdSource() {
    return new AuthenticationNameUserIdSource();
  }

}
