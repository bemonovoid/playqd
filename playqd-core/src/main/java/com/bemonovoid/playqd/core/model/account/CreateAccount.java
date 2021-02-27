package com.bemonovoid.playqd.core.model.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class CreateAccount {

    @JsonProperty("username")
    private String userName;
    private String password;

}
