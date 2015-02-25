package com.example.kp.mycommunicator;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public String contact;
    public String login;
    private static final String HOST = "192.168.0.18";
    private static final int PORT = 7777;
    PrintWriter printWriter;
    String log = " <Gecco> CustomDialogClass";

    public CustomDialogClass(Activity a, String contact, String login) {
        super(a);
        this.c = a;
        this.contact = contact;
        this.login = login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:

                RequestAddContact requestAddContact = new RequestAddContact();
                requestAddContact.execute();

                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private class RequestAddContact extends AsyncTask<Object, Integer, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("action", "AddContact");
                jsonObject.put("user", login);
                jsonObject.put("contactToAdd", contact);

                Socket s = new Socket(HOST, PORT);
                printWriter = new PrintWriter(s.getOutputStream(), true);
                printWriter.println(jsonObject.toString());
                Log.d(log, " Wysłano jsona: "+jsonObject.toString());
                printWriter.close();
                s.close();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //DODAĆ FUNKCJE DODAJĄCĄ NA SERWER DO ARRAYA KONTAKTY - DO OBIEKTU USERA
}