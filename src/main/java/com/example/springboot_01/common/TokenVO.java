package com.example.springboot_01.common;
import lombok.Data;

@Data
public class TokenVO {
    private String token;
    public TokenVO(String token) { this.token = token; }
}

