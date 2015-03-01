package com.example.kp.mycommunicator;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactsArrayAdapter extends ArrayAdapter<Contact> {

    private TextView tvContactName;
    private ImageView ivRedGreen;
    private ImageView ivEnvelope;

    private List<Contact> itemsArrayList = new ArrayList<>();
    private LinearLayout singleContactContainer;


    @Override
    public void add(Contact object) {               //nadpisanie metody add
        itemsArrayList.add(object);                //dodanie do arraylisty obiektu ChatMessage
        super.add(object);
        }

    //KONTRUKTOR
    public ContactsArrayAdapter(Context context, int textViewResourceId) {
        super(context, R.layout.contacts_list_view, textViewResourceId);   // wywołanie konstruktora nadrzędnego ArrayAdapter
    }

    @Override                                  //nadpisanie metody getView
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_single_message, parent, false);
        }
        singleContactContainer = (LinearLayout) row.findViewById(R.id.singleContactContainer);
        Contact listContact = getItem(position);
        tvContactName = (TextView)row.findViewById(R.id.contactName);
        tvContactName.setText(listContact.name);                //może lepiej getname zrobić, gettery i settery w Contact zrobic

        ivRedGreen = (ImageView)row.findViewById(R.id.onlineStatus);
        ivRedGreen.setImageResource(listContact.online ? R.drawable.green : R.drawable.red);

        ivEnvelope = (ImageView)row.findViewById(R.id.envelope);
        ivRedGreen.setImageResource(listContact.areThereMessages ? R.drawable.envelope : R.drawable.line);

        singleContactContainer.setGravity(Gravity.LEFT);

        return row;
    }


}
