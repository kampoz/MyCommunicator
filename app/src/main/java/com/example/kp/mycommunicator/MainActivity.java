package com.example.kp.mycommunicator;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
import java.net.*;

public class MainActivity extends ActionBarActivity {
    private TextView tv;
    private Button bLogin;
    private Button bRegistration;
    private EditText userEt;
    private EditText passwordEt;
    private String request;
    private static final String HOST = "192.168.0.18";
    //private static final String HOST = "192.168.0.11";
    private static final int PORT = 7777;
    private PrintWriter printWriter;
    private BufferedReader br;
    //private BufferedInputStream br;
    String username = null;
    String password = null;
    String response = "Incorrect";
    String resp;
    public static String interlocutor;
    private String log = "< GECCO GECKO >";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);
        bLogin = (Button) findViewById(R.id.button);
        bRegistration = (Button) findViewById(R.id.button4);
        userEt = (EditText) findViewById(R.id.editText);
        passwordEt = (EditText) findViewById(R.id.editText2);
        userEt.setText("kamil");
        passwordEt.setText("111");
        //GetResponse getResponseTask = new GetResponse();
        //getResponseTask.execute();

        bLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Time time = new Time();
                    Log.d(log,time.getTime()+" <MainActivity><bLogin/OnClick> Kliknięcie bLogin");
                Toast.makeText(getApplicationContext(),"Trwa logowanie...", Toast.LENGTH_SHORT).show();
                    Log.d(log,time.getTime()+" <MainActivity><bLogin/OnClick> Wyswietlenie Toastu");
                username = userEt.getText().toString();
                    Log.d(log,time.getTime()+" <MainActivity><bLogin/OnClick> Pobranie tekstu z editText / username");
                password = passwordEt.getText().toString();
                    Log.d(log,time.getTime()+"<MainActivity><bLogin/OnClick> Pobranie tekstu z editText / password");
                CheckLoginThread checkLoginThread = new CheckLoginThread();
                    Log.d(log,time.getTime()+" <MainActivity><bLogin/OnClick> Nowy obiekt wątku CheckLoginThread");
                checkLoginThread.execute();
                    Log.d(log,time.getTime()+" <MainActivity><bLogin/OnClick> checkLoginThread.execute();");
            }
        });

        bRegistration.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                startRegistrationActivity();
            }
        });
    }

    //wysyła request z logowaniem
    private class CheckLoginThread extends AsyncTask<Void, Void, Void> {
        Time time = new Time();

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket s = new Socket(HOST, PORT);
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground> Logowanie: otwarcie socketu");
                BufferedWriter bufOut = new BufferedWriter( new OutputStreamWriter( s.getOutputStream()));
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>Logowanie: Stworzenie BufferWriter");
                //printWriter = new PrintWriter(s.getOutputStream(), true);
                request = "{ \"action\": \"login\", \"user\": \""+username+"\", \"password\": \""+password+"\" }";

                bufOut.write( request );
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>Logowanie: bufOut.write( request )");
                bufOut.newLine();
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>Logowanie: bufOut.newLine()");
                bufOut.flush();
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>Logowanie: Wysłanie jsona z użytkownikiem i hasłem; bufOut.flush()");
                //printWriter.println(request);
                //printWriter.flush();
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>JSON wysłany!!!! na " + s);
                InputStream is =s.getInputStream();
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground/Stworzenie InputStreamReader isr>");
                InputStreamReader isr = new InputStreamReader(is);
                    Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground/Stworzenie InputStream is>");
                BufferedReader br1 = new BufferedReader(isr);
                    Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground/Stworzenie BufferedReader br1>");

                //br = new BufferedReader(new InputStreamReader(s.getInputStream())); <------TEGO ODKOMENTOWAC
                //br = new BufferedReader(new InputStreamReader(new BufferedInputStream(s.getInputStream())));
                        //Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>Stworzenie BufferReader");
                //response = br.readLine();
                response = br1.readLine();
                        Log.d(log ,time.getTime()+" <MainActivity/AsyncTask/doInBackground/br.readLine() "+response);
                //br.close(); <----------------- to odkomentować
                br1.close();
                //printWriter.close();
                s.close(); // zamykanie socketu, bo juz jest nieużywany wiecej

                if (response.equals("LOGIN CORRECT"))             //<-- wątek sendRequestTask chyba sie nie zakoncza przy pierwszym wywołaniu if
                {
                    UserInfo userInfo = UserInfo.getInstance();
                    userInfo.login = userEt.getText().toString();
                    contactsActivity();
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Niewłaściwy login lub hasło", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.d("onPostExecute", "onPostExecute");
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

    public void startRegistrationActivity(){
        Intent intent = new Intent (this, RegistrationActivity.class );
        startActivity(intent);
        System.exit(0);
    }



}


