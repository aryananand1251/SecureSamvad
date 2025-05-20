package com.example.securesamvad.adapter;



import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securesamvad.R;
import com.example.securesamvad.models.Contact;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    private final List<Contact> contacts;
    private final Context context;
    private final OnContactClickListener listener;

    public ChatListAdapter(List<Contact> contacts, Context context, OnContactClickListener listener) {
        this.contacts = contacts;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.userName.setText(contact.getName());

        // For demo, showing static last message; you can extend Contact to hold lastMessage
        holder.lastMessage.setText("Tap to chat");

        if (contact.getPhotoUri() != null) {
            holder.userImage.setImageURI(Uri.parse(contact.getPhotoUri()));
        } else {
            holder.userImage.setImageResource(R.drawable.app_logo); // Add a default user image drawable in your res/drawable
        }

        holder.itemView.setOnClickListener(v -> listener.onContactClick(contact));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}

