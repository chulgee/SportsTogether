package com.iron.dragon.sportstogether.ui.fragment;

import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.provider.MyContentProvider;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.adapter.MessageAdapter;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.DbUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";
    public static final String PARAM_FRAG_MSG = "Message";
    private static int mSportsId;

    @BindView(R.id.lvList) RecyclerView rView;
    @BindView(R.id.ibtnBack) ImageButton ibtnBack;
    @BindView(R.id.tvBuddyName) TextView tvBuddy;
    @BindView(R.id.btnSend) Button btnSend;
    @BindView(R.id.etChatMessage) EditText etMessage;

    static Map<String, Fragment> sChatRoom = new HashMap<String, Fragment>();
    Map<String, Bitmap> mAvatarMap = new HashMap<String, Bitmap>();

    OnFragmentInteractionListener mListener;
    ChatActivity mActivity;
    String mBuddyName;
    MessageAdapter mAdapter;
    String mContents;
    Profile mMe;
    Profile mBuddy;
    GitHubService mRetrofit;

    Handler mHandler = new Handler();

    public ChatFragment() {
    }

    public String getBuddyName() {
        return mBuddyName;
    }

    public Map<String, Bitmap> getAvatarMap() {
        return mAvatarMap;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance(Message message, Profile buddy_profile) {
        ChatFragment fragment = new ChatFragment();
        String buddy = null;
        if(message.getFrom() == Message.PARAM_FROM_ME){
            buddy = message.getReceiver();
        }else{
            buddy = message.getSender();
        }
        sChatRoom.put(buddy, fragment);
        Log.v(TAG, "sChatRoom newInstance buddy_profile="+buddy_profile+", fragment="+fragment);
        Bundle args = new Bundle();
        args.putSerializable(PARAM_FRAG_MSG, message);
        args.putSerializable("Buddy", buddy_profile);
        fragment.setArguments(args);
        mSportsId = buddy_profile.getSportsid();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG, "onAttach");
        mActivity = (ChatActivity)context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        mRetrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.chat_frag, container, false);
        ButterKnife.bind(this, v);
        mMe = LoginPreferences.GetInstance().loadSharedPreferencesProfile(getActivity(), mSportsId);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rView.setLayoutManager(llm);
        mAdapter = new MessageAdapter(this, null);
        rView.setAdapter(mAdapter);

        return v;
    }

    @OnClick({R.id.ibtnBack, R.id.btnSend})
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ibtnBack:
                Log.v(TAG, "hi sChatRoom.size()="+sChatRoom.size());
                Log.v(TAG, "getFragmentManager().getBackStackEntryCount()="+getFragmentManager().getBackStackEntryCount());
                mActivity.finish();
                break;
            case R.id.btnSend:
                if(!etMessage.getText().toString().isEmpty()) {
                    mContents = etMessage.getText().toString();
                    etMessage.setText("");
                    Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_MESSAGE).sender(mMe.getUsername()).receiver(mBuddyName)
                            .message(mContents).date(new Date().getTime()).room(mBuddyName).image(mMe.getImage()).build();
                    DbUtil.insert(getActivity(), message);
                    updateUI(message);
                    mActivity.send(message);
                }else
                    Toast.makeText(getActivity(), "Please enter message", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.v(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadChatHistory();
    }

    private void loadChatHistory(){
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                Log.v(TAG, "token="+token+", cursor="+cursor);
                super.onQueryComplete(token, cookie, cursor);
                if(cursor != null && cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        //String room = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_ROOM));
                        String sender = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_SENDER));
                        String receiver = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_RECEIVER));
                        String msg = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_MESSAGE));
                        int from = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_FROM));
                        long date = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_DATE));
                        int message_type = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_MESSAGE_TYPE));
                        Message message = new Message.Builder(from).msgType(message_type).sender(sender)
                                .receiver(receiver).message(msg).date(date).room(mBuddyName).build();
                        mAdapter.addMessage(message);
                        Log.v(TAG, "message="+message);
                    };
                    Bundle bd = getArguments();
                    Message message = (Message)bd.get(PARAM_FRAG_MSG);
                    if(message != null){
                        mAdapter.addMessage(message);
                    }
                    rView.scrollToPosition(mAdapter.getItemCount()-1);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        String[] projection = {
                DbHelper.COLUMN_ROOM,
                DbHelper.COLUMN_DATE,
                DbHelper.COLUMN_SENDER,
                DbHelper.COLUMN_RECEIVER,
                DbHelper.COLUMN_MESSAGE,
                DbHelper.COLUMN_FROM,
                DbHelper.COLUMN_MESSAGE_TYPE
        };
        queryHandler.startQuery(1, null, MyContentProvider.CONTENT_URI, projection, "sender=? or receiver=?", new String[]{mBuddyName, mBuddyName}, " date asc");
    }

    private void initView(){

        Bundle bd = getArguments();
        Message message = (Message)bd.getSerializable(PARAM_FRAG_MSG);
        mBuddy = (Profile)bd.getSerializable("Buddy");
        Log.v(TAG, "initView message="+message+", buddy="+mBuddy);

        if(mBuddy != null){
            if(message != null) {
                if(message.getFrom() == Message.PARAM_FROM_ME){
                    mBuddyName = message.getReceiver();
                }else{
                    mBuddyName = message.getSender();
                }
                tvBuddy.setText(mBuddyName);

                if(mBuddy.getImage() != null && !mBuddy.getImage().isEmpty()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fetchAvaTar(mBuddy.getImage());
                        }
                    });
                }
            }
        }else{
            //loadBuddyProfile(message);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadBuddyProfile(Message message){
        // buddy의 profile 가져오기

        Log.v(TAG, "mBuddyName="+mBuddyName+", mMe.getSportsid()="+mMe.getSportsid()+", mMe.getLocationid()="+mMe.getLocationid());
        final Call<String> call =
                mRetrofit.getProfiles(message.getSender(), mMe.getSportsid(), mMe.getLocationid(), 0);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("loadBuddyProfile", "code = " + response.code() + " is successful = " + response.isSuccessful());
                Log.d("loadBuddyProfile", "body = " + response.body().toString());
                Log.d("loadBuddyProfile", "message = " + response.toString());
                if (response.isSuccessful()) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.body().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    try {
                        String command = obj.getString("command");
                        String code = obj.getString("code");
                        JSONArray arr = obj.getJSONArray("message");
                        mBuddy = gson.fromJson(arr.get(0).toString(), Profile.class);
                        Log.v(TAG, "buddy: "+mBuddy.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(mBuddy.getImage() != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                fetchAvaTar(mBuddy.getImage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    public void fetchAvaTar(final String filename){
        // me와 buddy 아바타 가져오기
        String url = Const.MAIN_URL + "/upload_profile?filename=" + filename;
        //final Bitmap bmp;
        Log.v(TAG, "fetchAvaTar url="+url);
        Picasso.with(getActivity()).load(url).resize(50,50).centerInside().into(new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.v(TAG, "fetchAvaTar mBuddyName="+mBuddyName+", bitmap="+bitmap);
                mAvatarMap.put(mBuddyName, bitmap);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public static Map<String, Fragment> getChatRoom(){
        return sChatRoom;
    }

    public static Fragment getChatRoom(String buddy_name) {
        Fragment fr = sChatRoom.get(buddy_name);
        Log.v(TAG, "sChatRoom getChatRoom buddy="+buddy_name+", fragment="+fr);
        return fr;
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart mBuddyName="+mBuddyName);
        super.onStart();
    }

    @Override
    public void onDetach() {
        Log.v(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        Log.v(TAG, "onAttachFragment");
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onDestroyView() {
        Log.v(TAG, "onDestoryView");
        super.onDestroyView();
        Fragment fragment = sChatRoom.remove(mBuddyName);
        Log.v(TAG, "sChatRoom onDestory buddy="+mBuddyName+", fragment="+fragment);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestory");
        //Fragment fragment = sChatRoom.remove(mBuddyName);
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void updateUI(Message message) {
        mAdapter.addMessage(message);
        rView.scrollToPosition(mAdapter.getItemCount()-1);
        mAdapter.notifyDataSetChanged();
    }
}
