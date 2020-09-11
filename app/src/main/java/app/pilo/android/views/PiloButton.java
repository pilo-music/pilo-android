package app.pilo.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import app.pilo.android.R;

public class PiloButton extends LinearLayout {
    protected boolean isEnable, isProgress;

    public void setEnable(boolean enable) {
        isEnable = enable;
        handleEnable(enable);
    }

    public void setProgress(boolean progress) {
        isProgress = progress;
        handleProgress(progress);
    }

    public void setTitle(String title) {
        this.title = title;
        tv_title.setText(title);
    }

    private String title;
    private TextView tv_title;
    private ProgressBar progressBar;

    public PiloButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PiloButton, 0, 0);

        title = attributes.getString(R.styleable.PiloButton_text);
        isEnable = attributes.getBoolean(R.styleable.PiloButton_isEnable, false);
        isProgress = attributes.getBoolean(R.styleable.PiloButton_isProgress, false);
        attributes.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_primary_light));

        setClickable(true);
        setFocusable(true);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pilo_button, this, true);

        tv_title = (TextView) getChildAt(0);
        tv_title.setText(title);

        progressBar = (ProgressBar) getChildAt(1);


        handleEnable(isEnable);
        handleProgress(isProgress);
    }


    private void handleEnable(boolean isEnable) {
        if (!isEnable) {
            tv_title.setAlpha(0.5f);
            setEnabled(false);
        } else {
            tv_title.setAlpha(1);
            setEnabled(true);
        }
    }

    private void handleProgress(boolean isProgress) {
        if (isProgress) {
            handleEnable(false);
            tv_title.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);
        } else {
            handleEnable(true);
            tv_title.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
        }
    }
}
