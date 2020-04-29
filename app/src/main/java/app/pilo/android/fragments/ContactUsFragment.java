package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.MessageApi;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactUsFragment extends BaseFragment {

    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.et_contact_subject)
    EditText et_subject;
    @BindView(R.id.et_contact_text)
    EditText et_text;
    @BindView(R.id.ll_contact_send)
    LinearLayout ll_send;
    @BindView(R.id.progress_bar_contact)
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this, view);
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
            MessageApi messageApi = new MessageApi(getActivity());
            messageApi.message(et_subject.getText().toString(), et_text.getText().toString(), new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                    ll_send.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    if (status) {
                        Alerter.create(getActivity())
                                .setTitle(R.string.operation_done)
                                .setTextTypeface(Utils.font(getActivity()))
                                .setTitleTypeface(Utils.font(getActivity()))
                                .setButtonTypeface(Utils.font(getActivity()))
                                .setBackgroundColorRes(R.color.colorGreen)
                                .show();
                    } else {
                        new HttpErrorHandler(getActivity(), message);
                    }
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                    ll_send.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    new HttpErrorHandler(getActivity());
                }
            });
        });

        return view;
    }


    @OnClick(R.id.img_header_back)
    void back() {
        getActivity().onBackPressed();
    }


}
