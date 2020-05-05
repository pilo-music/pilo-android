package app.pilo.android.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.core.app.ActivityCompat;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    @BindView(R.id.et_profile_edit_name)
    EditText et_name;
    @BindView(R.id.et_profile_edit_email)
    EditText et_email;
    @BindView(R.id.et_profile_edit_password)
    EditText et_password;
    @BindView(R.id.et_profile_edit_confirm)
    EditText et_confirm;
    @BindView(R.id.ll_save)
    LinearLayout ll_save;
    @BindView(R.id.civ_profile_user)
    CircleImageView civ_user;
    @BindView(R.id.progress_bar_profile_edit)
    ProgressBar progressBar;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.tv_save)
    TextView tv_save;

    private User user;
    private UserApi userApi;
    private final int CODE_GALLERY_REQUEST = 999;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        tv_header_title.setText(getString(R.string.profile_edit));
        user = UserRepo.getInstance(this).get();
        userApi = new UserApi(this);
        initViews();
        getDataFromServer();
        ll_save.setOnClickListener(v -> sendDataFromServer());
    }

    private void initViews() {
        et_name.setText(user.getName());
        et_email.setText(user.getEmail());
        if (!user.getPic().equals("")) {
            Glide.with(this)
                    .load(user.getPic())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(civ_user);
        }
    }

    private void getDataFromServer() {
        userApi.me(new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    user.setName(((User) data).getName());
                    user.setPic(((User) data).getPic());
                    et_name.setText(((User) data).getName());
                    if (!user.getPic().equals("")) {
                        Glide.with(EditProfileActivity.this)
                                .load(user.getPic())
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(civ_user);
                    }
                } else {
                    new HttpErrorHandler(EditProfileActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                new HttpErrorHandler(EditProfileActivity.this);
            }
        });

    }

    private void sendDataFromServer() {

        if (!et_password.getText().toString().isEmpty() && et_password.getText().toString().length() < 5) {
            et_password.setError(getString(R.string.password_small_length));
            return;
        }

        if (!et_password.getText().toString().equals(et_confirm.getText().toString())) {
            et_confirm.setError(getString(R.string.password_not_match));
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        tv_save.setVisibility(View.GONE);
        ll_save.setEnabled(false);

        HashMap<String, Object> params = new HashMap<>();
        params.put("name", et_name.getText().toString());
        if (!et_password.getText().toString().isEmpty()) {
            params.put("password", et_password.getText().toString());
            params.put("password_confirmation", et_confirm.getText().toString());
        }
        if (!imageToString(bitmap).equals("")) {
            params.put("pic", imageToString(bitmap));
        }

        userApi.update(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                progressBar.setVisibility(View.GONE);
                tv_save.setVisibility(View.VISIBLE);
                ll_save.setEnabled(true);
                if (status) {
                    User newUser = (User) data;
                    UserRepo.getInstance(EditProfileActivity.this).update(newUser);
                    Alerter.create(EditProfileActivity.this)
                            .setTitle(message)
                            .setTextTypeface(Utils.font(EditProfileActivity.this))
                            .setTitleTypeface(Utils.font(EditProfileActivity.this))
                            .setButtonTypeface(Utils.font(EditProfileActivity.this))
                            .setBackgroundColorRes(R.color.colorGreen)
                            .show();
                } else {
                    new HttpErrorHandler(EditProfileActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.GONE);
                tv_save.setVisibility(View.VISIBLE);
                ll_save.setEnabled(true);
                new HttpErrorHandler(EditProfileActivity.this);
            }
        });

    }

    @OnClick({R.id.btn_profile_pick_photo, R.id.civ_profile_user})
    void pickImage() {
        ActivityCompat.requestPermissions(
                EditProfileActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                CODE_GALLERY_REQUEST
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_GALLERY_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_GALLERY_REQUEST);
            } else {
                Toast.makeText(this, "You don't have permission ", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                civ_user.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private String imageToString(Bitmap bitmap) {
        try {
            if (bitmap == null)
                return "";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    @OnClick(R.id.img_header_back)
    void back() {
        finish();
    }


}
