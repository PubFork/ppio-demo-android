package io.pp.net_disk_demo.activity;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.RecordInfo;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.widget.recyclerview.RecordAdapter;

public class RecordActivity extends BaseActivity {

    private RecyclerView mRecordRecyclerView = null;
    private RecordAdapter mRecordAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        init();
    }

    private void init() {
        mRecordRecyclerView =  findViewById(R.id.record_recyclerview);

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

        ArrayList<RecordInfo> recordList = new ArrayList<>();

        mRecordAdapter.setRecordList(recordList);
    }
}