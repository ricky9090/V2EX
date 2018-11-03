package cn.denua.v2ex.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.denua.v2ex.R;
import cn.denua.v2ex.adapter.RecyclerViewAdapter;
import cn.denua.v2ex.base.BaseNetworkFragment;
import cn.denua.v2ex.interfaces.ResponseListener;
import cn.denua.v2ex.model.Topic;
import cn.denua.v2ex.service.TopicService;

public class TopicFragment extends BaseNetworkFragment implements ResponseListener<List<Topic>>, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_topics)
    RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;
    private List<Topic> topics = new ArrayList<>();

    private TopicService topicService;

    public static TopicFragment newInstance(String contentType){

        TopicFragment topicFragment = new TopicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("contentType", contentType);
        topicFragment.setArguments(bundle);

        return topicFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( getArguments() != null){
            this.setContentType(getArguments().getString("contentType"));
        }
        topicService = new TopicService<>(this, this);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (this.savedView != null)
            return savedView;

        savedView = inflater.inflate(R.layout.frag_topic, container,false);

        ButterKnife.bind(this, savedView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(getContext(), topics);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        onRefresh();
        return savedView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        topicService.getTopic(getContentType());
    }

    @Override
    public void onComplete(List<Topic> result) {

        this.topics = result;
        adapter.setTopics(topics);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCompleteRequest() {
        super.onCompleteRequest();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailed(String msg) {
        ToastUtils.showShort(msg);
    }
}
