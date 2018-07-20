package gameontext.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.oauth2.UserInfo;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import gameontext.model.GitHubEmail;
import gameontext.model.TwitterProfileWithEmail;
import io.jsonwebtoken.impl.crypto.JwtSigner;

@RestController
@RequestMapping("/")
public class TokenRestController {

	@Inject
	private Environment environment;

	@Inject
	private JWTSigner tokenSigner;

	@Value("${FRONT_END_SUCCESS_CALLBACK}")
	private String successUrl;

	@Value("${FRONT_END_FAIL_CALLBACK}")
    private String failureUrl;

    @Value("${GAMEON_MODE}")
    private String mode;


	public TokenRestController() {
	}

	//github doesn't just have 1 email, it has many, but we want just
	//one.. ideally a verified one, and ideally the users primary.
	private String selectBestEmailForGitHub(GitHubEmail[] emails){
		String email = null;
		//is the primary email verified?
		for(GitHubEmail e : emails){
			if(e.isPrimary() && e.isVerified()){
				email = e.getEmail();
				break;
			}
		}
		//if not, is there a verified email at all?
		if(email==null){
			for(GitHubEmail e : emails){
				if(e.isVerified()){
					email = e.getEmail();
					break;
				}
			}
		}
		//just take the primary anyways
		if(email==null){
			for(GitHubEmail e : emails){
				if(e.isPrimary()){
					email = e.getEmail();
					break;
				}
			}
		}
		return email;
	}

	@RequestMapping(value = "/token", method = RequestMethod.GET)
	public ResponseEntity<String> ozzy(@AuthenticationPrincipal SocialAuthenticationToken p)
	throws Exception{
		Connection<?> connection = p.getConnection();
		Object o = connection.getApi();

		String type,id,email,name;
		type = connection.getKey().getProviderId();
		switch(type){
			case "facebook" : {
				Facebook facebook=(Facebook)o;
				org.springframework.social.facebook.api.User fu = facebook.userOperations().getUserProfile();
				id = "facebook:"+fu.getId();
				name = fu.getName();
				email = fu.getEmail();
				break;
			}
			case "twitter" : {
				//ideally we would do this.. except spring-social-twitter doesn't do email
				//https://github.com/spring-projects/spring-social-twitter/issues/97
				//Twitter twitter=(Twitter)o;
				//TwitterProfile tp = twitter.userOperations().getUserProfile();
				//id = ""+tp.getId();
				//name = tp.getScreenName();
				//email = tp.getEmail(); //will not work!

				//so instead, we'll instantiate our own rest template using the connection
				//credentials.
				String consumerKey = environment.getProperty("spring.social.twitter.appId");
				String consumerSecret = environment.getProperty("spring.social.twitter.appSecret");
				String twitterAccess = connection.createData().getAccessToken();
				String twitterSecret = connection.createData().getSecret();
				TwitterTemplate t = new TwitterTemplate(consumerKey, consumerSecret, twitterAccess, twitterSecret);
				RestTemplate rt = t.getRestTemplate();

				//using the template, we can request with the 'include-email=true' parameter.
				//Note that our TwitterProfileWithEmail class ignores all the fields we don't use.
				TwitterProfileWithEmail tp2 = rt.getForObject("https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true", TwitterProfileWithEmail.class);
				id = "twitter:"+tp2.getId();
				name = tp2.getScreenName();
				email = tp2.getEmail();
				break;
			}
			case "github" : {
				GitHub github=(GitHub)o;
				GitHubUserProfile ghu = github.userOperations().getUserProfile();
				id = "github:"+ghu.getId();
				name = ghu.getName();
				email = ghu.getEmail();

				//spring-social-github also seems unable to return an email from the user profile.
				//so like twitter, we can rebuild a RestTemplate, and use it to invoke the endpoint
				//ourselves, and retrieve all the users emails, and select the best one.
				//NOTE: requires scope=user:email on the intiial auth request!
				if(email==null){
					String accessToken = connection.createData().getAccessToken();
					GitHubTemplate ght = new GitHubTemplate(accessToken);
					GitHubEmail[] emails = ght.getRestTemplate().getForObject("https://api.github.com/user/emails", GitHubEmail[].class);
					email = selectBestEmailForGitHub(emails);
				}
				break;
			}
			case "google" : {
				Google google=(Google)o;
				UserInfo goou = google.oauth2Operations().getUserinfo();
				id = "google:"+goou.getId();
				name = goou.getName();
				email = goou.getEmail();  //requires scope=email on the initial auth request.
				break;
            }
            case "dummy" : {
                if(mode.equalsIgnoreCase("development")){
                    id="dummy:fish";
                    name="fish";
                    email="";
                }else{
                    throw new IllegalArgumentException("Dummy auth not allowed in production");
                }
                break;
            }
			default: {
				throw new IllegalArgumentException("Unknown Connection Type "+type);
			}
		}
		//build return JWT
		String token = tokenSigner.createJwt(id, name, email);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", successUrl + "/" + token);
		return new ResponseEntity<String>(headers,HttpStatus.FOUND);
	}

}
