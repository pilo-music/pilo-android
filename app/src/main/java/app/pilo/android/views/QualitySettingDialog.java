package app.pilo.android.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.radiobutton.MaterialRadioButton;

import app.pilo.android.R;
import app.pilo.android.helpers.UserSharedPrefManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QualitySettingDialog extends BottomSheetDialogFragment {
    private Context context;
    public final static String TAG = "QualitySettingDialog";

    @BindView(R.id.rd_download_quality_320)
    MaterialRadioButton rd_download_quality_320;
    @BindView(R.id.rd_download_quality_128)
    MaterialRadioButton rd_download_quality_128;
    @BindView(R.id.rd_stream_quality_320)
    MaterialRadioButton rd_stream_quality_320;
    @BindView(R.id.rd_stream_quality_128)
    MaterialRadioButton rd_stream_quality_128;
    @BindView(R.id.btn_success)
    Button btn_success;

    public QualitySettingDialog(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quality_setting_dialog, container, false);
        ButterKnife.bind(this, view);

        UserSharedPrefManager userSharedPrefManager = new UserSharedPrefManager(context);


        if (userSharedPrefManager.getStreamQuality().equals("320"))
            rd_stream_quality_320.setChecked(true);
        else
            rd_stream_quality_128.setChecked(true);

        if (userSharedPrefManager.getDownloadQuality().equals("320"))
            rd_download_quality_320.setChecked(true);
        else
            rd_download_quality_128.setChecked(true);



        btn_success.setOnClickListener(v -> {
            if (rd_stream_quality_128.isChecked()) {
                userSharedPrefManager.setStreamQuality("128");
            }
            if (rd_stream_quality_320.isChecked()) {
                userSharedPrefManager.setStreamQuality("320");
            }
            if (rd_download_quality_128.isChecked()) {
                userSharedPrefManager.setDownloadQuality("128");
            }
            if (rd_download_quality_320.isChecked()) {
                userSharedPrefManager.setDownloadQuality("320");
            }

            this.dismiss();
        });

        return view;
    }
}
