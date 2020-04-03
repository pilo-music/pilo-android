package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.MessageApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ContactUsActivity extends AppCompatActivity {

    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.et_contact_subject)
    EditText et_subject;
    @BindView(R.id.et_contact_text)
    EditText et_text;
    @BindView(R.id.ll_contact_send)
    LinearLayout ll_send;
    @BindView(R.id.progress_bar_contact)
    ProgressBar progressBar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        unbinder = ButterKnife.bind(this);
        tv_header_title.setText(getString(R.string.profile_support));
        ll_send.setOnClickListener(v -> {
            if (et_subject.getText().toString().length() == 0) {
                et_subject.setError(getText(R.string.filed_required));
                return;
            }
            if (et_text.getText().toString().length() == 0) {
                et_text.setError(getText(R.string.filed_required));
                return;
            }

            ll_send.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            MessageApi messageApi = new MessageApi(this);
            messageApi.message(et_subject.getText().toString(), et_text.getText().toString(), new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                    ll_send.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    if (status) {
                        Alerter.create(ContactUsActivity.this)
                                .setTitle(R.string.operation_done)
                                .setTextTypeface(Utils.font(ContactUsActivity.this))
                                .setTitleTypeface(Utils.font(ContactUsActivity.this))
                                .setButtonTypeface(Utils.font(ContactUsActivity.this))
                                .setBackgroundColorRes(R.color.colorGreen)
                                .show();
                    } else {
                        new HttpErrorHandler(ContactUsActivity.this, message);
                    }
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                    ll_send.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    new HttpErrorHandler(ContactUsActivity.this);
                }
            });
        });
    }


    @OnClick(R.id.img_header_back)
    void back() {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
