package com.ivanhoecambridge.mall.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.fragments.BirthDayPickerFragment;
import com.ivanhoecambridge.mall.signin.FormFillChecker;
import com.ivanhoecambridge.mall.signin.FormFillInterface;
import com.ivanhoecambridge.mall.signin.FormValidation;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AppcompatEditTextWithWatcher;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Kay on 2017-01-27.
 */

public class SignInActivity extends BaseActivity implements FormFillInterface, BirthDayPickerFragment.DateSelectedListener {


    /*@BindView(R.id.tilFirst) TextInputLayout tilFirst;
    @BindView(R.id.tilSecond) TextInputLayout tilSecond;*/

    ViewGroup rootContainer;
    private Scene mScene1;
    private Scene mScene2;
    private Scene mScene;
    private FormFillChecker formFillCheckerOne;
    private FormFillChecker formFillCheckerTwo;

    //SCENE COMMON
    TextView tvSignIn;
    //    TextInputLayout tilFirst;
//    TextInputLayout tilSecond;
//    TextInputLayoutListener etFirst;
//    TextInputLayoutListener etSecond;
    LinearLayout llSignInCreateAccount;

    //SCENE 1
//    CardView cvFb;
//    CardView cvGoogle;

    //SCENE2


    EditText etThird;
    EditText etFourth;
    EditText etFifth;

    //SET ERROR TO ET - LITTLE DOT ON THE RIGHT
    //SET ERROR TO TIL - ERROR MSG

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.signin_sign_in));

        rootContainer = (ViewGroup) findViewById(R.id.scene_root);

        mScene1 = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_one, this);

        mScene2 = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_two, this);

        mScene = mScene1;
        mScene.enter();

        initializeView();
        validateView();
    }

    private void initializeView() {

    }


    private void validateView(){
        if(mScene == mScene1) {
            //SIGN IN SCENE
            getSupportActionBar().setTitle(getString(R.string.signin_sign_in));
            formFillCheckerOne = new FormFillChecker(this);

            llSignInCreateAccount = (LinearLayout) findViewById(R.id.llSignInCreateAccount);
            llSignInCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SignInActivity.this, "SIGNING IN", Toast.LENGTH_SHORT).show();
                }
            });

            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene();
                }
            });


            //EMAIL
            final TextInputLayout tilSignInEmail = (TextInputLayout) findViewById(R.id.tilFirst);
            final AppcompatEditTextWithWatcher etSignInEmail = (AppcompatEditTextWithWatcher) findViewById(R.id.etFirst);
            etSignInEmail.setOnFieldFilledListener(formFillCheckerOne);
            etSignInEmail.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public void validate() {
                    String inputString = etSignInEmail.getTag().toString();
                    if(!inputString.equals("") && !FormValidation.isEmailValid(inputString.toString())) {
                        tilSignInEmail.setError(getString(R.string.warning_not_valid_address));
                        etSignInEmail.getOnFieldFilledListener().isFieldFilled(etSignInEmail, false);
                    } else {
                        tilSignInEmail.setErrorEnabled(false);
                    }
                }
            });

            //PASSWORD
            final AppcompatEditTextWithWatcher etSignInPassword = (AppcompatEditTextWithWatcher) findViewById(R.id.etSecond);
            etSignInPassword.setOnFieldFilledListener(formFillCheckerOne);

            formFillCheckerOne.addEditText(etSignInEmail, etSignInPassword);
        } else if (mScene == mScene2) {
            //CREATE ACCOUNT SCENE
            getSupportActionBar().setTitle(getString(R.string.signin_sign_up));
            formFillCheckerTwo = new FormFillChecker(this);

            llSignInCreateAccount = (LinearLayout) findViewById(R.id.llSignInCreateAccount);
            llSignInCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SignInActivity.this, "CREATING ACCOUNT", Toast.LENGTH_SHORT).show();
                }
            });


            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene();
                }
            });

            //FULL NAME
            final AppcompatEditTextWithWatcher etCreateAccountFullName = (AppcompatEditTextWithWatcher) findViewById(R.id.etFirst);
            etCreateAccountFullName.setOnFieldFilledListener(formFillCheckerOne);

            //EMAIL
            final TextInputLayout tilCreateAccountEmail = (TextInputLayout) findViewById(R.id.tilSecond);
            final AppcompatEditTextWithWatcher etCreateAccountEmail = (AppcompatEditTextWithWatcher) findViewById(R.id.etSecond);
            etCreateAccountEmail.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountEmail.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public void validate() {
                    String inputString = etCreateAccountEmail.getTag().toString();
                    if(!inputString.equals("") && !FormValidation.isEmailValid(inputString)) {
                        tilCreateAccountEmail.setError(getString(R.string.warning_not_valid_address));
                        etCreateAccountEmail.getOnFieldFilledListener().isFieldFilled(etCreateAccountEmail, false);
                    } else {
                        tilCreateAccountEmail.setErrorEnabled(false);
                    }
                }
            });


            //PASSWORD
            final TextInputLayout tilCreateAccountPassword = (TextInputLayout) findViewById(R.id.tilThird);
            final AppcompatEditTextWithWatcher etCreateAccountPassword = (AppcompatEditTextWithWatcher) findViewById(R.id.etThird);

            final TextInputLayout tilCreateAccountConfirm = (TextInputLayout) findViewById(R.id.tilFourth);
            final AppcompatEditTextWithWatcher etCreateAccountConfirm = (AppcompatEditTextWithWatcher) findViewById(R.id.etFourth);


            etCreateAccountPassword.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountPassword.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public void validate() {
                    String inputString = etCreateAccountPassword.getTag().toString();
                    String inputStringConfirm = etCreateAccountConfirm.getTag().toString();

                    if(!inputString.equals("")) {
                        if(!FormValidation.isNameLongEnough(inputString)) {
                            tilCreateAccountPassword.setError(getString(R.string.warning_must_be_longer));
                            etCreateAccountPassword.getOnFieldFilledListener().isFieldFilled(etCreateAccountPassword, false);
                        } else if(!inputStringConfirm.equals("") && !inputString.equals(inputStringConfirm)) {
                            tilCreateAccountPassword.setError(getString(R.string.warning_two_passwords_not_equal));
                            etCreateAccountPassword.getOnFieldFilledListener().isFieldFilled(etCreateAccountPassword, false);
                        } else {
                            tilCreateAccountPassword.setErrorEnabled(false);
                        }
                    } else {
                        tilCreateAccountPassword.setErrorEnabled(false);
                    }
//                    if(etCreateAccountConfirm.getOnValidateListener() != null) etCreateAccountConfirm.getOnValidateListener().validate();
                }
            });


            //CONFIRM PASSWORD
            etCreateAccountConfirm.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountConfirm.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public void validate() {

                    String inputString = etCreateAccountConfirm.getTag().toString();
                    String inputStringPassword = etCreateAccountPassword.getTag().toString();


                    if(!inputString.equals("")) {
                        if(!FormValidation.isNameLongEnough(inputString)) {
                            tilCreateAccountConfirm.setError(getString(R.string.warning_must_be_longer));
                            etCreateAccountConfirm.getOnFieldFilledListener().isFieldFilled(etCreateAccountConfirm, false);
                        } else if(!inputStringPassword.equals("") && !inputString.equals(inputStringPassword)) {
                            tilCreateAccountConfirm.setError(getString(R.string.warning_two_passwords_not_equal));
                            etCreateAccountConfirm.getOnFieldFilledListener().isFieldFilled(etCreateAccountConfirm, false);
                        } else {
                            tilCreateAccountConfirm.setErrorEnabled(false);
                        }
                    } else {
                        tilCreateAccountConfirm.setErrorEnabled(false);
                    }
                    if(etCreateAccountPassword.getOnValidateListener() != null) etCreateAccountPassword.getOnValidateListener().validate();
                }
            });


            //DATE OF BIRTH
            final TextInputLayout tilCreateAccountBirth = (TextInputLayout) findViewById(R.id.tilFifth);
            final AppcompatEditTextWithWatcher etCreateAccountBirth = (AppcompatEditTextWithWatcher) findViewById(R.id.etFifth);
            etCreateAccountBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b) {
                        DialogFragment newFragment = new BirthDayPickerFragment();
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    }
                }
            });
            etCreateAccountBirth.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountBirth.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public void validate() {

                }
            });


            formFillCheckerOne.addEditText(etCreateAccountFullName, etCreateAccountEmail);
        }
    }

    private void changeScene(){
        if(mScene == mScene1) mScene = mScene2;
        else if(mScene == mScene2) mScene = mScene1;
        TransitionManager.go(mScene);
        ButterKnife.bind(this);
        validateView();
    }

    @Override
    public void isFieldsCompletelyFilled(boolean filled) {
        if(mScene == mScene1) {
            if(filled) {
                llSignInCreateAccount.setBackgroundColor(getResources().getColor(R.color.sign_in_red));
                llSignInCreateAccount.setClickable(true);
                for (AppcompatEditTextWithWatcher textInputLayoutListener : formFillCheckerOne.getEditTextmap().keySet()) {
                    if(textInputLayoutListener.getOnValidateListener() != null) textInputLayoutListener.getOnValidateListener().validate();
                }
            } else {
                llSignInCreateAccount.setBackgroundColor(getResources().getColor(R.color.sign_in_disabled));
                llSignInCreateAccount.setClickable(false);
            }

        } else if(mScene == mScene2) {

        }
    }

    @Optional
    @OnClick(R.id.cvFb) void onFbClicked(View v) {
        Toast.makeText(this, "Fb Clicked", Toast.LENGTH_SHORT).show();
    }

    @Optional
    @OnClick(R.id.cvGoogle) void onGoogleClicked(View v) {
        Toast.makeText(this, "Google Clicked", Toast.LENGTH_SHORT).show();
    }

    @Optional
    @OnClick(R.id.tvTerms) void onClickTerms(View v) {
        Toast.makeText(this, "Terms", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBirthdaySelected(GregorianCalendar birthday) {

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
