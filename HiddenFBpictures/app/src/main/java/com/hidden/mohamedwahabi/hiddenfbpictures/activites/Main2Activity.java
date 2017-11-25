package com.hidden.mohamedwahabi.hiddenfbpictures.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.hidden.mohamedwahabi.hiddenfbpictures.classes.Pagination;
import com.hidden.mohamedwahabi.hiddenfbpictures.R;
import com.hidden.mohamedwahabi.hiddenfbpictures.classes.AlbumClass;
import com.hidden.mohamedwahabi.hiddenfbpictures.classes.PicturesClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Main2Activity extends AppCompatActivity {
    //grid for showing albums
    private GridView albumList;
    //arraylist of pictures , initialization on this activity when the user click in album
    public static ArrayList<PicturesClass> albumPic = new ArrayList<PicturesClass>();
    //when going to MainPictures this name of album put in our intent for to be a title of MainPicture
    private String nameAlbum = "";
    private Button btnPrev;
    private Button btnNext;
    // count the current numbers of albums in all albums
    private int count = 0;
    //number of item in one page this variable can changed in string
    private int num_item_albums ;
    private Pagination page;
    //arraylist of the albums in one page
    public static ArrayList<AlbumClass> albumOnePage = new ArrayList<AlbumClass>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //initialization the some attributs
        albumList = (GridView) findViewById(R.id.albumlist);
        btnPrev = (Button) findViewById(R.id.prev);
        btnNext = (Button) findViewById(R.id.next);
        num_item_albums = getResources().getInteger(R.integer.name_of_item_in_page_albums);
        page = new Pagination(MainActivity.albumListFb, num_item_albums);
        page.charge(count, num_item_albums, albumOnePage, btnNext, btnPrev);
        //perrmession for reading the user photos
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_photos"));
        //change actionbar name to name the user
        getSupportActionBar().setTitle(Profile.getCurrentProfile().getName() + "'s albums");
        //get first group of albums
        allListItems();
    }
    //oncreate fin



    @Override
    protected void onResume() {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        findViewById(R.id.viewid).setVisibility(View.INVISIBLE);
        allListItems();
        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    //button next click
    public void next(View view) {
        count = page.newtpage(count, albumOnePage, btnNext, btnPrev);
        allListItems();
    }



    //button preview click
    public void prev(View view) {
        count = page.prevpage(count, albumOnePage, btnNext, btnPrev);
        allListItems();
    }



    //AlbumAdabter for display my albums in albumrow and get this layouts to layout of Main2activiy
    public class AlbumAdabter extends BaseAdapter {
        ArrayList<AlbumClass> mylist = new ArrayList<>();

        public AlbumAdabter(ArrayList<AlbumClass> mylist) {
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
            TextView albumName = (TextView) view.findViewById(R.id.covertitle);
            TextView covernumpic = (TextView) view.findViewById(R.id.covernumpic);
            covernumpic.setText(mylist.get(position).getCountpic() + " photos ");
            final ImageView albumImage = (ImageView) view.findViewById(R.id.coverAlbum);
            albumName.setText(mylist.get(position).getName());
            Picasso.with(getApplicationContext()).load(mylist.get(position).getSource()).resize(200, 120).centerCrop().into(albumImage);
            albumImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(mylist.get(position).getCountpic()) > 0) {
                        view.setClickable(false);
                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        findViewById(R.id.viewid).setVisibility(View.VISIBLE);
                        nameAlbum = mylist.get(position).getName();
                        getAllpic(mylist.get(position).getId());
                        Mathread th1 = new Mathread();
                        th1.start();
                    } else {
                        Toast.makeText(Main2Activity.this, "" + getResources().getString(R.string.sorry_pictures_0), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return view;
        }
    }
    //adapter fin



    //charge my adapter from mylist
    public void allListItems() {
        albumList.invalidateViews();
        AlbumAdabter ml = new AlbumAdabter(albumOnePage);
        albumList.setAdapter(ml);
    }



    //using GraphRequest for get all pictures in album
    public void getAllpic(String id) {
        albumPic.clear();
        GraphRequest magraphe = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/photos?limit=5000",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject json = response.getJSONObject();
                            JSONArray jarray = json.getJSONArray("data");
                            for (int i = 0; i < jarray.length(); i++) {
                                PicturesClass pctr = new PicturesClass(jarray.getJSONObject(i).get("id").toString(), jarray.getJSONObject(i).get("source").toString());
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
    //graphrequest fin



    // this thread for waiting when albumPic charged on startactivity to MainPictures
       public class Mathread extends Thread {
        @Override
        public void run() {
            try {
                boolean check = true;
                while (check) {
                    sleep(200);
                    if (albumPic.size() > 0) {
                        check = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent myint = new Intent(Main2Activity.this, MainPictures.class);
                                myint.putExtra("nameAlbum", nameAlbum);
                                startActivity(myint);
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //mathreadfin



}
