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
import com.example.proyectochatmovil.Chat;
import com.example.proyectochatmovil.MessageActivity;
import com.example.proyectochatmovil.R;
import com.example.proyectochatmovil.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContex;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public MessageAdapter (Context mContex, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContex = mContex;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContex).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContex).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.mostrar_mensaje.setText(chat.getMensaje());

        if (imageurl != null && imageurl.equals("default")) {
            holder.imagen_deperfil.setImageResource(R.mipmap.ic_launcher);
        } else if (imageurl != null) {
            Glide.with(mContex).load(imageurl).into(holder.imagen_deperfil);
        } 



        /*User user = mUsers.get(position);
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
        });*/
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mostrar_mensaje;
        public ImageView imagen_deperfil;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mostrar_mensaje = itemView.findViewById(R.id.mostrar_mensaje);
            imagen_deperfil= itemView.findViewById(R.id.imagen_deperfil);

        }
    }

    @Override
    public int getItemViewType(int position) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getEmisor().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
