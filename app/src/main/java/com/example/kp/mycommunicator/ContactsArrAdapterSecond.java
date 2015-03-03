package com.example.kp.mycommunicator;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContactsArrAdapterSecond extends ArrayAdapter<Contact> implements View.OnClickListener{
    private Context context;
    private List<Contact> itemsArrayList;

    //konstruktor
    public ContactsArrAdapterSecond(Context context, List<Contact> itemsArrayList) {
        super(context, R.layout.single_contact_view, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.single_contact_view, parent, false);

        ImageView ivOnline = (ImageView) rowView.findViewById(R.id.onlineStatus);
        TextView tvName = (TextView) rowView.findViewById(R.id.contactName);
        ImageView ivEnvelope = (ImageView) rowView.findViewById(R.id.envelope);

        ivOnline.setImageResource(itemsArrayList.get(position).online ? R.drawable.green : R.drawable.red);
        tvName.setText(itemsArrayList.get(position).name);
        ivEnvelope.setImageResource(itemsArrayList.get(position).areThereMessages ? R.drawable.envelope : R.drawable.line);

        return rowView;
    }

    @Override
    public void onClick(View v) {
        Log.d("ASD", "ASD");
    }

}
