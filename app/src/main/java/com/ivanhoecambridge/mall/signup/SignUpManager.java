package com.ivanhoecambridge.mall.signup;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.janrain.android.Jump;
import com.janrain.android.engage.JREngage;
import com.janrain.android.engage.session.JRSession;

import org.json.JSONObject;

/**
 * Created by petar on 2017-09-01.
 */

public class SignUpManager implements Jump.SignInResultHandler {

    private final String TAG = "SignUpManager";
    private String provider;
    private onSignUpListener onSignUpListener;

    public enum ERROR_REASON {CANCELLED, INVALID_CREDENTIALS, UNKNOWN}

    public interface onSignUpListener {
        /**
         * Notifies the caller that the sign up request process has started.
         * @param provider Provider that sign up is being requested with.
         */
        void onSignUpRequest(String provider);

        /**
         * Called when the sign up request has completed successfully.
         */
        void onSignUpSuccess();
        /**
         * Called when the sign up request has failed.
         * @param errorReason Error reason.
         * @param provider Provider that sign up failed with.
         */
        void onSignUpFailure(ERROR_REASON errorReason, String provider);
    }

    public SignUpManager(onSignUpListener onSignUpListener) {
        this.onSignUpListener = onSignUpListener;
    }

    /**
     * Authenticates with the given social provider through Janrain.
     * @param activity activity which will host the provider login page.
     * @param provider provider to login with.
     */
    public void authenticate(Activity activity, String provider) {
        this.provider = provider;
        onSignUpListener.onSignUpRequest(provider);
        Jump.showSignInDialog(activity, provider, this, null);
    }

    @Override
    public void onSuccess() {
        onSignUpListener.onSignUpSuccess();
    }

    @Override
    public void onFailure(SignInError error) {
        if (error.reason == SignInError.FailureReason.CAPTURE_API_ERROR && error.captureApiError.isTwoStepRegFlowError()) {
            register(error.captureApiError.getPreregistrationRecord(), error.captureApiError.getSocialRegistrationToken());
        } else {
            onSignUpListener.onSignUpFailure(parseSignInError(error), provider);
        }
    }

    private ERROR_REASON parseSignInError(SignInError error) {
        if (error.reason == SignInError.FailureReason.AUTHENTICATION_CANCELLED_BY_USER) return ERROR_REASON.CANCELLED;
        return ERROR_REASON.UNKNOWN;
    }

    /**
     * Start the registration process through Janrain with a filled out user object, and a social registration token.
     * @param userObject JSONObject containing user details.
     * @param socialRegistrationToken If a social provider is used for registration then this token is required. <br />
     *                                Otherwise if the traditional method of manual user registration is done this token can be null.
     */
    private void register(JSONObject userObject, @Nullable String socialRegistrationToken) {
        Jump.registerNewUser(userObject, socialRegistrationToken, this);
    }

    /**
     * Utility method to check if the specified provider is enabled.
     * @param provider Provider to check for.
     * @return true if enabled, false if not.
     */
    public boolean isProviderEnabled(String provider) {
        return JRSession.getInstance().getProviderByName(provider) != null;
    }
}
