package com.ivanhoecambridge.mall.giftcard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Kay on 2017-02-21.
 */

public class GiftCardResponse {

    @SerializedName("available_balance")
    @Expose
    private float availableBalance;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("activation_amount")
    @Expose
    private float activationAmount;
    @SerializedName("amf_amount")
    @Expose
    private float amfAmount;
    @SerializedName("is_icc_card")
    @Expose
    private Boolean isIccCard;
    @SerializedName("has_atm_access")
    @Expose
    private Boolean hasAtmAccess;

    public float getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(float availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getActivationAmount() {
        return activationAmount;
    }

    public void setActivationAmount(float activationAmount) {
        this.activationAmount = activationAmount;
    }

    public float getAmfAmount() {
        return amfAmount;
    }

    public void setAmfAmount(float amfAmount) {
        this.amfAmount = amfAmount;
    }

    public Boolean getIsIccCard() {
        return isIccCard;
    }

    public void setIsIccCard(Boolean isIccCard) {
        this.isIccCard = isIccCard;
    }

    public Boolean getHasAtmAccess() {
        return hasAtmAccess;
    }

    public void setHasAtmAccess(Boolean hasAtmAccess) {
        this.hasAtmAccess = hasAtmAccess;
    }

}
