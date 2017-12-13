package com.ucs.xcbank.csiiupay.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import ucsmy.usp.api.ApiBaseService;
import ucsmy.usp.api.Log;

public class UspService {
    @Value("${ucsmy.usp.api.service_host}")
    private String uspServerHost ;
    @Value("${ucsmy.usp.api.system_id}")
    private String uspSystemId ;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public  UspService() throws Exception {
        initialize();
    }
    private void initialize() throws Exception{
        ApiBaseService.initialize(uspServerHost,uspSystemId);
    }

    public  void debug(String title, String message) {
        try {
            logger.debug(String.format("title:%s message:%s", title,message));
            Log.LogText(title, message,"");
        }
        catch (Exception ex){
            logger.error("usp 日志记录异常",ex);
        }
    }

}
