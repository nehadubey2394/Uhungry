package com.uhungry.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uhungry.R;

public class IngredientsFragment extends Fragment implements View.OnClickListener {
    private Context mContex;

    private LinearLayout lyAllGrocery,lyMyGrocery;
    private TextView tvAllGrocery,tvMyGrocery;
    private  String mParam1, orderId,isSetUp;
    private ImageView ivSend;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static IngredientsFragment newInstance(String param1,String orderId) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putString("setup", param1);
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("setup");
            orderId = getArguments().getString("orderId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ingredients_fragment, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        bindView(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){

        TextView tvTitle = (TextView) rootView.findViewById(R.id.actionbarLayout_title);
        tvAllGrocery = (TextView) rootView.findViewById(R.id.tvAllGrocery);
        tvMyGrocery = (TextView) rootView.findViewById(R.id.tvMyGrocery);
        lyAllGrocery = (LinearLayout) rootView.findViewById(R.id.lyAllGrocery);
        lyMyGrocery = (LinearLayout) rootView.findViewById(R.id.lyMyGrocery);
        RelativeLayout rlActionbar = (RelativeLayout) rootView.findViewById(R.id.recipeActionbar);
        ImageView actionbar_btton_back = (ImageView) rootView.findViewById(R.id.actionbar_btton_back);
        ivSend = (ImageView) rootView.findViewById(R.id.ivSend);

        if (mParam1.equals("setup")){
            actionbar_btton_back.setVisibility(View.VISIBLE);
            isSetUp = "1";
            // rlActionbar.setVisibility(View.VISIBLE);
        }else {
            isSetUp = "0";
            actionbar_btton_back.setVisibility(View.GONE);
            //rlActionbar.setVisibility(View.GONE);
        }


        tvTitle.setText(getString(R.string.title_setup_kitchen));

        lyAllGrocery.setOnClickListener(this);
        lyMyGrocery.setOnClickListener(this);
        actionbar_btton_back.setOnClickListener(this);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // KichensIngredientListFragment allGroceryFragment = new KichensIngredientListFragment();
        if (getView()!=null)
            getChildFragmentManager().beginTransaction().replace(R.id.tab_container, KichensIngredientListFragment.newInstance("isSetUp",orderId)).commit();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyAllGrocery :
                ivSend.setVisibility(View.GONE);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                lyAllGrocery.setBackgroundResource(R.drawable.bg_tab_selected);
                lyMyGrocery.setBackgroundResource(R.drawable.bg_tab_unselected);
                tvMyGrocery.setTextColor(getResources().getColor(R.color.gray));
                tvAllGrocery.setTextColor(getResources().getColor(R.color.white));

                getChildFragmentManager().beginTransaction().replace(R.id.tab_container, KichensIngredientListFragment.newInstance("",orderId)).commit();

                break;

            case R.id.lyMyGrocery :
                //   ivSend.setVisibility(View.VISIBLE);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                lyMyGrocery.setBackgroundResource(R.drawable.bg_second_tab_selected);
                lyAllGrocery.setBackgroundResource(R.drawable.bg_second_tab_unselected);
                tvAllGrocery.setTextColor(getResources().getColor(R.color.gray));
                tvMyGrocery.setTextColor(getResources().getColor(R.color.white));

                // MyGroceryListFragment myGroceryListFragment = new MyGroceryListFragment();
                // getFragmentManager().beginTransaction().replace(R.id.tab_container, myGroceryListFragment).commit();
                ivSend.setEnabled(true);
                ivSend.setAlpha(1.0f);
                ivSend.setVisibility(View.VISIBLE);

                getChildFragmentManager().beginTransaction().replace(R.id.tab_container,
                        MyGroceryListFragment.newInstance("",ivSend,orderId)).commit();

                break;

            case R.id.actionbar_btton_back :
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
