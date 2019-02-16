package io.pp.net_disk_demo.activity;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.RecordInfo;
import io.pp.net_disk_demo.mvp.presenter.RecordPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.RecordPresenterImpl;
import io.pp.net_disk_demo.mvp.view.RecordView;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.widget.recyclerview.RecordAdapter;

public class RecordActivity extends BaseActivity implements RecordView {

    private static final String TAG = "RecordActivity";

    private Toolbar mRecordToolBar = null;
    private LinearLayout mToolBarLeftTvLayout = null;
    private TextView mToolBarTitleTv = null;

    private ImageView mNoContentIv = null;
    private TextView mNoContentTv = null;

    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private RecyclerView mRecordRecyclerView = null;
    private RecordAdapter mRecordAdapter = null;

    private RecordPresenter mRecordPresenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        mRecordPresenter = new RecordPresenterImpl(RecordActivity.this);

        init();

        if (PossUtil.getUser() == null) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        mSwipeRefreshLayout.setRefreshing(false);

        if (mRecordPresenter != null) {
            mRecordPresenter.onDestroy();
            mRecordPresenter = null;
        }

        super.onDestroy();
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void showRequestingRecordView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void showRequestRecordFailView(String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);

                mNoContentIv.setVisibility(View.VISIBLE);
                mNoContentTv.setVisibility(View.VISIBLE);
                mNoContentTv.setText("No records");
                mRecordRecyclerView.setVisibility(View.INVISIBLE);

                ToastUtil.showToast(RecordActivity.this, "request records fails!", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void showRequestRecordFinishedView(ArrayList<RecordInfo> recordInfoList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);

                if (recordInfoList != null && recordInfoList.size() > 0) {
                    mRecordAdapter.setRecordList(recordInfoList);
                    mNoContentIv.setVisibility(View.INVISIBLE);
                    mNoContentTv.setVisibility(View.INVISIBLE);
                    mRecordRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mNoContentIv.setVisibility(View.VISIBLE);
                    mNoContentTv.setVisibility(View.VISIBLE);
                    mNoContentTv.setText("No records");
                    mRecordRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void init() {
        setImmersiveStatusBar();

        mRecordToolBar = findViewById(R.id.record_toolbar_layout);
        mRecordToolBar.setPadding(0, 0, 0, 0);
        mRecordToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mRecordToolBar);

        mToolBarLeftTvLayout = findViewById(R.id.actionbar_left_iv_layout);

        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        mNoContentIv = findViewById(R.id.no_content_iv);
        mNoContentTv = findViewById(R.id.no_content_tv);

        mSwipeRefreshLayout = findViewById(R.id.refresh_layout);

        mRecordRecyclerView = findViewById(R.id.record_recyclerview);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.account_background_blue));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRecordPresenter != null) {
                    mRecordPresenter.startRequestRecord();
                }
            }
        });

        View.OnClickListener toolBarLeftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mToolBarLeftTvLayout.setOnClickListener(toolBarLeftOnClickListener);

        mToolBarTitleTv.setText("Record");

        mNoContentIv.setVisibility(View.GONE);
        mNoContentTv.setVisibility(View.GONE);

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

        mRecordPresenter.startRequestRecord();
    }
}