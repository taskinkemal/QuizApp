package com.keplersegg.myself.async;

import android.os.AsyncTask;

import com.keplersegg.myself.helper.HttpClient;
import com.keplersegg.myself.helper.TokenType;
import com.keplersegg.myself.interfaces.ILoginHost;

import org.json.JSONObject;

public class LoginTask extends AsyncTask<String, Void, JSONObject> {

    private ILoginHost activity;

    public LoginTask(ILoginHost activity) {

        this.activity = activity;
    }

    public void Run(TokenType tokenType, String token, String email, String firstName, String lastName, String pictureUrl) {

        String deviceID = activity.getDeviceId();
        String apiEndpoint = tokenType == TokenType.Google ? "Google" : tokenType == TokenType.Facebook ? "Facebook" : "";

        if (apiEndpoint.equals("")) {

            activity.onLoginError("");
            return;
        }

        execute(apiEndpoint, token, email, firstName, lastName, pictureUrl, deviceID);
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String apiEndpoint = (params != null && params.length > 0) ? params[0] : null;
        String token = (params != null && params.length > 1) ? params[1] : null;
        String email = (params != null && params.length > 2) ? params[2] : null;
        String firstName = (params != null && params.length > 3) ? params[3] : null;
        String lastName = (params != null && params.length > 4) ? params[4] : null;
        String pictureUrl = (params != null && params.length > 5) ? params[5] : null;
        String deviceID = (params != null && params.length > 6) ? params[6] : null;
        String url = "token/" + apiEndpoint;

        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams.put("Email", email);
            jsonParams.put("FirstName", firstName);
            jsonParams.put("LastName", lastName);
            jsonParams.put("PictureUrl", pictureUrl);
            if (apiEndpoint != null && apiEndpoint.equals("Facebook"))
                jsonParams.put("FacebookToken", token);
            else
                jsonParams.put("GoogleToken", token);
            jsonParams.put("DeviceID", deviceID);
        }
        catch (Exception exc) {

            activity.logException(exc, "LoginTask.doInBackground");
        }
        return HttpClient.INSTANCE.send(activity, url, "post", jsonParams);
    }

    @Override
    protected void onPostExecute(JSONObject result) {

        try {

            if (result.has("Code") && result.has("Message")) {

                activity.onLoginError(result.getString("Message"));
            }
            else {

                activity.setAccessToken(result.getString("Token"));
                activity.onLoginSuccess();
            }
        }
        catch (Exception exc) {

            activity.logException(exc, "LoginTask.onPostExecute");
            activity.onLoginError("General error");
        }
    }
}