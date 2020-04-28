package app.pilo.android.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.google.android.material.radiobutton.MaterialRadioButton;

import app.pilo.android.R;
import app.pilo.android.helpers.UserSharedPrefManager;

public class QualitySettingDialog {
    private Context context;

    public QualitySettingDialog(Context context) {
        this.context = context;
    }

    public void show() {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.quality_setting_dialog);

        MaterialRadioButton rd_download_quality_320 = dialog.findViewById(R.id.rd_download_quality_320);
        MaterialRadioButton rd_download_quality_128 = dialog.findViewById(R.id.rd_download_quality_128);
        MaterialRadioButton rd_stream_quality_320 = dialog.findViewById(R.id.rd_stream_quality_320);
        MaterialRadioButton rd_stream_quality_128 = dialog.findViewById(R.id.rd_stream_quality_128);
        Button btn_success = dialog.findViewById(R.id.btn_success);

        UserSharedPrefManager userSharedPrefManager = new UserSharedPrefManager(context);


        if (userSharedPrefManager.getStreamQuality().equals("320"))
            rd_stream_quality_320.setChecked(true);
        else
            rd_stream_quality_128.setChecked(true);

        if (userSharedPrefManager.getDownloadQuality().equals("320"))
            rd_download_quality_320.setChecked(true);
        else
            rd_download_quality_128.setChecked(true);


        rd_download_quality_320.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (rd_download_quality_320.isChecked()) {
                userSharedPrefManager.setDownloadQuality("320");
            }
        });

        rd_download_quality_128.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (rd_download_quality_128.isChecked()) {
                userSharedPrefManager.setDownloadQuality("128");
            }
        });


        rd_stream_quality_320.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (rd_stream_quality_320.isChecked()) {
                userSharedPrefManager.setStreamQuality("320");
            }
        });
        rd_stream_quality_128.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (rd_stream_quality_128.isChecked()) {
                userSharedPrefManager.setStreamQuality("128");
            }
        });


        btn_success.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
