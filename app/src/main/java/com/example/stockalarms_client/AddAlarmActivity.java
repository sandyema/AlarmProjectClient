package com.example.stockalarms_client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;

public class AddAlarmActivity extends AppCompatActivity {

    private static String IDUSER;
    private static final int RESULT_KEY = 13;
    String idUser;

    EditText editAlarmNAme,editWantedPercente;
    CheckBox checkedTextOver,checkedTextLess;
    CircularProgressButton addButton;
    ServiceUser serviceUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        addButton=(CircularProgressButton)findViewById(R.id.addButton);
        editAlarmNAme=(EditText)findViewById(R.id.editAlarmNAme);
        editWantedPercente=(EditText)findViewById(R.id.editWantedPercente);
        checkedTextLess=(CheckBox)findViewById(R.id.checkedTextLess);
        checkedTextOver=(CheckBox)findViewById(R.id.checkedTextOver);

        serviceUser=ServiceFactory.getUserService();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idUser = bundle.getString(IDUSER);
            System.out.println("---------ID----" + idUser);
        }

        if(checkedTextLess.isChecked())
            checkedTextOver.setChecked(false);
        if(checkedTextOver.isChecked())
            checkedTextLess.setChecked(false);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String,String> alarm;
                alarm = handleAdd();
                serviceUser.addAlarm(alarm).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                        if(response.body().getResponse().equals("true")) {
                            Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
                            intent.putExtra(IDUSER, idUser);
                            startActivityForResult(intent, RESULT_KEY);
                            Log.i("Go to add", "succes");
                            SharedPreferences sharedpreferences = getSharedPreferences("myprefs",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString("items", alarm.toString());
                            editor.commit();
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddAlarmActivity.this);
                            builder.setTitle("Alert");
                            builder.setMessage("Something went wrong.");
                            builder.setNegativeButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                            dialog.show();
                            System.out.println("Error at request for adding the alarm");

                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });
            }
        });
    }

    private HashMap<String,String> handleAdd() {
        String alarmNAme = editAlarmNAme.getText().toString();
        String wantedPercente= editWantedPercente.getText().toString();
        String over,less;
        if(checkedTextLess.isChecked())
        {
            less = String.valueOf(1);
            over= String.valueOf(0);
        }


        else
            {
            less = String.valueOf(0);
            over= String.valueOf(1);
        }

        String errors = "";

        if(alarmNAme.equals("")){
            errors += "Introduceti nume\n";
        }
        if(wantedPercente.equals("")){
            errors += "Introduceti facultate\n";
        }

        if(!errors.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("EROARE");
            builder.setMessage(errors);
            builder.setNegativeButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();

            return null;
        }


        RequestQueue queue = Volley.newRequestQueue(AddAlarmActivity.this);


        HashMap<String, String> alarm = new HashMap<String, String>();
        alarm.put("idUser",idUser);
        alarm.put("alarmName", alarmNAme);
        alarm.put("wantedPercente", wantedPercente);
        alarm.put("over", over);
        alarm.put("less",less );

        System.out.println(alarm);
        return alarm;

    }


    public void goBack(View view) {
        Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
        intent.putExtra(IDUSER, idUser);
        startActivityForResult(intent, RESULT_KEY);
        Log.i("Go to add", "succes");
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
