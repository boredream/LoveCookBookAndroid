package com.boredream.baseapplication.entity;

import com.boredream.baseapplication.R;

import java.util.List;

/**
 * <p>
 * 清单组
 * </p>
 *
 * @author boredream
 */
public class TodoGroup extends Belong2UserEntity {

    private Integer icon;

    private String name;

    private List<Todo> todoList;

    public Integer getIcon() {
        if(icon != null) {
            switch(icon) {
                case 1:
                    return R.drawable.ic_todo_group1;
                case 2:
                    return R.drawable.ic_todo_group2;
                case 3:
                    return R.drawable.ic_todo_group3;
            }
        }
        return R.drawable.ic_todo_cus;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Todo> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
    }
}
