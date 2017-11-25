package com.hidden.mohamedwahabi.hiddenfbpictures.classes;

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
    //this class for do pagination for albums and pictures
    // i chose generic class because i will using this class with defences class albums and pictures
    ArrayList<E> listAll = new ArrayList<>();
    int numOfPage;

    public Pagination(ArrayList<E> listAll, int numOfPage) {
        this.listAll = listAll;
        this.numOfPage = numOfPage;
    }

    //this block is bringing us the next pictures/albums
    public int newtpage(int count, ArrayList<E> listSmall, Button next, Button prev) {
        int var = numOfPage;
        count += numOfPage;

        if (count + numOfPage >= listAll.size())
            var = (listAll.size() - count);

        listSmall.clear();
        for (int i = count; i < count + var; i++) {
            listSmall.add(listAll.get(i));
        }
        checkbutton(count, next, prev);
        return count;
    }

    //this block is bringing us the previews pictures/albums
    public int prevpage(int count, ArrayList<E> listSmall, Button next, Button prev) {
        count -= numOfPage;
        listSmall.clear();
        for (int i = count; i < count + numOfPage; i++) {
            listSmall.add(listAll.get(i));
        }
        checkbutton(count, next, prev);
        return count;
    }

    //this block is for making the buttons "next/prev" disappeared when the pictures/albums are at their endings/startings
    public void checkbutton(int count, Button next, Button prev) {
        if (count + numOfPage >= listAll.size()) {
            next.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }
        if (count <= 0) {
            prev.setVisibility(View.GONE);
        } else {
            prev.setVisibility(View.VISIBLE);
        }
    }

    // this block is for loading the first page of pictures/albums
    public void charge(int count, int numOfPage, ArrayList<E> listSmall, Button next, Button prev) {
        listSmall.clear();
        if (listAll.size() <= numOfPage)
            for (int i = count; i < listAll.size(); i++) {
                listSmall.add(listAll.get(i));
            }
        else
            for (int i = 0; i < numOfPage; i++) {
                listSmall.add(listAll.get(i));
            }
        checkbutton(count, next, prev);
    }
}
