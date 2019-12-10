package app.pilo.android.views;

import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.sothree.slidinguppanel.ScrollableViewHelper;

import java.lang.ref.WeakReference;

public class NestedScrollableViewHelper extends ScrollableViewHelper {
    private NestedScrollView nestedScrollView;

    public NestedScrollableViewHelper(WeakReference<NestedScrollView> nestedScrollView){
        this.nestedScrollView = nestedScrollView.get();
    }

    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (nestedScrollView != null) {
            if (isSlidingUp) {
                return nestedScrollView.getScrollY();
            } else {
                NestedScrollView nsv = nestedScrollView;
                View child = nsv.getChildAt(0);
                return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
            }
        } else {
            return 0;
        }
    }

}
