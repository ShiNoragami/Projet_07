package com.sn.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.sn.go4lunch.base.BaseActivity;

import java.util.Arrays;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    public int getFragmentLayout() { return R.layout.activity_main; }

    private static final int RC_SIGN_IN = 123;

    @OnClick(R.id.main_activity_button_login)
    public void onClickLoginButton() {
        this.startSignInActivity();
    }

    //Google & Facebook auth
     private void startSignInActivity(){
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                    Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(), //GOOGLE
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())) // FACEBOOK
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_logo_auth)
                .build(),
            RC_SIGN_IN);
       }


}