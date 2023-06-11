package com.example.apicoountrylist;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private TextView countryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryTextView = findViewById(R.id.countryTextView);


        //run when activity launches
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String countriesJsonString = fetchDataFromApi();
                // fetching data from api in json spring
                parseAndDisplayData(countriesJsonString);
                //displaying that data
            }
        });
        //starting the above process or thread
        thread.start();
    }

    private String fetchDataFromApi() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String countriesJsonString = null;
        // setting values null

        try {
            URL url = new URL("https://restcountries.com/v3.1/independent?status=true&fields=languages,capital,name,area,population,currencies");
            //our api
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            // api method
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            // if no data comping through api
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            //loop to read all data row by row pr line by line
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
                //reading data and entering next line
            }

            if (builder.length() == 0) {
                return null;
                //if null all data has been read
            }

            countriesJsonString = builder.toString();
            //countries add to this variable
        } catch (IOException e) {
            Log.e("MainActivity", "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
                //after getting data closing connections
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("MainActivity", "Error closing stream", e);
                }
            }
        }

        return countriesJsonString;
        // returning all countries in json_string_format
    }

    private void parseAndDisplayData(String countriesJsonString) {
        if (countriesJsonString != null) {
            //if apis get data
            try {
                JSONArray countriesArray = new JSONArray(countriesJsonString);

                //loop for all languages
                for (int i = 0; i < countriesArray.length(); i++) {
                    JSONObject countryObject = countriesArray.getJSONObject(i);

                    //creating a json object


                    //getting values from json

                    String name = countryObject.getJSONObject("name").getString("common");
                    String capital = "";
                    JSONArray capitalArray = countryObject.optJSONArray("capital");
                    if (capitalArray != null && capitalArray.length() > 0) {
                        capital = capitalArray.optString(0, "");
                    }

                    int area = countryObject.getInt("area");
                    int population = countryObject.getInt("population");
                    JSONObject languagesObject = countryObject.getJSONObject("languages");

                    StringBuilder languages = new StringBuilder();
                    Iterator<String> keysIterator = languagesObject.keys();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        Object languageObject = languagesObject.get(key);
                        if (languageObject instanceof String) {
                            String languageName = (String) languageObject;
                            languages.append(languageName).append(", ");
                        }
                    }

                    JSONObject currenciesObject = countryObject.optJSONObject("currencies");

                    StringBuilder currencies = new StringBuilder();

                    if (currenciesObject != null) {
                        Iterator<String> keysIteratorr = currenciesObject.keys();
                        while (keysIteratorr.hasNext()) {
                            String key = keysIteratorr.next();
                            JSONObject currencyObject = currenciesObject.getJSONObject(key);
                            String currencyName = currencyObject.getString("name");
                            currencies.append(currencyName).append(", ");
                        }
                    }

                    if (currencies.length() > 0) {
                        currencies.delete(currencies.length() - 2, currencies.length() - 1);
                    }




                    if (languages.length() > 0) {
                        languages.delete(languages.length() - 2, languages.length() - 1);
                    }


                    //creating string
                    String countryInfo = "Country: " + name +
                            "\nCapital: " + capital +
                            "\nArea: " + area +
                            "\nPopulation: " + population +
                            "\nLanguages: " + languages.toString() +
                            "\nCurrency: " + currencies.toString() +
                            "\n---------------------------\n";


                    //appending strings
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countryTextView.append(countryInfo);
                        }
                    });
                }
            } catch (JSONException e) {
                Log.e("MainActivity", "Error parsing JSON", e);
                //####Joker Testing
            }
        }
    }
}


