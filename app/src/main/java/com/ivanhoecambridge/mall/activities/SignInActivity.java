package com.ivanhoecambridge.mall.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.fragments.BirthDayPickerFragment;
import com.ivanhoecambridge.mall.signin.FormFillChecker;
import com.ivanhoecambridge.mall.signin.FormFillInterface;
import com.ivanhoecambridge.mall.signin.FormValidation;
import com.ivanhoecambridge.mall.signup.AuthenticationManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AppcompatEditTextWithWatcher;
import com.janrain.android.Jump;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;


/**
 * Created by Kay on 2017-01-27.
 */

public class SignInActivity extends BaseActivity implements FormFillInterface, BirthDayPickerFragment.DateSelectedListener, DialogInterface.OnDismissListener, AuthenticationManager.onJanrainAuthenticateListener {

    ViewGroup rootContainer;
    private Scene signInScene;
    private Scene signUpScene;
    private Scene forgotPasswordScene;
    private Scene activeScene;
    private FormFillChecker formFillCheckerOne;
    private FormFillChecker formFillCheckerTwo;
    private FormFillChecker formFillCheckerThree;

    private BirthDayPickerFragment birthdayFragment;

    private AuthenticationManager authenticationManager;

    public final static int SIGNIN_SCNE = 0;
    public final static int SIGNUP_SCENE = 1;
    public final static int FORGOT_PASSWORD_SCENE = 2;

    //SCENE COMMON
    private TextView tvSignIn;
    private TextView tvTerms;
    @BindView(R.id.tvError)
    TextView tvError;
    private CheckBox cbPromosDeals;
    LinearLayout llSignInCreateAccountReset;

    //SIGN IN SCENE
    private ImageView                    ivMallLogo;
    private AppcompatEditTextWithWatcher etSignInEmail;
    private AppcompatEditTextWithWatcher etSignInPassword;

    //SIGN UP SCENE
    private AppcompatEditTextWithWatcher etCreateAccountFullName;
    private AppcompatEditTextWithWatcher etCreateAccountEmail;
    private AppcompatEditTextWithWatcher etCreateAccountPassword;
    private AppcompatEditTextWithWatcher etCreateAccountPasswordConfirm;
    private AppcompatEditTextWithWatcher etCreateAccountBirth;
    //SIGN UP FIELDS
    private String formattedBirthday;
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.signin_sign_in));

        rootContainer = (ViewGroup) findViewById(R.id.scene_root);

        signInScene = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_one, this);

        signUpScene = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_two, this);

        forgotPasswordScene = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_three, this);

        activeScene = setActiveRootScene(getIntent().getExtras());
        activeScene.enter();

        initializeView();
        validateView();
        authenticationManager = new AuthenticationManager(this, new Handler(Looper.getMainLooper()));

    }

    @Override
    public void onPause() {
        Jump.saveToDisk(this);
        super.onPause();
    }

    private Scene setActiveRootScene(Bundle data) {
        if (data == null) {return signInScene;}
        switch (data.getInt(Constants.KEY_ACTIVE_SCENE_ORDER, SIGNUP_SCENE)) {
            case SIGNIN_SCNE:
            default:
                return signInScene;
            case SIGNUP_SCENE:
                return signUpScene;
            case FORGOT_PASSWORD_SCENE:
                return forgotPasswordScene;
        }

    }

    private void initializeView() {
        ButterKnife.bind(this);
    }


    private void validateView() {
        if (activeScene == signInScene) {
            //SIGN IN SCENE
            getSupportActionBar().setTitle(getString(R.string.signin_sign_in));
            formFillCheckerOne = new FormFillChecker(this);
            ivMallLogo = (ImageView) findViewById(R.id.ivMallLogo);

            int id = getResources().getIdentifier("img_signin_mall_logo_small", "drawable", getPackageName());
            if (id > 0) {
                ivMallLogo.setImageDrawable(ContextCompat.getDrawable(this, id));
            }

            llSignInCreateAccountReset = (LinearLayout) findViewById(R.id.llSignInCreateAccount);
            llSignInCreateAccountReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    authenticationManager.authenticate(getTextFromField(etSignInEmail), getTextFromField(etSignInPassword));
                }
            });
            llSignInCreateAccountReset.setClickable(false);
            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene(signUpScene);
                }
            });


            //EMAIL
            final TextInputLayout tilSignInEmail = (TextInputLayout) findViewById(R.id.tilFirst);
            etSignInEmail = (AppcompatEditTextWithWatcher) findViewById(R.id.etSignInEmail);
            etSignInEmail.setOnFieldFilledListener(formFillCheckerOne);
            etSignInEmail.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    String inputString = etSignInEmail.getTag().toString();
                    if (!inputString.equals("") && !FormValidation.isEmailValid(inputString.toString())) {
                        if (showErrorMsg)
                            tilSignInEmail.setError(getString(R.string.warning_not_valid_address));
                        return false;
                    } else {
                        tilSignInEmail.setErrorEnabled(false);
                        return true;
                    }
                }
            });

            //PASSWORD
            etSignInPassword = (AppcompatEditTextWithWatcher) findViewById(R.id.etSignInPassword);
            etSignInPassword.setOnFieldFilledListener(formFillCheckerOne);

            formFillCheckerOne.addEditText(etSignInEmail, etSignInPassword);
        } else if (activeScene == signUpScene) {
            //CREATE ACCOUNT SCENE
            getSupportActionBar().setTitle(getString(R.string.signin_create_account));
            formFillCheckerTwo = new FormFillChecker(this);

            llSignInCreateAccountReset = (LinearLayout) findViewById(R.id.llSignInCreateAccount);
            llSignInCreateAccountReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    authenticationManager.registerByEmail(createUserAccount());
                }
            });


            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene(signInScene);
                }
            });

            //FULL NAME
            etCreateAccountFullName = (AppcompatEditTextWithWatcher) findViewById(R.id.etCreateAccountFullName);
            etCreateAccountFullName.setOnFieldFilledListener(formFillCheckerTwo);

            //EMAIL
            final TextInputLayout tilCreateAccountEmail = (TextInputLayout) findViewById(R.id.tilSecond);
            etCreateAccountEmail = (AppcompatEditTextWithWatcher) findViewById(R.id.etCreateAccountEmail);
            etCreateAccountEmail.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountEmail.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    String inputString = etCreateAccountEmail.getTag().toString();
                    if (!inputString.equals("") && !FormValidation.isEmailValid(inputString)) {
                        if (showErrorMsg)
                            tilCreateAccountEmail.setError(getString(R.string.warning_not_valid_address));
                        return false;
                    } else {
                        tilCreateAccountEmail.setErrorEnabled(false);

                        return true;
                    }
                }
            });


            //PASSWORD
            final TextInputLayout tilCreateAccountPassword = (TextInputLayout) findViewById(R.id.tilThird);
            etCreateAccountPassword = (AppcompatEditTextWithWatcher) findViewById(R.id.etCreateAccountPassword);

            final TextInputLayout tilCreateAccountConfirm = (TextInputLayout) findViewById(R.id.tilFourth);
            etCreateAccountPasswordConfirm = (AppcompatEditTextWithWatcher) findViewById(R.id.etCreateAccountPasswordConfirm);


            etCreateAccountPassword.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountPassword.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    String inputString = etCreateAccountPassword.getTag().toString();
                    String inputStringConfirm = etCreateAccountPasswordConfirm.getTag().toString();

                    if (!inputString.equals("")) {
                        if (!FormValidation.isNameLongEnough(inputString)) {
                            if (showErrorMsg)
                                tilCreateAccountPassword.setError(getString(R.string.warning_must_be_longer));
                            return false;
                        } else if (!inputStringConfirm.equals("") && !inputString.equals(inputStringConfirm)) {
                            if (showErrorMsg)
                                tilCreateAccountPassword.setError(getString(R.string.warning_two_passwords_not_equal));
                            return false;
                        } else {
                            if (inputString.equals(inputStringConfirm))
                                etCreateAccountPasswordConfirm.getOnFieldFilledListener().isFieldFilled(etCreateAccountPasswordConfirm, true);
                            tilCreateAccountPassword.setErrorEnabled(false);
                            tilCreateAccountConfirm.setErrorEnabled(false);
                            return true;
                        }
                    } else {
                        tilCreateAccountPassword.setErrorEnabled(false);
                        tilCreateAccountConfirm.setErrorEnabled(false);
                        etCreateAccountPasswordConfirm.getOnFieldFilledListener().isFieldFilled(etCreateAccountPasswordConfirm, true);
                        return true;
                    }
                }
            });


            //CONFIRM PASSWORD
            etCreateAccountPasswordConfirm.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountPasswordConfirm.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {

                    String inputString = etCreateAccountPasswordConfirm.getTag().toString();
                    String inputStringPassword = etCreateAccountPassword.getTag().toString();

                    if (!inputString.equals("")) {
                        if (!FormValidation.isNameLongEnough(inputString)) {
                            if (showErrorMsg)
                                tilCreateAccountConfirm.setError(getString(R.string.warning_must_be_longer));
                            return false;
                        } else if (!inputStringPassword.equals("") && !inputString.equals(inputStringPassword)) {
                            if (showErrorMsg)
                                tilCreateAccountConfirm.setError(getString(R.string.warning_two_passwords_not_equal));
                            return false;
                        } else {
                            if (inputString.equals(inputStringPassword))
                                etCreateAccountPassword.getOnFieldFilledListener().isFieldFilled(etCreateAccountPassword, true);
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
            etCreateAccountBirth = (AppcompatEditTextWithWatcher) findViewById(R.id.etCreateAccountBirthday);
            etCreateAccountBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (birthdayFragment == null) {
                            birthdayFragment = new BirthDayPickerFragment();
                        }
                        birthdayFragment.show(getSupportFragmentManager(), "datePicker");
                    }
                }
            });
            etCreateAccountBirth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (birthdayFragment == null) {
                        birthdayFragment = new BirthDayPickerFragment();
                    }
                    birthdayFragment.show(getSupportFragmentManager(), "datePicker");
                }
            });
            etCreateAccountBirth.setOnFieldFilledListener(formFillCheckerTwo);
            etCreateAccountBirth.setOnValidateListener(new AppcompatEditTextWithWatcher.OnValidateListener() {
                @Override
                public boolean validate(boolean showErrorMsg) {
                    if (etCreateAccountBirth.getTag().toString().equals("")) {
                        tilCreateAccountBirth.setErrorEnabled(false);
                        etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, true);
                        return true;
                    } else if (etCreateAccountBirth.getTag() instanceof GregorianCalendar) {
                        Calendar datePicked = (GregorianCalendar) etCreateAccountBirth.getTag(); // calendar
                        if (FormValidation.isMinimumAgeRequired(datePicked)) {
                            tilCreateAccountBirth.setErrorEnabled(false);
                            etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, true);
                            return true;
                        } else {
                            if (showErrorMsg)
                                tilCreateAccountBirth.setError(getString(R.string.warning_minimum_age_date));
                            etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, false);
                            return false;
                        }

                    } else {
                        if (showErrorMsg)
                            tilCreateAccountBirth.setError(getString(R.string.warning_wrong_date_format));
                        etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, false);
                        return false;
                    }


                }
            });

            formFillCheckerTwo.addEditText(etCreateAccountFullName, etCreateAccountEmail, etCreateAccountPassword, etCreateAccountPasswordConfirm, etCreateAccountBirth);
            etCreateAccountBirth.getOnFieldFilledListener().isFieldFilled(etCreateAccountBirth, true); //because birthday's optional, set the default to true

            cbPromosDeals = (CheckBox) findViewById(R.id.cbPromosDeals);
            tvTerms = (TextView) findViewById(R.id.tvTerms);

            tvTerms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.openWebPage(SignInActivity.this, getString(R.string.url_terms));
                }
            });
        } else if (activeScene == forgotPasswordScene) {


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
                    changeScene(signUpScene);
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
                    if (!inputString.equals("") && !FormValidation.isEmailValid(inputString.toString())) {
                        if (showErrorMsg)
                            tilSignInEmail.setError(getString(R.string.warning_not_valid_address));
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

    /**
     * Retrieves all the filled out form information and creates a JSONObject from it.
     * @return JSONObject which contains user information.
     */
    private JSONObject createUserAccount() {
        JSONObject newUser = new JSONObject();
        String displayName = getTextFromField(etCreateAccountFullName);
        String[] nameSplit;
        if (displayName.length() > 0 && displayName.contains(" ")) {
            nameSplit = displayName.split("\\s+");
        } else {
            nameSplit = new String[2];
            nameSplit[0] = displayName;
            nameSplit[1] = "";
        }

        try {
            newUser.put("email", getTextFromField(etCreateAccountEmail))
                    .put("displayName", displayName)
                    .put("givenName", nameSplit[0])
                    .put("familyName", nameSplit[nameSplit.length-1])
                    .put("password", getTextFromField(etCreateAccountPassword));
            if (formattedBirthday != null && formattedBirthday.length() > 0) {
                newUser.put("birthday", formattedBirthday);
            }
            //temporary -- doesn't do anything yet.
            if (cbPromosDeals.isChecked()) {
                newUser.put("apps", createAppsElement());
            }

        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        return newUser;
    }



    private String getTextFromField(EditText edtField) {
        if (edtField == null || edtField.getText().toString().isEmpty()) return "";
        return edtField.getText().toString();
    }

    private JSONObject createAppsElement() throws JSONException {
        JSONObject appsElement = new JSONObject();
        appsElement.put("emailOptIn", cbPromosDeals.isChecked());
        return appsElement;
    }

    private void changeScene(Scene scene) {
        activeScene = scene;
        TransitionManager.go(activeScene);
        ButterKnife.bind(this);
        validateView();
    }

    @Override
    public void isFieldsCompletelyFilled(boolean filled) {
        llSignInCreateAccountReset.setClickable(filled);
        if (filled) {
            llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.themeColor));
        } else {
            llSignInCreateAccountReset.setBackgroundColor(getResources().getColor(R.color.sign_in_disabled));
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        etCreateAccountBirth.getOnValidateListener().validate(true);
    }

    @Optional
    @OnClick(R.id.cvFb)
    void onFbClicked(View v) {
        authenticationManager.authenticate(this, AuthenticationManager.PROVIDER_FB);
    }

    @Optional
    @OnClick(R.id.cvGoogle)
    void onGoogleClicked(View v) {
       authenticationManager.authenticate(this, AuthenticationManager.PROVIDER_GOOGLE);
    }

    @OnClick(R.id.tvError)
    public void onErrorMessageClicked(View v) {
        setErrorNotificationMessage(null, false);
    }


    @Override
    public void onAuthenticateRequest(String provider) {
        setProgressIndicator(true);
    }

    @Override
    public void onAuthenticateSuccess() {
        setResult(RESULT_OK);
        Analytics.getInstance(this).logEvent("Profile_Signin_Completed", "PROFILE", "Sign Up completed");
        setProgressIndicator(false);
        if (getCallingActivity() == null) {
            startActivity(createHomeIntent());
        }
        ActivityAnimation.startActivityAnimation(this);
        finishActivity();
    }

    @Override
    public void onAuthenticateFailure(AuthenticationManager.ERROR_REASON errorReason, String errorRawReason, String provider) {
        setResult(Constants.RESULT_FAILED);
        Analytics.getInstance(this).logEvent("Profile_Signin_Incompleted", "PROFILE", "Sign Up incompleted");
        setProgressIndicator(false);
        setErrorNotificationMessage(getErrorMessage(errorReason, errorRawReason), true);
    }

    private Intent createHomeIntent() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return homeIntent;
    }

    private void setProgressIndicator(boolean shouldShowProgress) {
        ProgressBarWhileDownloading.showProgressDialog(this, R.layout.layout_loading_item, shouldShowProgress);
    }

    private String getErrorMessage(AuthenticationManager.ERROR_REASON errorReason, String errorRawReason) {
        switch (errorReason) {
            case CANCELLED:
                return KcpUtility.getString(this, R.string.signin_error_cancelled);
            case INVALID_CREDENTIALS:
                return KcpUtility.getString(this, R.string.signin_error_invalid_credentials);
            case INVALID_CREDENTIALS_SIGNIN:
                return KcpUtility.getString(this, R.string.signin_error_invalid_credentials_try_again);
            case INVALID_FORM_INPUT:
                return errorRawReason.length() == 0 ? KcpUtility.getString(this, R.string.signin_error_unknown) : errorRawReason;
            case SOCIAL_ONLY:
                return KcpUtility.getString(this, R.string.signin_error_social_signin_only);
            case UNKNOWN:
            default:
                return KcpUtility.getString(this, R.string.signin_error_unknown);
        }
    }


    @Optional
    @OnClick(R.id.tvForgotPassword)
    void onClickForgotPassword(View v) {
        changeScene(forgotPasswordScene);
    }

    @Override
    public void onBirthdaySelected(GregorianCalendar date) {
        String birthdayString = date.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG,
                Locale.getDefault()) + " " + date.get(GregorianCalendar.DAY_OF_MONTH) + ", "
                + date.get(GregorianCalendar.YEAR);


        formatBirthdayForJanrain(date);
        etCreateAccountBirth.setText(birthdayString);
        etCreateAccountBirth.setTag(date);
        etCreateAccountBirth.getOnValidateListener().validate(true);
    }

    private void formatBirthdayForJanrain(GregorianCalendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.getStringFromResources(this, R.string.date_format_janrain_capture), Locale.US);
        formattedBirthday = dateFormat.format(date.getTime());
    }

    private void setErrorNotificationMessage(@Nullable String error, boolean shouldDisplay) {
        if (error != null) tvError.setText(error);
        animateErrorMessage(shouldDisplay);
        tvError.setVisibility(shouldDisplay ? View.VISIBLE : View.GONE);
    }

    private void animateErrorMessage(boolean isAppearing) {
        Animation slideDirection = AnimationUtils.loadAnimation(this, isAppearing ? R.anim.anim_slide_down : R.anim.anim_slide_up);
        tvError.startAnimation(slideDirection);
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
        setResult(RESULT_CANCELED);
        finishActivity();
    }


}
