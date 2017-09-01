package com.ivanhoecambridge.mall.signup;

import android.app.Activity;
import android.util.Log;

import com.janrain.android.Jump;

import org.json.JSONObject;

/**
 * Created by petar on 2017-09-01.
 */

public class SignUpManager implements Jump.SignInResultHandler {

    private final String TAG = "SignUpManager";
    private String provider;

    interface onSignUpListener {
        void onSignUpSuccess();
        void onSignUpFailure(String error);
    }

    /**
     * Authenticates with the given social provider through Janrain.
     * @param activity activity which will host the provider login page.
     * @param provider provider to login with.
     */
    public void authenticate(Activity activity, String provider) {
        this.provider = provider;
        Jump.showSignInDialog(activity, provider, this, null);
    }

    @Override
    public void onSuccess() {
        Log.i(TAG, "Registered by " + provider);
    }

    @Override
    public void onFailure(SignInError error) {
        if (error.reason == SignInError.FailureReason.CAPTURE_API_ERROR && error.captureApiError.isTwoStepRegFlowError()) {
            register(error.captureApiError.getPreregistrationRecord(), error.captureApiError.getSocialRegistrationToken());
        } else {
            Log.i("SignIn", error.toString());
        }
    }

    private void register(JSONObject userObject, String socialRegistrationToken) {
        Log.i(TAG, "Registration in progress...");
        Jump.registerNewUser(userObject, socialRegistrationToken, this);
    }
}
