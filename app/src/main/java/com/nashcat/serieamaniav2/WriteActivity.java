package com.nashcat.serieamaniav2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nashcat.serieamaniav2.vo.DefaultVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WriteActivity extends AppCompatActivity {
    DefaultVO userVo = new DefaultVO() ;
    String title,content,myResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent= getIntent();
        TextView WriteTitle = (TextView)findViewById(R.id.write_boardTitle);
        userVo = (DefaultVO)intent.getSerializableExtra("userVo");
        String midString = userVo.getMid();
        if ("calcioboard".equals(midString)){
            WriteTitle.setText("cacio게시판 글쓰기");
        } else if ("freeboard2".equals(midString)) {
            WriteTitle.setText("자유게시판 글쓰기");
        } else if ("multimedia1".equals(midString)){
            WriteTitle.setText("멀티미디어게시판 글쓰기");
        } else if ("qna1".equals(midString)){
            WriteTitle.setText("질문게시판 글쓰기");
        }else if("broadcast1".equals(midString)){
            WriteTitle.setText("중계게시판 글쓰기");
        }
        //전송버튼
        ImageButton writeButton = (ImageButton)findViewById(R.id.write_btn_write);
        writeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                title=((EditText)(findViewById(R.id.write_title))).getText().toString();
                content=((EditText)(findViewById(R.id.write_body))).getText().toString();

                if ("".equals(title) || title.isEmpty() || "".equals(content) || content.isEmpty()) {
                    Snackbar.make(view, "제목과 내용을 모두 적어주십시오", Snackbar.LENGTH_LONG)
                            .setAction("돌아가기", null).show();
                } else {

                    httpPost();
                }
            }
        });
    }

    public void httpPost() {
        try{
            StringBuffer buffer = new StringBuffer();

            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            buffer.append("<methodCall>");
            buffer.append("<params>");
            buffer.append("<_filter><![CDATA[insert]]></_filter>");
            buffer.append("<error_return_url><![CDATA[").append("/xe/index.php?mid=test&act=dispBoardWrite").append("]]></error_return_url>");
            buffer.append("<act><![CDATA[procBoardInsertDocument]]></act>");
            buffer.append("<mid><![CDATA[").append("test").append("]]></mid>");
            buffer.append("<content><![CDATA[").append(content).append("<p><img src=\"http://nashcat.i234.me/saa05.png\"><p>html테스트").append("]]></content>");
            buffer.append("<document_srl><![CDATA[").append("0").append("]]></document_srl>");
            buffer.append("<title><![CDATA[").append(title).append("]]></title>");
            buffer.append("<comment_status><![CDATA[ALLOW]]></comment_status>");
            buffer.append("<nick_name><![CDATA[글쓴이]]></nick_name>");
            buffer.append("<password><![CDATA[1234]]></password>");
            buffer.append("<module><![CDATA[board]]></module>");
            buffer.append("</params>");
            buffer.append("</methodCall>");

            URL url= new URL("http://nashcat96.cafe24.com/xe/index.php?mid=test&act=dispBoardWrite");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            // 설정
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Accept", "application/xml, text/xml, */*; q=0.01");
            http.setRequestProperty("Accept-Encoding", "gzip, deflate");
            http.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
            http.setRequestProperty("Connection", "keep-alive");
            http.setRequestProperty("Content-Length", "" + Integer.toString(buffer.toString().getBytes().length));
            http.setRequestProperty("Content-Type", "text/plain");
            //http.setRequestProperty("Cookie", "mobile=false; user-agent=7bbf6b91fb6a8a8334413fe6497a718d; PHPSESSID=9ed7jqhi2um1jg9tucmnjrg2e0");
            http.setRequestProperty("Host", "nashcat96.cafe24.com");
            http.setRequestProperty("Origin", "http://nashcat96.cafe24.com");
            http.setRequestProperty("Referer", "http://nashcat96.cafe24.com/xe/index.php?mid=test&act=dispBoardWrite");
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");

            //http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            http.setRequestProperty("Accept-Charset", "UTF-8");

            http.setRequestProperty("X-Requested-With", "XMLHttpRequest");




            OutputStream outStream = http.getOutputStream();
            outStream.write(buffer.toString().getBytes());
            outStream.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
            }
            myResult = builder.toString();                       // 전송결과를 전역 변수에 저장

        } catch (MalformedURLException e){
            //
        } catch (IOException e){

        }


    }

}
