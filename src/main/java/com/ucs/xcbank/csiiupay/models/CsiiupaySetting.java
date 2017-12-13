package com.ucs.xcbank.csiiupay.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class CsiiupaySetting {
    @Value("${csiiupay.merchantId}")
    @JsonProperty(value = "MerchantId")
    private  String merchantId ;

    @JsonProperty(value = "SubMerchantId")
    @Value("${csiiupay.subMerchantId}")
    private  String subMerchantId;

    @JsonProperty(value = "PayGateWayUrl")
    @Value("${csiiupay.payGateWayUrl}")
    private  String payGateWayUrl;


    public String getMerchantId() {
        return merchantId;
    }

    public String getPayGateWayUrl() {
        return payGateWayUrl;
    }

    public String getSubMerchantId() {
        return subMerchantId;
    }
}
