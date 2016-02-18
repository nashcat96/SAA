package com.nashcat.serieamaniav2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nashcat.serieamaniav2.vo.BoardContentsVO;
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
    BoardContentsVO resultVO =null;

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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView number;
        TextView subject;
        TextView reply;
        TextView name;
        ImageView userIcon;
        if (resultVO==null) resultVO = new BoardContentsVO();

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position

        resultVO = contentsData.get(position);


        // Locate the TextViews in listview_item.xml
        number = (TextView) itemView.findViewById(R.id.snumber);
        subject = (TextView) itemView.findViewById(R.id.ssubject);
        reply = (TextView) itemView.findViewById(R.id.reply);
        name = (TextView) itemView.findViewById(R.id.sname);

        //contentUrl = (TextView) itemView.findViewById(R.id.contentUrl);

        // Locate the ImageView in listview_item.xml
        userIcon = (ImageView) itemView.findViewById(R.id.userIcon);

        // Capture position and set results to the TextViews
        number.setText(resultVO.getNumber());
        subject.setText(resultVO.getSubject());
        reply.setText(resultVO.getReply());
        name.setText(resultVO.getName());

       // contentUrl.setText(resultp.get(MainActivity.CONTENTS));
        // Capture position and set results to the ImageView
        // Passes userIcon images URL into ImageLoader.class
        imageLoader.DisplayImage(resultVO.getUserIcon(), userIcon);
        // Capture ListView item click



        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                resultVO = contentsData.get(position);
                Intent intent = new Intent(context, SingleItemView.class);
                // Pass all data number
                intent.putExtra("number", resultVO.getNumber());
                // Pass all data subject
                intent.putExtra("subject", resultVO.getSubject());
                intent.putExtra("reply", resultVO.getReply());
                intent.putExtra("contentUrl", resultVO.getContentUrl());
                // Pass all data name
                intent.putExtra("name", resultVO.getName());
                // Pass all data userIcon
                intent.putExtra("userIcon", resultVO.getUserIcon());
                // Start SingleItemView Class
                context.startActivity(intent);

            }

        });
        return itemView;
    }
}