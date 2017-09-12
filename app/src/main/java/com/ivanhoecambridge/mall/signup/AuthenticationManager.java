package com.ivanhoecambridge.mall.signup;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.janrain.android.Jump;
import com.janrain.android.engage.session.JRSession;

import org.json.JSONObject;

/**
 * Created by petar on 2017-09-01.
 */

public class AuthenticationManager implements Jump.SignInResultHandler {

    private final String TAG = "AuthenticationManager";
    private String provider;
    private onJanrainAuthenticateListener onJanrainAuthenticateListener;

    public enum ERROR_REASON {CANCELLED, INVALID_CREDENTIALS, UNKNOWN}

    public final static String PROVIDER_FB = "facebook";
    public final static String PROVIDER_GOOGLE = "googleplus";

    private long socketTimeOutDuration = 60000;
    private Handler socketTimeHandler;
    private final Runnable socketError = new Runnable() {
        @Override
        public void run() {
            if (onJanrainAuthenticateListener != null) {
                onJanrainAuthenticateListener.onAuthenticateFailure(ERROR_REASON.UNKNOWN, provider);
            }
        }
    };

    public interface onJanrainAuthenticateListener {
        /**
         * Notifies the caller that the sign up or sign in request process has started.
         * @param provider Provider that sign up or sign in is being requested with.
         */
        void onAuthenticateRequest(String provider);

        /**
         * Called when the sign up or sign in request has completed successfully.
         */
        void onAuthenticateSuccess();
        /**
         * Called when the sign up or sign in request has failed.
         * @param errorReason Error reason.
         * @param provider Provider that sign up or sign in failed with.
         */
        void onAuthenticateFailure(ERROR_REASON errorReason, String provider);
    }

    public AuthenticationManager(onJanrainAuthenticateListener onJanrainAuthenticateListener, Handler socketTimeHandler) {
        this.onJanrainAuthenticateListener = onJanrainAuthenticateListener;
        this.socketTimeHandler = socketTimeHandler;
    }


    /**
     * Sets the socket timeout duration. Default is 1 minute.
     * @param duration Desired socket timeout duration in milliseconds.
     */
    public void setSocketTimeOutDuration(long duration) {
        this.socketTimeOutDuration = duration;
    }

    /**
     * Removes socket time out runnable from the handler.
     */
    public void removeSocketTimeOutCallback() {
        socketTimeHandler.removeCallbacks(socketError);
    }

    /**
     * Authenticates with the given social provider through Janrain.
     * @param activity activity which will host the provider login page.
     * @param provider provider to login with.
     */
    public void authenticate(Activity activity, String provider) {
        this.provider = provider;
        onJanrainAuthenticateListener.onAuthenticateRequest(provider);
        if (provider.equals(PROVIDER_GOOGLE)) {
            socketTimeHandler.postDelayed(socketError, socketTimeOutDuration);
        }
        Jump.showSignInDialog(activity, provider, this, null);
    }

    /**
     * Starts the email registration process.
     * @param userObject JSONObject that holds all required user information as specified by Janrain schema. */
    public void registerByEmail(JSONObject userObject) {
        onJanrainAuthenticateListener.onAuthenticateRequest(null);
        register(userObject, null);
    }

    @Override
    public void onSuccess() {
        onJanrainAuthenticateListener.onAuthenticateSuccess();
    }

    @Override
    public void onFailure(SignInError error) {
        if (error.reason == SignInError.FailureReason.CAPTURE_API_ERROR && error.captureApiError.isTwoStepRegFlowError()) {
            register(error.captureApiError.getPreregistrationRecord(), error.captureApiError.getSocialRegistrationToken());
        } else {
            onJanrainAuthenticateListener.onAuthenticateFailure(parseSignInError(error), provider);
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
