package com.chiang.scott.pannzoomimage;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

    private static final int INVALID_POINTER_ID = -1;

    /*private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastGestureX;
    private float mLastGestureY;*/
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    //private float mScaleFactor = 1.f;

    private Canvas canvas1 = null;
    private Canvas canvas2 = null;

    public SquareImageView(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        MyImage thisImage = MainActivity.imageMap.get(MainActivity.imgState);
        if (thisImage != null) {

            final int action = ev.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if (!mScaleDetector.isInProgress()) {
                        final float x = ev.getX();
                        final float y = ev.getY();

                        thisImage.mLastTouchX = x;
                        thisImage.mLastTouchY = y;
                        mActivePointerId = ev.getPointerId(0);
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_1_DOWN: {
                    if (mScaleDetector.isInProgress()) {
                        final float gx = mScaleDetector.getFocusX();
                        final float gy = mScaleDetector.getFocusY();
                        thisImage.mLastGestureX = gx;
                        thisImage.mLastGestureY = gy;
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {

                    // Only move if the ScaleGestureDetector isn't processing a gesture.
                    if (!mScaleDetector.isInProgress()) {
                        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                        final float x = ev.getX(pointerIndex);
                        final float y = ev.getY(pointerIndex);

                        final float dx = x - thisImage.mLastTouchX;
                        final float dy = y - thisImage.mLastTouchY;

                        thisImage.mPosX += dx;
                        thisImage.mPosY += dy;

                        invalidate();

                        thisImage.mLastTouchX = x;
                        thisImage.mLastTouchY = y;
                    } else {
                        final float gx = mScaleDetector.getFocusX();
                        final float gy = mScaleDetector.getFocusY();

                        final float gdx = gx - thisImage.mLastGestureX;
                        final float gdy = gy - thisImage.mLastGestureY;

                        thisImage.mPosX += gdx;
                        thisImage.mPosY += gdy;

                        invalidate();

                        thisImage.mLastGestureX = gx;
                        thisImage.mLastGestureY = gy;
                    }

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }
                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP: {

                    final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = ev.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        thisImage.mLastTouchX = ev.getX(newPointerIndex);
                        thisImage.mLastTouchY = ev.getY(newPointerIndex);
                        mActivePointerId = ev.getPointerId(newPointerIndex);
                    } else {
                        final int tempPointerIndex = ev.findPointerIndex(mActivePointerId);
                        thisImage.mLastTouchX = ev.getX(tempPointerIndex);
                        thisImage.mLastTouchY = ev.getY(tempPointerIndex);
                    }

                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {

        MyImage thisImage = MainActivity.imageMap.get(MainActivity.imgState);

        canvas.save();

        if(thisImage != null) {
            canvas.translate(thisImage.mPosX, thisImage.mPosY);

            if (mScaleDetector.isInProgress()) {
                canvas.scale(thisImage.mScaleFactor, thisImage.mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
            } else {
                canvas.scale(thisImage.mScaleFactor,thisImage.mScaleFactor, thisImage.mLastGestureX, thisImage.mLastGestureY);
            }
        }


        super.onDraw(canvas);
        canvas.restore();
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            MyImage thisImage = MainActivity.imageMap.get(MainActivity.imgState);

            if(thisImage != null) {
                thisImage.mScaleFactor *= detector.getScaleFactor();

                // Don't let the object get too small or too large.
                thisImage.mScaleFactor = Math.max(0.1f, Math.min(thisImage.mScaleFactor, 10.0f));
            }

            invalidate();
            return true;
        }
    }

}