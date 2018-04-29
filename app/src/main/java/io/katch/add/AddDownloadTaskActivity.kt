package io.katch.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import io.katch.R
import kotlinx.android.synthetic.main.activity_add_downlaod_task.*
import java.util.*


class AddDownloadTaskActivity : AppCompatActivity() {

    // Flag set during animation to prevent animation multiple-start.
    private var isAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_downlaod_task)
        scheduleDateEditText.setOnClickListener { _ -> showDatePicker() }
        scheduleTimeEditText.setOnClickListener { _ -> showTimePicker() }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, day -> scheduleDateEditText.setText("$year/$month/$day") }, year, month, day).show()
    }

    private fun showTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> scheduleTimeEditText.setText("$hourOfDay:$minute") }, hour, minute, false).show()
    }

    override fun onResume() {
        super.onResume()

        // Start the slide-up animation.
        window.setWindowAnimations(0)
        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.overlay_slide_up)
        shareDialog.startAnimation(anim)
    }

    override fun finish() {
        finish(true)
    }

    private fun finish(shouldOverrideAnimations: Boolean) {
        super.finish()
        if (shouldOverrideAnimations) {
            // Don't perform an activity-dismiss animation.
            overridePendingTransition(0, 0)
        }
    }

    /**
     * Slide the overlay down off the screen, and finish the activity.
     */
    private fun animateOut() {
        if (isAnimating) {
            return
        }
        isAnimating = true

        val animationToFinishActivity = AnimationUtils.loadAnimation(this, R.anim.overlay_slide_down)
        shareDialog.startAnimation(animationToFinishActivity)
        animationToFinishActivity.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                /* Unused. */
            }

            override fun onAnimationEnd(animation: Animation) {
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {
                /* Unused. */
            }
        })

        // Allows the user to dismiss the animation early.
        setFullscreenFinishOnClickListener()
    }

    /**
     * Sets a fullscreen [.finish] click listener. We do this rather than attaching an
     * onClickListener to the root View because in that case, we need to remove all of the
     * existing listeners, which is less robust.
     */
    private fun setFullscreenFinishOnClickListener() {
        fullscreenClickTarget.visibility = View.VISIBLE
        fullscreenClickTarget.setOnClickListener({ finish() })
    }

    /**
     * Close the dialog if back is pressed.
     */
    override fun onBackPressed() {
        animateOut()
    }

    /**
     * Close the dialog if the anything that isn't a button is tapped.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        animateOut()
        return true
    }
}
