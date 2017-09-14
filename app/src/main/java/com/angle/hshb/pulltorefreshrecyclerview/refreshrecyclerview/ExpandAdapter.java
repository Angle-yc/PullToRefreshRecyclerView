package com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weizhenbin on 2017/2/24.
 */
public class ExpandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * RecyclerView 没有添加头部和尾部相应的方法  都是加载不同类型view来实现的
     */
     private static final int TPEY_COUNT_START_TAG =100000;
    //创建头部view的集合
    public List<View> headViews = new ArrayList<>();
    public List<View> footViews = new ArrayList<>();
    public List<View> footViewTemp=new ArrayList<>();
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;


    public ExpandAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.adapter = adapter;
    }

    public void addHeadView(View v) {
        if (headViews != null) {
            if(v instanceof BaseRefreshHeader){
                v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                addRefreshHeadView((BaseRefreshHeader) v);
            }else {
                v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                headViews.add(v);
            }
        }
    }

    public void addFootView(View v) {
        if (footViews != null) {
            if(v instanceof BaseLoadFooter){
                v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                addLoadFootView((BaseLoadFooter) v);
            }else {
                v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if(footViews.size()>0&&footViews.get(footViews.size()-1) instanceof BaseLoadFooter){
                    footViews.add(footViews.size()-1,v);
                }else {
                    footViews.add(v);
                }

            }

        }
    }

    public void addLoadFootView(BaseLoadFooter v) {
        if (footViews != null) {
            if(footViews.size()>0&&footViews.get(footViews.size()-1) instanceof BaseLoadFooter){
                footViews.remove(footViews.size()-1);
                footViews.add(v);
            }else {
                footViews.add(v);
            }
        }
    }
    public void addRefreshHeadView(BaseRefreshHeader v) {
        if (headViews != null&&v !=null) {
            if(headViews.size()>0&&headViews.get(0) instanceof BaseRefreshHeader){
                headViews.remove(0);
                headViews.add(0,v);
            }else {
                headViews.add(0,v);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headViews.size() > 0&&position < headViews.size()) {
                return position+ TPEY_COUNT_START_TAG;
        }
        if (footViews.size() > 0&&position >= getItemCount() - footViews.size()) {
                return position+ TPEY_COUNT_START_TAG;
        }
        return adapter.getItemViewType(position);
    }
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder)
    {
        adapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if ((position >= 0 && position < headViews.size())||(position>=getItemCount()-footViews.size()&&position<getItemCount()))
        {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams)
            {

                StaggeredGridLayoutManager.LayoutParams p =
                        (StaggeredGridLayoutManager.LayoutParams) lp;

                p.setFullSpan(true);
            }
        }
    }
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    if (position >= 0 && position < headViews.size()) {
                        return ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                    }else if(position>=getItemCount()-footViews.size()&&position<getItemCount()){
                        return ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType- TPEY_COUNT_START_TAG >= 0 && viewType- TPEY_COUNT_START_TAG < headViews.size()) {
                return new HeaderHolder(headViews.get(viewType- TPEY_COUNT_START_TAG));
            }
            if (viewType- TPEY_COUNT_START_TAG >= getItemCount() - footViews.size() && viewType- TPEY_COUNT_START_TAG < getItemCount()) {
                return new FootHolder(footViews.get(viewType- TPEY_COUNT_START_TAG -adapter.getItemCount()-headViews.size()));
            }
        return adapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FootHolder ||holder instanceof HeaderHolder){
            return;
        }
        adapter.onBindViewHolder(holder, position-(headViews.size()));
    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount() + headViews.size() + footViews.size();
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    class FootHolder extends RecyclerView.ViewHolder {
        public FootHolder(View itemView) {
            super(itemView);
        }
    }


}
