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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class Registration extends ActionBarActivity {

    private TextView tvLogin;
    private EditText etSetLogin;
    private EditText etSetPassword;
    private EditText etConfirmPassword;
    private Button bRegistration;
    private String login;
    private String password;
    private String confirmpassword;
    private static final String HOST = "192.168.0.18";
    private static final int PORT = 7777;
    private PrintWriter printWriter;
    private String register = "registration";
    private String request = "";
    private BufferedReader br;
    private String response;
    private String JSONInputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        tvLogin = (TextView) findViewById(R.id.textView3);
        etSetLogin = (EditText) findViewById(R.id.editText4);
        etSetPassword = (EditText) findViewById(R.id.editText5);
        etConfirmPassword = (EditText) findViewById(R.id.editText6);
        bRegistration = (Button) findViewById(R.id.button5);

        bRegistration.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                 login = etSetLogin.getText().toString();
                 password = etSetPassword.getText().toString();
                 confirmpassword = etConfirmPassword.getText().toString();
                 if(!password.equals(confirmpassword))
                 {
                     etSetPassword.setText("Podaj hasło");
                     etConfirmPassword.setText("Potwierdź hasło");
                     Toast.makeText(getApplicationContext(),"Hasła są rózne", Toast.LENGTH_SHORT).show(); // tu moze byc bład!!
                 }
                else
                 {
                //tu odpalić wątek obslugujący komunikacje przy rejestracji
                    RegisterThread registerThread = new RegisterThread();
                    registerThread.execute();
                 }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class RegisterThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Socket s = null;
            try {
                s = new Socket(HOST, PORT);
                printWriter = new PrintWriter(s.getOutputStream(), true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", register);
                jsonObject.put("login", login);
                jsonObject.put("password", password);
                request = jsonObject.toString();          //toJSONString();
                System.out.println("request = "+request);
                printWriter = new PrintWriter(s.getOutputStream(), true);
                printWriter.println(request);
                printWriter.flush();
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                JSONInputMessage = br.readLine();
                jsonObject = new JSONObject(JSONInputMessage);
                response = (String) jsonObject.get("response");
                Log.d(">><CLIENT> Odpowiedź serwera na próbę rejestracji", response);
                br.close();
                //printWriter.close();
                s.close();
                printWriter.close();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.equals("registration_succeeded")){             // nie wrzuca jsonów na toast
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Rejestracja zakończona sukcesem!", Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startMainActivity();
            }
            else if (response.equals("registration_failed")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Login jest zajęty", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Błąd interpretacji JSONa", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

    public void startMainActivity(){
        Intent intent = new Intent (this, MainActivity.class );
        startActivity(intent);
    }
}
