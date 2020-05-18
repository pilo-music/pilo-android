package app.pilo.android.utils

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat.animate

class AnimateTest{
    fun showBonceAnimation(view: View) {
        view.apply {
            clearAnimation()
            scaleX = 0.9f
            scaleY = 0.9f
            visibility = View.VISIBLE
            pivotX = (view.width / 2).toFloat()
            pivotY = (view.height / 2).toFloat()

            animate().setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .withEndAction {
                        animate().setDuration(200)
                                .setInterpolator(AccelerateInterpolator())
                                .scaleX(1f)
                                .scaleY(1f)
                                .alpha(1f)
                                .start()
                    }
                    .start()
        }
    }

}