package com.example.proyectochatmovil.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectochatmovil.MessageActivity;
import com.example.proyectochatmovil.R;
import com.example.proyectochatmovil.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContex;
    private List<User> mUsers;

    public UserAdapter (Context mContex, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContex = mContex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContex).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());

        // Verificación para evitar el NullPointerException
        if (user.getImagenURL() != null && user.getImagenURL().equals("default")) {
            holder.imagen_deperfil.setImageResource(R.mipmap.ic_launcher);
        } else if (user.getImagenURL() != null) {
            Glide.with(mContex).load(user.getImagenURL()).into(holder.imagen_deperfil);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContex, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContex.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView imagen_deperfil;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            imagen_deperfil= itemView.findViewById(R.id.imagen_deperfil);

        }
    }

}
