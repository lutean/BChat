package com.prepod.bchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.prepod.bchat.R;
import com.prepod.bchat.activities.RoomActivity;
import com.prepod.bchat.containers.Message;
import com.prepod.bchat.containers.Room;
import com.prepod.bchat.interfaces.OnRoomItemClick;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Антон on 16.03.2017.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Message> messageList;
    private Context context;

    public RoomAdapter(Context context, List<Message> messageList){
        this.context = context;
        this.messageList = messageList;
    }


    @Override
    public RoomAdapter.RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message, parent));
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        if (messageList.get(position).getText().contains("http")){
            Log.i("My!", "Yes link");
            holder.msgImage.setVisibility(View.VISIBLE);
            parseImage(messageList.get(position).getText(), holder.msgImage);
        }
        holder.senderTxt.setText(messageList.get(position).getName());
        holder.messageText.setText(messageList.get(position).getText());
        Log.i("My!", "Text: " + messageList.get(position).getText() );
        if (messageList.get(position).getUserAvavatar() != null) {
            Glide.with(context)
                    .load(messageList.get(position).getUserAvavatar())
                    .into(holder.userAvatar);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private void parseImage(String url, ImageView imageView) {

        //String url = "hi there this is a URL String https://cs7060.userapi.com/c836525/v836525410/30fc2/Rs2GD47uIcQ.jpg";

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        String urlStr = "";
        while (m.find()) {
            urlStr = m.group();
            if (urlStr.contains(".jpg")
                    || urlStr.contains(".jpeg")
                    || urlStr.contains(".png")
                    || urlStr.contains(".gif")) {

                //imageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(urlStr)
                        .into(imageView);
            }
            if (!urlStr.equals("")) {

            }
        }
    }


    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        public ImageView userAvatar;
        public ImageView msgImage;
        public TextView senderTxt;
        public TextView messageText;
        public TextView lastMessageDate;
        public LinearLayout itemRoomLayout;

        public RoomViewHolder(View itemView) {
            super(itemView);

            senderTxt = (TextView) itemView.findViewById(R.id.senderTextView);
            messageText = (TextView) itemView.findViewById(R.id.messageTextView);
            userAvatar = (ImageView) itemView.findViewById(R.id.userAvatar);
            msgImage = (ImageView) itemView.findViewById(R.id.message_image);
        }
    }
}
