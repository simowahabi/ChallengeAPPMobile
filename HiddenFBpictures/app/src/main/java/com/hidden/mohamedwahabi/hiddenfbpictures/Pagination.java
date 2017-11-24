package com.hidden.mohamedwahabi.hiddenfbpictures;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by wahabiPro on 11/20/2017.
 */

public class Pagination<E> {
    ArrayList<E> listAll=new ArrayList<>();
    int numOfPage;

    public Pagination(ArrayList<E> listAll,int numOfPage) {
        this.listAll = listAll;
        this.numOfPage=numOfPage;

    }


    public int newtpage(int count,ArrayList<E> listSmall,Button next,Button prev){
        int var=numOfPage;
        count+=numOfPage;

        if(count+numOfPage>=listAll.size())
            var=(listAll.size()-count);

        listSmall.clear();
        for (int i=count;i<count+var;i++){
            listSmall.add(listAll.get(i));
        }
        checkbutton(count,next,prev);
        return count;
    }
    public int prevpage(int count ,ArrayList<E> listSmall,Button next,Button prev){
        count-=numOfPage;
        listSmall.clear();
        for (int i=count;i<count+numOfPage;i++){
            listSmall.add(listAll.get(i));
        }
        checkbutton(count,next,prev);
        return count;
    }
    public void checkbutton(int count,Button next,Button prev){

        if(count+numOfPage>=listAll.size()){
            next.setVisibility(View.GONE);
        }
        else{
            next.setVisibility(View.VISIBLE);
        }

        if(count<=0){
            prev.setVisibility(View.GONE);
        }
        else{
            prev.setVisibility(View.VISIBLE);

        }

    }
    public void charge(int count,int numOfPage,ArrayList<E> listSmall,Button next,Button prev){
        listSmall.clear();


        if(listAll.size()<=numOfPage)
            for (int i=count;i<listAll.size();i++){
                listSmall.add(listAll.get(i));
            }
        else
            for (int i=0;i<numOfPage;i++){
                listSmall.add(listAll.get(i));
            }
        checkbutton(count,next,prev);

    }
}
