package com.example.kp.mycommunicator;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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


    private Context context;
    public List<Contact> itemsArrayList;
    private LinearLayout singleContactContainer;


    @Override
    public void add(Contact contact) {               //nadpisanie metody add
        //itemsArrayList.add(contact);                //dodanie do arraylisty obiektu Contact
        super.add(contact);
    }

    //KONTRUKTOR
    public ContactsArrayAdapter(Context context, List<Contact> itemsArrayList) {
        //super(context, R.layout.contacts_list_view, textViewResourceId);
           super(context, R.layout.contacts_list_view, itemsArrayList);
           this.context = context;
           this.itemsArrayList = itemsArrayList;
           Log.d("KONTRUKTOR ADAPTERA", "konstruktor sie wykonał");
    }

    public int getCount() {                             //zwraca rozmiar arraya
        return this.itemsArrayList.size();
    }
    public Contact getItem(int index) {             //zwraca konkretny obiekt
        return this.itemsArrayList.get(index);
    }

    @Override                                  //zwraca widok pojedynczego elementu
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
                    Log.d("ADAPTER met. getView() ", "rozpoczęta");
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_contact_view, parent, false);
        }
        singleContactContainer = (LinearLayout) row.findViewById(R.id.singleContactContainer);

                    Log.d("listContact = getItem(position);", itemsArrayList.get(position).toString());

        tvContactName = (TextView)row.findViewById(R.id.contactName);   //Log.d("listContact.name",itemsArrayList.get(position).name);
        ivRedGreen = (ImageView)row.findViewById(R.id.onlineStatus);    //Log.d("listContact.online",itemsArrayList.get(position).online.toString());
        ivEnvelope = (ImageView)row.findViewById(R.id.envelope);        //Log.d("listContact.areThereMessages",itemsArrayList.get(position).areThereMessages.toString());

        tvContactName.setText(itemsArrayList.get(position).name);                //może lepiej getname zrobić, gettery i settery w Contact zrobic
        ivRedGreen.setImageResource(itemsArrayList.get(position).online ? R.drawable.green : R.drawable.red);
        ivEnvelope.setImageResource(itemsArrayList.get(position).areThereMessages ? R.drawable.envelope : R.drawable.line);
        singleContactContainer.setGravity(Gravity.LEFT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /*
    @Override
    public void onClick(View v) {
        Log.d("ASD", "ASD");
    }
    */


}
