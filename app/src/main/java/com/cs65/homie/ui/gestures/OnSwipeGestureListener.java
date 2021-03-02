package com.cs65.homie.ui.gestures;


/**
 * A listener interface for the SwipeGesture detector class
 */
public interface OnSwipeGestureListener
{
    void onSwipeLeft();
    void onSwipeRight();
    void onSwipeDown();
    void onSwipeUp();
}