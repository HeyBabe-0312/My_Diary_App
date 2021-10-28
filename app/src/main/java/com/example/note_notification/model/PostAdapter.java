package com.example.note_notification.model;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note_notification.R;
import com.example.note_notification.view.MainDiary;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> mListPost;
    private Context context;
    private int p;
    public PostAdapter(List<Post> mListPost, Context context) {
        this.mListPost = mListPost;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new PostViewHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Post post = mListPost.get(position);
        if(post == null){
            return;
        }
        holder.countDay.setText(tinhNgay(post.getDate())+" ngày trước");
        holder.tvTime.setText(post.getTime());
        holder.tvTitle.setText(post.getTitle());
        holder.tvDesc.setText(post.getContent());
        holder.countDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(position);
            }
        });
        holder.tvDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(position);
            }
        });
        holder.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(position);
            }
        });
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(position);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDetail(int position){
        String dtTitle = mListPost.get(position).getTitle();
        String dtDesc = mListPost.get(position).getContent();
        String dtDate = mListPost.get(position).getDate();
        String dtTime = mListPost.get(position).getTime();

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.detail_view);

        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        TextView txtDesc = dialog.findViewById(R.id.txt_desc);
        TextView txtDay = dialog.findViewById(R.id.txt_Day);
        TextView txtTime = dialog.findViewById(R.id.txt_time);
        Button Ok = dialog.findViewById(R.id.btn_ok);

        txtTitle.setText(dtTitle);
        txtTime.setText(dtTime);
        txtDay.setText(tinhNgay(dtDate)+" ngày trước");
        txtDesc.setText(dtDesc);
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public int getItemCount() {
        if(mListPost!=null) return mListPost.size();
        else return 0;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        public TextView countDay;
        public TextView tvTime;
        public TextView tvTitle;
        public TextView tvDesc;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            countDay = itemView.findViewById(R.id.count_day);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String tinhNgay(String Date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate firstDate = LocalDate.parse(sdf.format(new Date()), formatter);
        LocalDate secondDate = LocalDate.parse(Date, formatter);
        long days = ChronoUnit.DAYS.between(secondDate, firstDate);
        return String.valueOf(days);
    }
}
