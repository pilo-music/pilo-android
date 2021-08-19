package app.pilo.android.activities;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.android.volley.error.VolleyError;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.views.PiloButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.pushe.plus.Pushe;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_email)
    TextInputEditText et_email;
    @BindView(R.id.et_password)
    TextInputEditText et_password;
    @BindView(R.id.ll_login)
    PiloButton piloButton;

    private static final int RC_SIGN_IN = 111;
//    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        piloButton.setOnClickListener(v -> login());
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("8427120984-hu3aul4fnhlnufuefnbtjujk6o7rhn3v.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
        }
    }


    private void login() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (!isValidEmail(email)) {
            et_email.setError(getString(R.string.email_not_valid));
            return;
        }

        if (password.length() < 5) {
            et_password.setError(getString(R.string.password_small_length));
            return;
        }

        piloButton.setProgress(true);
        UserApi userApi = new UserApi(this);
        userApi.login(email, password, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                piloButton.setProgress(false);
                if (status) {
                    if (((Map) data).get("status").toString().equals("login")) {
                        doLogin((User) ((Map) data).get("user"));
                    } else {
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                } else {
                    new HttpErrorHandler(LoginActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                piloButton.setProgress(false);
                new HttpErrorHandler(LoginActivity.this);
            }
        });
    }

    @OnClick(R.id.ll_login_google)
    void loginGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.tv_forgot_password)
    void forgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

//    /**
//     * method to handle google sign in result
//     *
//     * @param completedTask from google onActivityResult
//     */
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            UserApi userApi = new UserApi(this);
//            userApi.loginWithGoogle(account.getIdToken(), new HttpHandler.RequestHandler() {
//                @Override
//                public void onGetInfo(Object data, String message, boolean status) {
//                    if (status) {
//                        doLogin((User) data);
//                    } else {
//                        new HttpErrorHandler(LoginActivity.this, message);
//                    }
//                }
//
//                @Override
//                public void onGetError(@Nullable VolleyError error) {
//                    piloButton.setProgress(false);
//                    new HttpErrorHandler(LoginActivity.this);
//                }
//            });
//
//        } catch (ApiException e) {
//            Toast.makeText(this, "Failed to do Sign In : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

    private void doLogin(User data) {
        if (data != null && data.getAccess_token() != null) {
            User user = new User();
            user.setAccess_token(data.getAccess_token());
            user.setEmail(data.getEmail());
            user.setPhone(data.getPhone());
            user.setBirth(data.getBirth());
            user.setGender(data.getGender());
            user.setPic(data.getPic());
            user.setGlobal_notification(data.isGlobal_notification());
            user.setMusic_notification(data.isMusic_notification());
            user.setAlbum_notification(data.isAlbum_notification());
            user.setVideo_notification(data.isVideo_notification());
            UserRepo.getInstance(this).insert(user);
            Pushe.setUserEmail(data.getEmail());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
