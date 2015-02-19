package com.example.kp.mycommunicator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TalkActivity extends ActionBarActivity {
    private EditText et;
    private  Button bSendMessage;
    private  String message;
    private  String interlocutor;
    private PrintWriter printWriter;
    private String JSONOutputMessage;
    private String JSONInputMessage;
    private static final String HOST = "192.168.0.18";
    private static final int PORT = 7777;
    private BufferedReader br;
    private Bundle extras;
    private ListView listView;
    private ArrayList<String> messegesArray;
    private ArrayAdapter<String> arrayAdapter;              //to nie bedzie potrzebne jak zadziala drugi adapter
    private MessageArrayAdapter messageArrayAdapter;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_talk);

        messegesArray = new ArrayList<String>();
        Intent intent = getIntent();
        extras = intent.getExtras();
//        Log.d(extras.getString("to"), extras.getString("to"));
        et = (EditText)findViewById(R.id.editText3);
        tv = (TextView)findViewById(R.id.textView2);
        tv.setText(MainActivity.interlocutor);
        listView = (ListView) findViewById(R.id.listView2);

        // prosty adapter
        /*arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.contacts_list_view,       //tu byly zmiany
                messegesArray);
        listView.setAdapter(arrayAdapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        */
            //nowy adapter/////////
        messageArrayAdapter = new MessageArrayAdapter(getApplicationContext(),R.layout.activity_single_message );
        listView.setAdapter(messageArrayAdapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
////////////////////////////////////////////////////////////////////////////////////////////
        RequestUnsentMessages requestUnsentMessages = new RequestUnsentMessages();
        requestUnsentMessages.execute();

        //wysyłanie wiadomości
        bSendMessage = (Button)findViewById(R.id.button3);
        bSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = et.getText().toString();
                interlocutor = "12";                //na razie rozmówca na sztywno
                SendMessage sendMessage = new SendMessage();
                sendMessage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                et.setText("");
            }
        });

    }

    private class SendMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Gecko", "SendMessagee");
            try {
                Socket s = new Socket(HOST, PORT);
                printWriter = new PrintWriter(s.getOutputStream(), true);
                String login = UserInfo.getInstance().login;
                JSONOutputMessage = "{ \"action\": \"message\", \"from\": \""+login+"\"," +
                                    " \"to\": \""+extras.getString("to")+"\",  " +
                                    "\"contents\": \""+message+"\" }";

                printWriter.println(JSONOutputMessage);
                Log.d("================<CLIENT>", "JSON "+JSONOutputMessage+" wysłany!!!! na " + s);
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                JSONInputMessage = br.readLine();       //to jest String

                // <----------Można potwierdzić JSONem dostarczenie wiadomosci

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //arrayAdapter.add("Ja: "+message);
            //messageArrayAdapter.chatText.add("Ja: "+message);
            messageArrayAdapter.add(new Message(true, message));
        }

        protected void onPostExecute(Void result) {
            Log.d("onPostExecute", "onPostExecute");
        }
    }

    private class RequestUnsentMessages extends AsyncTask<Object, Integer, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            while(true) {
                Log.d("watek", "watek");
                try {
                    Socket s = new Socket(HOST, PORT);
                    printWriter = new PrintWriter(s.getOutputStream(), true);
                    String login = UserInfo.getInstance().login;
                    JSONOutputMessage = "{ \"action\": \"RequestMessages\", \"from\": \"" + extras.getString("to") + "\", \"to\": \"" + login + "\" }";

                    printWriter.println(JSONOutputMessage);
                    Log.d("================<Gecco>", "JSON Zapytanie o nieodebrane wiadomości " + JSONOutputMessage + " wysłany!!!! na " + s);
                    br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    JSONInputMessage = br.readLine();

                    //if(JSONInputMessage!=null) {
                    Log.d("<Przyszło z serwera>=============", JSONInputMessage);
                    //}
                    publishProgress(1);

                    //Thread.sleep(1000);
                    br.close();
                    s.close();

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onProgressUpdate (Integer... values) {
            /*try {
                JSONObject jsonObject = new JSONObject(JSONInputMessage);
                JSONArray jsonArray = (JSONArray)jsonObject.get("messages");
                if(jsonArray!=null) {                           // wywalic warunek jak bedzie dzialac

//////////////////////////////////////////////////////////////////////
                    //Message mess = new Message();
                    //mess.message = jsonArray.getString(0);
                    //messageArrayAdapter.add(mess);
                    //////////////messageArrayAdapter.add(new Message(true, jsonArray.getString(0)));
////////////////////////////////////////////////////////////////////
                    //arrayAdapter.add(jsonArray.getString(0));   //zmiany w grafice muszą byc przeprowadzone tu, a nie w doInBackground
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            try {
                JSONObject jsonObject = new JSONObject(JSONInputMessage);
                JSONArray jsonArray = (JSONArray)jsonObject.get("messages");
                String message = extras.getString("to")+": "+jsonArray.getString(0);
                //arrayAdapter.add(s);
                messageArrayAdapter.add(new Message(false, message));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Void result) {
            Log.d("onPostExecute", "onPostExecute");
            try {
                JSONObject jsonObject = new JSONObject(JSONInputMessage);
                JSONArray jsonArray = (JSONArray)jsonObject.get("messages");
                String message = extras.getString("to")+": "+jsonArray.getString(0);
                //arrayAdapter.add(s);
                messageArrayAdapter.add(new Message(false, message));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_talk, menu);
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


    /*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_talk, container, false);
            return rootView;
        }
    }
    */
}
