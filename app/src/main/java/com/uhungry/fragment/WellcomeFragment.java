package com.uhungry.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.uhungry.R;
import com.uhungry.activity.WellcomeActivity;
import com.uhungry.utils.Uhungry;

public class WellcomeFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private Context mContex;
    private String mParam1,orderId;
    private Handler handler = new Handler();
    private Runnable runnable;
    public WellcomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WellcomeFragment newInstance(String param1,String orderId) {
        WellcomeFragment fragment = new WellcomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            orderId = getArguments().getString("orderId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wellcome, container, false);
        bindView(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){
        //  ImageView ivCross = (ImageView) rootView.findViewById(R.id.ivCross);
        Button btnSetUp = (Button) rootView.findViewById(R.id.btnSetUp);
        Button btnStart = (Button) rootView.findViewById(R.id.btnStart);
        TextView tvWellcomeName = (TextView) rootView.findViewById(R.id.tvWellcomeName);
        TextView tvOr = (TextView) rootView.findViewById(R.id.tvOr);
        TextView tvWelcomeTitle = (TextView) rootView.findViewById(R.id.tvWelcomeTitle);

        String name =  Character.toUpperCase(Uhungry.sessionManager.getFullName().charAt(0)) + Uhungry.sessionManager.getFullName().substring(1);

        if (mParam1.equals("User registration successfully done")) {
            // tvTitle.setText(mContex.getResources().getString(R.string.text_quick_search));
            btnStart.setVisibility(View.VISIBLE);
            tvWellcomeName.setVisibility(View.VISIBLE);
            btnSetUp.setVisibility(View.VISIBLE);
            tvOr.setVisibility(View.VISIBLE);
            tvWellcomeName.setVisibility(View.GONE);

            tvWelcomeTitle.setText(mContex.getResources().getString(R.string.text_title_quick_search));
            //  ivCross.setVisibility(View.VISIBLE);

            btnStart.setOnClickListener(this);
            btnSetUp.setOnClickListener(this);
        }else {

            tvWellcomeName.setText("Chef"+" "+name);
            //   ivCross.setVisibility(View.GONE);
            btnSetUp.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            tvOr.setVisibility(View.GONE);
            tvWellcomeName.setVisibility(View.VISIBLE);

            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    ((WellcomeActivity)getActivity()).replaceFragment(RecipeFragment.newInstance(""), false, R.id.lyHomeContainer);
                }
            }, 3000);
        }

        // ivCross.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStart :
                //WellcomeActivity.isRegistration = false;
                ((WellcomeActivity)getActivity()).replaceFragment(FoodTypeFragment.newInstance(""), true, R.id.lyHomeContainer);
                break;

            case R.id.btnSetUp :
                WellcomeActivity.isRegistration = false;
                ((WellcomeActivity)getActivity()).replaceFragment(IngredientsFragment.newInstance("setup",orderId), true, R.id.lyHomeContainer);
                break;

         /*   case R.id.ivCross :
                WellcomeActivity.isRegistration = false;
                ((WellcomeActivity)getActivity()).replaceFragment(IngredientsFragment.newInstance(""), false, R.id.lyHomeContainer);
                break;*/
        }
    }
}
