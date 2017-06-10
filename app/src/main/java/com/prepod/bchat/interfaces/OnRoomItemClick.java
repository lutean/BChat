package com.prepod.bchat.interfaces;

import android.widget.ImageView;

public interface OnRoomItemClick {

    void onClick(int position);
    void onDeleteBtnClick(int position);
    void onLongClick(ImageView btn);

}
