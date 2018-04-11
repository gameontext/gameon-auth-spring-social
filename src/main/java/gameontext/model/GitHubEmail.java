package gameontext.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEmail {

    @JsonProperty("email")
    private String email;
    @JsonProperty("primary")
    private boolean primary;
    @JsonProperty("verified")
    private boolean verified;
    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("primary")
    public boolean isPrimary() {
        return primary;
    }

    @JsonProperty("primary")
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @JsonProperty("verified")
    public boolean isVerified() {
        return verified;
    }

    @JsonProperty("verified")
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @JsonProperty("visibility")
    public String getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

}