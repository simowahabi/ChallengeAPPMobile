package com.hidden.mohamedwahabi.hiddenfbpictures;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hidden.mohamedwahabi.hiddenfbpictures.MainActivity.albumListFb;

public class Main2Activity extends AppCompatActivity {
    GridView albumList;
    public static ArrayList<picturesClass> albumPic=new ArrayList<picturesClass>();
    String nameofalbum="";
    Button btn_prev;
    Button btn_next;
    int count=0;
    int num_item_albums= 0;
    Pagination page;


    public static ArrayList<albumClass> albumOnePage=new ArrayList<albumClass>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        albumList=(GridView)findViewById(R.id.albumlist);
        getSupportActionBar().setTitle(Profile.getCurrentProfile().getName()+"'s albums");
        btn_prev     = (Button)findViewById(R.id.prev);
        btn_next     = (Button)findViewById(R.id.next);
        num_item_albums= Integer.parseInt(getResources().getString(R.string.name_of_item_in_page_albums));
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_photos"));
        page=new Pagination(albumListFb,num_item_albums);

        page.charge(count,num_item_albums,albumOnePage,btn_next,btn_prev);
        allListItems();


    }

    @Override
    protected void onResume() {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        findViewById(R.id.viewid).setVisibility(View.INVISIBLE);
        allListItems();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
            return true;
    }






















    public class albumAdabter extends BaseAdapter{
        ArrayList<albumClass> mylist=new ArrayList<>();
        public albumAdabter(ArrayList<albumClass> mylist) {
            this.mylist = mylist;
        }



        @Override
        public int getCount() {
            return mylist.size();
        }

        @Override
        public Object getItem(int position) {
            return mylist.get(position).getId();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.albumrow, null);
            TextView albumName=(TextView)view.findViewById(R.id.covertitle);
            TextView covernumpic=(TextView)view.findViewById(R.id.covernumpic);
            covernumpic.setText(mylist.get(position).getCountpic()+" photos ");
            final ImageView albumImage=(ImageView) view.findViewById(R.id.coverAlbum);
            albumName.setText(mylist.get(position).getName());
            Picasso.with(getApplicationContext()).load(mylist.get(position).getSource()).resize(200,120).centerCrop().into(albumImage);
            albumImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
              if(Integer.parseInt(mylist.get(position).getCountpic())>0) {
                  view.setClickable(false);
                  findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                  findViewById(R.id.viewid).setVisibility(View.VISIBLE);
                  nameofalbum = mylist.get(position).getName();
                  getAllpic(mylist.get(position).getId());
                  mathread th1 = new mathread();
                  th1.start();
              }
              else {
                  Toast.makeText(Main2Activity.this, "" + getResources().getString(R.string.sorry_pictures_0), Toast.LENGTH_SHORT).show();
              }

                }
            });

            //Picasso.with(getApplicationContext()).load(Profile.getCurrentProfile().getProfilePictureUri(800, 800)).into(albumImage);
            return view;
        }
    }



    public void allListItems(){
        albumList.invalidateViews();
        albumAdabter ml=new albumAdabter(albumOnePage);
        albumList.setAdapter(ml);


    }







    public class mathread extends Thread{
        @Override
        public void run() {
            try {
                boolean check = true;
                while (check){
                    sleep(200);
                    if(albumPic.size()>0){
                            check=false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent myint=new Intent(Main2Activity.this,MainPictures.class);
                                    myint.putExtra("nameofalbum",nameofalbum);
                                    startActivity(myint);
                                }
                            });
                        }}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getAllpic(String id){
        albumPic.clear();
        Toast.makeText(this, ""+albumPic.size(), Toast.LENGTH_SHORT).show();
       GraphRequest magraphe= new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+id+"/photos?limit=5000",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            JSONObject json = response.getJSONObject();
                            JSONArray jarray = json.getJSONArray("data");
                            for (int i = 0; i < jarray.length(); i++) {
                                picturesClass pctr=new picturesClass(jarray.getJSONObject(i).get("id").toString(),jarray.getJSONObject(i).get("source").toString());
                                albumPic.add(pctr);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "source,id");

        magraphe.setParameters(parameters);

        magraphe.executeAsync();

    }




    public void Next(View view) {
        count=  page.newtpage(count,albumOnePage,btn_next,btn_prev);
        allListItems();
    }

    public void Prev(View view) {
        count=  page.prevpage(count,albumOnePage,btn_next,btn_prev);
        allListItems();
    }

}
