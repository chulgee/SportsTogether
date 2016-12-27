package com.iron.dragon.sportstogether.ui.fragment;

import android.app.Fragment;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.http.retropit.RetrofitHelper;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.adapter.MessageAdapter;
import com.iron.dragon.sportstogether.util.Const;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    public static final String PARAM_MSG = "Message";
    public static final String TAG = "ChatFragment";
    public static final int HANDLER_PARAM_GET_PROFILE = 0;
    public static final int HANDLER_PARAM_GET_IMAGE = 1;

    RecyclerView rView;
    TextView tvBuddy;
    Button btnSend;
    EditText etMessage;
    CircleImageView civAvatar;

    public String mBuddyName;
    OnFragmentInteractionListener mListener;
    ChatActivity mActivity;
    MessageAdapter mAdapter;
    String mContents;
    Profile mMe;
    Profile mBuddy;

    Map<String, Bitmap> mAvatarMap = new HashMap<String, Bitmap>();
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == HANDLER_PARAM_GET_PROFILE){
                fetchBuddyProfile();
            }else if(msg.what == HANDLER_PARAM_GET_IMAGE){
                fetchAvaTar(mBuddy.getImage());
            }
        }
    };

    public ChatFragment() {
        // Required empty public constructor
    }

    public Map<String, Bitmap> getmAvatarMap() {
        return mAvatarMap;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance(Message message) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_MSG, message);
        fragment.setArguments(args);
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate getArguments()="+getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        return inflater.inflate(R.layout.frag_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.v(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "onViewCreated getArguments()="+getArguments());
        Log.v(TAG, "onViewCreated mActivity.mCurrentFrag="+mActivity.mCurrentFrag);
        Log.v(TAG, "onViewCreated mActivity.mCurrentFrag.mBuddyName="+mActivity.mCurrentFrag.mBuddyName);
        Iterator iter =ChatActivity.mChatRoom.keySet().iterator();
        while(iter.hasNext())
            Log.v(TAG, "onViewCreated iter="+iter.next());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        mMe = LoginPreferences.GetInstance().getLocalProfile(getActivity());
        tvBuddy = (TextView) getActivity().findViewById(R.id.buddyAlias);
        etMessage = (EditText) getActivity().findViewById(R.id.etChatMessage);
        rView = (RecyclerView) getActivity().findViewById(R.id.chatListView);
        civAvatar = (CircleImageView)getActivity().findViewById(R.id.buddyAvatar);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(llm);
        mAdapter = new MessageAdapter(this, null);
        rView.setAdapter(mAdapter);

        Message message = null;
        if (getArguments() != null) {
            message = (Message)getArguments().getSerializable(PARAM_MSG);
            if(message.getMsgType() == Message.PARAM_MSG_OUT){
                mBuddyName = message.getReceiver();
            }else{
                mBuddyName = message.getSender();
            }
            mActivity.addChatRoom(mBuddyName, this);
        }
        Log.v(TAG, "initView message="+message);

        tvBuddy.setText(mBuddyName);
        btnSend = (Button) getActivity().findViewById(R.id.sendButton);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etMessage.getText().toString().isEmpty()) {
                    mContents = etMessage.getText().toString();
                    Message message = new Message.Builder(Message.TYPE_CHAT_MESSAGE).msgType(Message.PARAM_MSG_OUT).sender(mMe.getUsername()).receiver(mBuddyName)
                            .message(mContents).date(new Date().getTime()).build();
                    updateUI(message);
                    mActivity.send(message);
                    etMessage.setText("");
                }else
                    Toast.makeText(getActivity(), "Please enter message", Toast.LENGTH_SHORT).show();
            }
        });
        if(message != null){
            updateUI(message);
            //mAdapter.addMessage(message);
            //mAdapter.notifyDataSetChanged();
        }

        /*if(mMe.getImage() != null){
            fetchAvaTar(mMe.getImage());
        }*/

        mHandler.sendEmptyMessage(HANDLER_PARAM_GET_PROFILE);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onDestroy() {
        Log.v(TAG, "onDestory");
        mActivity.removeChatRoom(mBuddyName);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.v(TAG, "onDestoryView");
        super.onDestroyView();
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
        void onUpdateUI();
    }

    public void updateUI(Message message) {
        mAdapter.addMessage(message);
        rView.scrollToPosition(mAdapter.getItemCount()-1);
        mAdapter.notifyDataSetChanged();
    }

    public void println(String data){
        System.out.println(data);
    }

    public void fetchBuddyProfile(){
        // buddy의 profile 가져오기
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        Log.v(TAG, "mBuddyName="+mBuddyName+", mMe.getSportsid()="+mMe.getSportsid()+", mMe.getLocationid()="+mMe.getLocationid());
        final Call<String> call =
                gitHubService.getProfiles(mBuddyName, mMe.getSportsid(), mMe.getLocationid(), 0);

        RetrofitHelper.enqueueWithRetry(call, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("executeHttp Test", "code = " + response.code() + " is successful = " + response.isSuccessful());
                Log.d("executeHttp Test", "body = " + response.body().toString());
                Log.d("executeHttp Test", "message = " + response.toString());
                //JSONObject obj = (JSONObject)response.body();
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
                if(mBuddy.getImage() != null)
                    mHandler.sendEmptyMessage(HANDLER_PARAM_GET_IMAGE);
                else{
                    civAvatar.setImageResource(R.drawable.default_user);
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
        Picasso.with(getActivity()).load(url).resize(50,50).centerInside().into(new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.v(TAG, "bitmap="+bitmap);
                if(filename.equals(mMe.getImage())){
                    mAvatarMap.put(mMe.getUsername(), bitmap);
                }else{
                    mAvatarMap.put(mBuddy.getUsername(), bitmap);
                    civAvatar.setImageBitmap(bitmap);
                }
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }
}
