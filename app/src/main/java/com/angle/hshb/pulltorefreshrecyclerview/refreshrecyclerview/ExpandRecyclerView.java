package com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by weizhenbin on 17/1/4.
 */

public class ExpandRecyclerView extends RecyclerView {

    private OnRefreshListener onRefreshListener;
    private OnLoadListener onLoadListener;
    private float rdownY = -1;
    private float ldownY = -1;
    private View refreshView;
    private View loadView;

    private ExpandAdapter expandAdapter;

    private boolean supportPullRefresh=false;
    private boolean supportPullLoad=false;

    private Context context;
    public ExpandRecyclerView(Context context) {
        this(context, null);
    }

    public ExpandRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d("ExpandRecyclerView", "onAttachedToWindow");
    }



    public void setSupportPullRefresh(boolean supportPullRefresh) {
        this.supportPullRefresh = supportPullRefresh;
    }

    public void setSupportPullLoad(boolean supportPullLoad) {
        this.supportPullLoad = supportPullLoad;
    }
    @Override
    public void setAdapter(Adapter adapter) {
        if(adapter!=null){
            adapter.registerAdapterDataObserver(dataObserver);
            expandAdapter = new ExpandAdapter(adapter);
            expandAdapter.addLoadFootView(new SimpleLoadFooter(context));
            expandAdapter.addRefreshHeadView(new SimpleRefeshHeader(context));
            super.setAdapter(expandAdapter);
        }else {
            super.setAdapter(adapter);
        }
    }



    public void addHeadView(View v) {
        if (expandAdapter != null && v != null) {
            expandAdapter.addHeadView(v);
           // expandAdapter.notifyDataSetChanged();
        }
    }

    public void addFootView(View v) {
        if (expandAdapter != null) {
            expandAdapter.addFootView(v);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (rdownY == -1) {
            rdownY = e.getRawY();
        }
        if(ldownY==-1){
            ldownY=e.getRawY();
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float rdy = e.getRawY() - rdownY;
                float ldy =ldownY- e.getRawY();
                if (refreshView == null) {
                    refreshView = getLayoutManager().findViewByPosition(0);
                    if (refreshView instanceof BaseRefreshHeader) {
                        ((BaseRefreshHeader) refreshView).setOnRefreshListener(onRefreshListener);
                    }
                }
                if(loadView==null){
                    loadView = getLayoutManager().findViewByPosition(getLayoutManager().getItemCount()-1);
                    if(loadView instanceof BaseLoadFooter){
                        ((BaseLoadFooter) loadView).setOnLoadListener(onLoadListener);
                    }
                }
                if (allowPulldown()&&!isLoading()&&supportPullRefresh) {
                    ((BaseRefreshHeader) refreshView).pulldown((int) (rdy / 2));
                    if (refreshView.getHeight() >= 0 && rdy > 0) {
                        return false;
                    }
                }else{
                    rdownY = e.getRawY();
                }
                if (allowPullup()&&!isRefreshing()&&supportPullLoad){
                    ((BaseLoadFooter) loadView).pullup((int) (ldy/ 2));
                    offsetChildrenVertical(1);//消除上拉时抖动  抖动的原因猜测是 view的高度是向下延伸的
                    if(loadView.getHeight()>0&&ldy>0){
                        scrollToPosition(getLayoutManager().getItemCount()-1);
                        return false;
                    }
                }else {
                    ldownY=e.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (refreshView instanceof BaseRefreshHeader&& refreshView.getParent() != null&&supportPullRefresh) {
                    ((BaseRefreshHeader) refreshView).loosen();
                }else if(loadView instanceof BaseLoadFooter && loadView.getParent() != null&&supportPullLoad){
                     ((BaseLoadFooter) loadView).loosen();
                }
                rdownY = -1;
                ldownY=-1;
                break;
        }
        return super.onTouchEvent(e);
    }

    /**是否正在刷新*/
    private boolean isRefreshing(){
        if(refreshView instanceof BaseRefreshHeader){
           return  ((BaseRefreshHeader) refreshView).getStatus()==BaseRefreshHeader.STATUS_REFRESHING;
        }
        return false;
    }
    /**是否正在加载*/
    private boolean isLoading(){
        if(loadView instanceof BaseLoadFooter){
            return  ((BaseLoadFooter) loadView).getStatus()==BaseLoadFooter.STATUS_LOADING;
        }
        return false;
    }

    /**允许下拉操作*/
    private boolean allowPulldown(){
       return refreshView instanceof BaseRefreshHeader
               && refreshView.getParent() != null
               && refreshView.getTop()==0;
    }

    /**允许上拉操作*/
    private boolean allowPullup(){
        return loadView instanceof BaseLoadFooter
                && loadView.getParent()!=null
                && loadView.getBottom()==getLayoutManager().getHeight();
    }


    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void refreshComplete() {

        if (refreshView instanceof BaseRefreshHeader) {
                    if (refreshView.getParent() != null) {
                        scrollToPosition(0);
                        ((BaseRefreshHeader) refreshView).complete();
                        rdownY = -1;
                    } else {
                        ((BaseRefreshHeader) refreshView).setHeaderHeight(0);
                        rdownY = -1;
                    }
                }
        }
    public void loadComplete() {

        if (loadView instanceof BaseLoadFooter) {
            if (loadView.getParent() != null) {
                scrollToPosition(getLayoutManager().getItemCount()-1);
                ((BaseLoadFooter) loadView).complete();
                ldownY = -1;
            } else {
                ((BaseLoadFooter) loadView).setLoadFootHeight(0);
                ldownY = -1;
            }
        }
    }


    private final RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if(getChildCount()>1) {
                removeViews(1, getChildCount()-1);
            }
            expandAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            expandAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            expandAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            expandAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            expandAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            expandAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };
}
