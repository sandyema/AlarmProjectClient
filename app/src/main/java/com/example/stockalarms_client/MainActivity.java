package com.example.stockalarms_client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {


    private ArrayList<Alarm> alarms = new ArrayList<>();

    private static String IDUSER;
    private static final int RESULT_KEY = 13;
    String idUser;
    String url="http://192.168.100.26:8080/StockAlarms";
    CustomAdapter customAdapter;

    ServiceUser serviceUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idUser = bundle.getString(IDUSER);
        }

        showAlarms();
        hideSoftKeyboard();

        serviceUser = ServiceFactory.getUserService();

        ImageView addImage = (ImageView) findViewById(R.id.addImage);

        ListView myListView = (ListView) findViewById(R.id.myListView);
        customAdapter = new CustomAdapter();
        myListView.setAdapter(customAdapter);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                intent.putExtra(IDUSER, idUser);
                startActivityForResult(intent, RESULT_KEY);
                Log.i("Go to add", "succes");
            }
        });

    }

    private void showAlarms() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url+"/getAllAlarms/"+idUser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                alarms.clear();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Alarms");

                    for (int i = 0; jsonArray.length() > i; i++) {
                        final JSONObject object = jsonArray.getJSONObject(i);

                        Alarm a = new Alarm();
                        a.setId(object.getInt("id"));
                        a.setAlarm_name(object.getString("alarm_name"));
                        a.setInitial_price(object.getDouble("initial_price"));
                        a.setWanted_percent(object.getDouble("wanted_percent"));
                        a.setCurrent_price(object.getDouble("current_price"));
                        a.setOver_price(object.getInt("over_price"));
                        a.setLess_price(object.getInt("less_price"));
                        a.setActive(object.getInt("active"));

                        alarms.add(a);
                        customAdapter.notifyDataSetChanged();
                        System.out.println("---UPDATE LIST----"+alarms);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return alarms.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {


            view=getLayoutInflater().inflate(R.layout.model,null);

            EditText alarmName=(EditText) view.findViewById(R.id.alarmName);
            TextView initialPrice=(TextView)view.findViewById(R.id.initialPrice);
            TextView currentPrice=(TextView)view.findViewById(R.id.currentPrice);
            EditText wantedPercent=(EditText) view.findViewById(R.id.wantedPercent);
            CheckBox checkedTextOver=(CheckBox) view.findViewById(R.id.checkedTextOver);
            CheckBox checkedTextLess=(CheckBox) view.findViewById(R.id.checkedTextLess);
            CircularProgressButton editButton=(CircularProgressButton) view.findViewById(R.id.editButton);
            CircularProgressButton deleteButton=(CircularProgressButton) view.findViewById(R.id.deleteButton);

            alarmName.setText(alarms.get(position).getAlarm_name());
            initialPrice.setText(Double.toString(alarms.get(position).getInitial_price()));
            currentPrice.setText(Double.toString(alarms.get(position).getCurrent_price()));
            wantedPercent.setText(Double.toString(alarms.get(position).getWanted_percent()));

            if(alarms.get(position).getOver_price().equals(1))
            {
                checkedTextOver.setChecked(true);
                checkedTextLess.setChecked(false);

            }
            else
            {
                checkedTextOver.setChecked(false);
                checkedTextLess.setChecked(true);
            }

            Integer id=alarms.get(position).getId();

            if(checkedTextLess.isChecked())
                checkedTextOver.setChecked(false);
            if(checkedTextOver.isChecked())
                checkedTextLess.setChecked(false);

            if(alarms.get(position).getActive().equals(0))
                editButton.setVisibility(View.GONE);

            editButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    HashMap<String, String> alarmsMap = new HashMap<String, String>();
                    alarmsMap.put("id", String.valueOf(id));
                    alarmsMap.put("alarm_name", String.valueOf(alarmName.getText()));
                    alarmsMap.put("wanted_percent", String.valueOf(wantedPercent.getText()));
                    if(checkedTextOver.isChecked()) {
                        alarmsMap.put("over_price", String.valueOf(1));
                        alarmsMap.put("less_price", String.valueOf(0));

                    }

                    else
                    {
                        alarmsMap.put("over_price", String.valueOf(0));
                        alarmsMap.put("less_price", String.valueOf(1));
                    }

                    serviceUser.editAlarm(alarmsMap).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            if(response.body().getResponse().equals("true")) {

                                System.out.println("Update alarm ");
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Alert");
                                builder.setMessage("Something went wrong with editing");
                                builder.setNegativeButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                                dialog.show();
                                System.out.println("Error at request for adding the car");

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    HashMap<String, String> alarmsMap = new HashMap<String, String>();
                    alarmsMap.put("id_alarm", String.valueOf(id));
                    alarmsMap.put("id_user", idUser);
                    serviceUser.deleteAlarm(alarmsMap).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            if(response.body().getResponse().equals("true")) {

                                System.out.println("Deleted alarm ");
                                setAlarms(id);
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Alert");
                                builder.setMessage("Can not delete the alarm");
                                builder.setNegativeButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                                dialog.show();
                                System.out.println("Error at request for deleting the alarm");

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }
            });


            return view;
        }

        private String getPriceStock(String stockName) throws IOException {
            String price="not found";
            Double priceInDouble=0.0;
            URL url=new URL("https://www.google.com/finance/quote/"+stockName+":NYSE?ei=ga4QWNiFOobBe4LShnAF");
            URLConnection urlConnection=url.openConnection();
            InputStreamReader inStream=new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader=new BufferedReader(inStream);
            String line=bufferedReader.readLine();
            while(line!=null)
            {
                if(line.contains("[\""+stockName+"\",")){
                    int target=line.indexOf(("[\""+stockName+"\","));
                    int deci=line.indexOf(".",target);
                    int start=deci;
                    while(line.charAt(start)!='\"'){
                        start--;
                    }
                    price= line.substring(start+3,deci+3);
                }
                line=bufferedReader.readLine();
            }
            priceInDouble=Double.valueOf(price);
            System.out.println("PRICE:"+priceInDouble);

            return price;
        }
    }

    public void setAlarms(Integer s) {
        showAlarms();
        Alarm alarm_to_remove = null;
        System.out.println(" s = " + s + " - Alarms === " + alarms.toString());
        for (Alarm alarm : this.alarms) {
            if (alarm.getId().equals(s)) {
                alarm_to_remove = alarm;
            }
        }
        alarms.remove(alarm_to_remove);
        customAdapter.notifyDataSetChanged();
    }
}
