package x.douban.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import x.douban.R;
import x.douban.utils.L;

/**
 * Created by Peter on 16/4/27.
 */
public class RippleTabLayout extends TabLayout {
    private Context mContext = null;
    public RippleTabLayout(Context context) {
        super(context);
        mContext = context;
    }

    public RippleTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public RippleTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private float mTouchX = -1;
    private float mTouchY = -1;

    private int mBackgroundColor = Color.WHITE;
    @Override
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setRealBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mBackgroundColor);
        canvas.save();
        canvas.clipRect((mTouchX - mRadius),
            (mTouchY - mRadius),
            (mTouchX + mRadius),
            (mTouchY + mRadius));
        canvas.drawCircle(mTouchX, mTouchY, mRadius, paint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    private float mRadius = 0;
    private int mRippleDuration = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mTouchX = ev.getX();
            mTouchY = ev.getY();

//            final int max = (getMeasuredHeight() > getMeasuredWidth() ? getMeasuredHeight() : getMeasuredWidth()) / 2;
            int max = (int) (getMeasuredWidth() - mTouchX);
            if (mTouchX > max) {
                max = (int) mTouchX;
            }
            ValueAnimator rippleAnim = ValueAnimator.ofInt(1, max);
            rippleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRadius = (int)animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            rippleAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    RippleTabLayout.super.setBackgroundColor(mBackgroundColor);
                }
            });
            if (mRippleDuration == 0) {
                mRippleDuration = mContext.getResources().getInteger(R.integer.ripple_anim_duration);
            }
            rippleAnim.setInterpolator(new DecelerateInterpolator());
            rippleAnim.setDuration(mRippleDuration);
            rippleAnim.setStartDelay(0);
            rippleAnim.start();
        }
        return super.dispatchTouchEvent(ev);
    }
}
