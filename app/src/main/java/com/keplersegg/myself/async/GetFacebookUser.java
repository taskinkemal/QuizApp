package com.keplersegg.myself.async;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.keplersegg.myself.interfaces.ISetFacebookUser;
import com.keplersegg.myself.models.User;

import org.json.JSONException;
import org.json.JSONObject;


public class GetFacebookUser {

    public void Run(final ISetFacebookUser activity, final AccessToken accessToken) {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday,picture.height(961)");

        new GraphRequest(
                accessToken,
                "/" + accessToken.getUserId() + "/",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        onSuccess(activity, response, accessToken);
                    }
                }
        ).executeAsync();
    }

    private void onSuccess(final ISetFacebookUser activity, final GraphResponse response, final AccessToken accessToken) {

        User user = null;
        String tokenString = null;
        JSONObject object = response.getJSONObject();

        try {

            if (object != null && object.has("email")) {

                tokenString = accessToken.getToken();

                user = new User();
                user.setEmail(object.getString("email"));
                user.setFirstName(object.getString("first_name"));
                user.setLastName(object.getString("last_name"));
                user.setFacebookToken(accessToken);
                user.setPictureUrl(object.getJSONObject("picture").getJSONObject("data").getString("url"));
            }

        } catch (JSONException e) {

            activity.logException(e, "GetFacebookUser.Run");
        }

        activity.onSetFacebookUser(user, tokenString);
    }
}
