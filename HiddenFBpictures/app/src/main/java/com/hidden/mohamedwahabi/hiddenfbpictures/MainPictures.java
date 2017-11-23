package com.hidden.mohamedwahabi.hiddenfbpictures;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.FileNameMap;
import java.util.ArrayList;

import static com.hidden.mohamedwahabi.hiddenfbpictures.Main2Activity.albumPic;
import static com.hidden.mohamedwahabi.hiddenfbpictures.MainActivity.albumListFb;

public class MainPictures extends AppCompatActivity {
GridView gridView;
     Button btn_prev;
     Button btn_next;
    int count=0;
    public static ArrayList<picturesClass> albumPage=new ArrayList<picturesClass>();
    int num_item_picturs= 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pictures);
        gridView=(GridView)findViewById(R.id.pictlist);
        getSupportActionBar().setTitle(getIntent().getStringExtra("nameofalbum"));
        num_item_picturs= Integer.parseInt(getResources().getString(R.string.name_of_item_in_page_pictures));
        btn_prev     = (Button)findViewById(R.id.prev);
        btn_next     = (Button)findViewById(R.id.next);
        chargePicture();



    }

    @Override
    public void onBackPressed() {
        albumPic.clear();
        super.onBackPressed();
    }






    public class picAdapter extends BaseAdapter {
        ArrayList<picturesClass> picList=new ArrayList<picturesClass>();

        public picAdapter(ArrayList<picturesClass> picList) {
            this.picList = picList;
        }

        @Override
        public int getCount() {
            return picList.size();
        }

        @Override
        public Object getItem(int position) {
            return picList.get(position).getId();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View viewCovert, ViewGroup viewGroup) {
                LayoutInflater layoutInflater = getLayoutInflater();
                final View view = layoutInflater.inflate(R.layout.picturerow, null);
                final ImageView picture = (ImageView) view.findViewById(R.id.imageone);
                Picasso.with(getApplicationContext()).load(picList.get(position).getUrlpic()).into(picture);
                  picture.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                         Dialog dialog = new Dialog(MainPictures.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                          dialog.setContentView(R.layout.imageone);
                          dialog.setCancelable(true);
                          dialog.show();
                          ImageView imageone=(ImageView)dialog.findViewById(R.id.imafeFullScreen);
                          Picasso.with(getApplicationContext()).load(picList.get(position).getUrlpic()).into(imageone);

                      }
                  });

                return view;

        }
    }

    public void allListItems(){
        gridView.invalidateViews();
        picAdapter ml=new picAdapter(albumPage);
        gridView.setAdapter(ml);


    }
    public void chargePicture(){
        albumPage.clear();


        if(albumPic.size()<=num_item_picturs)
            for (int i=count;i<albumPic.size();i++){
                albumPage.add(albumPic.get(i));
            }
            else
        for (int i=0;i<num_item_picturs;i++){
            albumPage.add(albumPic.get(i));
        }
        allListItems();
        checkButtons();
    }



    public void Next(View view) {
        int var=num_item_picturs;
        count+=num_item_picturs;

        if(count+num_item_picturs>=albumPic.size())
            var=(albumPic.size()-count);

        albumPage.clear();
        for (int i=count;i<count+var;i++){
            albumPage.add(albumPic.get(i));
        }
        allListItems();
        checkButtons();
    }

    public void Prev(View view) {
        count-=num_item_picturs;
        albumPage.clear();
        for (int i=count;i<count+num_item_picturs;i++){
            albumPage.add(albumPic.get(i));
        }
        allListItems();
        checkButtons();
    }
    public void checkButtons(){
        if(count+num_item_picturs>=albumPic.size()){
            btn_next.setVisibility(View.GONE);
        }
        else{
            btn_next.setVisibility(View.VISIBLE);
        }

        if(count<=0){
            btn_prev.setVisibility(View.GONE);
        }
        else{
            btn_prev.setVisibility(View.VISIBLE);

        }

    }




}
