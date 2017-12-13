package com.ucs.xcbank.csiiupay.controller;

import com.ucs.xcbank.csiiupay.models.ApiResponse;
import com.ucs.xcbank.csiiupay.models.CsiiupaySetting;
import com.ucs.xcbank.csiiupay.service.EncryptService;
import com.ucs.xcbank.csiiupay.service.RequestContentService;
import com.ucs.xcbank.csiiupay.service.UspService;
import com.ucs.xcbank.csiiupay.utils.JSONUtils;
import com.ucs.xcbank.csiiupay.utils.XmlUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ApiController {
    @Autowired
    private UspService uspService;
    @Autowired
    private CsiiupaySetting csiiupaySetting;
    @Autowired
    private RequestContentService requestContentService;
    @Autowired
    private  EncryptService encryptService;
    private  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /*
    卡信息验证
     */
    @RequestMapping("/Csiiupay/BindCard")
    public  ApiResponse  bindCard(HttpServletRequest request, @RequestHeader("head") String head, @RequestParam("body") String body)  throws Exception {
        String batchId  = UUID.randomUUID().toString();
        ApiResponse responseObj = new ApiResponse();
        try {
            Map<String, Object> headMap = requestContentService.getHeadMap(batchId, head, request);
            responseObj.setSerialNumber(headMap.get("SerialNumber").toString());
            Map<String, Object> bodyMap = requestContentService.getBodyMap(batchId, body, request);

            Map<String,Object> plainMap = new LinkedHashMap<>();
            plainMap.put("TransId","CSVR");
            plainMap.put("MerchantId",csiiupaySetting.getMerchantId());
            plainMap.put("MerSeqNo",batchId);
            plainMap.put("MerDateTime",bodyMap.get("MerDateTime"));
            plainMap.put("Name", bodyMap.get("Name"));
            plainMap.put("Account",bodyMap.get("Account"));
            plainMap.put("PayAcctType",bodyMap.get("PayAcctType"));
            plainMap.put("IdType",bodyMap.get("IdType"));
            plainMap.put("IdNo", bodyMap.get("IdNo"));
            plainMap.put("MobileNo", bodyMap.get("MobileNo"));
            plainMap.put("MsgExt", bodyMap.get("MsgExt"));
            //valid
            if(plainMap.get("MerDateTime") == null || plainMap.get("Name") ==null || plainMap.get("Account")==null
                    ||plainMap.get("PayAcctType") == null || plainMap.get("IdType") ==null || plainMap.get("IdNo")==null
                    ||plainMap.get("MobileNo") == null)
            {
                responseObj.setCode("01");
                responseObj.setMessage("MerDateTime,Name,Account,PayAcctType,IdType, IdNo,MobileNo 不能为空.");
                return  responseObj;
            }

            String plainText = XmlUtils.callMapToXML(plainMap);
            String signature = encryptService.signData(plainText);
            uspService.debug(String.format("[%s] %s Gateway Input", batchId, request.getRequestURL()), plainText);

            List<NameValuePair> parms = new ArrayList<>();
            parms.add(new BasicNameValuePair("TransId", "CSVR"));
            parms.add(new BasicNameValuePair("Plain", plainText));
            parms.add(new BasicNameValuePair("Signature", signature));
            Map<String, Object> messageMap = requestContentService.postGateway(batchId,request,parms);
            if(messageMap.get("RespCode").equals("000000")) {
                responseObj.getData().put("CheckStatus", messageMap.get("CheckStatus"));
                responseObj.getData().put("RespCode", messageMap.get("RespCode"));
            }
            else {
                responseObj.setCode("01");
                responseObj.getData().put("RespCode", messageMap.get("RespCode"));
                responseObj.setMessage( messageMap.get("RespMessage").toString());
            }
        }
        catch (Exception ex)
        {
            responseObj.setCode("01");
            responseObj.setMessage(ex.toString());
            logger.error(ex.getMessage(),ex);
        }
        uspService.debug(String.format("[%s] %s Response", batchId, request.getRequestURL()), JSONUtils.obj2json(responseObj));
        return  responseObj;
    }

    /*
    查询订单状态
    */
    @RequestMapping("/Csiiupay/OrderStatus")
    public ApiResponse orderStatus(HttpServletRequest request, @RequestHeader("head") String head, @RequestParam("body") String body) throws Exception {
        String batchId  = UUID.randomUUID().toString();
        ApiResponse responseObj = new ApiResponse();
        try {
            Map<String, Object> headMap = requestContentService.getHeadMap(batchId, head, request);
            responseObj.setSerialNumber(headMap.get("SerialNumber").toString());
            Map<String, Object> bodyMap = requestContentService.getBodyMap(batchId, body, request);

            Map<String, Object> plainMap = new LinkedHashMap<>();
            plainMap.put("TransId", "IQSR");
            plainMap.put("MerchantId", csiiupaySetting.getMerchantId());
            plainMap.put("SubMerchantId", csiiupaySetting.getSubMerchantId());
            plainMap.put("MerSeqNo", bodyMap.get("MerSeqNo"));
            plainMap.put("MerTransDate", bodyMap.get("MerTransDate"));
            plainMap.put("MerTransAmt", bodyMap.get("MerTransAmt"));
            //valid
            if (plainMap.get("MerSeqNo") == null || plainMap.get("MerTransDate") == null || plainMap.get("MerTransAmt") == null) {
                responseObj.setCode("01");
                responseObj.setMessage("MerSeqNo,MerTransDate,MerTransAmt 不能为空.");
                return responseObj;
            }

            String plainText = XmlUtils.callMapToXML(plainMap);
            String signature = encryptService.signData(plainText);
            uspService.debug(String.format("[%s] %s Gateway Input", batchId, request.getRequestURL()), plainText);

            List<NameValuePair> parms = new ArrayList<>();
            parms.add(new BasicNameValuePair("TransId", "IQSR"));
            parms.add(new BasicNameValuePair("Plain", plainText));
            parms.add(new BasicNameValuePair("Signature", signature));
            Map<String, Object> messageMap = requestContentService.postGateway(batchId,request,parms);
             /*0000：失败（代表银行账务处理失败）
                0001：成功（代表银行账务处理成功）
                0002：银行处理中（代表银行处理中间状态）
                0003：银行查无此订单。（若银行端发现无此订单，交易金额为0、交易日期填当前日期，交易状态是0003）
                0004：订单待支付
                0005：订单已取消
            */
            if(messageMap.get("RespCode").equals("000000")) {
                responseObj.getData().put("TransStatus", messageMap.get("TransStatus"));
                responseObj.getData().put("TransAmt", messageMap.get("TransAmt"));
                responseObj.getData().put("ReceiptAmount", messageMap.get("ReceiptAmount"));
                responseObj.getData().put("RespMessage", messageMap.get("ReceiptAmount"));
            }
            else {
                responseObj.setCode("01");
                responseObj.setMessage( messageMap.get("RespMessage").toString());
            }

        }
        catch (Exception ex) {
            responseObj.setCode("01");
            responseObj.setMessage(ex.toString());
            logger.error(ex.getMessage(),ex);
        }
        uspService.debug(String.format("[%s] %s Response", batchId, request.getRequestURL()), JSONUtils.obj2json(responseObj));
        return  responseObj;
    }

    /*
     退款
     */
    @RequestMapping("/Csiiupay/Refund")
    public  ApiResponse refund(HttpServletRequest request, @RequestHeader("head") String head, @RequestParam("body") String body) throws Exception    {
        String batchId  = UUID.randomUUID().toString();
        ApiResponse responseObj = new ApiResponse();
        try {
            Map<String, Object> headMap = requestContentService.getHeadMap(batchId, head, request);
            responseObj.setSerialNumber(headMap.get("SerialNumber").toString());
            Map<String, Object> bodyMap = requestContentService.getBodyMap(batchId, body, request);

            Map<String,Object> plainMap = new LinkedHashMap<>();
            plainMap.put("TransId","IPSR");
            plainMap.put("MerchantId",csiiupaySetting.getMerchantId());
            plainMap.put("MerSeqNo",bodyMap.get("MerSeqNo").toString());
            plainMap.put("MerDateTime",bodyMap.getOrDefault("MerDateTime",simpleDateFormat.format(new Date())).toString());
            plainMap.put("OrgMerSeqNo",bodyMap.get("OrgMerSeqNo").toString());
            plainMap.put("OrgMerDate",bodyMap.get("OrgMerDate").toString());
            plainMap.put("OrgTransAmt",bodyMap.get("OrgTransAmt"));
            plainMap.put("SubMerchantId",csiiupaySetting.getSubMerchantId());
            plainMap.put("SubMerSeqNo",UUID.randomUUID().toString());
            plainMap.put("SubMerDateTime",simpleDateFormat.format(new Date()));
            plainMap.put("OrgSubMerSeqNo",bodyMap.get("OrgSubMerSeqNo"));
            plainMap.put("OrgSubMerDate",bodyMap.get("OrgSubMerDate"));
            plainMap.put("SubTransAmt",bodyMap.get("SubTransAmt"));
            //valid
            if(plainMap.get("MerSeqNo") == null || plainMap.get("OrgMerSeqNo") ==null || plainMap.get("OrgMerDate")==null
                    ||plainMap.get("OrgTransAmt") == null || plainMap.get("OrgSubMerSeqNo") ==null || plainMap.get("OrgSubMerDate")==null
                    ||plainMap.get("SubTransAmt") == null)
            {
                responseObj.setCode("01");
                responseObj.setMessage("MerSeqNo,OrgMerSeqNo,OrgMerDate,OrgTransAmt,OrgSubMerSeqNo, OrgSubMerDate,SubTransAmt 不能为空.");
                return  responseObj;
            }
            String plainText = XmlUtils.callMapToXML(plainMap);
            String signature = encryptService.signData(plainText);
            uspService.debug(String.format("[%s] %s Gateway Input", batchId, request.getRequestURL()), plainText);

            List<NameValuePair> parms = new ArrayList<>();
            parms.add(new BasicNameValuePair("TransId","IPSR"));
            parms.add(new BasicNameValuePair("Plain",plainText));
            parms.add(new BasicNameValuePair("Signature",signature));
            Map<String, Object> messageMap =requestContentService.postGateway(batchId,request,parms);
                /*
                000000表示交易成功
                999999表示超时
                ORD003表示该笔订单已经成功
                OC0006表示退款金额超过原支付金额（可能是订单已退款，也有可能是支付金额和退款金额不一致）
                其余表示失败
                */
            if(messageMap.get("RespCode").equals("000000")) {
                responseObj.getData().put("RespCode", messageMap.get("RespCode"));
                responseObj.getData().put("TransAmt", messageMap.get("TransAmt"));
            }
            else {
                responseObj.setCode("01");
                responseObj.setMessage( messageMap.get("RespMessage").toString());
                responseObj.getData().put("RespCode", messageMap.get("RespCode"));
            }
        }
        catch (Exception ex)
        {
            responseObj.setCode("01");
            responseObj.setMessage(ex.toString());
            logger.error(ex.getMessage(),ex);
        }
        uspService.debug(String.format("[%s] %s Response", batchId, request.getRequestURL()), JSONUtils.obj2json(responseObj));
        return  responseObj;
    }

}
