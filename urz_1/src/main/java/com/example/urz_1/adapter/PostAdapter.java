package com.example.urz_1.adapter;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.urz_1.R;
import com.example.urz_1.model.Comment;
import com.example.urz_1.model.Post;
import com.example.urz_1.model.User;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts;
    private Context mContext;
    private PostViewHolder mHolder;
    private User currentUser;//保存当前登录的用户
    private String currentUsername;//保存当前登录的用户
    private CommentAdapter mCommentAdapter;


    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLike, tvComment;
        private TextView tvNickName, tvPostContent, tvTime, tvLikes, tvComments;
        private RecyclerView rvComments;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNickName = itemView.findViewById(R.id.tvNickName);//post发布者
            tvPostContent = itemView.findViewById(R.id.tvPostContent);//post内容
            tvTime = itemView.findViewById(R.id.tvTime);//post发布时间
            tvLikes = itemView.findViewById(R.id.tvLikes);//post的点赞数
            tvLike = itemView.findViewById(R.id.tvLike);//post点赞
            tvComments = itemView.findViewById(R.id.tvComments);//post评论数
            tvComment = itemView.findViewById(R.id.tvComment);//TODO: post评论,绑定监听事件（...）
            //评论的子布局（嵌套一个子RecyclerView）
            rvComments = itemView.findViewById(R.id.rvComments);

        }
    }

    public PostAdapter(List<Post> posts, String username) {
        this.posts = posts;
        this.currentUsername = username;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        mContext = view.getContext();
        //PostViewHolder viewHolder = new PostViewHolder(view);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        User user = LitePal.where("username like ?", currentUsername).find(User.class).get(0);
        final Post post = posts.get(position);
        post.setUser(user);
        if (position == 0) {
            currentUser = post.getUser();
        }

        if (post.getUser().getNickname() == null) {
            post.getUser().setNickname(post.getUser().getUsername());
        }
        holder.tvNickName.setText(post.getUser().getNickname());
        holder.tvPostContent.setText(post.getContent());
        holder.tvTime.setText(post.getDate());
        /**
         * ERROR: android.content.res.Resources$NotFoundException: String resource ID #0x0
         * 这种情况，很有可能是把一个int型业务数据的 设置setText（）或者类似的方法中，
         * 这样Android系统就会主动去资源文件当中寻找， 但是它不是一个资源文件ID， 所以就会报出这个bug。
         * 将int型业务数据，转换成String类型(String.valueOf())即可。
         */
        holder.tvLikes.setText(String.valueOf(post.getLikes()));
        post.setComments(0);
        holder.tvComments.setText(String.valueOf(post.getComments()));
        //设置子布局
        holder.rvComments.setHasFixedSize(true);
        holder.rvComments.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mCommentAdapter = new CommentAdapter(position);
        holder.rvComments.setAdapter(mCommentAdapter);
        drawRecyclerView();


        mHolder = holder;
        //为动态动态点赞操作
        holder.tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setLikes(post.getLikes() + 1);
                post.save();
                holder.tvLikes.setText(String.valueOf(post.getLikes()));
                ObjectAnimator animationDrawable = ObjectAnimator.ofFloat(holder.tvLike, "rotationY", 0f, 360f);
                animationDrawable.setDuration(500);
                animationDrawable.start();
            }
        });

        //评论功能（...）
        holder.tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);//评论的弹出框
                final EditText edt = new EditText(mContext);
                builder.setTitle("评论");
                builder.setPositiveButton("发布",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!"".equals(edt.getText().toString())) {
                                    post.setComments(post.getComments() + 1);//设置评论数+1
                                    Comment comment = new Comment();//新建评论的实例
                                    comment.setPost(post);//设置评论所属的动态
                                    comment.setUser(currentUser);//设置评论的用户（即当前登录的用户）
                                    comment.setComment_content(edt.getText().toString());//设置评论内容
                                    //设置评论日期
                                    Date date = new Date();
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String dateString = formatter.format(date);
                                    comment.setComment_date(dateString);
                                    comment.save();
                                    post.save();

                                    //TODO: 将评论显示出来（...）
                                    //通过RecyclerView里嵌套RecyclerView来实现

                                }

                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.setView(edt);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void drawRecyclerView() {
        //RecyclerView点击事件
        mCommentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });
    }

}
