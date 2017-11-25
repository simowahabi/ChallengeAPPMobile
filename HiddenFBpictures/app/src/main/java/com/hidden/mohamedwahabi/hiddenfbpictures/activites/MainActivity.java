package com.hidden.mohamedwahabi.hiddenfbpictures.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hidden.mohamedwahabi.hiddenfbpictures.R;
import com.hidden.mohamedwahabi.hiddenfbpictures.classes.AlbumClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //i use Picasso lib for loading image from url to my application
    //arraylist of albums , initialization on this activity before the user connect
    public static ArrayList<AlbumClass> albumListFb = new ArrayList<AlbumClass>();
    private static final String[] PERMISSIONS = {"user_photos"};
    //calbackManger for connecting the user via his account facebook
    private CallbackManager callbackManager;
    private ImageView imageView;
    private Mathread th1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialization the some attributs
        th1 = new Mathread();
        th1.start();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        imageView = (ImageView) findViewById(R.id.imageView);
        //perrmession for reading the user photos
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_photos"));
        //try to coonect the user
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        namesAlbums();
                    }
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, "" + getResources().getString(R.string.connection_prob), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //oncreate fin



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }



    //using GraphRequest for get all albums
    public void namesAlbums() {
        GraphRequest magraphe = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums?limit=5000",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject json = response.getJSONObject();
                            JSONArray jarray = json.getJSONArray("data");

                            for (int i = 0; i < jarray.length(); i++) {
                                AlbumClass objectAlcumClass = new AlbumClass(
                                        jarray.getJSONObject(i).get("id").toString(),
                                        "0",
                                        jarray.getJSONObject(i).get("name").toString(),
                                        jarray.getJSONObject(i).get("count").toString()
                                );
                                albumListFb.add(objectAlcumClass);
                            }
                            getCovers();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "link,name,count");
        magraphe.setParameters(parameters);
        magraphe.executeAsync();
    }
    //using GraphRequest for get cover image  in albums
    public void getCovers() {
        int sizeee = albumListFb.size();
        for (int i = 0; i < sizeee; i++) {
            final int finalI = i;
            GraphRequest magraphe2 = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + albumListFb.get(finalI).getId() + "/photos?limit=5000",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            JSONObject json = response.getJSONObject();
                            try {
                                JSONArray jarray = json.getJSONArray("data");
                                albumListFb.get(finalI).setSource(jarray.getJSONObject(0).get("source").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            Bundle parameters = new Bundle();
            parameters.putString("fields", "source");
            magraphe2.setParameters(parameters);
            magraphe2.executeAsync();
        }
    }
    //GraphRequests fin






    // this thread for waiting when albumListFb charged on startactivity to Main2
    public class Mathread extends Thread {
        @Override
        public void run() {
            try {
                boolean check = true;
                while (check) {
                    sleep(200);
                    if (albumListFb.size() > 0) {
                        if (albumListFb.get(albumListFb.size() - 1).getSource() != "0") {
                            check = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                                    finish();
                                }
                            });
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //matherad fin


}
