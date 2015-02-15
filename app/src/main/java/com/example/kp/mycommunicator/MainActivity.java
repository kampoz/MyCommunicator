package com.example.kp.mycommunicator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.*;
import java.net.*;

public class MainActivity extends ActionBarActivity {

    private TextView tv;
    private Button bLogin;
    private EditText userEt;
    private EditText passwordEt;
    private String request;
    private static final String HOST = "192.168.0.18";
    private static final int PORT = 7777;
    private PrintWriter printWriter;
    private BufferedReader br;
    String username = null;
    String password = null;
    String response = "Incorrect";
    String resp;
    public static String interlocutor;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);
        bLogin = (Button) findViewById(R.id.button);
        userEt = (EditText) findViewById(R.id.editText);
        passwordEt = (EditText) findViewById(R.id.editText2);

        userEt.setText("kamil");
        passwordEt.setText("111");
        //GetResponse getResponseTask = new GetResponse();
        //getResponseTask.execute();

        bLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                username = userEt.getText().toString();
                password = passwordEt.getText().toString();

                CheckLoginThread checkLoginThread = new CheckLoginThread();
                checkLoginThread.execute();
            }
        });
    }

    //wysyła request z logowaniem
    private class CheckLoginThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket s = new Socket(HOST, PORT);
                printWriter = new PrintWriter(s.getOutputStream(), true);
                request = "{ \"action\": \"login\", \"user\": \""+username+"\", \"password\": \""+password+"\" }";
                printWriter.println(request);
                printWriter.flush();
                Log.d("================<CLIENT>", "JSON wysłany!!!! na " + s);
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                response = br.readLine();
                Log.d(">><CLIENT> Odpowiedź serwera", response);
                br.close();
                printWriter.close();
                s.close(); // zamykanie socketu, bo juz jest nieużywany wiecej

                if (response.equals("LOGIN CORRECT"))             //<-- wątek sendRequestTask chyba sie nie zakoncza przy pierwszym wywołaniu if
                {
                    UserInfo userInfo = UserInfo.getInstance();
                    userInfo.login = userEt.getText().toString();
                    contactsActivity();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void contactsActivity(){
        Intent intent = new Intent (this, ContactsActivity.class );
        startActivity(intent);
    }

    public void talkActivity(){
        Intent intent = new Intent (this, TalkActivity.class );
        startActivity(intent);
    }



}


