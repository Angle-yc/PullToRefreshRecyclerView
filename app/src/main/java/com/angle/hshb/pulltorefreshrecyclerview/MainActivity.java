package com.angle.hshb.pulltorefreshrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview.ExpandRecyclerView;
import com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview.JDRefreshHeader;
import com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview.OnLoadListener;
import com.angle.hshb.pulltorefreshrecyclerview.refreshrecyclerview.OnRefreshListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ExpandRecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<String>list=new ArrayList<>();
    TestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView= (ExpandRecyclerView) findViewById(R.id.recycler_view);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSupportPullRefresh(true);
        recyclerView.setSupportPullLoad(true);
        for (int i = 0; i < 10; i++) {
            list.add("angle ---"+i);
        }
        adapter=new TestAdapter(this,list);
        recyclerView.setAdapter(adapter);
        recyclerView.addHeadView(new JDRefreshHeader(this));

        recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("RecyclerViewActivity", "刷新");
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        list.clear();
                        for (int i=0;i<2;i++){
                            list.add("字符新加"+i);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.refreshComplete();
                    }
                },3000);
            }
        });

        recyclerView.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                Log.d("RecyclerViewActivity", "加载");
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.loadComplete();
                        for (int i=0;i<10;i++){
                            list.add("字符新加"+i);
                        }
                        adapter.notifyDataSetChanged();
                    }
                },2000);
            }
        });
    }

















    class TestAdapter extends RecyclerView.Adapter<MyHolder>{
        /**RecyclerView 没有添加头部和尾部相应的方法  都是加载不同类型view来实现的*/
        private Context context;
        private ArrayList<String> ss;
        public TestAdapter(Context context,ArrayList<String> ss) {
            this.context = context;
            this.ss=ss;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(context).inflate(R.layout.test_list_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {
            holder.textView.setText("普通item"+ss.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TestAdapter", "点击了" + position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ss.size();
        }
    }


    class MyHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyHolder(View itemView) {
            super(itemView);
            textView= (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
