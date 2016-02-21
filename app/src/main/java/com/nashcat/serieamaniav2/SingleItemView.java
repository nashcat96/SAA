package com.nashcat.serieamaniav2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nashcat.serieamaniav2.vo.BoardContentsVO;
import com.nashcat.serieamaniav2.vo.DefaultVO;
import com.nashcat.serieamaniav2.vo.ReplyContentsVO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nash on 2015-07-17.
 */
public class SingleItemView extends Activity {
    static final int LOGIN_REQUEST_CODE = 100;

    ListView replyListview;
    ReplyListViewAdapter replyAdapter;
    String longTitle,longCon,longCon2,longCon3, longDate ;
    ArrayList<ReplyContentsVO> replyList;
    ImageLoader imageLoader = new ImageLoader(this);
    ImageView bannerimg;
    BoardContentsVO boardContentsVO = null;
    DefaultVO userVo = new DefaultVO();
    int m_iCommentWidth;

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
        final ImageButton imgBtnReply = (ImageButton)findViewById(R.id.singleview_btn_reply);
        final ImageButton imgBtnDelete = (ImageButton)findViewById(R.id.singleview_btn_delete);
        //툴버튼을 누르면 리플을 달수있는 에디트텍스트와 버튼이 보이게, 보여져 있는 상태면 감추기
        imgBtnTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (replyLinear.getVisibility()== View.VISIBLE){
                   replyLinear.setVisibility(View.GONE);
                   imgBtnEdit.setVisibility(View.GONE);
                   imgBtnReply.setVisibility(View.GONE);
                   imgBtnDelete.setVisibility(View.GONE);
                   imgBtnTool.setImageResource(R.drawable.ic_expand_less_24dp);

               } else {
                   replyLinear.setVisibility(View.VISIBLE);
                   imgBtnEdit.setVisibility(View.VISIBLE);
                   imgBtnReply.setVisibility(View.VISIBLE);
                   imgBtnDelete.setVisibility(View.VISIBLE);
                   imgBtnTool.setImageResource(R.drawable.ic_expand_more_24dp);
               }

            }
        });
        if (boardContentsVO==null) boardContentsVO = new BoardContentsVO();

        Intent i = getIntent();
        // Get the result of
        Map<String, String> cook = new HashMap<>();
        cook.put("PHPSESSID",i.getStringExtra("phpsessid"));
        userVo.setLoginCookies(cook);
        boardContentsVO.setName(i.getStringExtra("number"));
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
        bannerimg=(ImageView) findViewById(R.id.imgtitle);

        // Locate the TextViews in singleitemview.xml

        TextView txtName = (TextView) findViewById(R.id.singleName);
        ImageView imgUserIcon = (ImageView) findViewById(R.id.singleUserIcon);
        txtName.setText(boardContentsVO.getName());
        imageLoader.DisplayImage(boardContentsVO.getUserIcon(), imgUserIcon);




        switch (MainActivity.TYPEC) {
            case 1:
                bannerimg.setImageResource(R.drawable.cal_hd);
                break;
            case 2:
                bannerimg.setImageResource(R.drawable.free_hd);
                break;
            case 3:
                bannerimg.setImageResource(R.drawable.multi_hd);
                break;
            case 4:
                bannerimg.setImageResource(R.drawable.qna_hd);
                break;
            case 5:
                bannerimg.setImageResource(R.drawable.bro_hd);
                break;

        }
        new JsoupView().execute();



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
            int dateLength = longDate.length();
            String aDate = longDate.substring(dateLength-19,dateLength);
            txtSubject.setText(longTitle);
            txtDate.setText(aDate);
            WebView wb = (WebView) findViewById(R.id.webView);

            //이미지,아이프레임 리사이징과 자바스크립트 설정, 한글구현
            wb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            wb.getSettings().setJavaScriptEnabled(true);
            wb.loadData("<style>img{width:100% !important;}</style><style>iframe{width:100% !important;}</style>"
                                        +longCon2, "text/html; charset=UTF-8", null);



            //txtcontents.setText(longCon);

            replyListview = (ListView)findViewById(R.id.replyListview);
            replyAdapter= new ReplyListViewAdapter(SingleItemView.this,replyList);
            replyListview.setAdapter(replyAdapter);
            setListViewHeightBasedOnChildren(replyListview);
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
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        View replyListItem = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            replyListItem = listAdapter.getView(i, replyListItem, listView);
            replyListItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += replyListItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
