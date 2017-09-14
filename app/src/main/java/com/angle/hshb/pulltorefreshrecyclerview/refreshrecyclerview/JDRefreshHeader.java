package com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.angle.hshb.pulltorefreshrecyclerview.R;

/**
 * Created by weizhenbin on 2017/3/17.
 */
public class JDRefreshHeader extends BaseRefreshHeader {
    ImageView ivPeople,ivGoods,ivGoodsAnim;
    TextView tv;

    public JDRefreshHeader(Context context) {
        this(context,null);
    }

    public JDRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public JDRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.jd_refresh_header,this);
        ivPeople= (ImageView) findViewById(R.id.iv_people);
        ivGoods= (ImageView) findViewById(R.id.iv_goods);
        ivGoodsAnim= (ImageView) findViewById(R.id.iv_goods_anim);
        tv= (TextView) findViewById(R.id.tv);
        ivGoodsAnim.setVisibility(GONE);
    }

    @Override
    protected void move(int height) {

    }

    @Override
    protected int refreshHeight() {
        return 140;
    }

    @Override
    protected void refresh(boolean canRefresh) {
         if(canRefresh){
             tv.setText("松开刷新");
         }else {
             tv.setText("下拉刷新");
         }
    }

    @Override
    protected void loosenAndRefresh() {
        tv.setText("更新中...");
        ivGoods.setVisibility(GONE);
        ivPeople.setVisibility(GONE);
        ivGoodsAnim.setVisibility(VISIBLE);
        ivGoodsAnim.setBackgroundResource(R.drawable.jd_anim);
        AnimationDrawable animationDrawable;
        animationDrawable= (AnimationDrawable) ivGoodsAnim.getBackground();
        animationDrawable.start();
    }

    @Override
    protected void reset() {
        tv.setText("");
        ivGoods.setVisibility(VISIBLE);
        ivPeople.setVisibility(VISIBLE);
        ivGoodsAnim.setVisibility(GONE);
    }
}
