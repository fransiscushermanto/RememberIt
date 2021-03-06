package com.example.rememberit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rememberit.AddNewTask;
import com.example.rememberit.DetailTask;
import com.example.rememberit.MainActivity;
import com.example.rememberit.Models.DateTimeModel;
import com.example.rememberit.Models.ToDoModel;
import com.example.rememberit.R;
import com.example.rememberit.Utils.DatabaseHandler;

import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;

    public ToDoAdapter(MainActivity activity, DatabaseHandler db) {
        this.activity = activity;
        this.db = db;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);

        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        db.openDatabase();
        final ToDoModel item = todoList.get(position);
        final DateTimeModel dateTimeModel = new DateTimeModel();
        holder.taskTitle.setText(item.getTaskTitle());
        holder.task.setChecked( item.getStatus() == 1 ? true : false );
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    item.setStatus(1);
                    item.setFinished_at(Calendar.getInstance().getTime().toString());
                    db.updateStatus(item);
                }else {
                    item.setStatus(0);
                    item.setFinished_at("");
                    db.updateStatus(item);
                }

            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailTask.class);
                intent.putExtra("task_id", item.getId());
                getContext().startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {return activity;}

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", item.getId());
        bundle.putString("task", item.getTaskTitle());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView taskTitle;
        View mView;
        ViewHolder(View view){
            super(view);
            mView = view;
            task = view.findViewById(R.id.todoCheckBox);
            taskTitle = view.findViewById(R.id.todoTextTitle);
        }
    }
}
