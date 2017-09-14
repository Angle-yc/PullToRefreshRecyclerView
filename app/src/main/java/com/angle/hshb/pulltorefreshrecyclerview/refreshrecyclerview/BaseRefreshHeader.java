package com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by weizhenbin on 17/1/6.
 */

public abstract class BaseRefreshHeader extends RelativeLayout {


    public static final int STATUS_NORMAL = -1;//无刷新状态

    public static final int STATUS_REFRESHING = 1;//刷新中


    private  int status = STATUS_NORMAL;

    private OnRefreshListener onRefreshListener;

    public BaseRefreshHeader(Context context) {
        this(context, null);
    }

    public BaseRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
    }

    public void setHeaderHeight(int height) {
        if (height > 0) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) getLayoutParams();
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        } else {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) getLayoutParams();
            layoutParams.height = 0;
            setLayoutParams(layoutParams);
            status = STATUS_NORMAL;
            reset();
        }
    }


    public void pulldown(int height) {
        if(height<0){
            return;
        }
        if(status==STATUS_REFRESHING){
            setHeaderHeight(refreshHeight()+height);
            move(refreshHeight()+height);
        }else {
            setHeaderHeight(height);
            move(height);
        }
        if(status!=STATUS_REFRESHING) {
            if (height > refreshHeight()) {
                refresh(true);
            } else {
                refresh(false);
            }
        }
    }

    public int getStatus() {
        return status;
    }

    public void loosen() {
        if (getHeight()>=refreshHeight()) {
            animMove(100, getHeight(), refreshHeight());
            if(status!=STATUS_REFRESHING){
                status = STATUS_REFRESHING;
                if (onRefreshListener!=null){
                    onRefreshListener.onRefresh();
                    loosenAndRefresh();
                }
            }
        } else {
            animMove(50, getHeight(), 0);
        }
    }

    /**拉伸过程*/
    protected abstract void move(int height);

    /**可刷新的高度*/
    protected abstract int refreshHeight();

    /**是否已经达到可刷新状态*/
    protected abstract void refresh(boolean canRefresh);

    /**松开并刷新*/
    protected abstract void loosenAndRefresh();

    /**复位*/
    protected abstract void reset();

    private void animMove(long duratuon, int... values) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(values);
        valueAnimator.setDuration(duratuon);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dy = (int) animation.getAnimatedValue();
                setHeaderHeight(dy);
            }
        });
        valueAnimator.start();
    }

    public void complete(){
        status = STATUS_NORMAL;
        animMove(200, getHeight(), 0);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }
}
