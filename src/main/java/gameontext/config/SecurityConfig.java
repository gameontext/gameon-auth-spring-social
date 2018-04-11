package gameontext.config;

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

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
        .ignoring()
        .antMatchers("/**/*.css", "/**/*.png", "/**/*.gif", "/**/*.jpg");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    SpringSocialConfigurer socialConfig = new SpringSocialConfigurer();
    socialConfig.postLoginUrl("/token");

    http
          .authorizeRequests()
            .antMatchers("/", "/resources/**", "/health", "/auth/**", "/signin/**")
              .permitAll()
            .antMatchers("/**")
              .authenticated()
        .and()
          .apply(socialConfig);
  }

  @Bean
  public SocialUserDetailsService socialUsersDetailService() {
    return new SimpleSocialUsersDetailService();
  }

}
