package gameontext.config;

import org.springframework.beans.factory.annotation.Value;
import gameontext.security.SimpleSocialUsersDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${frontend.auth.url}")
  private String baseAuthUrl;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
        .ignoring()
        .antMatchers("/**/*.css", "/**/*.png", "/**/*.gif", "/**/*.jpg");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    SpringSocialConfigurer socialConfig = new SpringSocialConfigurer();
    System.out.println("Token Redirect Url : "+baseAuthUrl+"/token");
    socialConfig.postLoginUrl(baseAuthUrl+"/token");

    http
          .authorizeRequests()
            .antMatchers("/", "/resources/**", "/health", "/auth/**", "/signin/**")
              .permitAll()
            .antMatchers("/**")
              .authenticated()
        .and()
              .csrf().ignoringAntMatchers("/auth/dummy/fake/**")
        .and()
          .apply(socialConfig);
  }

  @Bean
  public SocialUserDetailsService socialUsersDetailService() {
    return new SimpleSocialUsersDetailService();
  }

}
