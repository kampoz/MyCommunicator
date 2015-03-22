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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends ActionBarActivity {
    private TextView tv;
    private Button bLogin;
    private Button bRegistration;
    private EditText userEt;
    private EditText passwordEt;
    private String request;

    private static final String HOST = "192.168.0.18";
    //private static final String HOST = "kaseka.strangled.net";
    //private static final String HOST = "89.79.118.161";

    private static final int PORT = 7777;
    String username = null;
    String password = null;
    String response = "Incorrect";
    public static String interlocutor;
    //private Executor executor = Executors.newScheduledThreadPool(2);
    private String log = "<kl. MainActivity/";

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

        //CheckIfServerIsOnlie checkIfServerIsOnlie = new CheckIfServerIsOnlie();
        //checkIfServerIsOnlie.execute();

        bLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Time time = new Time();
                Toast.makeText(getApplicationContext(),"Trwa logowanie...", Toast.LENGTH_SHORT).show();
                username = userEt.getText().toString();
                password = passwordEt.getText().toString();
                CheckLoginThread checkLoginThread = new CheckLoginThread();
                    //Log.d(log,time.getTime()+" <MainActivity><bLogin/OnClick> Nowy obiekt wątku CheckLoginThread");
                checkLoginThread.execute();
                //checkLoginThread.executeOnExecutor(executor);
                    Log.d(log,time.getTime()+" <MainActivity><bLogin/OnClick> checkLoginThread.execute();\n" +" ");
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
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground> Logowanie: new Socket(HOST, PORT)");
                BufferedWriter bufOut = new BufferedWriter( new OutputStreamWriter( s.getOutputStream()));
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>Logowanie: Stworzenie BufferWriter");
                //printWriter = new PrintWriter(s.getOutputStream(), true);
                request = "{ \"action\": \"login\", \"user\": \""+username+"\", \"password\": \""+password+"\" }";
                bufOut.write( request );
                bufOut.newLine();
                bufOut.flush();
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground>Logowanie: Wysłanie jsona z użytkownikiem i hasłem; bufOut.flush()");
                //printWriter.println(request);
                //printWriter.flush();
                InputStream is =s.getInputStream();
                        //Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground/Stworzenie InputStreamReader isr>");
                InputStreamReader isr = new InputStreamReader(is);
                        //Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground/Stworzenie InputStream is>");
                BufferedReader br1 = new BufferedReader(isr);
                        Log.d(log,time.getTime()+" <MainActivity/AsyncTask/doInBackground/Stworzenie BufferedReader br1>\n" +
                                "--------------------------------------------------------------------------------------------------");
                response = br1.readLine();
                        Log.d(log ,time.getTime()+" <MainActivity/AsyncTask/doInBackground/br.readLine() "+response);
                br1.close();
                s.close(); // zamykanie socketu, bo juz jest nieużywany wiecej

                if (response.equals("LOGIN CORRECT"))
                {
                    UserInfo userInfo = UserInfo.getInstance();
                    userInfo.login = userEt.getText().toString();
                    startContactsActivity(username);
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


    /*
    private class CheckIfServerIsOnline extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            while (true) {
                Date dzien = new Date();
                if (exists("http://89.68.58.161:7777")) {                //<----wstawić ip
                    Toast.makeText(getApplicationContext(),"Wykryto serwer Gecco", Toast.LENGTH_LONG).show();
                    Log.d(log, "Działa "+dzien);            //tekst na konsoli
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Brak połączenia z serwerem", Toast.LENGTH_LONG).show();
                    Log.d(log, "Serwer nie działa!");
                }
                try{
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public boolean exists(String URLName) {          //metoda sprawdza czy dany IP jest online

            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) new URL(URLName).openConnection();
                con.setRequestMethod("HEAD");
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d(log+"/(AsyncTask)CheckIfServerIsOnlie", "Błąd IOException");
            }
            return false;
        }

    } */

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

    public void startContactsActivity(String username){
        Intent intent = new Intent (this, ContactsActivity.class );
        intent.putExtra("login", username );
        startActivity(intent);

    }

    public void startRegistrationActivity(){
        Intent intent = new Intent (this, RegistrationActivity.class );
        startActivity(intent);
        System.exit(0);
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        OnlineStatusOff onlineStatusOff = new OnlineStatusOff();
        onlineStatusOff.executeOnExecutor(executor);

    }
    */

    /*
    private class OnlineStatusOff extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Time time = new Time();
            Log.d(log,time.getTime()+ " AsyncTask/OnlineStatusOff. Start metody");
            try {
                    Socket s = new Socket(HOST, PORT);
                    JSONObject jOut = new JSONObject();
                    jOut.put("action", "onlineStatusOff");
                    jOut.put("user", UserInfo.getInstance());
                    String request = jOut.toString();

                    PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
                    printWriter.println(request);
                    printWriter.flush();
                    Log.d(log,time.getTime()+ " AsyncTask/OnlineStatusOff . JSON wysłany "+request);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    */
}


