package com.ivanhoecambridge.mall.giftcard;

import android.text.TextUtils;

/**
 * Created by Kay on 2017-02-16.
 */

public class GiftCard {
    private String cardNumber = "";
    private float cardBalance = 0.0f;
    private static final int GIFT_CARD_DIGITS = 16;
    public static final String EXTRA_GIFT_CARD_NUMBER = "gift_card_number";
    public static final String EXTRA_GIFT_CARD_BALANCE = "gift_card_balance";

    private boolean isChecked = false;

    public GiftCard(){}

    public GiftCard (String cardNumber, float cardBalance){
        this.cardNumber = cardNumber;
        this.cardBalance = cardBalance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public float getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(float cardBalance) {
        this.cardBalance = cardBalance;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    /**
     *
     * @return formatted card number with - in between numbers
     */
    public String getFormattedCardNumber(){
        return insertDashInNumber(cardNumber);
    }

    public static String insertDashInNumber(String cardNumber) {
        cardNumber = cardNumber.replace(" ", "");
        if(cardNumber == null || cardNumber.length() != GIFT_CARD_DIGITS) return "";
        cardNumber =    cardNumber.substring(0, 3) + "-" +
                        cardNumber.substring(3, 6) + "-" +
                        cardNumber.substring(6, 9) + "-" +
                        cardNumber.substring(9, 12) + "-" +
                        cardNumber.substring(12);

        return cardNumber;
    }
}
