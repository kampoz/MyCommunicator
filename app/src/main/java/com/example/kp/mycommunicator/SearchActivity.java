package com.example.kp.mycommunicator;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;



public class SearchActivity extends ActionBarActivity {

    private ImageButton bSearch;
    private EditText editText;
    private ListView listview;
    private static final String HOST = "192.168.0.18";
    private static final int PORT = 7777;
    private ArrayList<String> users = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bSearch = (ImageButton)findViewById(R.id.buttonSearch);
        editText = (EditText)findViewById(R.id.editText7);
        listview = (ListView)findViewById(R.id.listView3);

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestUsersSearch requestUsersSearch = new RequestUsersSearch();
                requestUsersSearch.execute();
            }
        });

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
                String searchResponse = br.readLine();
                JSONObject jsonSearchResponse = new JSONObject(searchResponse);

                Log.d("------------< G E C C O  gecko gecco>  JSON-Odpowiedź z wynikami wyszukiwania:", searchResponse );

                //rozpakowac jsona wg pól
                //dodać wyniki, które mają byc w formie tablicy do arraya
               //jsonObject.put("users", searchResults);
                printWriter.close();

                //String serverAction = (String)jsonObject.get("serverAction");
                //JSONArray
                //if (serverAction.equals("searchResults")){
                    JSONArray JSONresults = (JSONArray)jsonSearchResponse.get("foundLogins");
                    ArrayList<String> arrayResults = new ArrayList<String>();

                    if (JSONresults != null) {
                        for (int i=0;i<JSONresults.length();i++){
                            arrayResults.add(JSONresults.get(i).toString());
                        }
                    }
                    Log.d("------------< G E C C O  gecko gecco> Arraylista arrayResults", arrayResults.toString());
                    //String foundLogin = (String) jsonObject.get("fou");           //WYPAKOWAC TABLICE JSONARRAY
                //}
                //else{
                //    Log.d("------------< G E C C O  gecko gecco> Arraylista arrayResults", "Nie przetworzona jsonarray");
                //}


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
