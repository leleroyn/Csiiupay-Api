package com.ucs.xcbank.csiiupay.service;

import com.ucs.xcbank.csiiupay.utils.RsaEncrypt;
import com.ucs.xcbank.csiiupay.utils.StreamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;

import java.io.InputStream;

public class EncryptService {
    @Value("${ucsmy.private_key}")
    private  String ucsmyPrivateKeyFile;
    @Value("${csiiupay.cert_file}")
    private  String csiiupayCertFile;

    public String signData(String content) throws Exception{
        RsaEncrypt rsaEncrypt = new RsaEncrypt();
        InputStream fileStream = this.getClass().getResourceAsStream("/"+ ucsmyPrivateKeyFile);
        rsaEncrypt.loadPrivateKey(StreamUtils.ConvertToString(fileStream, false));
        byte[] signData =  rsaEncrypt.rsaSign(content, rsaEncrypt.getPrivateKey());
        String signContent = Base64Utils.encodeToString(signData);
        return  signContent;
    }

    public boolean verify(String content,String signedData) throws Exception{
        RsaEncrypt rsaEncrypt = new RsaEncrypt();
        InputStream fileStream = this.getClass().getResourceAsStream("/"+ csiiupayCertFile);
        return rsaEncrypt.verifyByCert(content,signedData,fileStream);
    }

}
