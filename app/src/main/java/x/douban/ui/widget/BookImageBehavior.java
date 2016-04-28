package x.douban.ui.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import x.douban.R;
import x.douban.utils.L;
import x.douban.utils.MiscUtil;

/**
 * Created by Peter on 16/4/28.
 */
public class BookImageBehavior extends CoordinatorLayout.Behavior {
    private Context mContext = null;
    private int mBarHeight = 0;
    public BookImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mBarHeight = MiscUtil.getAttrInteger(mContext,android.R.attr.actionBarSize) + MiscUtil.getStatusBarHeight(mContext);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
//        return dependency instanceof NestedScrollView;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        final int height = dependency.getHeight();
        final int offset = height - mBarHeight;
        final int childDefaultY = height - child.getHeight();
        final int childDefaultX = (parent.getWidth() - child.getWidth()) / 2;
        final float threshold = 0.7f;
        int bottom = dependency.getBottom();
        float move = Math.abs(dependency.getY());
        float position = move / (float)offset;
        float areaHeight = (offset - move) + mBarHeight - MiscUtil.getStatusBarHeight(mContext);
        final int childHeight = child.getHeight();
        float scale = 1f;
        if (position < threshold) {
            if (childHeight > areaHeight) {
                scale = areaHeight / childHeight;
                child.setScaleX(scale);
                child.setScaleY(scale);
                float mY = childDefaultY + childHeight * (1 - scale) / 2 - (height - bottom);
                child.setY(mY);
            } else {
                child.setScaleX(1);
                child.setScaleY(1);
                child.setY(childDefaultY - (height - bottom));
            }
            child.setX(childDefaultX);
        } else {
            scale = areaHeight / childHeight;
            child.setScaleX(scale);
            child.setScaleY(scale);
            float scaleXOffset = (child.getWidth() * (1.0f - scale) / 2);
            float toX = scaleXOffset + childDefaultX - mContext.getResources().getDimension(R.dimen.default_margin);
            float mY = childDefaultY + childHeight * (1 - scale) / 2 - (height - bottom);
            child.setY(mY);
            float xPrecent = (1 - position) / (1 - threshold);
            child.setX(childDefaultX - toX * (1 - xPrecent));
            child.bringToFront();;
        }
        return true;
    }
}
