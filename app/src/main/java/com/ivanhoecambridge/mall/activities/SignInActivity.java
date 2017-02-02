package com.ivanhoecambridge.mall.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.fragments.BirthDayPickerFragment;
import com.ivanhoecambridge.mall.signin.FormFillChecker;
import com.ivanhoecambridge.mall.signin.FormFillInterface;
import com.ivanhoecambridge.mall.signin.FormValidation;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AppcompatEditTextWithWatcher;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Kay on 2017-01-27.
 */

public class SignInActivity extends BaseActivity implements FormFillInterface, BirthDayPickerFragment.DateSelectedListener {

    ViewGroup rootContainer;
    private Scene mScene1;
    private Scene mScene2;
    private Scene mScene3;
    private Scene mScene;
    private FormFillChecker formFillCheckerOne;
    private FormFillChecker formFillCheckerTwo;
    private FormFillChecker formFillCheckerThree;

    //SCENE COMMON
    TextView tvSignIn;
    LinearLayout llSignInCreateAccountReset;

    //SCENE 1

    //SCENE2
    private AppcompatEditTextWithWatcher etCreateAccountBirth;

    EditText etThird;
    EditText etFourth;
    EditText etFifth;

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

        mScene3 = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_three, this);

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

            llSignInCreateAccountReset = (LinearLayout) findViewById(R.id.llSignInCreateAccount);
            llSignInCreateAccountReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SignInActivity.this, "SIGNING IN", Toast.LENGTH_SHORT).show();
                }
            });

            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene(mScene2);
                }
            });


            //EMAIL
            final TextInputLayout tilSignInEmail = (TextInputLayout) findViewById(R.id.tilFirst);
            final AppcompatEditTextWithWatcher etSignInEmail = (AppcompatEditTextWithWatcher) findViewById(R.id.etFirst);
            etSignInEmail.setOnFieldFilledListener(formFillCheckerOne);
            etSignInEmail.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    String inputString = etSignInEmail.getTag().toString();
                    if(!inputString.equals("") && !FormValidation.isEmailValid(inputString.toString())) {
                        if(showErrorMsg) tilSignInEmail.setError(getString(R.string.warning_not_valid_address));
                        return false;
                    } else {
                        tilSignInEmail.setErrorEnabled(false);
                        return true;
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

            llSignInCreateAccountReset = (LinearLayout) findViewById(R.id.llSignInCreateAccount);
            llSignInCreateAccountReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SignInActivity.this, "CREATING ACCOUNT", Toast.LENGTH_SHORT).show();
                }
            });


            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene(mScene1);
                }
            });

            //FULL NAME
            final AppcompatEditTextWithWatcher etCreateAccountFullName = (AppcompatEditTextWithWatcher) findViewById(R.id.etFirst);
            etCreateAccountFullName.setOnFieldFilledListener(formFillCheckerTwo);

            //EMAIL
            final TextInputLayout tilCreateAccountEmail = (TextInputLayout) findViewById(R.id.tilSecond);
            final AppcompatEditTextWithWatcher etCreateAccountEmail = (AppcompatEditTextWithWatcher) findViewById(R.id.etSecond);
            etCreateAccountEmail.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountEmail.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    String inputString = etCreateAccountEmail.getTag().toString();
                    if(!inputString.equals("") && !FormValidation.isEmailValid(inputString)) {
                        if(showErrorMsg) tilCreateAccountEmail.setError(getString(R.string.warning_not_valid_address));
                        return false;
                    } else {
                        tilCreateAccountEmail.setErrorEnabled(false);

                        return true;
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
                public boolean validate(boolean showErrorMsg) {
                    String inputString = etCreateAccountPassword.getTag().toString();
                    String inputStringConfirm = etCreateAccountConfirm.getTag().toString();

                    if(!inputString.equals("")) {
                        if(!FormValidation.isNameLongEnough(inputString)) {
                            if(showErrorMsg)tilCreateAccountPassword.setError(getString(R.string.warning_must_be_longer));
                            return false;
                        } else if(!inputStringConfirm.equals("") && !inputString.equals(inputStringConfirm)) {
                            if(showErrorMsg)tilCreateAccountPassword.setError(getString(R.string.warning_two_passwords_not_equal));
                            return false;
                        } else {
                            if(inputString.equals(inputStringConfirm)) etCreateAccountConfirm.getOnFieldFilledListener().isFieldFilled(etCreateAccountConfirm, true);
                            tilCreateAccountPassword.setErrorEnabled(false);
                            tilCreateAccountConfirm.setErrorEnabled(false);
                            return true;
                        }
                    } else {
                        tilCreateAccountPassword.setErrorEnabled(false);
                        tilCreateAccountConfirm.setErrorEnabled(false);
                        etCreateAccountConfirm.getOnFieldFilledListener().isFieldFilled(etCreateAccountConfirm, true);
                        return true;
                    }
                }
            });


            //CONFIRM PASSWORD
            etCreateAccountConfirm.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountConfirm.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {

                    String inputString = etCreateAccountConfirm.getTag().toString();
                    String inputStringPassword = etCreateAccountPassword.getTag().toString();

                    if(!inputString.equals("")) {
                        if(!FormValidation.isNameLongEnough(inputString)) {
                            if(showErrorMsg)tilCreateAccountConfirm.setError(getString(R.string.warning_must_be_longer));
                            return false;
                        } else if(!inputStringPassword.equals("") && !inputString.equals(inputStringPassword)) {
                            if(showErrorMsg)tilCreateAccountConfirm.setError(getString(R.string.warning_two_passwords_not_equal));
                            return false;
                        } else {
                            if(inputString.equals(inputStringPassword)) etCreateAccountPassword.getOnFieldFilledListener().isFieldFilled(etCreateAccountPassword, true);
                            tilCreateAccountConfirm.setErrorEnabled(false);
                            tilCreateAccountPassword.setErrorEnabled(false);
                            return true;
                        }
                    } else {
                        tilCreateAccountConfirm.setErrorEnabled(false);
                        tilCreateAccountPassword.setErrorEnabled(false);
                        etCreateAccountPassword.getOnFieldFilledListener().isFieldFilled(etCreateAccountPassword, true);
                        return true;
                    }
                }
            });

            //DATE OF BIRTH
            final TextInputLayout tilCreateAccountBirth = (TextInputLayout) findViewById(R.id.tilFifth);
            etCreateAccountBirth = (AppcompatEditTextWithWatcher) findViewById(R.id.etFifth);
            etCreateAccountBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b) {
                        DialogFragment newFragment = new BirthDayPickerFragment();
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    } else {
                        etCreateAccountBirth.getOnValidateListener().validate(true);
                    }
                }
            });
            etCreateAccountBirth.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountBirth.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    if(etCreateAccountBirth.getTag().toString().equals("")) {
                        tilCreateAccountBirth.setErrorEnabled(false);
                        etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, true);
                        return true;
                    } else if(etCreateAccountBirth.getTag() instanceof GregorianCalendar) {
                        Calendar datePicked = (GregorianCalendar) etCreateAccountBirth.getTag(); // calendar
                        if(FormValidation.isDateBeforeToday(datePicked)) {
                            tilCreateAccountBirth.setErrorEnabled(false);
                            etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, true);
                            return true;
                        } else {
                            if(showErrorMsg)tilCreateAccountBirth.setError(getString(R.string.warning_pick_earlier_date));
                            etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, false);
                            return false;
                        }

                    } else {
                        if(showErrorMsg) tilCreateAccountBirth.setError(getString(R.string.warning_wrong_date_format));
                        etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, false);
                        return false;
                    }


                }
            });

            formFillCheckerTwo.addEditText(etCreateAccountFullName, etCreateAccountEmail, etCreateAccountPassword, etCreateAccountConfirm, etCreateAccountBirth);
            etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, true); //because birthday's optional, set the default to true
        } else if (mScene == mScene3){



            //SIGN IN SCENE
            getSupportActionBar().setTitle(getString(R.string.signin_password_reset));
            formFillCheckerThree = new FormFillChecker(this);

            llSignInCreateAccountReset = (LinearLayout) findViewById(R.id.llSignInCreateAccount);
            llSignInCreateAccountReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SignInActivity.this, "Send Reset Instruction", Toast.LENGTH_SHORT).show();
                }
            });

            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene(mScene2);
                }
            });


            //EMAIL
            final TextInputLayout tilSignInEmail = (TextInputLayout) findViewById(R.id.tilFirst);
            final AppcompatEditTextWithWatcher etSignInEmail = (AppcompatEditTextWithWatcher) findViewById(R.id.etFirst);

            etSignInEmail.setOnFieldFilledListener(formFillCheckerThree);
            etSignInEmail.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    String inputString = etSignInEmail.getTag().toString();
                    if(!inputString.equals("") && !FormValidation.isEmailValid(inputString.toString())) {
                        if(showErrorMsg) tilSignInEmail.setError(getString(R.string.warning_not_valid_address));
                        return false;
                    } else {
                        tilSignInEmail.setErrorEnabled(false);
                        return true;
                    }
                }
            });

            formFillCheckerThree.addEditText(etSignInEmail);
        }
    }

    private void changeScene(Scene scene){
        mScene = scene;
        TransitionManager.go(mScene);
        ButterKnife.bind(this);
        validateView();
    }

    @Override
    public void isFieldsCompletelyFilled(boolean filled) {
        if(mScene == mScene1) {
            if(filled) {
                llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.sign_in_red));
                llSignInCreateAccountReset.setClickable(true);
            } else {
                llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.sign_in_disabled));
                llSignInCreateAccountReset.setClickable(false);
            }
        } else if(mScene == mScene2) {
            if(filled) {
                llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.sign_in_red));
                llSignInCreateAccountReset.setClickable(true);
            } else {
                llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.sign_in_disabled));
                llSignInCreateAccountReset.setClickable(false);
            }
        }  else if(mScene == mScene3) {
            if(filled) {
                llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.sign_in_red));
                llSignInCreateAccountReset.setClickable(true);
            } else {
                llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.sign_in_disabled));
                llSignInCreateAccountReset.setClickable(false);
            }
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
        Utility.openWebPage(this, getString(R.string.url_terms));
    }

    @Optional
    @OnClick(R.id.tvForgotPassword) void onClickForgotPassword(View v) {
        changeScene(mScene3);
    }

    @Override
    public void onBirthdaySelected(GregorianCalendar date) {
        String birthdayString = date.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG,
                Locale.getDefault()) + " " + date.get(GregorianCalendar.DAY_OF_MONTH) + ", "
                + date.get(GregorianCalendar.YEAR);

        etCreateAccountBirth.setText(birthdayString);
        etCreateAccountBirth.setTag(date);
        etCreateAccountBirth.getOnValidateListener().validate(true);
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
