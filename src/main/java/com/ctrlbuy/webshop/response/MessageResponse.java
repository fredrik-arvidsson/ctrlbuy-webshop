package com.ctrlbuy.webshop.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // Empty constructor for Jackson deserialization
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}