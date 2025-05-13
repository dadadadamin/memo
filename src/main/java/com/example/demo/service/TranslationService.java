// service/TranslationService.java
package com.example.demo.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TranslationService {

    public String translateText(String text, String targetLang) throws IOException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ClassPathResource("translation-key.json").getInputStream())
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        Translate translate = TranslateOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();

        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLang),
                Translate.TranslateOption.model("nmt")
        );

        return translation.getTranslatedText();
    }
}
