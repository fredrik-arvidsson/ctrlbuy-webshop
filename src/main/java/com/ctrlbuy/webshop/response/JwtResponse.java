package com.ctrlbuy.webshop.response; // ✅ Korrigerat stavningen "package"

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String refreshToken; // ✅ Lagt till refresh token!

    public JwtResponse(String token, Long id, String username, String email, List<String> roles, String refreshToken) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.refreshToken = refreshToken; // ✅ Nu stöder vi refresh tokens!
    }
}
