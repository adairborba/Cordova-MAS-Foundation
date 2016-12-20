package com.ca.mas.cordova.core;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASFoundationStrings;
import com.ca.mas.foundation.MASSessionUnlockCallback;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.identity.user.MASAddress;
import com.ca.mas.identity.user.MASEmail;
import com.ca.mas.identity.user.MASPhone;
import com.ca.mas.identity.user.MASPhoto;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by trima09 on 12/13/2016.
 */

public class MASPluginUser extends CordovaPlugin {
    private static final String TAG = MASPluginUser.class.getCanonicalName();

    private Command command = null;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        command = new Command() {
            @Override
            public void execute(Context context, JSONArray args, CallbackContext callbackContext) {
                return;
            }

            @Override
            public String getAction() {
                return null;
            }
        };
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equalsIgnoreCase("isAuthenticated")) {
            isAuthenticated(callbackContext);
        } else if (action.equalsIgnoreCase("isCurrentUser")) {
            isCurrentUser(callbackContext);
        } else if (action.equalsIgnoreCase("currentUser")) {
            getCurrentUser(callbackContext);
        } else if (action.equalsIgnoreCase("isSessionLocked")) {
            isSessionLocked(callbackContext);
        } else if (action.equalsIgnoreCase("lockSession")) {
            lockSession(callbackContext);
        } else if (action.equalsIgnoreCase("unlockSession")) {
            unlockSession(callbackContext);
        } else if (action.equalsIgnoreCase("unlockSessionWithMessage")) {
            unlockSessionWithMessage(callbackContext, args);
        } else if (action.equalsIgnoreCase("removeSessionLock")) {
            removeSessionLock(callbackContext);
        } else if (action.equalsIgnoreCase("loginWithUsernameAndPassword")) {
            loginWithUsernameAndPassword(args, callbackContext);
        } else if (action.equalsIgnoreCase("logoutUser")) {
            logoutUser(callbackContext);
        } else if (action.equalsIgnoreCase("requestUserInfo")) {
            requestUserInfo(callbackContext);
        } else {
            callbackContext.error("Invalid action");
            return false;
        }
        return true;
    }

    /**
     * Checks the boolean status if the user is logged in or not
     */
    private void isAuthenticated(CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        if (masUser.isAuthenticated()) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, true);
            callbackContext.sendPluginResult(result);
        } else {
            PluginResult result = new PluginResult(PluginResult.Status.OK, false);
            callbackContext.sendPluginResult(result);
        }
    }

    /**
     * Checks the boolean status if the user is current user
     */
    private void isCurrentUser(CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        if (masUser.isCurrentUser()) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, true);
            callbackContext.sendPluginResult(result);
        } else {
            PluginResult result = new PluginResult(PluginResult.Status.OK, false);
            callbackContext.sendPluginResult(result);
        }
    }

    /**
     * Fetches the current logged in MASUser and returns as json object
     */
    private void getCurrentUser(CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        try {
            Log.i(TAG, "CurrUser::" + masUser.getAsJSONObject().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }

        try {
            //callbackContext.success(masUser.getAsJSONObject());
            callbackContext.success(convertUserToJSModel(masUser));
        } catch (JSONException jse) {
            callbackContext.error(jse.getLocalizedMessage());
        }
    }

    /**
     * checks if the current User's session is locked?
     */
    private void isSessionLocked(CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        if (masUser.isSessionLocked()) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, true);
            callbackContext.sendPluginResult(result);
        } else {
            PluginResult result = new PluginResult(PluginResult.Status.OK, false);
            callbackContext.sendPluginResult(result);
        }
    }

    /**
     * locks the current User's session
     */
    private void lockSession(final CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        masUser.lockSession(new MASCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String result = "Session lock complete";
                callbackContext.success(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                callbackContext.error(command.getError(throwable));
            }
        });
    }

    /**
     * unlocks the current User's session
     */
    @TargetApi(23)
    private void unlockSession(final CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        masUser.unlockSession(new MASSessionUnlockCallback<Void>() {
            @Override
            public void onUserAuthenticationRequired() {
                final int FINGERPRINT_REQUEST_CODE = 0x1000;
                CordovaInterface cordova = (CordovaInterface) MASPlugin.getMasPlugin().cordova;
                KeyguardManager keyguardManager = (KeyguardManager) cordova.getActivity().getSystemService(Context.KEYGUARD_SERVICE);
                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
                if (intent != null) {
                    cordova.startActivityForResult(MASPlugin.getMasPlugin(), intent, FINGERPRINT_REQUEST_CODE);
                }
                cordova.setActivityResultCallback(new MASPlugin() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                        super.onActivityResult(requestCode, resultCode, intent);
                        if (requestCode == FINGERPRINT_REQUEST_CODE) {
                            if (resultCode == RESULT_OK) {
                                MASUser.getCurrentUser().unlockSession(new MASSessionUnlockCallback<Void>() {
                                    @Override
                                    public void onUserAuthenticationRequired() {
                                    }

                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        String result = "Session unlock complete";
                                        callbackContext.success(result);
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        Log.e(TAG, throwable.getMessage(), throwable);
                                        callbackContext.error(command.getError(throwable));
                                    }
                                });
                            } else if (resultCode == RESULT_CANCELED) {
                                String errMsg = "Security error has occurred.";
                                MASCordovaException exp = new MASCordovaException(errMsg);
                                Log.i(TAG, errMsg);
                                callbackContext.error(command.getError(exp));
                            }
                        }
                    }
                });


            }

            @Override
            public void onSuccess(Void aVoid) {
                String result = "Session unlock complete";
                callbackContext.success(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                callbackContext.error(command.getError(throwable));
            }
        });
    }

    /**
     * unlocks the current User's session with a promt message provided by user
     */
    @TargetApi(23)
    private void unlockSessionWithMessage(final CallbackContext callbackContext, JSONArray args) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        final String message = args.optString(0);
        masUser.unlockSession(new MASSessionUnlockCallback<Void>() {
            @Override
            public void onUserAuthenticationRequired() {
                final int FINGERPRINT_REQUEST_CODE = 0x1000;
                CordovaInterface cordova = (CordovaInterface) MASPlugin.getMasPlugin().cordova;
                KeyguardManager keyguardManager = (KeyguardManager) cordova.getActivity().getSystemService(Context.KEYGUARD_SERVICE);
                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, message);
                if (intent != null) {
                    cordova.startActivityForResult(MASPlugin.getMasPlugin(), intent, FINGERPRINT_REQUEST_CODE);
                }
                cordova.setActivityResultCallback(new MASPlugin() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                        super.onActivityResult(requestCode, resultCode, intent);
                        if (requestCode == FINGERPRINT_REQUEST_CODE) {
                            if (resultCode == RESULT_OK) {
                                MASUser.getCurrentUser().unlockSession(new MASSessionUnlockCallback<Void>() {
                                    @Override
                                    public void onUserAuthenticationRequired() {
                                        //TODO : Add the code to handle this scenario
                                    }

                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        String result = "Session unlock complete";
                                        callbackContext.success(result);
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        Log.e(TAG, throwable.getMessage(), throwable);
                                        callbackContext.error(command.getError(throwable));
                                    }
                                });
                            } else if (resultCode == RESULT_CANCELED) {
                                String errMsg = "Security error has occurred.";
                                MASCordovaException exp = new MASCordovaException(errMsg);
                                Log.i(TAG, errMsg);
                                callbackContext.error(command.getError(exp));
                            }
                        }
                    }
                });


            }

            @Override
            public void onSuccess(Void aVoid) {
                String result = "Session unlock complete";
                callbackContext.success(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                callbackContext.error(command.getError(throwable));
            }
        });
    }

    /**
     * removes any existing session lock for the logged in current user
     */
    private void removeSessionLock(final CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        masUser.removeSessionLock(new MASCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String result = "Session lock removed";
                callbackContext.success(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                callbackContext.error(command.getError(throwable));
            }
        });
    }

    /**
     * is used to login with provided username and password.
     */
    private void loginWithUsernameAndPassword(final JSONArray args, final CallbackContext callbackContext) {
        String username;
        String password;
        try {
            username = args.getString(0);
            password = args.getString(1);
        } catch (JSONException e) {
            callbackContext.error(command.getError(e));
            return;
        }

        MASUser.login(username, password, new MASCallback<MASUser>() {
            @Override
            public void onSuccess(MASUser masUser) {
                String result = "Login with username and password complete";
                callbackContext.success(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                callbackContext.error(command.getError(throwable));
            }
        });
    }

    /**
     * logs out the current logged in user
     */
    private void logoutUser(final CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        masUser.logout(new MASCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String result = "Logoff user complete";
                callbackContext.success(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                callbackContext.error(command.getError(throwable));
            }
        });
    }

    /**
     * Fetches the present logged in user's profile from server and stores in the local data store
     */
    private void requestUserInfo(final CallbackContext callbackContext) {
        MASUser masUser = MASUser.getCurrentUser();
        if (masUser == null) {
            Exception e = new Exception(MASFoundationStrings.USER_NOT_CURRENTLY_AUTHENTICATED);
            callbackContext.error(command.getError(e));
            return;
        }
        masUser.requestUserInfo(new MASCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                callbackContext.error(command.getError(throwable));
            }
        });
    }

    private JSONObject convertUserToJSModel(MASUser masUser) throws JSONException {
        JSONObject map = new JSONObject();

        map.put("userName", masUser.getUserName());
        map.put("displayName", masUser.getDisplayName());
        map.put("givenName", masUser.getName().getGivenName());
        map.put("familyName", masUser.getName().getFamilyName());
        map.put("formattedName", masUser.getName().getGivenName() + " " + masUser.getName().getFamilyName());

        map.put("active", masUser.isActive());

        Map<String, String> emailMap = new HashMap<String, String>();
        if (masUser.getEmailList() != null && !masUser.getEmailList().isEmpty()) {
            for (MASEmail email : masUser.getEmailList()) {
                emailMap.put(email.getType(), email.getValue()); // TODO: How to set primary flag
            }
        }
        map.put("emailAddresses", emailMap);

        Map<String, JSONObject> addressMap = new HashMap<String, JSONObject>();
        if (masUser.getAddressList() != null && !masUser.getAddressList().isEmpty()) {
            for (MASAddress address : masUser.getAddressList()) {
                try {
                    addressMap.put(address.getType(), address.getAsJSONObject());// TODO : Breakdown complex type
                } catch (JSONException jce) {
                }
            }
        }
        map.put("addresses", addressMap);

        Map<String, String> phoneMap = new HashMap<String, String>();
        if (masUser.getPhoneList() != null && !masUser.getPhoneList().isEmpty()) {
            for (MASPhone phone : masUser.getPhoneList()) {
                phoneMap.put(phone.getType(), phone.getValue()); // TODO: How to set primary flag
            }
        }
        map.put("phoneNumbers", phoneMap);

        Map<String, String> photoMap = new HashMap<String, String>();
        if (masUser.getPhotoList() != null && !masUser.getPhotoList().isEmpty()) {
            for (MASPhoto photo : masUser.getPhotoList()) {
                photoMap.put(photo.getType(), photo.getValue());
            }
        }
        map.put("photos", photoMap);
        if (masUser.getGroupList() != null && !masUser.getGroupList().isEmpty()) {
            map.put("groups", masUser.getGroupList());
        }

        return map;
    }
}