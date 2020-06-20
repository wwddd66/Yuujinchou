package com.example.urz_1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urz_1.R;
import com.example.urz_1.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> implements View.OnClickListener {
    private List<Comment> commentList;

    //新建一个私有变量用于保存用户设置的监听器
    private OnItemClickListener mOnItemClickListener = null;


    //set方法：
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    //定义接口
    public static interface OnItemClickListener {
        void onItemClick(View v, int position);
    }


    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentUser, tvCommentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentUser = itemView.findViewById(R.id.tvCommentUser);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }

    public CommentAdapter(int position) {
        commentList = new ArrayList<>();

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, null);
        view.setOnClickListener(this);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.tvCommentUser.setText(commentList.get(position).getUser().getNickname());
        holder.tvCommentContent.setText(commentList.get(position).getComment_content());
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
