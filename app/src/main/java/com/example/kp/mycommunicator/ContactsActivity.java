package com.example.kp.mycommunicator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ContactsActivity extends ActionBarActivity {

    ListView listView;
    Button bWyszukiwanie;
    private String log = "<Gecco> /ContactsActivity";
    private Bundle extras;
    String login;
    private static final String HOST = "192.168.0.18";
    private static final int PORT = 7777;
    String Input = " ";
    private List<Contact> contactsList = new ArrayList<>();
    private ContactsArrayAdapter contactsArrayAdapter;
    private Executor executor= Executors.newScheduledThreadPool(3);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsArrayAdapter = new ContactsArrayAdapter(this, contactsList);

        Intent intent = getIntent();
        extras = intent.getExtras();
        login = extras.getString("login");
            Log.d(log, "Zalogowany jako (dane z extras)"+login);

        //GetContactsList getContactsList = new GetContactsList();              //Stara klasa z kontaktsmi
        //getContactsList.execute();

        GetContacts getContacts = new GetContacts();                            //nowa klasa z kontaktami i nowym adapterem
        getContacts.executeOnExecutor(executor);

        //Klasa tworzy wątek ustaw. status usera na online na serwerze, wysyłapotwierdzenie online co kilka sekund
        OnlineStatus onlineStatus = new OnlineStatus();
        onlineStatus.execute();

        bWyszukiwanie = (Button)findViewById(R.id.button2);
        listView = (ListView) findViewById(R.id.listView);
        Toast.makeText(getApplicationContext(), "Wczytywanie kontaktów...", Toast.LENGTH_LONG).show();

        listView.setAdapter(contactsArrayAdapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        /*
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(           //stary prosty adapter
                this,
                R.layout.contacts_list_view,
                contacts);
        listView.setAdapter(arrayAdapter);
        */

        //obługa zdarzenia kliknięcia kontaktu z listy kontaków
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(log, "<CLIENT>Kliknięto pozycje : "+contactsList.get(position));
                conversationActivity(contactsList.get(position).name);
                MainActivity.interlocutor = contactsList.get(position).name;
            }
        });


        bWyszukiwanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity(login);
            }
        });
    }

    private class GetContactsList extends AsyncTask<Void, Void, Void> {
        ArrayList<String> contactsList;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Socket s = new Socket(HOST, PORT);
                JSONObject jOut = new JSONObject();
                jOut.put("action", "getContactsList");
                jOut.put("user", login);
                String request = jOut.toString();
                PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
                printWriter.println(request);
                printWriter.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String Input = br.readLine();
                JSONObject jInput = new JSONObject(Input);
                String serverAction = (String)jInput.get("serverAction");
                if (serverAction.equals("sendContactsList")){
                    JSONArray jContactsList = (JSONArray) jInput.get("contactsList");
                    contactsList = new ArrayList<String>();
                    //zamiana jsonarray na ArrayList<String>
                    if (jContactsList != null) {
                        for (int i=0;i<jContactsList.length();i++){
                            contactsList.add(jContactsList.get(i).toString());
                        }
                    }
                    Log.d(log, " Lista kontaktów contactsList: "+contactsList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(Void result) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.contacts_list_view, contactsList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.d(log, "<CLIENT>Kliknięto pozycje : "+contactsList.get(position));
                    conversationActivity(contactsList.get(position));
                    MainActivity.interlocutor = contactsList.get(position);
                }
            });
        }
    }

    private class GetContacts extends AsyncTask<Object, Integer, Void>  {         //nowy wątek do pobierania konataktów

        boolean running = true;
        Time time = new Time();

        @Override
        protected Void doInBackground(Object... params) {


            try {

                while (true)
                {
                    Socket s = new Socket(HOST, PORT);
                    JSONObject jOut = new JSONObject();
                    jOut.put("action", "actualizeContacts");
                    jOut.put("user", login);
                    String request = jOut.toString();
                    PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
                        Log.d(log, time.getTime() + " /AsyncTask GetContacts/doInBackground " + jOut.toString());
                    printWriter.println(request);
                    printWriter.flush();
                        Log.d(log, time.getTime() + " /AsyncTask GetContacts/doInBackground/PrintWriter ");
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        Log.d(log, time.getTime() + " /AsyncTask GetContacts/Bufferreader ");
                    Input = br.readLine();
                        Log.d(log, time.getTime() + " /AsyncTask GetContacts/doInBackground/ Input " + Input);
                    publishProgress(1);
                    Thread.sleep(3000);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
            }

        /*
        protected void onPostExecute(Void result) {
            boolean online =  false;
            boolean unsedMessage = false;
            //String contactName = "";
            String contactName;

            try {
                JSONObject jInput = new JSONObject(Input);
                String serverAction = (String)jInput.get("serverAction");

                JSONArray jContactsArray = (JSONArray)jInput.get("contacts");

                Log.d("DDDDDDługość jContactsArray", new Integer(jContactsArray.length()).toString());      //jest 5
                for (int i=0; i<jContactsArray.length(); i++ )
                {

                    JSONObject jRow = (JSONObject) jContactsArray.get(i);       Log.d(log, time.getTime()+" jRow "+jRow.toString());


                    contactName = (String) jRow.get("name");
                    String onlineInfo = (String) jRow.get("online");        //wyjmuje stringa "true" lub "false"
                    String unsendMessagesInfo = (String) jRow.get("unsendMessages");


                    if(onlineInfo.equals("true")){
                        online = true;
                    }else if(onlineInfo.equals("false")){
                        online = false;
                    }

                    if(unsendMessagesInfo.equals("true")){
                        unsedMessage = true;
                    }else if(unsendMessagesInfo.equals("false")){
                        unsedMessage = false;
                    }


                    Log.d(log, time.getTime()+" "+i+") przejscie petli for, przed add"+contactsList.toString());
                    contactsArrayAdapter.add(new Contact(online, contactName, unsedMessage));

                    Log.d(log, time.getTime()+" "+i+") przejscie petli for, po add"+contactsList.toString());


                }

                Log.d(log, time.getTime()+" Cała tablica dodana do adaptera: "+contactsList.toString());


            }catch (JSONException e) {
                e.printStackTrace();
            }

            //super.onPostExecute(result);


        }
        */

        protected void onProgressUpdate(Integer... values) {
            boolean online =  false;
            boolean unsedMessage = false;
            //String contactName = "";
            String contactName;

            try {
                JSONObject jInput = new JSONObject(Input);
                String serverAction = (String)jInput.get("serverAction");

                JSONArray jContactsArray = (JSONArray)jInput.get("contacts");

                Log.d("DDDDDDługość jContactsArray", new Integer(jContactsArray.length()).toString());      //jest 5
                contactsArrayAdapter.clear();
                for (int i=0; i<jContactsArray.length(); i++ )
                {

                    JSONObject jRow = (JSONObject) jContactsArray.get(i);       Log.d(log, time.getTime()+" jRow "+jRow.toString());


                    contactName = (String) jRow.get("name");
                    String onlineInfo = (String) jRow.get("online");        //wyjmuje stringa "true" lub "false"
                    String unsendMessagesInfo = (String) jRow.get("unsendMessages");


                    if(onlineInfo.equals("true")){
                        online = true;
                    }else if(onlineInfo.equals("false")){
                        online = false;
                    }

                    if(unsendMessagesInfo.equals("true")){
                        unsedMessage = true;
                    }else if(unsendMessagesInfo.equals("false")){
                        unsedMessage = false;
                    }
                    contactsArrayAdapter.add(new Contact(online, contactName, unsedMessage));
                        Log.d(log, time.getTime()+" "+i+") GetContacts/onProgressUpdate  przejscie petli for, po add"+contactsList.toString());
                }

                Log.d(log, time.getTime()+" GetContacts/onProgressUpdate  Cała tablica dodana do adaptera: "+contactsList.toString());
            }catch (JSONException e) {
                e.printStackTrace();
            }
           //super.onPostExecute(result);
        }
    }


    private class OnlineStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            Time time = new Time();
            try {
                while(true)
                {
                    Socket s = new Socket(HOST, PORT);
                    JSONObject jOut = new JSONObject();
                    jOut.put("action", "onlineStatus");
                    jOut.put("user", login);
                    String request = jOut.toString();

                    PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
                    printWriter.println(request);
                    printWriter.flush();
                    Log.d(log,time.getTime()+ " AsyncTask/OnlineStatus. JSON wysłany "+request);
                    Thread.sleep(5000);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
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

    public void conversationActivity (String to){
        Intent intent = new Intent (this, TalkActivity.class );
        intent.putExtra("to", to );
        startActivity(intent);
    }

    public void startSearchActivity(String login){
        Intent intent = new Intent (this, SearchActivity.class );
        intent.putExtra("login", login );
        startActivity(intent);
        //System.exit(0);
    }

    @Override
    public void onBackPressed() {

    }
}
