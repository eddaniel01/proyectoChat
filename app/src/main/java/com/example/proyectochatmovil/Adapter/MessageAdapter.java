package com.example.proyectochatmovil.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
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
    private String imageUrl;


    FirebaseUser fuser;

    public MessageAdapter (Context mContex, List<Chat> mChat, String imageUrl){
        this.mChat = mChat;
        this.mContex = mContex;
        this.imageUrl = imageUrl;

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

          String palabra = chat.getMensaje();

        // Verificar si el mensaje contiene una URL de imagen
        if (palabra.contains("https://firebasestorage.googleapis.com/v0/b/proyectochatmovil-451.appspot.com/o/chat_images%")) {
            holder.mostrar_mensaje.setVisibility(View.GONE);
            holder.mostrar_imagen.setVisibility(View.VISIBLE);
            Glide.with(mContex).load(chat.getMensaje()).centerCrop().into(holder.mostrar_imagen); // Cargar la imagen del mensaje
        } else {
            holder.mostrar_mensaje.setText(chat.getMensaje()); // Mostrar el mensaje de texto
            holder.mostrar_mensaje.setVisibility(View.VISIBLE);
            holder.mostrar_imagen.setVisibility(View.GONE); // Ocultar ImageView si es texto
        }

        // Cargar la imagen de perfil
        if (imageUrl != null && imageUrl.equals("default")) {
            holder.imagen_deperfil.setImageResource(R.mipmap.ic_launcher); // Imagen por defecto
        } else if (imageUrl != null) {
            Glide.with(mContex).load(imageUrl).into(holder.imagen_deperfil); // Cargar la imagen de perfil
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mostrar_mensaje;
        public ImageView imagen_deperfil;
        public ImageView mostrar_imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mostrar_mensaje = itemView.findViewById(R.id.mostrar_mensaje);
            imagen_deperfil= itemView.findViewById(R.id.imagen_deperfil);
            mostrar_imagen = itemView.findViewById(R.id.mostrar_imagen);

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
