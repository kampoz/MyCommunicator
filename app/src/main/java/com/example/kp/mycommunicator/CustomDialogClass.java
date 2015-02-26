package com.example.kp.mycommunicator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
    //Context context;

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

                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String addContactResponse = br.readLine();

                JSONObject JSONServerAddContactResponse = new JSONObject(addContactResponse);
                String serverAction = (String) JSONServerAddContactResponse.get("serverAction");
                if(serverAction.equals("addUserConfirm")) {
                    String result = (String) JSONServerAddContactResponse.get("result");
                    if(result.equals("success")) {

                        Log.d(log, "Kontakt dodany");
                        //Toast.makeText(this,"Kontakt dodany", Toast.LENGTH_SHORT).show();
                       /* runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Koniec wyszukiwania", Toast.LENGTH_LONG).show();
                            }
                        });*/
                    }

                };


                printWriter.close();
                br.close();
                s.close();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
        public void showToast(){
            Toast.makeText(this,"Kontakt dodany", Toast.LENGTH_SHORT).show();
        }
        */
        protected void onPostExecute(Void result) {
            Toast.makeText(getContext(),"Kontakt dodany", Toast.LENGTH_SHORT).show();
            //arrayResults = null;
        }
    }

    //DODAĆ FUNKCJE DODAJĄCĄ NA SERWER DO ARRAYA KONTAKTY - DO OBIEKTU USERA
}