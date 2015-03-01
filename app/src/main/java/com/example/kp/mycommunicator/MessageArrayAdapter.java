package com.example.kp.mycommunicator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MessageArrayAdapter extends ArrayAdapter<Message>{
    private TextView chatText;
    private List<Message> itemsArrayList = new ArrayList<>();
    private LinearLayout singleMessageContainer;

    @Override
    public void add(Message object) {               //nadpisanie metody add
        itemsArrayList.add(object);                //dodanie do arraylisty obiektu ChatMessage
        super.add(object);
    }

    //konstruktor
    public MessageArrayAdapter(Context context, int textViewResourceId) {
        super(context, R.layout.activity_single_message, textViewResourceId);   // wywołanie konstruktora nadrzędnego ArrayAdapter
                                                                                // z pojedynczym layoutem activity_single_message
    }

    @Override                                  //nadpisanie metody getView
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null)
            {
                //LayoutInflater służy do dynamicznego ładowania layoutu
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //określa widok pojedynczego rzędu
                row = inflater.inflate(R.layout.activity_single_message, parent, false);
            }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);  //??? LinearLayout z xml
        Message chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);         // TextView z xml
        chatText.setText(chatMessageObj.message);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;

    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
