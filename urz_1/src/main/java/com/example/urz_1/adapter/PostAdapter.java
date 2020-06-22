package com.example.urz_1.adapter;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urz_1.R;
import com.example.urz_1.model.Comment;
import com.example.urz_1.model.Post;
import com.example.urz_1.model.User;
import com.example.urz_1.shape.RoundImageView;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts;
    private Context mContext;
    //private PostViewHolder mHolder;
    //private CommentAdapter mCommentAdapter;
    private User currentUser;//保存当前登录的用户
    private String currentUsername;//保存当前登录的用户
    private Bitmap image;


    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLike, tvComment;
        private TextView tvNickName, tvPostContent, tvTime, tvLikes, tvComments;
        private TextView tvCommentsTest;
        private RoundImageView ivAvatar;
        /*private RecyclerView rvComments;
        private CommentAdapter commentAdapter;
        private List<Comment> commentList = new ArrayList<>();
        private StaggeredGridLayoutManager manager;*/

       /* public void showItem(List<Comment> commentList) {
            commentAdapter.setData(commentList);
            rvComments.setAdapter(commentAdapter);
        }*/

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNickName = itemView.findViewById(R.id.tvNickName);//post发布者
            tvPostContent = itemView.findViewById(R.id.tvPostContent);//post内容
            tvTime = itemView.findViewById(R.id.tvTime);//post发布时间
            tvLikes = itemView.findViewById(R.id.tvLikes);//post的点赞数
            tvLike = itemView.findViewById(R.id.tvLike);//post点赞
            tvComments = itemView.findViewById(R.id.tvComments);//post评论数
            tvComment = itemView.findViewById(R.id.tvComment);//TODO: post评论,绑定监听事件（...）
            tvCommentsTest = itemView.findViewById(R.id.tvCommentsTest);
            //评论的子布局（嵌套一个子RecyclerView）
            //rvComments = itemView.findViewById(R.id.rvComments);
            /**
             * 新添
             */
            /*commentAdapter = new CommentAdapter(context);
            manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            rvComments.setLayoutManager(manager);*/


        }
    }


    public PostAdapter(Context context, List<Post> posts, String username, Bitmap bitmap) {
        mContext = context;
        this.posts = posts;
        this.currentUsername = username;
        image = bitmap;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {
        currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class, true);
        final Post post = posts.get(position);
        if (image != null && post.getUser().getId() == currentUser.getId()) {//image不为空且是当前登录的用户，更换头像
            holder.ivAvatar.setImageBitmap(image);
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
        holder.tvComments.setText(String.valueOf(post.getComments()));
        /*holder.showItem(post.getCommentList());*/

        //评论内容展示
        List<Comment> commentList = LitePal.where("post_id like ?", String.valueOf(post.getId())).order("comment_date desc").find(Comment.class, true);
        if (commentList == null) {
            Toast.makeText(mContext, "没有评论内容", Toast.LENGTH_SHORT).show();
            return;
        } else {
            for (int i = 0; i < commentList.size(); i++) {
                String commentAuthor = commentList.get(i).getUser().getNickname() + "：";
                String commentContent = commentList.get(i).getComment_content() + "\n";
                String commentDate = commentList.get(i).getComment_date() + "\n";
                if (!holder.tvCommentsTest.getText().toString().equals("")) {
                    String[] index = {commentAuthor, commentContent, commentDate};
                    SpannableString msp = new SpannableString(holder.tvCommentsTest.getText().toString() + index[0] + index[1] + index[2]);
                    holder.tvCommentsTest.setText(msp);
                } else {
                    holder.tvCommentsTest.setText(commentAuthor + commentContent + commentDate);
                }

            }
        }

        //动态内容点击事件（询问是否删除该动态（当然，只有动态的主人可以删除））
        holder.tvPostContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User postUser = LitePal.where("id like ?", String.valueOf(post.getUser().getId())).findFirst(User.class, true);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);//评论的弹出框
                builder.setTitle("将执行的操作")
                        .setMessage("是否删除该动态")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (currentUser.getId() == postUser.getId()) {
                                    post.delete();
                                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "嘿嘿，不是你的动态你还想删除，想得美。" + "\n" + "“那我就是不想看这个，咋办？”" + "\n" + "“你可以删除这个好友，是不是非常的可来碗儿呢？”",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("算了，不删了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();


            }
        });

        //mHolder = holder;
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
                                    holder.tvComments.setText(post.getComments() + "");
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
                                    post.update(post.getId());

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
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


}
