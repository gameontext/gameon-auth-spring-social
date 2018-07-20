package gameontext.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@ConditionalOnProperty( prefix="gameon.", value="mode", havingValue="development")
@RestController
public class DummyOAuthEndpoints {

    public static class TokenReply {
        private String access_token;
        private String token_type;

        @JsonProperty("access_token")
        public String getAccess_Token(){ return access_token; }
        @JsonProperty("token_type")
        public String getToken_Type(){ return token_type; }

        public void setAccess_Token(String p){ this.access_token = p; }
        public void setToken_Type(String p){ this.token_type = p; }

    }

    @GetMapping(value="/auth/dummy/fake/auth")
    public ResponseEntity<Object> auth(@RequestParam Map<String,String> allRequestParams, HttpServletResponse httpServletResponse)
      throws IOException, URISyntaxException{
        System.out.println(allRequestParams);
        String redirectUrl = allRequestParams.get("redirect_uri");

        if(redirectUrl==null){
            return new ResponseEntity<>("Missing redirect URI", HttpStatus.BAD_REQUEST);
        }

        redirectUrl+="?state="+allRequestParams.get("state");
        redirectUrl+="&code=FISH";

        URI redirect = new URI(redirectUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirect);
        return new ResponseEntity<>(headers,HttpStatus.FOUND);

    }

    @RequestMapping(value="/auth/dummy/fake/token", method = RequestMethod.POST, produces = "application/json",  consumes = "application/x-www-form-urlencoded")
    public @ResponseBody TokenReply token(@RequestParam Map<String,String> allRequestParams) {
        TokenReply t = new TokenReply();
        t.setAccess_Token("FISH_TOKEN");
        t.setToken_Type("bearer");
        return t;
    }

}

