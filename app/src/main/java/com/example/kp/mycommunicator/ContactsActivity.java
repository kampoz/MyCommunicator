package com.example.kp.mycommunicator;

import android.content.Intent;
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
import java.util.ArrayList;


public class ContactsActivity extends ActionBarActivity {

    public static ArrayList<String> contacts;
    ListView listView;
    Button bWyszukiwanie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contacts = new ArrayList<String>();
        //dodanie uzytkowników do listy uzytkowników w kliencie/telefonie
        // TU WSTAWIC FUNKCJĘ MAJĄCA PĘTLE FOR ODCZUTUJĄCĄ JSONA Z KONTAKTAMI
        contacts.add("Kylo");
        contacts.add("Roman");
        contacts.add("kamil");

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
                Log.d("<CLIENT>Kliknięto pozycje : ", contacts.get(position));
                conversationActivity(contacts.get(position));
                MainActivity.interlocutor = contacts.get(position);
            }
        });

        bWyszukiwanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity();
            }
        });


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

    public void startSearchActivity(){
        Intent intent = new Intent (this, SearchActivity.class );
        startActivity(intent);
        //System.exit(0);
    }
}
