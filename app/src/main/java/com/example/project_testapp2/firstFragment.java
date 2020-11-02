package com.example.project_testapp2;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link firstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class firstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView cases_today;
    private TextView deaths_today;
    private TextView active_today;
    private TextView recovered_today;

    private String JsonText;

    public firstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment firstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static firstFragment newInstance(String param1, String param2) {
        firstFragment fragment = new firstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cases_today = (TextView) findViewById(R.id.active_today_label);

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View root =  inflater.inflate(R.layout.fragment_first, container, false);
        final TextView textView = root.findViewById(R.id.active_today_label);
        textView.setClickable(true);
        cases_today = root.findViewById(R.id.cases_today_tv);
        deaths_today = root.findViewById(R.id.deaths_today_tv);
        active_today = root.findViewById(R.id.active_today_tv);
        recovered_today = root.findViewById(R.id.recovered_today_tv);
        String today = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(LocalDateTime.now().minusDays(1));
        String yesterday = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(LocalDateTime.now().minusDays(3));
        Log.v("Today's date", today + " ------ " + yesterday);

        //"https://api.covid19api.com/country/united-arab-emirates?from=2020-10-31T00:00:00Z&to=2020-10-31T23:59:59Z"

        new JsonTask().execute(MessageFormat.format("https://api.covid19api.com/country/united-arab-emirates?from={0}&to={1}", yesterday, today));

        return root;
    }
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            //pd = new ProgressDialog(MainActivity.this);
            //pd.setMessage("Please wait");
            //pd.setCancelable(false);
            //pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }
                return buffer.toString();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /*if (pd.isShowing()){
                pd.dismiss();
            }*/
            JsonText = result;
            Log.v("Result JSON", result);
            try {
                JSONArray json_array = new JSONArray(result);
                for(int i =0; i<json_array.length(); i++){
                    Log.v("Json array item: ", json_array.get(i).toString());
                }
                json_array.getJSONObject(0); //Yesterday
                json_array.getJSONObject(1); //Today

                //Cases today = Total confirmed as per today - total confirmed as per yesterday
                cases_today.setText(String.valueOf(Integer.parseInt(json_array.getJSONObject(1).getString("Confirmed"))
                        - Integer.parseInt(json_array.getJSONObject(0).getString("Confirmed"))
                ));

                //Deaths today = Total deaths as per today - total deaths as per yesterday
                deaths_today.setText(String.valueOf(Integer.parseInt(json_array.getJSONObject(1).getString("Deaths"))
                                - Integer.parseInt(json_array.getJSONObject(0).getString("Deaths"))));

                //Recovered today = Total recovered as per today - total recovered as per yesterday
                recovered_today.setText(String.valueOf(Integer.parseInt(json_array.getJSONObject(1).getString("Recovered"))
                        - Integer.parseInt(json_array.getJSONObject(0).getString("Recovered"))));

                active_today.setText(json_array.getJSONObject(1).getString("Active"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.v("Json Body:", JsonText);
        }
    }

}