package com.uhungry.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.activity.ViewAllActivity;
import com.uhungry.adapter.DiscoverChefListAdapter;
import com.uhungry.adapter.DiscoverFilterListAdapter;
import com.uhungry.adapter.DiscoverVideoListAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.listner.EndlessRecyclerViewScrollListener;
import com.uhungry.model.Discover;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscoverFragment extends Fragment implements View.OnClickListener,CustomAdapterButtonListener {

    private Context mContex;
    private ArrayList<Discover>chefses;
    private ArrayList<Discover>videoses;
    private ArrayList<Discover>newses;
    private ArrayList<Discover>uhungryContents;
    private ArrayList<Discover>filterList;
    private ArrayList<Discover>tmpFilterList;
    private Dialog alertDailog;
    private DiscoverChefListAdapter chefListAdapter,uhungryContenAdapter,newsListAdapter;
    private DiscoverVideoListAdapter videoListAdapter;
    private LinearLayout lyMainDisc,lyNews,lyUcontent,lyChef,lyVideo;

    private boolean isFilterChange;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tmpFilterList = new ArrayList<>();
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("ARG_PARAM");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.discover_fragment, container, false);
        bindView(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){
        chefses = new ArrayList<>();
        videoses = new ArrayList<>();
        newses = new ArrayList<>();
        filterList = new ArrayList<>();
        uhungryContents = new ArrayList<>();

        TextView tvTitle = (TextView) rootView.findViewById(R.id.actionbarLayout_title);
        ImageView ivFilter = (ImageView) rootView.findViewById(R.id.ivFilter);
        tvTitle.setText(getString(R.string.title_discover));
        //    addChef();

        lyNews = (LinearLayout) rootView.findViewById(R.id.lyNews);
        lyUcontent = (LinearLayout) rootView.findViewById(R.id.lyUcontent);
        lyChef = (LinearLayout) rootView.findViewById(R.id.lyChef);
        lyVideo = (LinearLayout) rootView.findViewById(R.id.lyVideo);
        LinearLayout lyViewAllChef = (LinearLayout) rootView.findViewById(R.id.lyViewAllChef);
        LinearLayout lyViewAllVideo = (LinearLayout) rootView.findViewById(R.id.lyViewAllVideo);
        LinearLayout lyViewAllNews = (LinearLayout) rootView.findViewById(R.id.lyViewAllNews);
        LinearLayout lyViewAllUhungryContent = (LinearLayout) rootView.findViewById(R.id.lyViewAllUhungryContent);

        lyMainDisc = (LinearLayout) rootView.findViewById(R.id.lyMainDisc);

        chefListAdapter = new DiscoverChefListAdapter(mContex, chefses,"fragment");
        uhungryContenAdapter = new DiscoverChefListAdapter(mContex, uhungryContents,"fragment");
        newsListAdapter = new DiscoverChefListAdapter(mContex, newses,"fragment");

        RecyclerView rvChef = (RecyclerView)rootView.findViewById(R.id.rvChef);
        rvChef.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvChef.setItemAnimator(new DefaultItemAnimator());
        rvChef.setLayoutManager(layoutManager1);
        rvChef.setAdapter(chefListAdapter);

        if(AppUtility.isNetworkAvailable(getActivity())) {
            getDiscoverData();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }

        RecyclerView rvVideo = (RecyclerView)rootView.findViewById(R.id.rvVideo);
        videoListAdapter = new DiscoverVideoListAdapter(mContex, videoses,"fragment");
        rvVideo.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvVideo.setLayoutManager(layoutManager2);
        rvVideo.setAdapter(videoListAdapter);

        RecyclerView rvUhungryCOntent = (RecyclerView)rootView.findViewById(R.id.rvUhungryCOntent);
        rvUhungryCOntent.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvUhungryCOntent.setLayoutManager(layoutManager3);
        rvUhungryCOntent.setAdapter(uhungryContenAdapter);


        RecyclerView rvNews = (RecyclerView)rootView.findViewById(R.id.rvNews);
        rvNews.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvNews.setLayoutManager(layoutManager4);
        rvNews.setAdapter(newsListAdapter);

        rvUhungryCOntent.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        rvNews.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        EndlessRecyclerViewScrollListener scrollListener1 = new EndlessRecyclerViewScrollListener(layoutManager1) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page,"1");
            }
        };

        EndlessRecyclerViewScrollListener scrollListener2 = new EndlessRecyclerViewScrollListener(layoutManager2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page,"2");
            }
        }; EndlessRecyclerViewScrollListener scrollListener3 = new EndlessRecyclerViewScrollListener(layoutManager3) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page,"3");
            }
        };
        EndlessRecyclerViewScrollListener scrollListener4 = new EndlessRecyclerViewScrollListener(layoutManager4) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page,"4");
            }
        };


        rvChef.addOnScrollListener(scrollListener1);
        rvVideo.addOnScrollListener(scrollListener2);
        rvUhungryCOntent.addOnScrollListener(scrollListener3);
        rvNews.addOnScrollListener(scrollListener3);

        lyViewAllChef.setOnClickListener(this);
        lyViewAllNews.setOnClickListener(this);
        lyViewAllUhungryContent.setOnClickListener(this);
        lyViewAllVideo.setOnClickListener(this);
        ivFilter.setOnClickListener(this);

        rvChef.setNestedScrollingEnabled(true);
        rvVideo.setNestedScrollingEnabled(true);
        rvUhungryCOntent.setNestedScrollingEnabled(true);
        rvNews.setNestedScrollingEnabled(true);

    }

    public void loadNextDataFromApi(int offset,String tagId) {
        // Send an API request to retrieve appropriate paginated data
        String page = String.valueOf(offset);

        if(AppUtility.isNetworkAvailable(mContex)) {
            getDiscoverData(page,tagId);
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lyViewAllChef :
                if (chefses.size()!=0) {
                    Intent intent = new Intent(mContex, ViewAllActivity.class);
                    intent.putExtra("discover", chefses);
                    intent.putExtra("key", chefses.get(0).disCId);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                break;

            case R.id.lyViewAllVideo :
                if (videoses.size()!=0){
                    Intent intent2  = new Intent(mContex, ViewAllActivity.class );
                    intent2.putExtra("discover",videoses);
                    intent2.putExtra("key",videoses.get(0).disCId);
                    startActivity(intent2);
                    getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

                }

                break;
            case R.id.lyViewAllNews :
                if (newses.size()!=0){
                    Intent intent3  = new Intent(mContex, ViewAllActivity.class );
                    intent3.putExtra("discover",newses);
                    intent3.putExtra("key",newses.get(0).disCId);
                    startActivity(intent3);
                    getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

                }

                break;

            case R.id.lyViewAllUhungryContent :
                if (uhungryContents.size()!=0){
                    Intent intent4  = new Intent(mContex, ViewAllActivity.class );
                    intent4.putExtra("discover",uhungryContents);
                    intent4.putExtra("key",uhungryContents.get(0).disCId);
                    startActivity(intent4);
                    getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

                }

                break;

            case R.id.ivFilter :
                if (chefses.size()!=0 && videoses.size()!=0 && uhungryContents.size()!=0 && newses.size()!=0)
                    showFilterDialog();
                break;
        }
    }

    public  void showFilterDialog() {
        View DialogView = View.inflate(getActivity(), R.layout.filter_dialog_layout, null);

        alertDailog = new Dialog(getContext(), android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(true);
        alertDailog.setCancelable(true);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        Button btnApply = (Button) DialogView.findViewById(R.id.btnApply);
        btnApply.setEnabled(false);
        btnApply.setAlpha(0.5f);

        if(filterList==null || filterList.size()==0)
            displayFilterListView();
        else {
            filterList.clear();
            filterList.addAll(tmpFilterList);
        }

        tmpFilterList.clear();
        for (Discover foo: filterList){
            tmpFilterList.add(new Discover(foo));
        }


        RecyclerView rvFilter = (RecyclerView) DialogView.findViewById(R.id.rvFilter);
        LinearLayout lyDialogCross = (LinearLayout) DialogView.findViewById(R.id.lyDialogCross);

        DiscoverFilterListAdapter adapter = new DiscoverFilterListAdapter(mContex, filterList, btnApply,lyDialogCross);
        adapter.setCustomListener(DiscoverFragment.this);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvFilter.setItemAnimator(new DefaultItemAnimator());
        rvFilter.setLayoutManager(layoutManager1);
        rvFilter.setAdapter(adapter);

       /* lyDialogCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
            }
        });*/

        alertDailog.show();
    }

    private void displayFilterListView() {
        filterList.clear();
        Discover discover;
        for(int i=0;i<4;i++) {
            discover = new Discover();
            switch (i) {
                case 0:
                    discover.disCId=  "1";
                    discover.isChacked = false;
                    discover.isBtnChecked = false;
                    discover.disCName = "Chef";
                    break;
                case 1:
                    discover.disCId=  "2";
                    discover.isChacked = false;
                    discover.isBtnChecked = false;
                    discover.disCName = "Video";
                    break;
                case 2:
                    discover.disCId=  "3";
                    discover.isChacked = false;
                    discover.isBtnChecked = false;
                    discover.disCName = "Uhungary content";
                    break;

                case 3:
                    discover.disCId=  "4";
                    discover.isChacked = false;
                    discover.isBtnChecked = false;
                    discover.disCName = "News";
                    break;

            }
            filterList.add(discover);
        }
    }


    public void getDiscoverData() {
        final ProgressDialog customDialog = new ProgressDialog(mContex);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL_USER+"allDiscover" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();

                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            String userstatus = jsonObj.getString("userstatus");
                            if (userstatus.equals("1")){
                                if (status.equalsIgnoreCase("success")) {
                                    lyMainDisc.setVisibility(View.VISIBLE);
                                    chefses.clear();
                                    videoses.clear();
                                    newses.clear();
                                    uhungryContents.clear();

                                    //  ingredientses.clear();
                                    JSONObject dataObj = jsonObj.getJSONObject("data");

                                    JSONObject chefObj = dataObj.getJSONObject("Chef");
                                    JSONObject VideoObj = dataObj.getJSONObject("Video");
                                    JSONObject uhungaryContentObj = dataObj.getJSONObject("Uhungary Content");
                                    JSONObject NewsObj = dataObj.getJSONObject("News");

                                    if (chefObj.has("disCName")){

                                        JSONArray jsonArray = chefObj.getJSONArray("contentData");
                                        if(jsonArray != null && jsonArray.length() > 0 ) {

                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                Discover item = new Discover();

                                                item.disCName = chefObj.getString("disCName").trim();
                                                item.disCId = chefObj.getString("disCId").trim();

                                                item.id = jsonObject.getString("id").trim();
                                                item.title = jsonObject.getString("title").trim();
                                                item.contentType = jsonObject.getString("contentType").trim();
                                                item.content = jsonObject.getString("content").trim();
                                                item.contentImage = jsonObject.getString("contentImage").trim();

                                                chefses.add(item);
                                            }

                                            chefListAdapter.notifyDataSetChanged();
                                        }

                                    }

                                    if (VideoObj.has("disCName")){

                                        JSONArray jsonArray = VideoObj.getJSONArray("contentData");
                                        if(jsonArray != null && jsonArray.length() > 0 ) {

                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                Discover item = new Discover();

                                                item.disCName = VideoObj.getString("disCName").trim();
                                                item.disCId = VideoObj.getString("disCId").trim();

                                                item.id = jsonObject.getString("id").trim();
                                                item.title = jsonObject.getString("title").trim();
                                                item.contentType = jsonObject.getString("contentType").trim();
                                                item.content = jsonObject.getString("content").trim();
                                                item.contentImage = jsonObject.getString("contentImage").trim();

                                                videoses.add(item);
                                            }

                                            videoListAdapter.notifyDataSetChanged();
                                        }

                                    }
                                    if (uhungaryContentObj.has("disCName")){

                                        JSONArray jsonArray = uhungaryContentObj.getJSONArray("contentData");
                                        if(jsonArray != null && jsonArray.length() > 0 ) {

                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                Discover item = new Discover();

                                                item.disCName = uhungaryContentObj.getString("disCName").trim();
                                                item.disCId = uhungaryContentObj.getString("disCId").trim();

                                                item.id = jsonObject.getString("id").trim();
                                                item.title = jsonObject.getString("title").trim();
                                                item.contentType = jsonObject.getString("contentType").trim();
                                                item.content = jsonObject.getString("content").trim();
                                                item.contentImage = jsonObject.getString("contentImage").trim();

                                                uhungryContents.add(item);
                                            }

                                            uhungryContenAdapter.notifyDataSetChanged();
                                        }

                                    }
                                    if (NewsObj.has("disCName")){

                                        JSONArray jsonArray = NewsObj.getJSONArray("contentData");
                                        if(jsonArray != null && jsonArray.length() > 0 ) {

                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                Discover item = new Discover();

                                                item.disCName = NewsObj.getString("disCName").trim();
                                                item.disCId = NewsObj.getString("disCId").trim();

                                                item.id = jsonObject.getString("id").trim();
                                                item.title = jsonObject.getString("title").trim();
                                                item.contentType = jsonObject.getString("contentType").trim();
                                                item.content = jsonObject.getString("content").trim();
                                                item.contentImage = jsonObject.getString("contentImage").trim();

                                                newses.add(item);
                                            }

                                            newsListAdapter.notifyDataSetChanged();
                                        }

                                    }



                                    else if(message.equals("no result")){
                                        AppUtility.showAlertDialog_SingleButton(getActivity(),message,"Alert!","Ok");
                                    }
                                }
                                else {
                                    customDialog.cancel();
                                }
                            }else{
                                AppUtility.sessionExpire(getActivity(), "User currently inactive from admin.", "Alert!","Ok");
                            }

                        } catch (Exception ex) {
                            Log.e("TAG",ex.toString());
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Request timeout error";
                        customDialog.cancel();
                        if (networkResponse == null) {
                            AppUtility.showAlertDialog_SingleButton(getActivity(), errorMessage, "Alert!","Ok");

                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                            AppUtility.showAlertDialog_SingleButton(getActivity(), errorMessage, "Alert!","Ok");

                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = response.getString("message");
                                if (message.equals("Invalid Auth Token")){
                                    AppUtility.sessionExpire(getActivity(), "Your Session is expired, please login again.", "Alert!","Ok");
                                }

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message+" Please login again";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message+ "Session expired";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message+"‘Ooops! Something went wrong";
                                }else if (networkResponse.statusCode == 300) {
                                    errorMessage = message+" Your Session is expired, please login again.";
                                }

                                if (!message.equals("Invalid Auth Token") || networkResponse.statusCode != 400){
                                    AppUtility.showAlertDialog_SingleButton(getActivity(), message, "Alert!","Ok");

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                // header.put("page", "");
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){

                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    Toast.makeText(getActivity(), "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

    private void getDiscoverData(final String page,final String tagId) {
        final ProgressDialog customDialog = new ProgressDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"getDiscoverData",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObj.getJSONArray("discoverData");
                                if(jsonArray != null && jsonArray.length() > 0 ) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        Discover item = new Discover();

                                        item.id = jsonObject.getString("id").trim();
                                        item.title = jsonObject.getString("title").trim();
                                        item.contentType = jsonObject.getString("contentType").trim();
                                        item.content = jsonObject.getString("content").trim();
                                        item.contentImage = jsonObject.getString("contentImage").trim();

                                        if (tagId.equals("1")){
                                            chefses.add(item);
                                        }else  if (tagId.equals("2")){
                                            videoses.add(item);
                                        }else  if (tagId.equals("3")){
                                            uhungryContents.add(item);
                                        }else {
                                            newses.add(item);
                                        }
                                    }

                                    switch (tagId) {
                                        case "1":
                                            chefListAdapter.notifyDataSetChanged();
                                            break;
                                        case "2":
                                            videoListAdapter.notifyDataSetChanged();
                                            break;
                                        case "3":
                                            uhungryContenAdapter.notifyDataSetChanged();
                                            break;
                                        default:
                                            newsListAdapter.notifyDataSetChanged();
                                            break;
                                    }
                                }
                            }/*else {
                                if (page.equals("0")){
                                    tvExploreEmptyList.setVisibility(View.VISIBLE);
                                    rvExploreEvent.setVisibility(View.GONE);
                                    tvExploreEmptyList.setText("No Event available");
                                }
                            }*/

                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        AppUtility.showAlertDialog_SingleButton(getActivity(),"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("page", page);
                header.put("disCid", tagId);

                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        alertDailog.dismiss();
        filterList.clear();
        filterList.addAll(tmpFilterList);
    }

    @Override
    public void onFilterApply(ArrayList<Discover> arrayList, boolean b) {
        tmpFilterList.clear();
        tmpFilterList.addAll(arrayList);
        alertDailog.dismiss();
        lyMainDisc.setVisibility(View.VISIBLE);


        int length = 0;
        for (Discover discover: arrayList) {
            if(!discover.isChacked)
                length++;
        }

        int visibility = arrayList.size()==length?View.VISIBLE:View.GONE;
        lyUcontent.setVisibility(visibility);
        lyVideo.setVisibility(visibility);
        lyNews.setVisibility(visibility);
        lyChef.setVisibility(visibility);

        for(Discover tmp :arrayList){
            if (tmp.disCId.equals("1") && tmp.isChacked){
                lyChef.setVisibility(View.VISIBLE);
            }else if (tmp.disCId.equals("2") && tmp.isChacked){
                lyVideo.setVisibility(View.VISIBLE);
            }else if (tmp.disCId.equals("3") && tmp.isChacked){
                lyUcontent.setVisibility(View.VISIBLE);
            }else if (tmp.disCId.equals("4") && tmp.isChacked){
                lyNews.setVisibility(View.VISIBLE);
            }
        }
    }
}
