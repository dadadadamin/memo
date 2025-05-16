// dto/TranslateRequest.java
package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslateRequest {
    private String text;
    private String targetLanguage;
}
