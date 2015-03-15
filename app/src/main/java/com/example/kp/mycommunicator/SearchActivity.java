package com.example.kp.mycommunicator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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



public class SearchActivity extends ActionBarActivity {

    private ImageButton bSearch;
    private EditText editText;
     ListView lvSearchResults;
    private static final String HOST = "192.168.0.18";
    //private static final String HOST = "localhost";
    private static final int PORT = 7777;
    private ArrayList<String> users = new ArrayList<String>();
    public static ArrayList<String> arrayResults = new ArrayList<String>();
    private String searchResponse;
    private ArrayAdapter arrayAdapter;
    Context context = SearchActivity.this;
    String log = " <Gecco> SearchActivity";
    private Bundle extras;
    String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Time time = new Time();

        Intent intent = getIntent();
        extras = intent.getExtras();
        login = extras.getString("login");

        bSearch = (ImageButton)findViewById(R.id.buttonSearch);
        editText = (EditText)findViewById(R.id.editText7);
        lvSearchResults = (ListView)findViewById(R.id.listView3);

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Wyszukiwanie...", Toast.LENGTH_SHORT).show();
                RequestUsersSearch requestUsersSearch = new RequestUsersSearch();
                requestUsersSearch.execute();

                /*
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context,
                        R.layout.contacts_list_view,
                        arrayResults);
                lvSearchResults.setAdapter(arrayAdapter);
                */
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(log, "Kliknięcie elemntu z listy lvSearchResults: "+ arrayResults.get(position));
                String user = arrayResults.get(position); //<--- to przekazac do extras i potem do CustomDialogClass
                CustomDialogClass cdd = new CustomDialogClass(SearchActivity.this, user, login);

                cdd.show();
                Log.d(log, "Otwarcie okna dialogowego: Dodać kontakt?");
                // AddUserActivity(arrayResults.get(position)); <------- odblokować potem??
                //MainActivity.interlocutor = arrayResults.get(position);
            }
        });

////////////////////////////////////////////////////////////////////////////////////////////////////////

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    private class RequestUsersSearch extends AsyncTask<Object, Integer, Void> {
        @Override
        protected Void doInBackground(Object... params) {

            arrayResults = new ArrayList<String>();

            try {
                Socket s = new Socket(HOST, PORT);
                        // BufferedWriter bufOut = new BufferedWriter( new OutputStreamWriter( s.getOutputStream()));
                PrintWriter printWriter;

                JSONObject jsonObject = new JSONObject();
                JSONArray searchResults = new JSONArray();
                jsonObject.put("action","SearchResults");
                String keyWord = editText.getText().toString();
                jsonObject.put("keyWord",keyWord);
                Log.d("--------------< G E C C O  gecko gecco>  JSON-Request o wyszukiwanie", "JSON" + jsonObject.toString() );

                printWriter = new PrintWriter(s.getOutputStream(), true);
                printWriter.println(jsonObject.toString()); // write the request to output stream
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                searchResponse = br.readLine();

                Log.d("------------< G E C C O  gecko gecco>  JSON-Odpowiedź z wynikami wyszukiwania:", searchResponse );

                printWriter.close();

                JSONObject jsonSearchResponse = new JSONObject(searchResponse);
                JSONArray JSONresults = (JSONArray)jsonSearchResponse.get("foundLogins");
                //arrayResults = new ArrayList<String>();

                if (JSONresults != null) {
                    for (int i=0;i<JSONresults.length();i++){
                        arrayResults.add(JSONresults.get(i).toString());
                    }
                }
                Log.d("------------< G E C C O  gecko gecco> Arraylista arrayResults", arrayResults.toString());
                if (arrayResults.size()==0){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Nic nie znaleziono", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Koniec wyszukiwania", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.contacts_list_view, arrayResults);
            lvSearchResults.setAdapter(arrayAdapter);
            //arrayResults = null;
        }
}

    //to dodane 15/03/2015
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
