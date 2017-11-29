package com.ivanhoecambridge.mall.signup;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.user.AccountManager;
import com.janrain.android.Jump;
import com.janrain.android.engage.session.JRSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by petar on 2017-09-01.
 */

public class AuthenticationManager implements Jump.SignInResultHandler, AccountManager.KcpAccountMergeListener {

    private final String TAG = "AuthenticationManager";
    private Context                       context;
    private String                        provider;
    private String                        errorRawReason;
    private onJanrainAuthenticateListener onJanrainAuthenticateListener;
    private String uuid;



    public enum ERROR_REASON {CANCELLED, INVALID_CREDENTIALS, INVALID_CREDENTIALS_SIGNIN, SOCIAL_ONLY, INVALID_FORM_INPUT, UNKNOWN}

    public final static String PROVIDER_FB = "facebook";
    public final static String PROVIDER_GOOGLE = "googleplus";

    private long socketTimeOutDuration = 60000;
    private Handler socketTimeHandler;
    private final Runnable socketError = new Runnable() {
        @Override
        public void run() {
            if (onJanrainAuthenticateListener != null) {
                onJanrainAuthenticateListener.onAuthenticateFailure(ERROR_REASON.UNKNOWN, null,  provider);
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
         * @param errorRawReason Error reason as returned by the Janrain response.
         * @param provider Provider that sign up or sign in failed with.
         */
        void onAuthenticateFailure(ERROR_REASON errorReason, String errorRawReason, String provider);
    }

    public AuthenticationManager(Context context, onJanrainAuthenticateListener onJanrainAuthenticateListener, Handler socketTimeHandler) {
        this.context = context;
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
     * Authenticates with an email and password.
     * @param userEmail Email.
     * @param userPassword Password.
     */
    public void authenticate(String userEmail, String userPassword) {
        onJanrainAuthenticateListener.onAuthenticateRequest(null);
        Jump.performTraditionalSignIn(userEmail, userPassword, this, null);
    }

    /**
     * Starts the email registration process.
     * @param userObject JSONObject that holds all required user information as specified by Janrain schema. */
    public void registerByEmail(JSONObject userObject) {
        onJanrainAuthenticateListener.onAuthenticateRequest(null);
        register(modifyDisplayNameAsEmail(userObject), null);
    }

    @Override
    public void onSuccess() {
        updateUUID();
        mergeToKcpAccount();
    }

    @Override
    public void onFailure(SignInError error) {
        if (error.reason == SignInError.FailureReason.CAPTURE_API_ERROR && error.captureApiError.isTwoStepRegFlowError()) {
            register(error.captureApiError.getPreregistrationRecord(), error.captureApiError.getSocialRegistrationToken());
        } else {
            onJanrainAuthenticateListener.onAuthenticateFailure(parseSignInError(error), getErrorRawReason(), provider);
        }
    }

    private ERROR_REASON parseSignInError(SignInError error) {
        ERROR_REASON errorReason;
        if (error.reason == SignInError.FailureReason.AUTHENTICATION_CANCELLED_BY_USER) {
            errorReason = ERROR_REASON.CANCELLED;
        } else if (error.reason == SignInError.FailureReason.CAPTURE_API_ERROR && error.captureApiError.code == 210) {
            errorReason = ERROR_REASON.INVALID_CREDENTIALS_SIGNIN;
        } else if (error.captureApiError.isMergeFlowError()) {
            errorReason = ERROR_REASON.SOCIAL_ONLY;
        } else if (error.captureApiError.isFormValidationError()) {
            errorReason = ERROR_REASON.INVALID_FORM_INPUT;
            setErrorRawReason(parseRawResponse(error.captureApiError.raw_response, "invalid_fields"));
        } else {
            errorReason = ERROR_REASON.UNKNOWN;
        }
        return errorReason;
    }

    /**
     * Parses the raw response as returned by Janrain. This will return the first response that matches under the given key.
     * @param jsonResponse JSONObject response.
     * @param keyToFind The key to look for within the response object.
     * @return value stored under the specified key or null if it does not exist.
     */
    private String parseRawResponse(JSONObject jsonResponse, String keyToFind) {
        if (jsonResponse == null || keyToFind == null || keyToFind.length() == 0) {
            return null;
        }
        String rawResponse = null;
        try {
            if (jsonResponse.has(keyToFind)) {
                if (jsonResponse.get(keyToFind) instanceof JSONObject) {
                    JSONObject keyObject = jsonResponse.getJSONObject(keyToFind);
                    String firstKey = keyObject.keys().next();
                    if (keyObject.get(firstKey) instanceof JSONArray) {
                        JSONArray responseArr = keyObject.getJSONArray(firstKey);
                        if (!responseArr.isNull(0)) {
                            rawResponse = responseArr.getString(0);
                        }
                    } else {
                        rawResponse = keyObject.optString(firstKey);
                    }

                } else {
                    rawResponse =  jsonResponse.getString(keyToFind);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rawResponse;

    }

    private String getErrorRawReason() {
        return errorRawReason == null ? "" : errorRawReason;
    }

    private void setErrorRawReason(String errorRawReason) {
        this.errorRawReason = errorRawReason;
    }

    /**
     * Start the registration process through Janrain with a filled out user object, and a social registration token.
     * @param userObject JSONObject containing user details.
     * @param socialRegistrationToken If a social provider is used for registration then this token is required. <br />
     *                                Otherwise if the traditional method of manual user registration is done this token can be null.
     */
    private void register(JSONObject userObject, @Nullable String socialRegistrationToken) {
        Jump.registerNewUser(modifyDisplayNameAsEmail(userObject), socialRegistrationToken, this);
    }

    /**
     * Modifies an existing JSONObject containing user information to set the display name attribute as the user email.
     * <br /> This is done because Janrain display names are unique and two users cannot have the same display name.
     * @param userObject JSONObject to modify.
     * @return Modified user JSONObject.
     */
    private JSONObject modifyDisplayNameAsEmail(JSONObject userObject) {
        try {
            userObject.put("displayName", userObject.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userObject;
    }

    /**
     * Utility method to check if the specified provider is enabled.
     * @param provider Provider to check for.
     * @return true if enabled, false if not.
     */
    public boolean isProviderEnabled(String provider) {
        return JRSession.getInstance().getProviderByName(provider) != null;
    }

    private void updateUUID() {
        try {
            uuid = Jump.getSignedInUser().getString("uuid");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void mergeToKcpAccount() {
        AccountManager accountManager = null;
        if (context != null) {
            accountManager = new AccountManager(context);
        }
        if (accountManager == null || uuid == null) {
            onJanrainAuthenticateListener.onAuthenticateFailure(ERROR_REASON.UNKNOWN, null, provider);
        } else {
            accountManager.updateUserTokenWithJanrainId(uuid, this);
        }

    }

    @Override
    public void onAccountMergeSuccess() {
        KcpUtility.cacheToPreferences(context, JanrainRecordManager.KEY_USER_ID, uuid);
        onJanrainAuthenticateListener.onAuthenticateSuccess();
    }

    @Override
    public void onAccountMergeFailed(int errorCode, String error) {
        Log.i(TAG, errorCode + ": " + error);
        onJanrainAuthenticateListener.onAuthenticateFailure(ERROR_REASON.UNKNOWN, null, provider);
    }

}
