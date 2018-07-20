package gameontext.dummy.api;

import org.springframework.social.ApiBinding;

public interface Dummy extends ApiBinding {
    public String getUserId();
    public String getEmail();
    public String getUserName();
}
