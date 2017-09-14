package com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by weizhenbin on 17/2/7.
 */

public abstract class BaseLoadFooter extends RelativeLayout {

    public static final int STATUS_NORMAL = -1;//无刷新状态

    public static final int STATUS_LOADING = 1;//刷新中

    private  int status = STATUS_NORMAL;

    private OnLoadListener onLoadListener;
    public BaseLoadFooter(Context context) {
        super(context);
    }

    public BaseLoadFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseLoadFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
    }
    public void setLoadFootHeight(int height) {
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

    public void pullup(int height) {
        if(height<0){
            return;
        }
        if(status==STATUS_LOADING){
            setLoadFootHeight(loadHeight()+height);
            move(loadHeight()+height);
        }else {
            setLoadFootHeight(height);
            move(height);
        }
        if(status!=STATUS_LOADING){
        if (height > loadHeight()) {
            load(true);
        } else {
            load(false);
        }}
    }

    protected abstract void move(int height);

    protected abstract int loadHeight();

    protected abstract void load(boolean canRefresh);

    protected abstract void loosenAndLoad();

    protected abstract void reset();

    public void loosen() {
        if (getHeight()>=loadHeight()) {
            animMove(100, getHeight(), loadHeight());
            if(status!=STATUS_LOADING){
                status = STATUS_LOADING;
                if (onLoadListener!=null){
                    onLoadListener.onLoad();
                }
                loosenAndLoad();
            }
        } else {
            animMove(50, getHeight(), 0);
        }
    }

    public int getStatus() {
        return status;
    }

    public void complete(){
        status = STATUS_NORMAL;
        animMove(200, getHeight(), 0);
    }
    private void animMove(long duratuon, int... values) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(values);
        valueAnimator.setDuration(duratuon);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dy = (int) animation.getAnimatedValue();
                setLoadFootHeight(dy);
            }
        });
        valueAnimator.start();
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }
}
