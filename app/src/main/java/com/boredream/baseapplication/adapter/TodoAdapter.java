package com.boredream.baseapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.entity.Todo;
import com.boredream.baseapplication.entity.TodoGroup;
import com.boredream.baseapplication.net.GlideHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private final TodoGroup group;
    private final List<Todo> infoList;
    private final TodoGroupAdapter.OnTodoActionListener onTodoActionListener;

    public TodoAdapter(TodoGroup group, List<Todo> infoList, TodoGroupAdapter.OnTodoActionListener onTodoActionListener) {
        this.group = group;
        this.infoList = infoList;
        this.onTodoActionListener = onTodoActionListener;
    }

    @Override
    public int getItemCount() {
        return infoList.size() + 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        boolean showAdd = position == infoList.size();
        if (showAdd) {
            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivImage.setImageResource(R.drawable.ic_add_red);
            holder.ivImage.setOnClickListener(v -> {
                if (onTodoActionListener != null) {
                    onTodoActionListener.onTodoAdd(group.getId());
                }
            });
        } else {
            Todo data = infoList.get(position);
            if (data.isDone()) {
                holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                String image = data.getImages();
                if (image != null && image.contains(",")) {
                    image = image.split(",")[0];
                }
                GlideHelper.loadOvalImg(holder.ivImage, image, R.drawable.img_todo_done_default);
            } else {
                holder.ivImage.setScaleType(ImageView.ScaleType.CENTER);
                holder.ivImage.setImageResource(R.drawable.ic_lock);
            }
            holder.tvName.setText(data.getName());
            String doneDate = data.getDoneDate();
            if(StringUtils.isEmpty(doneDate)) {
                holder.tvDate.setText("");
            } else {
                holder.tvDate.setText(doneDate);
            }
            holder.ivImage.setOnClickListener(v -> {
                if (onTodoActionListener != null) {
                    onTodoActionListener.onTodoEdit(data);
                }
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
