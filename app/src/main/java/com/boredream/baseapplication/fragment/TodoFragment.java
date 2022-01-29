package com.boredream.baseapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.CollectionUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.activity.TodoEditActivity;
import com.boredream.baseapplication.adapter.TodoGroupAdapter;
import com.boredream.baseapplication.base.BaseFragment;
import com.boredream.baseapplication.dialog.BottomInputDialog;
import com.boredream.baseapplication.dialog.BottomSelectDialog;
import com.boredream.baseapplication.entity.Todo;
import com.boredream.baseapplication.entity.TodoGroup;
import com.boredream.baseapplication.entity.event.TodoUpdateEvent;
import com.boredream.baseapplication.entity.event.UserUpdateEvent;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.view.TitleBar;
import com.boredream.baseapplication.view.loading.RefreshListLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoFragment extends BaseFragment implements TodoGroupAdapter.OnTodoActionListener {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.rll_list)
    RefreshListLayout rllList;
    @BindView(R.id.tv_progress)
    TextView tvProgress;

    private ArrayList<TodoGroup> infoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(activity, R.layout.frag_todo, null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TodoUpdateEvent event) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserUpdateEvent(UserUpdateEvent event) {
        initData();
    }

    private void initView() {
        titleBar.setTitle("清单")
                .setLeftMode()
                .setRight("创建清单", R.drawable.ic_add_oval_whte, v -> addTodoGroup());

        rllList.setEnableRefresh(true);
        rllList.setEnableLoadmore(false);
        rllList.setOnRefreshListener(refresh -> loadData());
        rllList.getRv().setLayoutManager(new LinearLayoutManager(activity));
        rllList.getRv().setAdapter(new TodoGroupAdapter(infoList, this));
        rllList.setOnDefaultAction(new RefreshListLayout.OnDefaultActionListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

            @Override
            public void onCreate() {
                addTodoGroup();
            }
        });
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        HttpRequest.getInstance()
                .getApiService()
                .getTodoGroup()
                .compose(RxComposer.commonRefresh(this, rllList, false))
                .subscribe(new SimpleObserver<List<TodoGroup>>() {
                    @Override
                    public void onNext(List<TodoGroup> response) {
                        int totalCount = 0;
                        int progressCount = 0;
                        for (TodoGroup group : response) {
                            if (CollectionUtils.isEmpty(group.getTodoList())) continue;
                            for (Todo todo : group.getTodoList()) {
                                totalCount++;
                                if (todo.isDone()) {
                                    progressCount++;
                                }
                            }
                        }
                        tvProgress.setText(String.format(Locale.getDefault(), "已打卡%d/%d", progressCount, totalCount));

                        infoList.clear();
                        infoList.addAll(response);
                        rllList.checkEmpty();
                    }
                });
    }

    @Override
    public void onTodoEdit(Todo todo) {
        TodoEditActivity.start(activity, todo);
    }

    @Override
    public void onTodoAdd(Long groupId) {
        BottomInputDialog dialog = new BottomInputDialog(activity, "清单名称", null, data -> {
            Todo param = new Todo();
            param.setTodoGroupId(groupId);
            param.setName(data);
            HttpRequest.getInstance()
                    .getApiService()
                    .postTodo(param)
                    .compose(RxComposer.commonProgress(this))
                    .subscribe(new SimpleObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            showTip("创建成功");
                            loadData();
                        }
                    });
        });
        dialog.show();
    }

    @Override
    public void onTodoGroupMore(TodoGroup group) {
        BottomSelectDialog dialog = new BottomSelectDialog(activity, null,
                Arrays.asList("重命名", "删除"),
                (parent, view, position, id) -> {
                    switch (position) {
                        case 0:
                            renameGroup(group);
                            break;
                        case 1:
                            deleteGroup(group);
                            break;
                    }
                });
        dialog.show();
    }

    private void deleteGroup(TodoGroup group) {
        DialogUtils.showDeleteConfirmDialog(getContext(),
                v -> HttpRequest.getInstance()
                        .getApiService()
                        .deleteTodoGroup(group.getId())
                        .compose(RxComposer.commonProgress(this))
                        .subscribe(new SimpleObserver<String>() {
                            @Override
                            public void onNext(String s) {
                                showTip("删除成功");
                                loadData();
                            }
                        }));
    }

    private void addTodoGroup() {
        BottomInputDialog dialog = new BottomInputDialog(activity, "清单组名称", null, data -> {
            TodoGroup param = new TodoGroup();
            param.setName(data);
            HttpRequest.getInstance()
                    .getApiService()
                    .postTodoGroup(param)
                    .compose(RxComposer.commonProgress(this))
                    .subscribe(new SimpleObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            showTip("创建成功");
                            loadData();
                        }
                    });
        });
        dialog.show();
    }

    private void renameGroup(TodoGroup group) {
        BottomInputDialog dialog = new BottomInputDialog(activity, "清单组名称", group.getName(), data -> {
            group.setName(data);
            HttpRequest.getInstance()
                    .getApiService()
                    .putTodoGroup(group.getId(), group)
                    .compose(RxComposer.commonProgress(this))
                    .subscribe(new SimpleObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            showTip("修改成功");
                            loadData();
                        }
                    });
        });
        dialog.show();
    }
}