package com.sn.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.sn.go4lunch.R;
import com.sn.go4lunch.utils.UserHelper;

import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.http2.ErrorCode;

public class LoginActivity extends AppCompatActivity {

    //identifier for sign in
    private static final int RC_SIGN_IN=100;

    @BindView(R.id.main_constraint_layout)
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //remove toolbar main screen
        //getSupportActionToolBar().hide();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    // Start sign-in activities when buttons clicked
    @OnClick(R.id.email_button)
    public void onClickEmailLoginButton() {
        startEmailSignInActivity();
    }

    @OnClick(R.id.google_button)
    public void onClickGoogleLoginButton() {
        startGoogleSignInActivity();
    }

    @OnClick(R.id.facebook_button)
    public void onClickFacebookLoginButton() {
        startFacebookSignInActivity();
    }

    @OnClick(R.id.twitter_button)
    public void onClickTwitterLoginButton() {
        startTwitterSignInActivity();
    }

    // Launch email sign-in activity
    private void startEmailSignInActivity() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN);

    }

    // Launch Google sign-in activity
    private void startGoogleSignInActivity() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN);

    }

    // Launch Facebook sign-in activity
    private void startFacebookSignInActivity() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN);

    }

    // Launch Twitter sign-in activity
    private void startTwitterSignInActivity() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        super.onActivityResult( requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode,resultCode,data);
    }

    //create SnackBar
    private void showSnackBar(ConstraintLayout constraintLayout, String message){

        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    //show Message depending on result from sign in
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                //show message, create user and start MainPageActivity
                showSnackBar(constraintLayout, getString(R.string.connection_succeed));
                createUserInFirestore();
                Intent intent = new Intent(LoginActivity.this,MainPageActivity.class);
                startActivity(intent);
            } else {
                if (response == null ){
                    showSnackBar(constraintLayout, getString(R.string.error_authentication_canceled));
                } else if (Objects.requireNonNull(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)){
                    showSnackBar(constraintLayout, getString(R.string.error_no_internet));
                } else if (Objects.requireNonNull(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)) {
                    showSnackBar(constraintLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    //Create user in Firestore
    private void createUserInFirestore(){
        String urlPicture =
                FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null ?
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()
                        .toString()) : null;
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String uid = FirebaseAuth.getInstance().getUid();

        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()
                .addOnSuccessListener(documentSnapshot -> {
                    User currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser !=null)
                        UserHelper.createUser(uid, username, urlPicture, currentUser.getChosenRestaurant(),
                                currentUser.getLikedRestaurants(), currentUser.isNotificationEnabled());
                    else
                        UserHelper.createUser(uid, username, urlPicture, null, null, false);
        }));
    }
}
