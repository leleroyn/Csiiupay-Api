package com.ucs.xcbank.csiiupay.service;

import com.ucs.xcbank.csiiupay.models.CsiiupaySetting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public UspService getUspService() throws Exception{
        return  new UspService();
    }

    @Bean
    public  EncryptService getEncryptService(){
        return new EncryptService();
    }

    @Bean
    public CsiiupaySetting getCsiiupaySetting(){
        return  new CsiiupaySetting();
    }
}
