package com.ucs.xcbank.csiiupay.service;

import com.ucs.xcbank.csiiupay.models.CsiiupaySetting;
import com.ucs.xcbank.csiiupay.utils.EncodeUtils;
import com.ucs.xcbank.csiiupay.utils.HttpClientUtils;
import com.ucs.xcbank.csiiupay.utils.JSONUtils;
import com.ucs.xcbank.csiiupay.utils.XmlUtils;
import org.apache.http.NameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequestContentService {
    @Autowired
    private UspService uspService;
    @Autowired
    private CsiiupaySetting csiiupaySetting;
    @Autowired
    private EncryptService encryptService;

    public Map<String, Object> getHeadMap(String batchId, String sourceHeadString, HttpServletRequest request) throws Exception {
        String headStr = new String(EncodeUtils.base64UrlDecode(sourceHeadString), "utf-8");
        uspService.debug(String.format("[%s] %s requestHead", batchId, request.getRequestURL()), headStr);
        Map<String, Object> headMap = JSONUtils.json2map(headStr);
        return headMap;
    }

    public Map<String, Object> getBodyMap(String batchId, String sourceBodyString, HttpServletRequest request) throws Exception {
        String bodyStr = new String(EncodeUtils.base64UrlDecode(sourceBodyString), "utf-8");
        uspService.debug(String.format("[%s] %s requestBody", batchId, request.getRequestURL()), bodyStr);
        Map<String, Object> bodyMap = JSONUtils.json2map(bodyStr);
        return bodyMap;
    }

    public Map<String, Object> postGateway(String batchId, HttpServletRequest request, List<NameValuePair> parms) throws Exception {
        Map<String, Object> messageMap = null;
        Map<String, String> gatewayResponseObj = HttpClientUtils.post(csiiupaySetting.getPayGateWayUrl(), parms);
        String gateWayResponseString = gatewayResponseObj.get("Content");
        uspService.debug(String.format("[%s] %s Gateway Output", batchId, request.getRequestURL()), gateWayResponseString);

        if (gatewayResponseObj.isEmpty() || !gatewayResponseObj.get("Status").equals("200")) {
            throw new Exception(String.format("Csiiupay 内部异常 %s ", gatewayResponseObj.get("Status")));
        } else {
            Document document = DocumentHelper.parseText(gateWayResponseString);
            String messageXml = XmlUtils.getElementText(document, "Message", true);
            String signXml = messageXml.replace("\n", "\r\n");
            String signatureString = XmlUtils.getElementText(document, "Signature", false).replace("\r", "").replace("\n", "");
            if (!encryptService.verify(signXml, signatureString)) {
                throw new Exception("csiiupay 返回内容签名异常.");
            } else {
                messageMap = XmlUtils.Dom2Map(DocumentHelper.parseText(messageXml));
            }
        }
        return messageMap;
    }
}

