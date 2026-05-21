package api.dto_data_transfer_object;

import api.utils.Input;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Credentials {
    private String email;
    private String password;
    private String domain;
    private String baseUri;

    public Credentials() {
        Input input = new Input();
        this.email = input.getProperty("email");
        this.password = input.getProperty("password");
        this.domain = input.getProperty("domain");
        this.baseUri = input.getProperty("base_uri");
    }

    public Credentials(String email, String password, String domain) {
        this.email = email;
        this.password = password;
        this.domain = domain;
    }
}
