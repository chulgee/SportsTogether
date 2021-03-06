package com.iron.dragon.sportstogether.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.factory.Factory;
import com.iron.dragon.sportstogether.ui.adapter.MainAdapter;

/**
 * Created by user on 2016-08-12.
 */
public class SportsFragment extends Fragment {
    ListView listView;
    //SingerAdapter adapter;
    Factory mFactory;
    RecyclerView Lv_list;
    RecyclerView.Adapter mAdapter;
    StaggeredGridLayoutManager mLayoutManager;
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.sports_frag, container, false);

        Lv_list = (RecyclerView)rootView.findViewById(R.id.list);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        Lv_list.setLayoutManager(mLayoutManager);

        /*List<SportsType> sports = Arrays.asList(SportsType.class.getEnumConstants());
        mFactory = new SportsFactory();
        for(SportsType st : sports){
            mFactory.create(StringUtil.getStringByType(getActivity(), st));
        }
        Log.v(TAG, "getList="+mFactory.getList().toString());*/
        mAdapter = new MainAdapter(getActivity());//, mFactory.getList());
        Lv_list.addItemDecoration((new SpacesItemDecoration(SpacesItemDecoration.MARGIN_BETWEEN_ITEM)));
        Lv_list.setAdapter(mAdapter);

        return rootView;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        public static final int MARGIN_BETWEEN_ITEM = 16;
        private final int mSpace;

        public SpacesItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if(position == 0 || (position%2 == 1 && position != 1))
                outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1)
                outRect.top = mSpace;
        }
    }
}