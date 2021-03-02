package com.cs65.homie.ui.gestures;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.cs65.homie.MainActivity;


/**
 * A swipe gesture detection implementation, listening to touch events
 * on a single view, and only able to have one listening client due to
 * context restrictions
 */
public class SwipeGesture
    extends GestureDetector.SimpleOnGestureListener
    implements View.OnTouchListener
{

    /**
     * The required ratio difference between X and Y differences
     * for the swipe to be considered an X swipe
     */
    public static final double RELATIVE_X_THRESHOLD = 2.0;
    /**
     * The required ratio difference between X and Y differences
     * for the swipe to be considered an Y swipe
     */
    public static final double RELATIVE_Y_THRESHOLD = 0.5;
    /**
     * The required difference between touch events in a fling for the
     * fling to be considered a swipe
     */
    public static final int SWIPE_THRESHOLD = 100;
    /**
     * The required velocity of a fling for the fling to be considered a swipe
     */
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;

    /**
     *  We require a gesture detector from Android to detect the touch events
     *  that will compose the fling events we handle
     */
    private final GestureDetector gestureDetector;
    /**
     * The callback listener
     */
    private final OnSwipeGestureListener listener;


    public SwipeGesture(
        Context context, View view, OnSwipeGestureListener listener
    )
    {
        this.gestureDetector = new GestureDetector(context, this);
        this.listener = listener;
        view.setOnTouchListener(this);
    }

    public boolean onDown(MotionEvent event)
    {
        // We must listen and consume onDown events for onFling events
        // to be recorded and propagate
        return true;
    }

    public boolean onFling (
        MotionEvent e1, MotionEvent e2, float velocityX, float velocityY
    )
    {

        boolean actionConsumed = false;

        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();
        float diffXAbs = Math.abs(diffX);
        float diffYAbs = Math.abs(diffY);
        float ratio = diffXAbs / diffYAbs;

        Log.d(
            MainActivity.TAG, String.format(
            "Swipe Gesture, diffX: %f\tdiffY: %f\tVelX: %f\tVelY: %f",
            diffX, diffY, velocityX, velocityY
        ));

        if (
            ratio > RELATIVE_X_THRESHOLD
            && diffXAbs > SWIPE_THRESHOLD
            && velocityX > SWIPE_VELOCITY_THRESHOLD
        )
        {
            actionConsumed = true;
            if (diffX > 0)
            {
                this.listener.onSwipeLeft();
            }
            else
            {
                this.listener.onSwipeRight();
            }
        }
        else if (
            ratio > RELATIVE_Y_THRESHOLD
            && diffYAbs > SWIPE_THRESHOLD
            && velocityY > SWIPE_VELOCITY_THRESHOLD
        )
        {
            actionConsumed = true;
            // TODO REP doesn't actually know if these are the correct signs,
            // e.g. is positive down? Since this branch is untested
            if (diffY > 0)
            {
                listener.onSwipeDown();
            }
            else
            {
                listener.onSwipeUp();
            }
        }

        return actionConsumed;

    }

    public boolean onTouch(View view, MotionEvent event)
    {
        // Pass along the touch event to the Android gesture detector
        // we set up earlier
        view.performClick();
        return this.gestureDetector.onTouchEvent(event);
    }

}