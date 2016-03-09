package com.nashcat.serieamaniav2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nashcat.serieamaniav2.vo.BoardContentsVO;
import com.nashcat.serieamaniav2.vo.DefaultVO;
import com.nashcat.serieamaniav2.vo.ReplyContentsVO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nash on 2015-07-17.
 */
public class SingleItemView extends Activity {
    static final int LOGIN_REQUEST_CODE = 100;
    LayoutInflater inflater;
    ListView replyListview;
    ReplyListViewAdapter replyAdapter;
    String longTitle,longCon,longCon2,longCon3, longDate ;
    ArrayList<ReplyContentsVO> replyList;
    ImageLoader imageLoader = new ImageLoader(this);
    ImageView bannerimg;
    ImageView bannerimg2;
    BoardContentsVO boardContentsVO = null;
    DefaultVO userVo = new DefaultVO();
    String replyCount;
    String replyText;
    String myResult;
    String txtMid=null;
    String numResult;
//    ListView listview2;
    //ListViewReplyAdapter adapter2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.singleitemview);
        final EditText editTxtReply = (EditText)findViewById(R.id.textEditReply);

        final LinearLayout replyLinear = (LinearLayout)findViewById(R.id.reply_linear);
        final ImageButton imgBtnReplyCamera = (ImageButton)findViewById(R.id.btn_reply_camera);
        final ImageButton imgBtnRepltDone = (ImageButton)findViewById(R.id.btn_reply_done);
        final ImageButton imgBtnTool = (ImageButton)findViewById(R.id.singleview_btn_tool);
        final ImageButton imgBtnEdit = (ImageButton)findViewById(R.id.singleview_btn_edit);

        final ImageButton imgBtnDelete = (ImageButton)findViewById(R.id.singleview_btn_delete);
        final ImageButton imgBtnSubject = (ImageButton)findViewById(R.id.singleview_btn_subject_area);
        final ImageButton imgBtnReply = (ImageButton)findViewById(R.id.singleview_btn_reply_area);
        final LinearLayout singleSubjectLinear = (LinearLayout)findViewById(R.id.single_subject_layout);
        final LinearLayout singleReplyLinear = (LinearLayout)findViewById(R.id.single_reply_layout);
        final LinearLayout singleReplyArea = (LinearLayout)findViewById(R.id.single_Reply_Area_Layout);
        final ScrollView singleSubjectArea = (ScrollView)findViewById(R.id.single_Subject_Area_Layout);



        //툴버튼을 누르면 리플을 달수있는 에디트텍스트와 버튼이 보이게, 보여져 있는 상태면 감추기
        imgBtnTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (replyLinear.getVisibility()== View.VISIBLE){
                   replyLinear.setVisibility(View.GONE);
                   imgBtnEdit.setVisibility(View.GONE);

                   imgBtnDelete.setVisibility(View.GONE);
                   imgBtnTool.setImageResource(R.drawable.ic_expand_less_24dp);

               } else {
                   replyLinear.setVisibility(View.VISIBLE);
                   imgBtnEdit.setVisibility(View.VISIBLE);

                   imgBtnDelete.setVisibility(View.VISIBLE);
                   imgBtnTool.setImageResource(R.drawable.ic_expand_more_24dp);
               }

            }
        });
        imgBtnSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleSubjectLinear.setBackgroundColor(Color.parseColor("#FFF2F2F2"));
                singleReplyLinear.setBackgroundColor(Color.parseColor("#FFAAAAAA"));
                singleSubjectArea.setVisibility(View.VISIBLE);
                singleReplyArea.setVisibility(View.INVISIBLE);
                singleSubjectArea.scrollTo(0,0);


            }
        });
        imgBtnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleSubjectLinear.setBackgroundColor(Color.parseColor("#FFAAAAAA"));
                singleReplyLinear.setBackgroundColor(Color.parseColor("#FFF2F2F2"));
                singleSubjectArea.setVisibility(View.INVISIBLE);
                singleReplyArea.setVisibility(View.VISIBLE);


            }
        });
        if (boardContentsVO==null) boardContentsVO = new BoardContentsVO();



        Intent i = getIntent();
        // Get the result of
        Map<String, String> cook = new HashMap<>();
        cook.put("PHPSESSID", i.getStringExtra("phpsessid"));
        userVo.setLoginCookies(cook);
        boardContentsVO.setNumber(i.getStringExtra("number"));
        // Get the result of subject
        boardContentsVO.setSubject(i.getStringExtra("subject"));
        // Get the result of name
        boardContentsVO.setName(i.getStringExtra("name"));
        // Get the result of reply
        boardContentsVO.setReply(i.getStringExtra("reply"));
        // Get the result of userIcon
        boardContentsVO.setUserIcon(i.getStringExtra("userIcon"));
        // Get the result of url
        boardContentsVO.setContentUrl(i.getStringExtra("contentUrl"));
        String dNum = boardContentsVO.getContentUrl();
        String dNums[] = dNum.split("&");
        numResult = dNums[(dNums.length)-1];
        numResult = numResult.replaceAll("[^0-9]", "");

        // Get the result of replyCnt
        boardContentsVO.setReplyCnt(i.getStringExtra("replyCnt"));
        bannerimg=(ImageView) findViewById(R.id.imgtitle);
        bannerimg2=(ImageView) findViewById(R.id.imgtitle2);
        // Locate the TextViews in singleitemview.xml

        TextView txtName = (TextView) findViewById(R.id.singleName);
        ImageView imgUserIcon = (ImageView) findViewById(R.id.singleUserIcon);
        txtName.setText(boardContentsVO.getName());
        imageLoader.DisplayImage(boardContentsVO.getUserIcon(), imgUserIcon);



        final ImageButton replyWriteButton = (ImageButton)findViewById(R.id.btn_reply_done);
        replyWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyText=editTxtReply.getText().toString();
                if ("".equals(replyText) || replyText.isEmpty()) {
                    //빈페이지에서는 내용없음을 알림
                    Toast.makeText(getApplicationContext(), "내용이 비어있습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if (userVo.getLoginCookies().isEmpty() || "N".equals(userVo.getLoginYn())) {
                        //java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare() 방지
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "로그인 쿠키가 없습니다. 다시로그인해 주세요", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                        loginAct();
                    } else {
                        replyText= replyText.replaceAll("(\r\n|\n\r|\r|\n)", "<br>");

                        replyPost(userVo,replyText);
                    }
                }

            }
        });


        switch (MainActivity.TYPEC) {
            case 1:
                bannerimg.setImageResource(R.drawable.cal_hd);
                bannerimg2.setImageResource(R.drawable.cal_hd);
                txtMid="calcioboard";
                break;
            case 2:
                bannerimg.setImageResource(R.drawable.free_hd);
                bannerimg2.setImageResource(R.drawable.free_hd);
                txtMid="freeboard2";
                break;
            case 3:
                bannerimg.setImageResource(R.drawable.multi_hd);
                bannerimg2.setImageResource(R.drawable.multi_hd);
                txtMid="multimedia1";
                break;
            case 4:
                bannerimg.setImageResource(R.drawable.qna_hd);
                bannerimg2.setImageResource(R.drawable.qna_hd);
                txtMid="qna1";
                break;
            case 5:
                bannerimg.setImageResource(R.drawable.bro_hd);
                bannerimg2.setImageResource(R.drawable.bro_hd);
                txtMid="broadcast1";
                break;

        }
        new JsoupView().execute();



    }

    public void replyPost(DefaultVO userVo, String replyTxt) {
        String phpsessid = userVo.getLoginCookies().get("PHPSESSID");
        String txtReferer = "http://www.serieamania.com/xe/"+txtMid+"/"+numResult;

        try{
            StringBuffer buffer = new StringBuffer();
            String sUrl = "http://www.serieamania.com/xe/?mid=" + txtMid + "&document_srl="+numResult+"&m=0&act=dispBoardReplyComment ";

            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            buffer.append("<methodCall>");
            buffer.append("<params>");
            buffer.append("<_filter><![CDATA[insert_comment]]></_filter>");
            buffer.append("<mid><![CDATA[").append(txtMid).append("]]></mid>");
            buffer.append("<document_srl><![CDATA[").append(numResult).append("]]></document_srl>");
            buffer.append("<content><![CDATA[").append(replyTxt).append("]]></content>");
            buffer.append("<module><![CDATA[board]]></module>");
            buffer.append("<act><![CDATA[procBoardInsertComment]]></act>");
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
            http.setRequestProperty("Accept", "application/xml, text/xml, */*;");
            http.setRequestProperty("Accept-Encoding", "gzip, deflate");
            http.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
            http.setRequestProperty("Connection", "keep-alive");
            http.setRequestProperty("Content-Length", "" + Integer.toString(buffer.toString().getBytes().length));
            //http.setRequestProperty("Content-Type", "text/plain");
            http.setRequestProperty("Cookie", "PHPSESSID="+phpsessid);
            http.setRequestProperty("Host", "www.serieamania.com");
            http.setRequestProperty("Origin", "http://www.serieamania.com");
            http.setRequestProperty("Referer", txtReferer);
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

        Toast clsToast = Toast.makeText( this, "데이터를 전송하였습니다. 앱/xe 상태에 따라 리플이 등록되지 않을수 있습니다.", Toast.LENGTH_LONG );
        clsToast.show( );

        finish();
        startActivity(getIntent());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==event.KEYCODE_BACK)
        {

            WebView wb = (WebView) findViewById(R.id.webView);
            wb.loadUrl("");
            wb.stopLoading();

            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
    protected void loginAct(){
        Intent loginIntent = new Intent(this,LoginActivity.class);
        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
    }
    private class JsoupView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            if(replyList == null) replyList = new  ArrayList<ReplyContentsVO>();
            String rUrl2;
            String replyTxt="";
            int rPage;

            try {

                //쿠키가 없으면 다시 로그인으로
//                if (userVo.getLoginCookies().isEmpty() || "N".equals(userVo.getLoginYn())){
//
//                    //java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare() 방지
//                    Handler mHandler = new Handler(Looper.getMainLooper());
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "로그인 쿠키가 없습니다. 다시로그인해 주세요", Toast.LENGTH_SHORT).show();
//                        }
//                    }, 0);
//
//                    loginAct();
//                    return null;
//
//                }
                Map<String, String> loginCookies = userVo.getLoginCookies();

                replyCount=boardContentsVO.getReplyCnt();
                // Connect to the Website URL
                Document doc = Jsoup.connect(boardContentsVO.getContentUrl()).cookies(loginCookies).get();
                // Identify Table Class "worldpopulation"
                // 게시판 에서 tbody 태그 안쪽을 파싱
                Elements replyPage = doc.select("div[class=pagination]");

                if (replyPage.size()==0){
                    rUrl2= boardContentsVO.getContentUrl();
                    rPage=0;

                }else {
                    Elements aTag = replyPage.select("a[class=prevEnd]");
                    String rUrl = aTag.attr("href");
                    rUrl2 = rUrl.substring(0, rUrl.length() - 9);
                    Elements rPageNumeber = replyPage.select("Strong");

                    rPage = Integer.parseInt(rPageNumeber.get(0).text());
                }

                Elements ttt = doc.select("div[class=boardRead]");

                Elements atitle = ttt.select("div[class=titleArea]");
                longTitle = atitle.get(0).text();
                longDate = atitle.get(1).text();
                longCon =  ttt.select("div[class=boardReadBody]").first().html();


                String k1 =longCon
                        .replaceAll("(?i)<object ", "<ke")
                        .replaceAll("(?i)</object>", "")
                        .replaceAll("(?i)<embed src=(?:\"|)(?:http|https)://www.youtube.com/v/", "<iframe frameborder=\"0\" allowfullscreen height=\"315\" src=\"https://www.youtube.com/embed/")
                        .replaceAll("(?i)<embed src=(?:\"|)(?:\\/\\/|)www.youtube.com/v/", "<iframe frameborder=\"0\" allowfullscreen height=\"315\" src=\"https://www.youtube.com/embed/")
                        .replace("version=3\"><param name=\"allowFullScreen\"", "\"><param name=\"allowFullScreen\"\"")
                        .replace("?version=3\" ", "\"></iframe><sde ")
                        .replace("?version=3", "\"></iframe><sde ");


                longCon2=k1;
                //리플 파싱
                replyTxt=replyAppend(rUrl2,rPage);




            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                replyTxt ="";
            }
            // 리스트뷰에 리플 넣기위해서 분리
            Document replyDoc = Jsoup.parse(replyTxt);
            //리플 한개한개 분리
            Elements replyItems = replyDoc.select("div[class^=item]");
            int i=0;
            for (org.jsoup.nodes.Element oneReplyHtml : replyItems) {
                ReplyContentsVO replyContentsVO = new ReplyContentsVO();
                String replyNum = oneReplyHtml.attr("id").replace("comment_", "");
                //리플 번호
                replyContentsVO.setReplyNumber(replyNum);
                //리플 마진
                Element forReplyMargin = oneReplyHtml.select("div[class=indent").first();
                String replyMargin = forReplyMargin.attr("style");
                if (replyMargin.contains("margin-left")){
                    replyContentsVO.setRplyMargin(replyMargin.replace("margin-left:","").replace("px",""));
                } else{
                    replyContentsVO.setRplyMargin("0");
                }

                Element forGetData = oneReplyHtml.select("div[class=auther").first();
                //아이콘 추출
                Elements imgSrc = forGetData.select("img[src]");
                replyContentsVO.setUserIcon(imgSrc.attr("src"));

                //날짜 추출
                Elements replyDt = forGetData.select("font[color=gray]");
                replyContentsVO.setReplyDt(replyDt.text());
                //유저 추출
                Elements  replyUser = forGetData.select("Strong");
                replyContentsVO.setReplyUser(replyUser.text());

                //내용 추출
                Element replyCon = oneReplyHtml.select("div[class$=xe_content").first();
                replyContentsVO.setReplyContent(replyCon.html());



                //자기가쓴글 여부
                Element yN = forGetData.select("span[class=replyOption").first();
                Elements writeYn = yN.select("a");
                String wrYn= writeYn.get(0).text();
                if (wrYn == "..." || "...".equals(wrYn)){
                    replyContentsVO.setReplyMineYn("N");
                }else{
                    replyContentsVO.setReplyMineYn("Y");
                }
                i++;
                replyList.add(replyContentsVO);
            }
            //리플 add완료
            Log.v("te","te");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            TextView txtSubject = (TextView) findViewById(R.id.singleSubject);
            TextView txtDate = (TextView) findViewById(R.id.singleDate);
            TextView txtReply = (TextView) findViewById(R.id.replytext);
            int dateLength = longDate.length();
            String aDate = longDate.substring(dateLength-19,dateLength);
            txtSubject.setText(longTitle);
            txtReply.setText(longTitle);
            txtDate.setText(aDate);
            WebView wb = (WebView) findViewById(R.id.webView);
            TextView textViewReplyCount = (TextView) findViewById(R.id.replyCount);

            //이미지,아이프레임 리사이징과 자바스크립트 설정, 한글구현
            textViewReplyCount.setText(replyCount);
            wb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            wb.getSettings().setJavaScriptEnabled(true);
            wb.loadData("<style>img{width:100% !important;}</style><style>iframe{width:100% !important;}</style>"
                    + longCon2, "text/html; charset=UTF-8", null);



            //txtcontents.setText(longCon);

            replyListview = (ListView)findViewById(R.id.replyListview);
            replyAdapter= new ReplyListViewAdapter(SingleItemView.this,replyList);
            replyListview.setAdapter(replyAdapter);

        }

        //리플 파싱
        protected String replyAppend(String url,int page) {
            int totalReplyPageNumber = page;
            int currentReplyPageNumber = 1;
            String replyUrl;
            StringBuffer replyContents = new StringBuffer();

            //리플페이지 갯수로 리플 파싱
            if (page==0){
                //리플 페이지가 한페이지만 있으면

                replyUrl=url;

                try {
                    Map<String, String> loginCookies = userVo.getLoginCookies();
                    Document doc = Jsoup.connect(replyUrl).cookies(loginCookies).get();

                    Elements box = doc.select("div[class=replyBox]");

                    String kk="<style>img{width:100% !important;}</style><style>iframe{width:100% !important;}</style><style>div{font-size:95% !important;}</style><style>table{font-size:95% !important;}</style>"+box.html();
                    String kk2 = kk.replaceAll("<span class=\"mem", "<table border=\"0\"><span class=\"mem")
                                    .replaceAll("</strong></span>", "</strong></td></tr></span></table>")
                                    .replaceAll("\"><strong>", "\"><tr><td><strong>")
                                    .replaceAll("\"><img src=\"http://www.serieamania", "\"><tr><td width=\"20px\"><img src=\"http://www.serieamania")
                                    .replace("margin-right:3px\"><tr><td><strong>", "margin-right:3px\"></td><td><strong>")
                                    .replace("margin-right:3px\"><tr><td width=\"20px\"><img", "margin-right:3px\"></td><td width=\"20px\"><img")
                                    .replace("</div> \n<div class=\"item", "</div> \n<hr size=\"1\" color=\"#bbbbbb\"><div class=\"item")
                                    .replace("--> \n   <br>\n   <br>", "--> \n")
                                    .replaceAll("(?i)<object ", "<ke")
                                    .replaceAll("(?i)</object>", "")
                                    .replaceAll("(?i)height=(?:\"|)\\d+(?:\"|)", "")
                                    .replaceAll("(?i)<embed src=(?:\"|)(?:http|https)://www.youtube.com/v/", "<iframe frameborder=\"0\" allowfullscreen height=\"315\" src=\"https://www.youtube.com/embed/")
                                    .replaceAll("(?i)<embed src=(?:\"|)(?:\\/\\/|)www.youtube.com/v/", "<iframe frameborder=\"0\" allowfullscreen height=\"315\" src=\"https://www.youtube.com/embed/")
                                    .replace("version=3\"><param name=\"allowFullScreen\"", "\"><param name=\"allowFullScreen\"\"")
                                    .replace("?version=3\" ", "\"></iframe><sde ")
                                    .replace("?version=3", "\"></iframe><sde ");
                    //
                    longCon3=kk2;

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else {
                // 리플페이지가 여럿있으면
                // 각페이지를 파싱해서 replyContents에 합침
                for (int i = 0; i < page; i++) {

                    replyUrl = url + currentReplyPageNumber + "#comment";

                    try {
                        Document doc = Jsoup.connect(replyUrl).get();

                        Elements box = doc.select("div[class=replyBox]");
                        String kk = box.html();
                        if (i == 0) {
                            kk = "<style>img{width:100% !important;}</style><style>iframe{width:100% !important;}</style><style>div{font-size:95% !important;}</style><style>table{font-size:95% !important;}</style>" + kk;
                        }

                        //String kk2 = kk.replace("<img src=\"http://www.serieamania.com/xe", "<img src=\"http://www.serieamania.com/xe");
                        String kk2 = kk.replaceAll("<span class=\"mem", "<table border=\"0\"><span class=\"mem")
                                .replaceAll("</strong></span>", "</strong></td></tr></span></table>")
                                .replaceAll("\"><strong>", "\"><tr><td><strong>")
                                .replaceAll("\"><img src=\"http://www.serieamania", "\"><tr><td width=\"20px\"><img src=\"http://www.serieamania")
                                .replace("margin-right:3px\"><tr><td><strong>", "margin-right:3px\"></td><td><strong>")
                                .replace("margin-right:3px\"><tr><td width=\"20px\"><img", "margin-right:3px\"></td><td width=\"20px\"><img")
                                .replace("</div> \n<div class=\"item", "</div> \n<hr size=\"1\" color=\"#bbbbbb\"><div class=\"item")
                                .replace("--> \n   <br>\n   <br>", "--> \n")
                                .replaceAll("(?i)<object ", "<ke")
                                .replaceAll("(?i)</object>", "")
                                .replaceAll("(?i)height=(?:\"|)\\d+(?:\"|)", "")
                                .replaceAll("(?i)<embed src=(?:\"|)(?:http|https)://www.youtube.com/v/", "<iframe frameborder=\"0\" allowfullscreen  height=\"315\"src=\"https://www.youtube.com/embed/")
                                .replaceAll("(?i)<embed src=(?:\"|)(?:\\/\\/|)www.youtube.com/v/", "<iframe frameborder=\"0\" allowfullscreen  height=\"315\"src=\"https://www.youtube.com/embed/")
                                .replace("version=3\"><param name=\"allowFullScreen\"", "\"><param name=\"allowFullScreen\"\"")
                                .replace("?version=3\" ", "\"></iframe><sde ")
                                .replace("?version=3", "\"></iframe><sde ");

                        replyContents.append(kk2 + "<hr size=\"1\" color=\"#bbbbbb\">");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    currentReplyPageNumber ++;
                }
                longCon3 = replyContents.toString();
            }


            return longCon3;
        }

    }


}
