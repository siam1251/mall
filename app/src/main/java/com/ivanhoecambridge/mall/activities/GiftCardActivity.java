package com.ivanhoecambridge.mall.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.giftcard.GiftCard;
import com.ivanhoecambridge.mall.giftcard.GiftCardResponse;
import com.ivanhoecambridge.mall.managers.GiftCardManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kay on 2017-02-15.
 */

public class GiftCardActivity extends BaseActivity {

    @BindView(R.id.etCard) EditText etCard;
    @BindView(R.id.llSignInCreateAccount) LinearLayout llSignInCreateAccount;
    @BindView(R.id.tvSign) TextView tvSign;
    @BindView(R.id.pb) ProgressBar pb;

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftcard);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.gc_add_card));

        initializeView();
    }


    private void initializeView() {
        llSignInCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cardNumber = etCard.getText().toString();
                //check if this gift card has already been added
                if(GiftCardManager.getInstance(GiftCardActivity.this).isCardAdded(cardNumber)){
                    AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                    alertDialogForInterest.getAlertDialog(
                            GiftCardActivity.this,
                            GiftCardActivity.this.getResources().getString(R.string.title_oops),
                            GiftCardActivity.this.getResources().getString(R.string.warning_gift_card_already_exist),
                            GiftCardActivity.this.getResources().getString(R.string.action_ok),
                            null,
                            new AlertDialogForInterest.DialogAnsweredListener() {
                                @Override
                                public void okClicked() {
                                    return;
                                }
                            });
                    return;
                }

                showProgressBar(true);
                GiftCardManager giftCardManager = new GiftCardManager(GiftCardActivity.this, new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        showProgressBar(false);
                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                String errorMessage = (String) inputMessage.obj;
                                AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                                alertDialogForInterest.getAlertDialog(
                                        GiftCardActivity.this,
                                        GiftCardActivity.this.getResources().getString(R.string.title_oops),
                                        errorMessage,
                                        GiftCardActivity.this.getResources().getString(R.string.action_ok),
                                        null,
                                        new AlertDialogForInterest.DialogAnsweredListener() {
                                            @Override
                                            public void okClicked() {
                                                return;
                                            }
                                        });
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                GiftCardResponse giftCardResponse = (GiftCardResponse) inputMessage.obj;
                                Intent giftCardIntent = new Intent();
                                giftCardIntent.putExtra(GiftCard.EXTRA_GIFT_CARD_NUMBER, cardNumber);
                                giftCardIntent.putExtra(GiftCard.EXTRA_GIFT_CARD_BALANCE, giftCardResponse.getAvailableBalance());
                                setResult(Activity.RESULT_OK, giftCardIntent);
                                finishActivity();
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
                giftCardManager.checkCardBalance(cardNumber);
            }
        });

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
        etCard.setTypeface(tf);

        etCard.addTextChangedListener(new TextWatcher() {
            private final char DIVIDER = ' ';
            private final int TOTAL_DIGITS = 20;
            private final int NUM_OF_CHAR_DIVIDER = 3;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % (NUM_OF_CHAR_DIVIDER + 1)) == 0) { //when the cursor's at NUM_OF_CHAR_DIVIDER + 1
                    final char currentCharacter = s.charAt(s.length() - 1); //last character written
                    if (DIVIDER == currentCharacter) { //when deleting if the current letter is DIVIDER, delete it
                        s.delete(s.length() - 1, s.length());
                    }

                    //if current character is at NUM_OF_CHAR_DIVIDER + 1, insert DIVIDER unless you reach the 5th block (5th block should have 4 digits)
                    if (Character.isDigit(currentCharacter) && TextUtils.split(s.toString(), String.valueOf(DIVIDER)).length <= 4) {
                        s.insert(s.length() - 1, String.valueOf(DIVIDER));
                    }
                }

                isFieldsCompletelyFilled(s.length() == TOTAL_DIGITS);
            }
        });
    }

    private void showProgressBar(boolean enable){
        if(enable) {
            llSignInCreateAccount.setClickable(false);
            tvSign.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
        } else {
            llSignInCreateAccount.setClickable(true);
            tvSign.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }

    public void isFieldsCompletelyFilled(boolean filled) {
        llSignInCreateAccount.setClickable(filled);
        if(filled) {
            llSignInCreateAccount.setBackgroundColor(getResources().getColor(R.color.sign_in_red));
        } else {
            llSignInCreateAccount.setBackgroundColor(getResources().getColor(R.color.sign_in_disabled));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishActivity() {
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }
}
