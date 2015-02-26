package com.example.kp.mycommunicator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ContactsActivity extends ActionBarActivity {

    public static ArrayList<String> contacts;
    ListView listView;
    Button bWyszukiwanie;
    private String log = "<Gecco> /ContactsActivity";
    private Bundle extras;
    String login;
    private static final String HOST = "192.168.0.18";
    private static final int PORT = 7777;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Intent intent = getIntent();
        extras = intent.getExtras();
        login = extras.getString("login");
            Log.d(log, "Zalogowany jako (dane z extras)"+login);

        contacts = new ArrayList<String>();
        //dodanie uzytkowników do listy uzytkowników w kliencie/telefonie
        // TU WSTAWIC FUNKCJĘ MAJĄCA PĘTLE FOR ODCZUTUJĄCĄ JSONA Z KONTAKTAMI
        contacts.add("Kylo");
        contacts.add("Roman");
        contacts.add("kamil");
        //contacts.add("karol");
        contacts.add("marzena");

        GetContactsList getContactsList = new GetContactsList();
        getContactsList.execute();

        //wyswietlenie zawartości tablicy contacts
        for(int i = 0; i< contacts.size(); i++)
        {
            Log.d("-------------<CLIENT>Arraylist contacts, pole "+i+" ", contacts.get(i));
        }

        bWyszukiwanie = (Button)findViewById(R.id.button2);
        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.contacts_list_view,
                contacts);
        listView.setAdapter(arrayAdapter);

        //obługa zdarzenia kliknięcia kontaktu z listy kontaków
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(log, "<CLIENT>Kliknięto pozycje : "+contacts.get(position));
                conversationActivity(contacts.get(position));
                MainActivity.interlocutor = contacts.get(position);
            }
        });

        bWyszukiwanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity(login);
            }
        });
    }

    //POPRAWIĆ NAZWY ARRAYLIST CONTACTS I CONTACTSLIST, BO SIE NIE DA KLIKAĆ W LISTĘ KONTAKTÓW PRZEZ RÓZNE NAZWY!!!!!!!
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
                contacts = null;
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.contacts_list_view, contactsList);
            listView.setAdapter(arrayAdapter);

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
}
