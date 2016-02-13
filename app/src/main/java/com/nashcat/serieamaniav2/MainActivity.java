package com.nashcat.serieamaniav2;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nashcat.serieamaniav2.vo.BoardContentsVO;
import com.nashcat.serieamaniav2.vo.DefaultVO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listview;
    ListViewAdapter adapter;
    //ProgressDialog mProgressDialog;
    ArrayList<BoardContentsVO> contentsList;
    String url = "http://www.serieamania.com/xe/?mid=freeboard2&m=0";
    static int TYPEC =0;
    private final long F_I_TIME = 2000;
    private long backPressedTime =0;
    public int cPage ,cType,lCount,nCount;
    Map<String,String> loginCookies;
    DefaultVO userVo = new DefaultVO();
    static final int LOGIN_REQUEST_CODE = 100;
    static final int WRITE_REQUEST_CODE = 200;
    String midString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final LinearLayout bottomBtn = (LinearLayout)findViewById(R.id.CTNLinear);
        //플로팅 버튼 클릭하면 하단버튼 바 보이게
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomBtn.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
        });
        //하단 버튼바 중 닫기 버튼 누르면 하단버튼바를 숨기고 플로팅 버튼 보이게하기
        final ImageButton bottomClose = (ImageButton)findViewById(R.id.btnClose);
        bottomClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomBtn.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });
        //글쓰기 버튼 클릭하면
        final ImageButton writeButton = (ImageButton)findViewById(R.id.btnNew);
        writeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ("".equals(midString) || midString.isEmpty()){
                    //빈페이지에서는 버튼 안눌림
                } else {
                    if (userVo.getLoginCookies().isEmpty() || "N".equals(userVo.getLoginYn())){
                        //java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare() 방지
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "로그인 쿠키가 없습니다. 다시로그인해 주세요", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                        loginAct();
                    }else{
                        userVo.setMid(midString);
                        writeAct(userVo);
                    }
                }

            }
        });
        // 로그인 액티비티 실행
        loginAct();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void writeBtnOn(){
        ImageButton writeBtn = (ImageButton)findViewById(R.id.btnNew);
        writeBtn.setImageResource(R.drawable.ic_edit_24dp);
    }
    protected void writeBtnOff(){

    }
    protected void writeAct(DefaultVO nowVo){
        Intent writeIntent = new Intent(this,WriteActivity.class);
        writeIntent.putExtra("userVo",nowVo);
        startActivityForResult(writeIntent, WRITE_REQUEST_CODE);
    }

    protected void loginAct(){
        Intent loginIntent = new Intent(this,LoginActivity.class);
        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK){
                userVo = (DefaultVO)data.getSerializableExtra("userVo");
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View header = navigationView.getHeaderView(0);
                TextView text = (TextView)header.findViewById(R.id.profileId);
                text.setText(userVo.getUserNick());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime-backPressedTime;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            if (0<= intervalTime && F_I_TIME>=intervalTime){
                super.onBackPressed();
            }
            else {
                backPressedTime=tempTime;
                Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한버더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        TextView textView = (TextView)findViewById(R.id.boardTitle);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.calcioBoard) {
            textView.setText("Calcio Board ");
            contentsList=null;
            cPage=1;
            cType=1;
            lCount=0;
            nCount=0;
            midString="calcioboard";
            url = "http://www.serieamania.com/xe/?mid=calcioboard&m=0&page="+cPage;
            writeBtnOn();
            new JsoupListView().execute();

        } else if (id == R.id.freeBoard) {
            textView.setText("Free Board ");
            contentsList=null;
            cPage=1;
            cType=2;
            lCount=0;
            nCount=0;
            midString="freeboard2";
            url = "http://www.serieamania.com/xe/?mid=freeboard2&m=0&page="+cPage;
            writeBtnOn();
            new JsoupListView().execute();

        } else if (id == R.id.multiBoard) {
            textView.setText("Multimedia Board ");
            contentsList=null;
            cPage=1;
            cType=3;
            lCount=0;
            nCount=0;
            midString="multimedia1";
            url = "http://www.serieamania.com/xe/?mid=multimedia1&m=0&page="+cPage;
            writeBtnOn();
            new JsoupListView().execute();

        } else if (id == R.id.qnaBoard) {
            textView.setText("Q&A Board ");
            contentsList=null;
            cPage=1;
            cType=4;
            lCount=0;
            nCount=0;
            midString="qna1";
            url = "http://www.serieamania.com/xe/?mid=qna1&m=0&page="+cPage;
            writeBtnOn();
            new JsoupListView().execute();

        } else if (id == R.id.castBoard) {
            textView.setText("Broadcast Board ");
            contentsList=null;
            cPage=1;
            cType=5;
            lCount=0;
            nCount=0;
            midString="broadcast1";
            url = "http://www.serieamania.com/xe/?mid=broadcast1&m=0&page="+cPage;
            writeBtnOn();
            new JsoupListView().execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class JsoupListView extends AsyncTask<Void, Void, Void> {



        int x,y;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            TYPEC=cType;

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            //게시판을 새로 열어 contentsList 아무것도 없을때만 new로
            if(contentsList == null) contentsList = new  ArrayList<BoardContentsVO>();

            String replytext;
            String subjexttext;
            String subjexttext2;
            try {

                //쿠키가 없으면 다시 로그인으로
                if (userVo.getLoginCookies().isEmpty() || "N".equals(userVo.getLoginYn())){

                    //java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare() 방지
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "로그인 쿠키가 없습니다. 다시로그인해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);

                    loginAct();
                    return null;

                }

                // Connect to the Website URL
                Map<String, String> loginCookies = userVo.getLoginCookies();
                Document doc = Jsoup.connect(url).cookies(loginCookies).get();
                // Identify Table Class "worldpopulation"
                // 게시판 에서 tbody 태그 안쪽을 파싱

                Elements ttt = doc.select("tbody");

                for (Element table : ttt) {
                    // Identify all the table row's(tr)
                    for (Element row : table.select("tr")) {
                        BoardContentsVO boardContentsVO = new BoardContentsVO();
                        // Identify all the table cell's(td)
                        // 번호, 제목, 이름 아이콘을 추출
                        Elements tds = row.select("td");
                        // Identify all img src's
                        Elements imgSrc = row.select("img[src]");
                        // Get only src from img src
                        boardContentsVO.setUserIcon(imgSrc.attr("src"));
                        Elements url = tds.select("a[href]");

                        //** contentList에 URL 넣기 */
                        boardContentsVO.setContentUrl(url.attr("href"));
                        // Retrive Jsoup Elements
                        // Get the first td
                        Element span = tds.select("span[class=replyNum]").first();

                        //** contentList에 이름 넣기 */
                        boardContentsVO.setName(tds.get(2).text());

                        // 리플이 있는 경우 와 없는 경우를 구분해서 따로 저장
                        if (span != null) {
                            replytext = span.text();
                            subjexttext = tds.get(1).text();
                            int replylength = replytext.length();
                            int sublength = subjexttext.length();
                            subjexttext2 = subjexttext.substring(0, sublength - replylength);
                            //** contentList에 리플갯수 넣기(리플이 있는 경우) */
                            boardContentsVO.setReply("|  reply: " + replytext);
                        } else {
                            //** contentList에 리플갯수 넣기(리플이 없는 경우) */
                            boardContentsVO.setReply("|  reply: 0");
                            subjexttext2 = tds.get(1).text();
                        }


                        // 제목이 긴경우 줄임말 표시를 삽입
                        if (subjexttext2.length() > 28) {
                            //** contentList에 제목넣기 넣기(글줄임표넣기) */
                            boardContentsVO.setSubject(subjexttext2.substring(0, 25) + "...");
                        } else {
                            //** contentList에 제목 넣기 */
                            boardContentsVO.setSubject(subjexttext2);
                        }
                        if (cPage==1&& tds.get(0).text().equals("공지")){nCount++; }

                        if (cPage>1 && tds.get(0).text().equals("공지")){

                        }else {
                            //** contentList에 번호 넣기 */
                            boardContentsVO.setNumber(" [" + tds.get(0).text() + "]  ");
                            contentsList.add(boardContentsVO);
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(MainActivity.this, contentsList);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);

            // 리스트뷰 아이템 갯수를 저장
            if (cPage==1){
                lCount=adapter.getCount();
            }

            //리스트뷰가 바닥치면 다음 페이지 읽기
            listview.setOnScrollListener(new AbsListView.OnScrollListener() {

                                             @Override
                                             public void onScrollStateChanged(AbsListView view, int scrollState) { // TODO Auto-generated method stub
                                                 int threshold = 1;
                                                 int count = listview.getCount();

                                                 if (scrollState == SCROLL_STATE_IDLE) {
                                                     if (listview.getLastVisiblePosition() >= count - threshold) {
                                                         cPage++;

                                                         // 게시판 타입에 따라 읽어오는 주소를 변경
                                                         switch (cType) {
                                                             case 1:
                                                                 url = "http://www.serieamania.com/xe/?mid=calcioboard&m=0&page=" + cPage;
                                                                 break;

                                                             case 2:
                                                                 url = "http://www.serieamania.com/xe/?mid=freeboard2&m=0&page=" + cPage;
                                                                 break;

                                                             case 3:
                                                                 url = "http://www.serieamania.com/xe/?mid=multimedia1&m=0&page=" + cPage;
                                                                 break;

                                                             case 4:
                                                                 url = "http://www.serieamania.com/xe/?mid=qna1&m=0&page=" + cPage;
                                                                 break;

                                                             case 5:
                                                                 url = "http://www.serieamania.com/xe/?mid=broadcast1&m=0&page=" + cPage;
                                                                 break;

                                                         }


                                                         showMsg("다음 목록을 표시합니다.",500);
                                                         new JsoupListView().execute();

                                                     }
                                                 }
                                             }

                                             @Override
                                             public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                             }
                                             public void showMsg(String msg, final long duration) {
                                                 final Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                                                 toast.show();
                                                 Thread t = new Thread() {
                                                     public void run(){
                                                         try {
                                                             sleep(duration);
                                                             toast.cancel();
                                                         } catch (InterruptedException e) {
                                                             e.printStackTrace();
                                                         }
                                                         finally { }
                                                     }
                                                 };
                                                 t.start();
                                             }
                                         }

            );

            // Close the progressdialog
            //mProgressDialog.dismiss();


            Rect rectangle = new Rect();
            Window window = getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
            int statusBarHeight = rectangle.top;
            int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
            int titleBarHeight= contentViewTop - statusBarHeight;

            int keysize=0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int usableHeight = metrics.heightPixels;
                getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
                int realHeight = metrics.heightPixels;

                if (realHeight > usableHeight)
                    keysize = realHeight - usableHeight;
                else
                    keysize = 0;
            }


            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int height = size.y;

            if (cPage==1){

            }else

                listview.setSelectionFromTop(adapter.getCount()-(lCount+1-nCount),height- keysize-(statusBarHeight*2)-titleBarHeight-25);
            //    listview.setSelectionFromTop(x,y);

        }

    }




}
