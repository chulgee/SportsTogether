package com.iron.dragon.sportstogether.ui.adapter.item;

/**
 * Created by P16018 on 2016-12-08.
 */

public class HeaderItem extends ListItem {

    private String mDate;

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    public void setDate(String date) {
        mDate = date;
    }
    public String getDate() {
        return mDate;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof HeaderItem)
        {
            isEqual = (this.mDate.equals(((HeaderItem) object).getDate()));
        }

        return isEqual;
    }
}