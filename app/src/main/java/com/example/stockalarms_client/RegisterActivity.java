package com.example.stockalarms_client;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private static String USERID ;
    private static final int RESULT_KEY = 13;

    CircularProgressButton cirRegisterButton;
    ServiceUser serviceUser;

    EditText editFirstName;
    EditText editLastName;
    EditText editEmail;
    EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();

        cirRegisterButton = (CircularProgressButton) findViewById(R.id.cirRegisterButton);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        serviceUser=ServiceFactory.getUserService();

        cirRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> user;
                user = handleRegister();
                serviceUser.register(user).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                        if (response.body().getResponse().equals("true")) {

                            Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
                            startActivity(intent);

                            SharedPreferences sharedpreferences = getSharedPreferences("myprefs",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString("users", user.toString());
                            editor.commit();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Alert");
                            builder.setMessage("Something went wrong.");
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
        // Fill list
        // adapter = new ArrayAdapter<>(AddBalletActivity.this, android.R.layout.simple_list_item_single_choice, new ArrayList<>());
        // mFillListTask = new UserArea.FillListTask(token);
        // mFillListTask.execute();
    }

    private HashMap<String, String> handleRegister() {
        String first_name = editFirstName.getText().toString();
        String last_name = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String pass = editPassword.getText().toString();

        String errors = "";

        if (first_name.equals("")) {
            errors += "First name is empty\n";
        }
        if (last_name.equals("")) {
            errors += "Last name is empty\n";
        }
        if (email.equals("")) {
            errors += "Email is empty\n";
        }
        if (pass.equals("")) {
            errors += "Password is empty\n";
        }

        if (!errors.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ERROR");
            builder.setMessage(errors);
            builder.setNegativeButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();

            return null;
        }
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);


        HashMap<String, String> user = new HashMap<String, String>();
        user.put("first_name", first_name);
        user.put("last_name", last_name);
        user.put("email", email);
        user.put("pass",pass );

        return user;
    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,LogInActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }
}
