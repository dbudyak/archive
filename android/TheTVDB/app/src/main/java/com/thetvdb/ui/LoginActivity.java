package com.thetvdb.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thetvdb.R;
import com.thetvdb.model.AuthRequest;
import com.thetvdb.model.AuthResponse;
import com.thetvdb.util.Config;

import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dmitry Budyak on 24.06.16.
 */
public class LoginActivity extends BaseActivity implements Callback<AuthResponse> {

    private static final int RC_SIGN_IN = 2000;

    @Inject GoogleApiClient googleApiClient;
    @Inject Config config;

    @BindView(R.id.login_tv1) TextView tv1;
    @BindView(R.id.login_tv2) TextView tv2;
    @BindView(R.id.login_pb) ProgressBar pb;
    @BindView(R.id.sign_in_button) SignInButton signButton;
    @BindView(R.id.sign_in_simple_button) Button simpleLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        injector.inject(this);
    }

    @OnClick(R.id.sign_in_button)
    public void onGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        showUI(false);
    }

    @OnClick(R.id.sign_in_simple_button)
    public void onSimpleLogin() {
        prefs.setUserId(String.valueOf(new Random().nextInt(1000)));
        prefs.setUserName("Anonymous User");
        prefs.setUserEmail("nobody@nobody");
        tvDbRestApi.login(new AuthRequest(config.getApiKey())).enqueue(this);
    }

    private void showUI(boolean showUI) {
        if (showUI) {
            simpleLoginButton.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            signButton.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.INVISIBLE);
            tv2.setVisibility(View.INVISIBLE);
            signButton.setVisibility(View.INVISIBLE);
            simpleLoginButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String userId = acct != null ? acct.getId() : "";
                String name = acct != null ? acct.getDisplayName() : "";
                String email = acct != null ? acct.getEmail() : "";
                prefs.setUserId(userId);
                prefs.setUserName(name);
                prefs.setUserEmail(email);

                if (acct != null && acct.getPhotoUrl() != null) {
                    prefs.setPhotoUri(acct.getPhotoUrl().toString());
                }
                tvDbRestApi.login(new AuthRequest(config.getApiKey())).enqueue(this);
            } else {
                showUI(true);
                Log.e("DEBUG", "google sign in failed");
            }
        }
    }

    @Override
    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
        if (response.isSuccessful()) {
            prefs.setTokenHeader(response.body().getToken());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Log.e("DEBUG", "Auth failed");
        }
    }

    @Override
    public void onFailure(Call<AuthResponse> call, Throwable t) {
        Log.e("DEBUG", t.getMessage());
    }
}
