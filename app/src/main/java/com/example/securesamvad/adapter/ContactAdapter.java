package com.example.securesamvad.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securesamvad.R;
import com.example.securesamvad.activities.chat;
import com.example.securesamvad.activities.chatList;
import com.example.securesamvad.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private List<Contact> filteredList;
    private Context context;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.filteredList = new ArrayList<>(contactList);
    }

    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(contactList);
        } else {
            for (Contact contact : contactList) {
                if (contact.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = filteredList.get(position);
        holder.name.setText(contact.getName());

        if (contact.getPhotoUri() != null) {
            holder.image.setImageURI(Uri.parse(contact.getPhotoUri()));
        } else {
            holder.image.setImageResource(R.drawable.app_logo); // fallback image
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, chat.class);
            intent.putExtra("receiverName", contact.getName());
            intent.putExtra("receiverNumber", contact.getPhoneNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            image = itemView.findViewById(R.id.contactImage);
        }
    }




}
