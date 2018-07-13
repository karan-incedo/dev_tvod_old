package com.viewlift.models.data.appcms.api;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 10/10/17.
 */

@UseStag
public class AppCMSSignedURLResult implements Serializable{
    @SerializedName("signed")
    @Expose
    String signed;

    String policy;

    String signature;

    String keyPairId;

    public String getSigned() {
        return signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getKeyPairId() {
        return keyPairId;
    }

    public void setKeyPairId(String keyPairId) {
        this.keyPairId = keyPairId;
    }

    public void parseKeyValuePairs() {
        if (!TextUtils.isEmpty(signed)) {
            if (TextUtils.isEmpty(signature) &&
                    TextUtils.isEmpty(policy) &&
                    TextUtils.isEmpty(keyPairId)) {
                int valueIndex = signed.indexOf("Policy=");
                int paramIndex = signed.indexOf("&");
                if (0 < paramIndex && 0 <= valueIndex) {
                    policy = signed.substring(valueIndex + "Policy=".length(), paramIndex);

                    valueIndex = signed.indexOf("Signature=", paramIndex);
                    paramIndex = signed.indexOf("&", paramIndex + 1);
                    if (0 < paramIndex && 0 <= valueIndex) {
                        signature = signed.substring(valueIndex + "Signature=".length(), paramIndex);

                        valueIndex = signed.indexOf("Key-Pair-Id=", paramIndex);
                        if (0 <= valueIndex) {
                            keyPairId = signed.substring(valueIndex + "Key-Pair-Id=".length());
                        }
                    }
                }
            }
        }
    }
}
