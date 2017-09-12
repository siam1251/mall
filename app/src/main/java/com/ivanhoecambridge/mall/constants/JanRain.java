package com.ivanhoecambridge.mall.constants;

import com.janrain.android.JumpConfig;

/**
 * Created by petar on 2017-08-29.
 */

public class JanRain {

    private final static String CAPTURE_FLOW_NAME = "standard";
    private final static String CAPTURE_LOCALE = "en-US";
    private final static String CAPTURE_TRADITIONAL_SIGN_IN_NAME = "signInForm";
    private final static String CAPTURE_TRADITIONAL_REGISTRATION_NAME = "registrationForm";
    private final static String CAPTURE_SOCIAL_REGISTRATION_NAME = "socialRegistrationForm";
    private final static boolean CAPTURE_ENABLE_THIN = false;
    private final static String CAPTURE_FLOW_VERSION = "HEAD";
    private final static String CAPTURE_FORGOT_PASSWORD_NAME = "forgotPasswordForm";
    private final static String CAPTURE_EDIT_PROFILE_NAME = "editProfileForm";
    private final static String CAPTURE_RESEND_VERIFICATION_NAME = "resendVerificationForm";


    public enum AppType {DEV, PROD}

    public static JumpConfig configure(AppType appType) {
        JumpConfig jumpConfig = new JumpConfig();

        switch (appType) {
            case DEV:
                jumpConfig.engageAppId = "fbclnfflndmnignnjcjc";
                jumpConfig.captureAppId = "yhyw2aqa6t6zm649nkffkdamr8";
                jumpConfig.captureDomain = "ivanhoe-cambridge-dev.us-dev.janraincapture.com";
                jumpConfig.captureClientId = "dtc8qwcnnus2djkzf45yjxchngcntsh2";
                jumpConfig.captureFlowName = CAPTURE_FLOW_NAME;
                jumpConfig.captureLocale = CAPTURE_LOCALE;
                jumpConfig.captureTraditionalSignInFormName = CAPTURE_TRADITIONAL_SIGN_IN_NAME;
                jumpConfig.captureEnableThinRegistration = CAPTURE_ENABLE_THIN;
                jumpConfig.captureTraditionalRegistrationFormName = CAPTURE_TRADITIONAL_REGISTRATION_NAME;
                jumpConfig.captureSocialRegistrationFormName = CAPTURE_SOCIAL_REGISTRATION_NAME;
                jumpConfig.captureFlowVersion = CAPTURE_FLOW_VERSION;
                jumpConfig.captureForgotPasswordFormName = CAPTURE_FORGOT_PASSWORD_NAME;
                jumpConfig.captureEditUserProfileFormName = CAPTURE_EDIT_PROFILE_NAME;
                jumpConfig.captureResendEmailVerificationFormName = CAPTURE_RESEND_VERIFICATION_NAME;
                break;
            case PROD:
                jumpConfig.engageAppId = "kchliobppnpaaggibkcg";
                jumpConfig.captureAppId = "4dtcmxp2e4z2jgwzsmaftk7zn3";
                jumpConfig.captureDomain = "ivanhoe-cambridge.us.janraincapture.com";
                jumpConfig.captureClientId = "caycqadcfnjt7mbhwccvy5hxdfgpxzch";
                jumpConfig.captureFlowName = CAPTURE_FLOW_NAME;
                jumpConfig.captureLocale = CAPTURE_LOCALE;
                jumpConfig.captureTraditionalSignInFormName = CAPTURE_TRADITIONAL_SIGN_IN_NAME;
                jumpConfig.captureEnableThinRegistration = CAPTURE_ENABLE_THIN;
                jumpConfig.captureTraditionalRegistrationFormName = CAPTURE_TRADITIONAL_REGISTRATION_NAME;
                jumpConfig.captureSocialRegistrationFormName = CAPTURE_SOCIAL_REGISTRATION_NAME;
                jumpConfig.captureFlowVersion = CAPTURE_FLOW_VERSION;
                jumpConfig.captureForgotPasswordFormName = CAPTURE_FORGOT_PASSWORD_NAME;
                jumpConfig.captureEditUserProfileFormName = CAPTURE_EDIT_PROFILE_NAME;
                jumpConfig.captureResendEmailVerificationFormName = CAPTURE_RESEND_VERIFICATION_NAME;
                break;
        }

        return jumpConfig;
    }
}
