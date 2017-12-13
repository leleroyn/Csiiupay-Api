package com.ucs.xcbank.csiiupay.controller;


import com.ucs.xcbank.csiiupay.models.ApiResponse;
import com.ucs.xcbank.csiiupay.models.CsiiupaySetting;
import com.ucs.xcbank.csiiupay.service.EncryptService;
import com.ucs.xcbank.csiiupay.service.RequestContentService;
import com.ucs.xcbank.csiiupay.service.UspService;
import com.ucs.xcbank.csiiupay.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@RestController
public class CsiiupayInfoController {
    @Autowired
    private EncryptService encryptService;
    @Autowired
    private UspService uspService;
    @Autowired
    private RequestContentService requestContentService;
    @Autowired
    private CsiiupaySetting csiiupaySetting;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/Csiiupay/Sign")
    public ApiResponse signData(HttpServletRequest request, @RequestHeader("head") String head, @RequestParam("body") String body) throws Exception{
        String batchId  = UUID.randomUUID().toString();
        ApiResponse responseObj = new ApiResponse();
        try {
            Map<String, Object> headMap = requestContentService.getHeadMap(batchId, head, request);
            responseObj.setSerialNumber(headMap.get("SerialNumber").toString());
            Map<String, Object> bodyMap = requestContentService.getBodyMap(batchId, body, request);
            String content = bodyMap.get("Content").toString();
            String signedContent = encryptService.signData(content);
            responseObj.getData().put("SignedContent", signedContent);
        }
        catch (Exception ex) {
            responseObj.setCode("01");
            responseObj.setMessage(ex.toString());
            logger.error(ex.getMessage(),ex);
        }
        uspService.debug(String.format("[%s] %s Response", batchId, request.getRequestURL()), JSONUtils.obj2json(responseObj));
        return  responseObj;
    }

    @RequestMapping("/Csiiupay/Verify")
    public ApiResponse verifyData (HttpServletRequest request, @RequestHeader("head") String head, @RequestParam("body") String body) throws Exception{
        String batchId  = UUID.randomUUID().toString();
        ApiResponse responseObj = new ApiResponse();
        try {
            Map<String, Object> headMap = requestContentService.getHeadMap(batchId, head, request);
            responseObj.setSerialNumber(headMap.get("SerialNumber").toString());
            Map<String, Object> bodyMap = requestContentService.getBodyMap(batchId, body, request);
            String content = bodyMap.get("Content").toString();
            String signedContent = bodyMap.get("SignedContent").toString();
            responseObj.getData().put("VerifyResult", encryptService.verify(content,signedContent));
        }
        catch (Exception ex) {
            responseObj.setCode("01");
            responseObj.setMessage(ex.toString());
            logger.error(ex.getMessage(),ex);
        }
        uspService.debug(String.format("[%s] %s Response", batchId, request.getRequestURL()), JSONUtils.obj2json(responseObj));
        return  responseObj;
    }

    @RequestMapping("/Csiiupay/BaseInfo")
    public ApiResponse baseInfo (HttpServletRequest request, @RequestHeader("head") String head) throws Exception{
        String batchId  = UUID.randomUUID().toString();
        ApiResponse responseObj = new ApiResponse();
        try {
            Map<String, Object> headMap = requestContentService.getHeadMap(batchId, head, request);
            responseObj.setSerialNumber(headMap.get("SerialNumber").toString());
            responseObj.getData().put("BaseInfo", csiiupaySetting);
        }
        catch (Exception ex) {
            responseObj.setCode("01");
            responseObj.setMessage(ex.toString());
            logger.error(ex.getMessage(),ex);
        }
        uspService.debug(String.format("[%s] %s Response", batchId, request.getRequestURL()), JSONUtils.obj2json(responseObj));
        return  responseObj;
    }
}
