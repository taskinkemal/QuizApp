package com.keplersegg.myself.async;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.keplersegg.myself.interfaces.ISignOut;
import com.keplersegg.myself.models.User;

import androidx.annotation.NonNull;

public class SignOut {

    public void Run(final ISignOut activity) {

        User user = activity.GetUser();

        if (user != null && user.getFacebookToken() != null) {
            new GraphRequest(user.getFacebookToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    LoginManager.getInstance().logOut();

                    GoogleSignOut(activity);
                }
            }).executeAsync();
        }
        else {
            GoogleSignOut(activity);
        }
    }

    private void GoogleSignOut(final ISignOut activity) {

        activity.GetGoogleSignInClient().signOut()
                .addOnCompleteListener(activity.GetMasterActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        FinalizeSignOut(activity);
                    }
                });
    }

    private void FinalizeSignOut(final ISignOut activity) {

        activity.onSignOut();
    }
}
