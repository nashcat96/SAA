package com.nashcat.serieamaniav2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nashcat.serieamaniav2.vo.DefaultVO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {
    DefaultVO userVo = new DefaultVO() ;
    String title,content,myResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        TextView WriteTitle = (TextView) findViewById(R.id.write_boardTitle);
        userVo = (DefaultVO) intent.getSerializableExtra("userVo");
        String midString = userVo.getMid();
        Map<String, String> kk = userVo.getLoginCookies();


        if ("calcioboard".equals(midString)) {
            WriteTitle.setText("cacio게시판 글쓰기");
        } else if ("freeboard2".equals(midString)) {
            WriteTitle.setText("자유게시판 글쓰기");
        } else if ("multimedia1".equals(midString)) {
            WriteTitle.setText("멀티미디어게시판 글쓰기");
        } else if ("qna1".equals(midString)) {
            WriteTitle.setText("질문게시판 글쓰기");
        } else if ("broadcast1".equals(midString)) {
            WriteTitle.setText("중계게시판 글쓰기");
        }
        //전송버튼
        ImageButton writeButton = (ImageButton) findViewById(R.id.write_btn_write);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = ((EditText) (findViewById(R.id.write_title))).getText().toString();
                content = ((EditText) (findViewById(R.id.write_body))).getText().toString();
                content = content.replaceAll("(\r\n|\n\r|\r|\n)", "<br>");
                if ("".equals(title) || title.isEmpty() || "".equals(content) || content.isEmpty()) {
                    Snackbar.make(view, "제목과 내용을 모두 적어주십시오", Snackbar.LENGTH_LONG)
                            .setAction("돌아가기", null).show();
                } else {

                    httpPost(userVo);
                }
            }
        });

        //뒤로가기대신 종료 버튼을 따로 두기
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("글쓰기 취소")        // 제목 설정
                .setMessage("글쓰기창을 닫으시겠습니까?\n 창을 닫으면 작성중인 내용이 모두 사라집니다.")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        ImageButton closeBtn = (ImageButton)findViewById(R.id.write_btn_exit);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();
            }
        });
    }

    public void httpPost(DefaultVO userVo) {
        String phpsessid = userVo.getLoginCookies().get("PHPSESSID");
        String mid = userVo.getMid();

        try{
            StringBuffer buffer = new StringBuffer();
            String sUrl = "http://www.serieamania.com/xe/index.php?mid=" + mid + "&m=0&act=dispBoardWrite";
            String sUrl2 = "http://www.serieamania.com/xe/?mid=" + mid + "&m=0&act=dispBoardWrite";
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            buffer.append("<methodCall>");
            buffer.append("<params>");
            buffer.append("<_filter><![CDATA[insert]]></_filter>");
            buffer.append("<mid><![CDATA[").append(mid).append("]]></mid>");
            buffer.append("<content><![CDATA[").append(content).append("]]></content>");
            buffer.append("<allow_comment><![CDATA[Y]]></allow_comment>");
            buffer.append("<title><![CDATA[").append(title).append("]]></title>");
            buffer.append("<module><![CDATA[board]]></module>");
            buffer.append("<act><![CDATA[procBoardInsertDocument]]></act>");
            buffer.append("</params>");
            buffer.append("</methodCall>");

            URL url= new URL(sUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            // 설정
            http.setDefaultUseCaches(false);
            http.setUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Accept", "application/xml, text/xml, */*; q=0.01");
            http.setRequestProperty("Accept-Encoding", "gzip, deflate");
            http.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
            http.setRequestProperty("Connection", "keep-alive");
            http.setRequestProperty("Content-Length", "" + Integer.toString(buffer.toString().getBytes().length));
            //http.setRequestProperty("Content-Type", "text/plain");
            http.setRequestProperty("Cookie", "PHPSESSID="+phpsessid);
            http.setRequestProperty("Host", "www.serieamania.com");
            http.setRequestProperty("Origin", "http://www.serieamania.com");
            http.setRequestProperty("Referer", sUrl2);
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
            http.setRequestProperty("content-type", "application/xml");
            //http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            http.setRequestProperty("Accept-Charset", "UTF-8");
            http.setRequestProperty("X-Requested-With", "XMLHttpRequest");




            OutputStream outStream = http.getOutputStream();
            outStream.write(buffer.toString().getBytes());
            outStream.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream());
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
            }
            myResult = builder.toString();                       // 전송결과를 전역 변수에 저장
            Log.d("result",myResult);
        } catch (MalformedURLException e){
            //
        } catch (Exception e){
            String kk = e.toString();
            Log.e("result",kk);
        }
        finish();

    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
