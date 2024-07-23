package com.btsl.security;

public class SecurityConfigVO {
    private boolean nonceSignatureValidation;
    private boolean nonceEncryption;
    private boolean refererValidation;
    private boolean payloadEncryption;
    private String nonceRegex = "[^[a-zA-Z0-9]+$]{16}";

    public boolean isNonceSignatureValidation() {
        return nonceSignatureValidation;
    }

    public void setNonceSignatureValidation(boolean nonceSignatureValidation) {
        this.nonceSignatureValidation = nonceSignatureValidation;
    }

    public boolean isNonceEncryption() {
        return nonceEncryption;
    }

    public void setNonceEncryption(boolean nonceEncryption) {
        this.nonceEncryption = nonceEncryption;
    }

    public boolean isRefererValidation() {
        return refererValidation;
    }

    public void setRefererValidation(boolean refererValidation) {
        this.refererValidation = refererValidation;
    }

    public boolean isPayloadEncryption() {
        return payloadEncryption;
    }

    public void setPayloadEncryption(boolean payloadEncryption) {
        this.payloadEncryption = payloadEncryption;
    }

    public String getNonceRegex() {
        return nonceRegex;
    }

    public void setNonceRegex(String nonceRegex) {
        this.nonceRegex = nonceRegex;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SecurityConfigVO{");
        sb.append("nonceSignatureValidation=").append(nonceSignatureValidation);
        sb.append(", nonceEncryption=").append(nonceEncryption);
        sb.append(", refererValidation=").append(refererValidation);
        sb.append(", payloadEncryption=").append(payloadEncryption);
        sb.append(", nonceRegex='").append(nonceRegex).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
