package com.ivanhoecambridge.mall.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.label;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

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
        getSupportActionBar().setTitle(getString(R.string.signin_sign_in));

        initializeView();
    }


    private void initializeView() {
        llSignInCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSign.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);

                ScheduledExecutorService scheduler= Executors.newScheduledThreadPool(1);
                ScheduledFuture<?> handl = scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        setResult(Activity.RESULT_OK, new Intent());
                        finishActivity();
                    }
                }, 2, SECONDS);

            }
        });

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
        etCard.setTypeface(tf);

        etCard.addTextChangedListener(new TextWatcher() {
            private final char DIVIDER = ' ';
            private final int TOTAL_DIGITS = 20;
            private final int NUMB_OF_CHAR_DIVIDER = 3;


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % (NUMB_OF_CHAR_DIVIDER + 1)) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (DIVIDER == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }

                if (s.length() > 0 && (s.length() % (NUMB_OF_CHAR_DIVIDER + 1)) == 0) {
                    char c = s.charAt(s.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(DIVIDER)).length <= 4) {
                        s.insert(s.length() - 1, String.valueOf(DIVIDER));
                    }
                }

                isFieldsCompletelyFilled(s.length() == TOTAL_DIGITS);
            }
        });
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
