package com.huhukun.tickteeforandroid;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.providers.WebApiConstants;
import com.huhukun.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends ActionBarActivity{



    private static final String TAG = App_Constants.APP_TAG +"LoginActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
        setContentView(R.layout.activity_login);
        setupActionBar();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        autoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void autoComplete(){

        if (TickTeeAndroid.appSetting != null && TickTeeAndroid.appSetting.contains(App_Constants.PREF_EMAIL) && TickTeeAndroid.appSetting.getString(App_Constants.PREF_EMAIL, null) != null)
        {
            mEmailView.setText(TickTeeAndroid.appSetting.getString(App_Constants.PREF_EMAIL, ""));
        }

    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {

        getSupportActionBar().setIcon(R.drawable.artwork);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Intent > {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Intent  doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            List<NameValuePair> loginInfo = new ArrayList<NameValuePair>(2);
            loginInfo.add(new BasicNameValuePair("email", mEmail));
            loginInfo.add(new BasicNameValuePair("password", mPassword));



            try {
                JSONObject json = new JSONObject(JSONParser.getStringFromUrlViaPost(WebApiConstants.LOGIN_URL, null, loginInfo));

                Log.d(App_Constants.PREF_APP, json.toString());
                if (!json.has("success")) {
                    String email = json.getJSONObject("user").getString("email");
                    String auth_token = json.getJSONObject("user").getString("auth_token");
                    Log.d(TAG, email + auth_token);

                    final Intent res = new Intent();
                    res.putExtra(AccountManager.KEY_ACCOUNT_NAME, mEmail);
                    res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, App_Constants.ACCOUNT_TYPE);
                    res.putExtra(AccountManager.KEY_AUTHTOKEN, auth_token);
                    res.putExtra(App_Constants.PREF_PASSWORD, mPassword);
                    return res;


                }
            } catch (JSONException e)
            {
                Log.d(TAG, e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mAuthTask = null;
            showProgress(false);

            if (intent != null) {
                String email = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                String accountPassword = intent.getStringExtra(App_Constants.PREF_PASSWORD);
                String auth_token = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                SharedPreferences settings = getSharedPreferences(App_Constants.PREF_APP, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(App_Constants.PREF_EMAIL, email);
                editor.putString(App_Constants.PREF_TOKEN, auth_token);
                editor.commit();
                final Account account = new Account(email, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));


                AccountManager accountManager = AccountManager.get(LoginActivity.this);

                accountManager.addAccountExplicitly(account, accountPassword, null);
                accountManager.setAuthToken(account, App_Constants.AUTH_TOKEN_TYPE, auth_token);

//                ContentResolver.setIsSyncable(account, App_Constants.AUTHORITY, 1);
//                ContentResolver.setSyncAutomatically(account, App_Constants.AUTHORITY, true);
//                final Bundle bundle = new Bundle(1);
//                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
//                ContentResolver.addPeriodicSync(account, App_Constants.AUTHORITY, bundle, 1800);

                ContentResolver.setIsSyncable(account, App_Constants.AUTHORITY, 1);
                Bundle params = new Bundle();
                params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
                params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
                params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
                ContentResolver.addPeriodicSync(account, App_Constants.AUTHORITY, params, 60);
                ContentResolver.setSyncAutomatically(account, App_Constants.AUTHORITY, true);
                ContentResolver.requestSync(account,App_Constants.AUTHORITY,params);

                setAccountAuthenticatorResult(intent.getExtras());
                setResult(RESULT_OK, intent);
                if (mAccountAuthenticatorResponse != null) {
                    // send the result bundle back if set, otherwise send an error.
                    if (mResultBundle != null) {
                        mAccountAuthenticatorResponse.onResult(mResultBundle);
                    } else {
                        mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                                "canceled");
                    }
                    mAccountAuthenticatorResponse = null;
                }
                App_Constants.currentAccount = account;
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


    }
}



