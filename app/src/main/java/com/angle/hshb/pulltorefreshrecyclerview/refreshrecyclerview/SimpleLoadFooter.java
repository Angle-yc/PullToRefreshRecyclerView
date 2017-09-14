package com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angle.hshb.pulltorefreshrecyclerview.R;


/**
 * Created by weizhenbin on 2017/3/7.
 */
public class SimpleLoadFooter extends BaseLoadFooter {
    ProgressBar progressBar;
    TextView tv;
    public SimpleLoadFooter(Context context) {
        this(context,null);
    }

    public SimpleLoadFooter(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleLoadFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.simple_load_foot,this);
        progressBar= (ProgressBar) findViewById(R.id.pb);
        tv= (TextView) findViewById(R.id.tv);
        progressBar.setVisibility(GONE);
    }

    @Override
    protected void move(int height) {

    }

    @Override
    protected int loadHeight() {
        return 120;
    }

    @Override
    protected void load(boolean canRefresh) {
        if(canRefresh){
            tv.setText("松开加载");
        }else {
            tv.setText("继续上拉");
        }
    }

    @Override
    protected void loosenAndLoad() {
        progressBar.setVisibility(VISIBLE);
        tv.setText("加载...");
    }

    @Override
    protected void reset() {
        progressBar.setVisibility(GONE);
        tv.setText("");
    }
}
