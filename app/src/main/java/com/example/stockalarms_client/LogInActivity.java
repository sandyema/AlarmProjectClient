package com.example.stockalarms_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Random;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity {
    private static String USERID ;
    private static final int RESULT_KEY = 13;

    ServiceUser serviceUser;

    EditText editTextEmail;
    EditText editTextPassword;
    CircularProgressButton cirLoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceUser = ServiceFactory.getUserService();

        //for changing status bar icon colors
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.editEmail);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        cirLoginButton = (CircularProgressButton) findViewById(R.id.cirLoginButton);

        System.out.println(editTextEmail.toString());
        System.out.println(editTextPassword.toString());

        cirLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                final String[] idUser = new String[1];

                final String email = editTextEmail.getText().toString().trim();
                final String pass = editTextPassword.getText().toString().trim();
                // RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                if (email.isEmpty() || pass.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                    builder.setTitle("LOG IN ERROR");
                    System.out.println("_____________OFFLINE__________");
                    builder.setMessage("Email and password can't be empty\n Please try again");
                    builder.setNegativeButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                    dialog.show();

                } else {
                    final HashMap<String, String> user = new HashMap<String, String>();
                    user.put("email", email);
                    user.put("pass", pass);

                    SharedPreferences sharedpreferences = getSharedPreferences("myprefs",
                            Context.MODE_PRIVATE);

                    String token = sharedpreferences.getString("token", null);
                    String[] items;
                    if (token != null) {
                        items = token.split(",");
                        if (email.equals(items[0]) && pass.equals(items[1])) {
                            startActivity(new Intent(LogInActivity.this, MainActivity.class));
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                            builder.setTitle("LOG IN ERROR");
                            System.out.println("_____________OFFLINE__________");
                            builder.setMessage("Wrong email or password\n Please try again");
                            builder.setNegativeButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                            dialog.show();
                        }
                    }


                    serviceUser.doLogin(user).enqueue(new Callback<MyResponse>() {

                        @Override
                        public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            if (response.body().getResponse().equals("true")) {

                                serviceUser.findID(user).enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                        idUser[0] = response.body().getResponse();
                                        intent.putExtra(USERID, response.body().getResponse());
                                        System.out.println(USERID);
                                        startActivityForResult(intent, RESULT_KEY);
                                        Log.i("Login", "succes");
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });

                                SharedPreferences sharedpreferences = getSharedPreferences("myprefs",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.remove("token");
                                editor.commit();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                                builder.setTitle("LOG IN ERROR");
                                builder.setMessage("Wrong email or password\n Please try again");
                                builder.setNegativeButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                                dialog.show();

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                            builder.setTitle("LOG IN ERROR");
                            builder.setMessage("Wrong email or password\n Please try again");
                            builder.setNegativeButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    public void onRegisterClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

    }

    public void resetPassword(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LogInActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.resetpassword, null);

                EditText email=(EditText) mView.findViewById(R.id.editEmail);
                Button reset = (Button) mView.findViewById(R.id.reset);
                Button back = (Button) mView.findViewById(R.id.back);

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String user_email = email.getText().toString();
                        String new_pass= String.valueOf(generatePassword(10));

                        if(!user_email.equals("")) {
                            final HashMap<String, String> user = new HashMap<String, String>();
                            user.put("user_email", user_email);
                            user.put("new_pass", new_pass);

                            serviceUser.resetPass(user).enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                                    if (response.body().getResponse().equals("true")) {

                                        dialog.hide();

                                    }
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                                        builder.setTitle("ERROR");
                                        builder.setMessage("Wrong email \n Please try again");
                                        builder.setNegativeButton("OK", null);
                                        AlertDialog dialog = builder.create();
                                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                                        dialog.show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }


                            });

                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                            builder.setTitle("ERROR");
                            builder.setMessage("Empty email \n Please try again");
                            builder.setNegativeButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                            dialog.show();
                        }
                    }

                });


                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
    }

    private static char[] generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< length ; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return password;
    }
}
