/*
 * Copyright (c) 2018 denua.
 */

package cn.denua.v2ex.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import cn.denua.v2ex.R;
import cn.denua.v2ex.base.BaseNetworkActivity;
import cn.denua.v2ex.interfaces.ResponseListener;
import cn.denua.v2ex.model.Topic;
import cn.denua.v2ex.service.RxObserver;
import cn.denua.v2ex.service.TopicService;
import cn.denua.v2ex.utils.DialogUtil;
import io.reactivex.Observable;

/*
 * @author denua
 * @email denua@foxmail.com
 * @date 2018/12/30 12
 */
public class PostTopicActivity extends BaseNetworkActivity implements ResponseListener<Topic> {


    EditText mEtTitle;

    EditText mEtContent;

    TextView mTvNode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_post_topic);

        bindView();

        mTvNode.setOnClickListener(this::selectNode);
    }

    private void bindView() {
        mEtTitle = findViewById(R.id.et_title);
        mEtContent = findViewById(R.id.et_content);
        mTvNode = findViewById(R.id.tv_node);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_post_topic, menu);
        menu.findItem(R.id.it_confirm).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.it_confirm) {
            TopicService.postTopic(this,
                    mEtTitle.getText().toString(),
                    mEtContent.getText().toString(),
                    mTvNode.getText().toString(),
                    this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectNode(View view) {

        DialogUtil.showInputDialog(
                this,
                "输入节点名称",
                null,
                "sandbox",
                value -> mTvNode.setText(value));
    }

    @Override
    public void onComplete(Topic result) {
        Observable.timer(500, TimeUnit.MILLISECONDS).subscribe(new RxObserver<Long>() {
            @Override
            public void _onNext(Long aLong) {
                TopicActivity.start(PostTopicActivity.this, result.getId());
            }
        });

    }

    @Override
    public boolean onFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        return true;
    }
}
