package app.pilo.android.views;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

public class AutoScrollingTextView extends androidx.appcompat.widget.AppCompatTextView {

    public AutoScrollingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttres();
    }

    public AutoScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttres();
    }

    public AutoScrollingTextView(Context context) {
        super(context);
        setAttres();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    private void setAttres(){
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setGravity(Gravity.CENTER);
        this.setMarqueeRepeatLimit(-1);
        this.setSingleLine(true);
    }
}