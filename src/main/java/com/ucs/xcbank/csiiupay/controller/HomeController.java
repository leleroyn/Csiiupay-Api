package com.ucs.xcbank.csiiupay.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Encoder;

import javax.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class HomeController {

    @RequestMapping("/")
    @ResponseBody
    public String Hello (){
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return "csiiupay service is online at " + dateFormat.format(date);
    }

    @RequestMapping("/getPublicKey")
    @ResponseBody
    public  String getPublicKey(@RequestBody String cerStr) throws Exception{
        X509Certificate cert = X509Certificate.getInstance( new ByteArrayInputStream(cerStr.getBytes()));
        PublicKey publicKey = cert.getPublicKey();
        BASE64Encoder base64Encoder=new BASE64Encoder();
        String publicKeyString = base64Encoder.encode(publicKey.getEncoded());  
        System.out.println("-----------------公钥--------------------");  
        System.out.println(publicKeyString);  
        System.out.println("-----------------公钥--------------------");
        return  publicKeyString;
    }
}
