package io.pp.net_disk_demo.activity;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.RecordInfo;
import io.pp.net_disk_demo.ppio.RpcUtil;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.widget.recyclerview.RecordAdapter;

public class RecordActivity extends BaseActivity {

    private Toolbar mRecordToolBar = null;
    private LinearLayout mToolBarLeftTvLayout = null;
    private TextView mToolBarTitleTv = null;

    private RecyclerView mRecordRecyclerView = null;
    private RecordAdapter mRecordAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        init();
    }

    private void init() {
        setImmersiveStatusBar();

        mRecordToolBar = findViewById(R.id.record_toolbar_layout);
        mRecordToolBar.setPadding(0, 0, 0, 0);
        mRecordToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mRecordToolBar);

        mToolBarLeftTvLayout = findViewById(R.id.actionbar_left_iv_layout);

        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        View.OnClickListener toolBarLeftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mToolBarLeftTvLayout.setOnClickListener(toolBarLeftOnClickListener);

        mToolBarTitleTv.setText("Record");

        mRecordRecyclerView = findViewById(R.id.record_recyclerview);

        mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(RecordActivity.this));
        mRecordRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = Util.dp2px(RecordActivity.this, 1);
                }
            }

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });

        mRecordAdapter = new RecordAdapter(RecordActivity.this, null);

        mRecordRecyclerView.setAdapter(mRecordAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<RecordInfo> recordInfList = RpcUtil.transferRecord();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecordAdapter.setRecordList(recordInfList);
                    }
                });
            }
        }).start();
    }
}