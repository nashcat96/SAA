package com.nashcat.serieamaniav2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nashcat.serieamaniav2.vo.ReplyContentsVO;

import java.util.ArrayList;


/**
 * Created by nash on 2015-07-17.
 */
public class ReplyListViewAdapter extends BaseAdapter {


    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<ReplyContentsVO> contentsData;
    ImageLoader imageLoader;
    ReplyContentsVO resultVO =null;

    public ReplyListViewAdapter(Context context,
                                ArrayList<ReplyContentsVO> replyList) {
        this.context = context;
        contentsData = replyList;

        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return contentsData.size();
    }

    @Override
    public Object getItem(int position) {
        if(position <0 || position >=contentsData.size()) return null;
        return contentsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View replyItemView, ViewGroup parent) {
        // 대댓글 마진
        ImageView replyMargin;
        //리플에 달린 유저 아이콘
        ImageView replyUserIcon;
        //리플에 달린 유저 이름(VO.replyUser)
        TextView replyName;
        //리플 날짜
        TextView replyDate;
        // 리플번호 (히든속성)
        TextView replyNumber;
        // 본인리플여부 (히든속성)
        TextView replyMineYn;
        //리플 내용
        WebView replyContents;




        if (resultVO==null) resultVO = new ReplyContentsVO();

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (replyItemView == null) {
            replyItemView = inflater.inflate(R.layout.replylistview_item, parent, false);
        }

        // Get the position

        resultVO = contentsData.get(position);


        // Locate the TextViews in replylistview_item.xml

        replyMargin = (ImageView) replyItemView.findViewById(R.id.reply_marginIcon);
        replyUserIcon = (ImageView) replyItemView.findViewById(R.id.reply_userIcon);
        replyName = (TextView) replyItemView.findViewById(R.id.txtView_reply_name);
        replyDate = (TextView) replyItemView.findViewById(R.id.txtView_reply_dt);
        replyNumber = (TextView) replyItemView.findViewById(R.id.txtView_reply_number);
        replyMineYn = (TextView) replyItemView.findViewById(R.id.txtView_reply_mine_yn);
        replyContents = (WebView) replyItemView.findViewById(R.id.replyWebView);
        //내용
//        replyContents.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        replyContents.getSettings().setJavaScriptEnabled(true);
//        replyContents.loadData(resultVO.getReplyContent(), "text/html; charset=UTF-8", null);

        RelativeLayout rReply=(RelativeLayout) replyItemView.findViewById(R.id.relativeMargin);
        // Capture position and set results to the TextViews
        //대댓글 마진주기
        int margin =Integer.parseInt(resultVO.getRplyMargin());
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) replyMargin.getLayoutParams();
        marginParams.setMargins(margin,0,0,0);
        //대댓글이 아니면 대댓글 그림 안보이기
        if (margin==0 || margin <0){
            rReply.setVisibility(View.GONE);
        }

        //리플유저아이콘 이미지 vo에서 넣기
        imageLoader.DisplayImage(resultVO.getUserIcon(), replyUserIcon);
        //리플유저 이름
        replyName.setText(resultVO.getReplyUser());
        //리플 시간
        replyDate.setText(resultVO.getReplyDt());
        //리플번호
        replyNumber.setText(resultVO.getReplyNumber());
        //본인리플여부
        replyMineYn.setText(resultVO.getReplyMineYn());



        replyItemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                // 리플 눌렀을때 소스
//                Get the position
//                resultVO = contentsData.get(position);
//                Intent intent = new Intent(context, SingleItemView.class);
//                // Pass all data number
//                intent.putExtra("number", resultVO.getNumber());
//                // Pass all data subject
//                intent.putExtra("subject", resultVO.getSubject());
//                intent.putExtra("reply", resultVO.getReply());
//                intent.putExtra("contentUrl", resultVO.getContentUrl());
//                // Pass all data name
//                intent.putExtra("name", resultVO.getName());
//                // Pass all data userIcon
//                intent.putExtra("userIcon", resultVO.getUserIcon());
//                // Start SingleItemView Class
//                context.startActivity(intent);

            }

        });
        return replyItemView;
    }
}