package com.board.web.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty
    private String userName;

    @NotEmpty
    private String password;
}
