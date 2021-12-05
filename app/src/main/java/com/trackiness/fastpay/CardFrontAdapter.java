package com.trackiness.fastpay;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.trackiness.services.models.CreditCard;

import java.util.ArrayList;
import java.util.List;

public class CardFrontAdapter extends FragmentStateAdapter{

    List<Fragment> cff = new ArrayList<>();
    public CardFrontAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        //start with the front

        if(position < 3) {
            //when replacing a fragment, it automatically calls this method so we have to treat the case when
            //the element already exists
            try{
                if(cff.get(position) != null){
                    return cff.get(position);
                }
            }catch(IndexOutOfBoundsException e){
                cff.add(new CardFrontFragment());
                return cff.get(cff.size()-1);
            }
        }
        return cff.get(position);
    }

    public int replaceFragment(int position){
        if(cff.size() > 0) {
            if (cff.get(position) instanceof CardFrontFragment) {
                cff.set(position, new CardBackFragment());
            } else if (cff.get(position) instanceof CardBackFragment) {
                cff.set(position, new CardFrontFragment());
            }
        }
        return cff.size();

    }



    public int getItemCount() {
        return cff.size();
    }

    @Override
    public long getItemId(int position) {
        return cff.get(position).hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        for (Fragment fr : cff) {
            if(fr.getId() == itemId){
                return true;
            }
        }
        return false;
    }

    public int removeFragment(int position){
        if(cff.size() > 0) {
            Fragment f = cff.remove(position);
            f.onDestroy();
        }
        return cff.size();
    }

    public void showInfo(CreditCard credit_card, int position, Boolean show){

        if(cff.size() > 0){
            if(!show){
                show = true;
                if(cff.get(position) instanceof CardFrontFragment){
                    CardFrontFragment f = (CardFrontFragment)cff.get(position);
                    f.getTvName().setText(credit_card.getName());
                    f.getTvNumber().setText(credit_card.getNumber());
                    f.getTvValidity().setText(credit_card.getExpDate());
                }else{
                    CardBackFragment f = (CardBackFragment)cff.get(position);
                    f.binding.tvCvv.setText(credit_card.getCvv());
                }
            }else{
                show = false;
                if(cff.get(position) instanceof CardFrontFragment){
                    CardFrontFragment f = (CardFrontFragment)cff.get(position);
                    f.getTvName().setText(R.string.card_holders_name);
                    f.getTvNumber().setText("**** **** **** ****");
                    f.getTvValidity().setText(R.string.card_validity);
                }else{
                    CardBackFragment f = (CardBackFragment)cff.get(position);
                    f.binding.tvCvv.setText("***");
                }
            }
        }


    }
}
