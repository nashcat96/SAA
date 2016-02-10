package com.nashcat.serieamaniav2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.nashcat.serieamaniav2.vo.DefaultVO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nashc on 2016-02-04.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    DefaultVO userVo = new DefaultVO();

    @InjectView(R.id.input_id) EditText _idText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        userVo.setLoginYn("N");
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        EditText eTextId = (EditText)findViewById(R.id.input_id);
        EditText eTextPw = (EditText)findViewById(R.id.input_password);
        CheckBox chkSaveIdPw = (CheckBox)findViewById(R.id.saveIdPwCheck);

        String prefId = pref.getString("ID","");
        String prefPw = pref.getString("PW","");
        Boolean chk = pref.getBoolean("CHK",false);
        if (chk) {
            eTextId.setText(prefId);
            eTextPw.setText(prefPw);
            chkSaveIdPw.setChecked(chk);
        } else {
            eTextId.setText("");
            eTextPw.setText("");
            chkSaveIdPw.setChecked(false);
        }


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


    }

    public void onStop(){
        super.onStop();
        EditText eTextId = (EditText)findViewById(R.id.input_id);
        EditText eTextPw = (EditText)findViewById(R.id.input_password);
        CheckBox chkSaveIdPw = (CheckBox)findViewById(R.id.saveIdPwCheck);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (chkSaveIdPw.isChecked()){
            editor.putString("ID", eTextId.getText().toString());
            editor.putString("PW", eTextPw.getText().toString());
            editor.putBoolean("CHK", chkSaveIdPw.isChecked());

        } else {
            editor.putString("ID", "");
            editor.putString("PW", "");
            editor.putBoolean("CHK", false);

        }
        editor.commit();


    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        String id = _idText.getText().toString();
        String password = _passwordText.getText().toString();

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("로그인...");
        progressDialog.show();


        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();

                StrictMode.setThreadPolicy(policy);
            }
            Connection.Response res = Jsoup.connect("http://www.serieamania.com/xe/?m=0&act=procMemberLogin").data("user_id", id).data("password", password).method(Connection.Method.POST).execute();
            Map<String, String> loginCookies = res.cookies();
            Document doc = Jsoup.connect("http://www.serieamania.com/xe/?m=0&act=procMemberLogin").cookies(loginCookies).get();

            userVo.setUserId(_idText.getText().toString());
            userVo.setLoginCookies(loginCookies);

            try {
                Element ttt = doc.select("p[class=latestLogin]").first();

                if (ttt.text().contains("최근 로그인")){
                    String nickNm = getNick(doc);
                    if (!nickNm.isEmpty()){
                        userVo.setUserNick(nickNm);
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {

                                        // On complete call either onLoginSuccess or onLoginFailed
                                        onLoginSuccess(userVo);
                                        // onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 1000);
                    } else {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        // onLoginSuccess();
                                        onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 1000);
                    }

                } else{
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    // onLoginSuccess();
                                    onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 1000);
                }
            } catch (NullPointerException e){
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                // onLoginSuccess();
                                onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 1000);
            }

        } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(DefaultVO userVo) {
        _loginButton.setEnabled(true);
        Intent retIntent = new Intent();
        userVo.setLoginYn("Y");
        retIntent.putExtra("userVo",userVo);
        setResult(RESULT_OK,retIntent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        return valid;
    }

    public String getNick(Document doc) {
        String nickname3;
        try {
            Element elmFieldset = doc.select("fieldset").first();
            Element elmStrong = elmFieldset.select("strong").first();
            nickname3 = elmStrong.text();

        } catch (NullPointerException e) {

            return null;
        }


        return nickname3;
    }

}
