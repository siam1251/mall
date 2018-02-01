package com.ivanhoecambridge.mall.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.giftcard.GiftCard;
import com.ivanhoecambridge.mall.giftcard.GiftCardResponse;
import com.ivanhoecambridge.mall.managers.GiftCardManager;
import com.ivanhoecambridge.mall.signup.JanrainRecordManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.FormEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by Kay on 2017-02-15.
 */

public class GiftCardActivity extends BaseActivity implements GiftCardManager.GiftCardListener {

    private final char   DIVIDER     = '-';
    private final int    MAX_DIGITS  = 20;
    private final String TEST_NUMBER = "1000140032956896";
    private final String PATTERN = "###-###-###-###-####";

    @BindView(R.id.rlParent)
    RelativeLayout rlParent;
    @BindView(R.id.tvErrorDropDown)
    TextView tvErrorDropDown;
    @BindView(R.id.edtGiftCardNumber)
    FormEditText   edtCardNumber;
    @BindView(R.id.tvGiftCardNumberError)
    TextView       tvGiftCardNumberError;
    @BindView(R.id.edtGiftCardBalance)
    FormEditText   edtCardBalance;
    @BindView(R.id.tvSaveCardBalance)
    TextView       tvSaveCardBalance;
    @BindView(R.id.pbGiftCardNumber)
    ProgressBar    pbGiftCardNumber;

    private GiftCardManager gcManager;
    private GiftCard        giftCard;
    private boolean         isUserSignedIn;
    private String uid;


    private final String TAG = getClass().getSimpleName();


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftcard);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.gc_page_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initializeView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.getInstance(this).logScreenView(this, "Add a Gift Card");
    }


    private void initializeView() {
        uid = getSignedInUser();
        isUserSignedIn = (uid != null);
        tvSaveCardBalance.setText(getString(isUserSignedIn ? R.string.gc_save_card_balance : R.string.gc_sign_up_save_card_balance));

        gcManager = new GiftCardManager(this, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                setProgressIndicator(false);
                switch (message.arg1) {
                    case GiftCardManager.DOWNLOAD_FAILED:
                        if (message.what == GiftCardManager.ERROR_INVALID_CARD) {
                            setFormErrorDisplay(true);
                        } else {
                            showDropDownError(String.valueOf(message.obj), true);
                        }
                        break;
                    case GiftCardManager.DOWNLOAD_COMPLETE:
                        updateCardBalance((GiftCardResponse) message.obj);
                        tvSaveCardBalance.setEnabled(true);
                        break;
                    default:
                        super.handleMessage(message);

                }
            }
        });
        gcManager.setGiftCardListener(this);
    }

    @OnTextChanged(value = R.id.edtGiftCardNumber, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    public void beforeCardNumberChanged(CharSequence cs, int start, int count, int after) {
        if (tvGiftCardNumberError.getVisibility() == View.VISIBLE) {
            setFormErrorDisplay(false);
        }
    }

    @OnTextChanged(value = R.id.edtGiftCardNumber, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onCardNumberChanged(Editable s) {
        if (!inputMatchesPattern(s.toString(), PATTERN)) {
            String reformatted = reformatString(s.toString().replace(String.valueOf(DIVIDER), ""), PATTERN);
            s.replace(0, s.length(), reformatted);
        }
        if (s.length() == MAX_DIGITS) {
            checkCardBalance();
        }

    }

    private String reformatString(String rawString, String pattern) {
        StringBuilder patternSb = new StringBuilder(pattern);
        StringBuilder rawSb = new StringBuilder(rawString);
        int dividerCount = 0;
        int index = 0;
        for (int i = 0; i < rawString.length() + dividerCount; i++) {
            if (pattern.charAt(i) == '#') {
                patternSb.setCharAt(i, rawSb.charAt(0));
                rawSb.deleteCharAt(0);
            } else {
                dividerCount++;
            }
            index++;
        }
        patternSb.delete(index, patternSb.length());
        return patternSb.toString();
    }

    private boolean inputMatchesPattern(String input, String pattern) {
        int index = 0;
        boolean patternMatches = true;
        while (patternMatches && index < input.length()) {
            if (Character.isDigit(input.charAt(index)) && pattern.charAt(index) == DIVIDER ||
                    input.charAt(index) == DIVIDER && pattern.charAt(index) != DIVIDER) {
                patternMatches = false;
            }
            index++;
        }
        return patternMatches;
    }



    @OnFocusChange(R.id.edtGiftCardNumber)
    public void onCardNumberFocusChanged(boolean hasFocus) {
        edtCardNumber.setFocusStateDrawable(hasFocus);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                if (edtCardNumber.getText().length() == MAX_DIGITS) {
                    checkCardBalance();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private String getSignedInUser() {
        return KcpUtility.loadFromCache(this, JanrainRecordManager.KEY_USER_ID, null);
    }


    private void checkCardBalance() {
        setProgressIndicator(true);
        rlParent.requestFocus();
        KcpUtility.hideKeyboard(this);
        gcManager.checkCardBalance(edtCardNumber.getText().toString());
    }

    @OnClick(R.id.tvSaveCardBalance)
    public void onSaveCardBalance() {
        if (isUserSignedIn) {
            gcManager.saveCardToAccount(uid, giftCard);
        } else {
           startSignUpActivity(giftCard);
        }
    }

    @Override
    public void onGiftCardAdded() {
        Analytics.getInstance(this).logEvent("Giftcard_Add", "Gift Card", "Gift Card Added", giftCard.getCardNumber(), (int)(giftCard.getCardBalance()*100));
        //set the ok before showing the dialog in the case that the user wants to add another and then goes back anyways.
        //or if any gift card has been added successfully.
        setResult(RESULT_OK);
        createGiftCardDialog(getString(R.string.gc_added_dialog_title), getString(R.string.gc_add_another), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                resetForm();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishActivity();
            }
        }).show();
    }

    @Override
    public void onGiftCardError(int giftCardError) {
        logGiftCardFailEvent();
        if (giftCardError == GiftCardManager.ERROR_DUPLICATE_CARD) {
            createGiftCardDialog(getString(R.string.title_oops), getString(R.string.warning_gift_card_already_exist), getString(R.string.action_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null).show();
        }
    }

    @OnClick(R.id.tvCheckAnotherGiftCard)
    public void onCheckAnotherGiftCard() {
        resetForm();
    }

    private void resetForm() {
        edtCardNumber.getText().clear();
        edtCardBalance.getText().clear();
        tvSaveCardBalance.setEnabled(false);
    }

    private AlertDialog createGiftCardDialog(@Nullable String title, @NonNull String message, @Nullable String positiveText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog gcDialog = new AlertDialog.Builder(this)
                .setTitle(title == null ? "" : title)
                .setMessage(message)
                .setPositiveButton(positiveText == null ? getString(R.string.action_yes) : positiveText, positiveListener)
                .create();
        if (negativeListener != null) {
            gcDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.action_no), negativeListener);
        }
        return gcDialog;
    }

    private void startSignUpActivity(GiftCard giftCard) {
        Intent signUpIntent = new Intent(this, SignInAfterOnBoardingActivity.class);
        gcManager.saveGiftCardAfterSignUp(giftCard);
        startActivityForResult(signUpIntent, Constants.REQUEST_CODE_SIGN_UP);
    }

    private void updateCardBalance(GiftCardResponse giftCardResponse) {
        if (giftCard == null || !giftCard.getCardNumber().equals(edtCardNumber.getText().toString())) {
            giftCard = new GiftCard(edtCardNumber.getText().toString(), giftCardResponse.getAvailableBalance());
        }
        setProgressIndicator(false);
        edtCardBalance.setText(String.format(getString(R.string.gc_card_balance_format), giftCard.getCardBalance()));
    }

    private void setFormErrorDisplay(boolean shouldShowError) {
        edtCardNumber.setFocusStateDrawable(true);
        edtCardNumber.setErrorState(shouldShowError);
        tvGiftCardNumberError.setVisibility(shouldShowError ? View.VISIBLE : View.GONE);
        edtCardBalance.getText().clear();
    }

    private void showDropDownError(String errorMessage, boolean shouldDisplay) {
        if (errorMessage != null) tvErrorDropDown.setText(errorMessage);
        animateErrorMessage(shouldDisplay);
        tvErrorDropDown.setVisibility(shouldDisplay ? View.VISIBLE : View.GONE);
    }

    private void animateErrorMessage(boolean isAppearing) {
        Animation slideDirection = AnimationUtils.loadAnimation(this, isAppearing ? R.anim.anim_slide_down : R.anim.anim_slide_up);
        tvErrorDropDown.startAnimation(slideDirection);
    }

    @OnClick(R.id.tvErrorDropDown)
    public void onErrorDropDownClick() {
        showDropDownError(null, false);
    }

    private void setProgressIndicator(boolean showProgress) {
        edtCardNumber.toggleDrawableVisibility(!showProgress);
        pbGiftCardNumber.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    private void logGiftCardFailEvent() {
        Analytics.getInstance(GiftCardActivity.this).logEvent("Giftcard_Fail", "Gift Card", "Gift Card Rejected");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_SIGN_UP) {
            if (resultCode == RESULT_OK) {
                isUserSignedIn = true;
                tvSaveCardBalance.setText(getString(R.string.gc_save_card_balance));
                gcManager.applySavedGiftCardToAccount();
            } else if (resultCode == Constants.RESULT_FAILED) {
                finishActivity();
            }
        }
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
