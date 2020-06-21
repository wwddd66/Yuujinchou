package com.example.urz_1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urz_1.R;
import com.example.urz_1.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context commentContext;
    private List<Comment> commentList;


    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentUser, tvCommentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentUser = itemView.findViewById(R.id.tvCommentUser);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }

    public CommentAdapter(Context context) {
        this.commentContext = context;

    }

    public void setData(List<Comment> commentList) {
        this.commentList = commentList;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentViewHolder viewHolder = holder;
        Comment comment = commentList.get(position);
        viewHolder.tvCommentUser.setText(comment.getUser().getNickname() + "：");
        viewHolder.tvCommentContent.setText(comment.getComment_content());
        //将position保存在itemView的Tag中，以便点击时进行获取
        //holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
