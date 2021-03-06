package gameontext.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthCompatRestController {

    @Value("${frontend.auth.url}")
    String authBase;

    @GetMapping(value="/DummyAuth")
    public ResponseEntity<Object> dauth( HttpServletResponse httpServletResponse)
      throws IOException, URISyntaxException{
        String redirectUrl = authBase+"/dummy";
        URI redirect = new URI(redirectUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirect);
        return new ResponseEntity<>(headers,HttpStatus.FOUND);
    }
    @GetMapping(value="/FacebookAuth")
    public ResponseEntity<Object> fauth( HttpServletResponse httpServletResponse)
      throws IOException, URISyntaxException{
        String redirectUrl = authBase+"/facebook";
        URI redirect = new URI(redirectUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirect);
        return new ResponseEntity<>(headers,HttpStatus.FOUND);
    }
    @GetMapping(value="/TwitterAuth")
    public ResponseEntity<Object> tauth( HttpServletResponse httpServletResponse)
      throws IOException, URISyntaxException{
        String redirectUrl = authBase+"/twitter";
        URI redirect = new URI(redirectUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirect);
        return new ResponseEntity<>(headers,HttpStatus.FOUND);
    }
    @GetMapping(value="/GitHubAuth")
    public ResponseEntity<Object> ghauth( HttpServletResponse httpServletResponse)
      throws IOException, URISyntaxException{
        String redirectUrl = authBase+"/github?scope=user:email";
        URI redirect = new URI(redirectUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirect);
        return new ResponseEntity<>(headers,HttpStatus.FOUND);
    }
    @GetMapping(value="/GoogleAuth")
    public ResponseEntity<Object> gauth( HttpServletResponse httpServletResponse)
      throws IOException, URISyntaxException{
        String redirectUrl = authBase+"/google?scope=profile%20email";
        URI redirect = new URI(redirectUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirect);
        return new ResponseEntity<>(headers,HttpStatus.FOUND);
    }
}
