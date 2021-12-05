package com.trackiness.fastpay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trackiness.fastpay.databinding.FragmentCardBackBinding;
import com.trackiness.utility.FontTypeChange;


public class CardBackFragment extends Fragment {

    private TextView tv_cvv;
    FontTypeChange fontTypeChange;

    public TextView getTv_cvv() {
        return tv_cvv;
    }

    public void setTv_cvv(TextView tv_cvv) {
        this.tv_cvv = tv_cvv;
    }


    FragmentCardBackBinding binding;

    public CardBackFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CardBackFragment newInstance(int position) {
        CardBackFragment fragment = new CardBackFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCardBackBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fontTypeChange = new FontTypeChange(getActivity());
        tv_cvv = binding.tvCvv;
        tv_cvv.setTypeface(fontTypeChange.get_fontface(3));

        return view;

    }
}