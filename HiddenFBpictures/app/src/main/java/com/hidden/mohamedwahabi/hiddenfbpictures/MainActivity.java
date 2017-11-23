package com.hidden.mohamedwahabi.hiddenfbpictures;

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
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    ImageView imageView;
    public static ArrayList<albumClass> albumListFb=new ArrayList<albumClass>();
    mathread th1;
    private static final String[] PERMISSIONS = {"user_photos"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        th1=new mathread();
        th1.start();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        //check if user conncet
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        imageView=(ImageView)findViewById(R.id.imageView);
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_photos"));






        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        namesAlbums();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });










    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Picasso.with(this).load(Profile.getCurrentProfile().getProfilePictureUri(800, 800)).into(imageView);

    }


    public void namesAlbums(){

        GraphRequest magraphe= new GraphRequest(
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
                                albumClass objectAlcumClass=new albumClass(
                                        jarray.getJSONObject(i).get("id").toString(),
                                        "0",
                                        jarray.getJSONObject(i).get("name").toString(),
                                                jarray.getJSONObject(i).get("count").toString()+" photos"

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




    public void getCovers(){
        int sizeee=albumListFb.size();
        for (int i=0;i<sizeee;i++) {
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

            /* handle the result */
                        }
                    }
            );

            Bundle parameters = new Bundle();
            parameters.putString("fields", "source");
            magraphe2.setParameters(parameters);
            magraphe2.executeAsync();
        }

    }











    public class mathread extends Thread{
        @Override
        public void run() {
            try {
                boolean check = true;
                while (check){
                    sleep(200);
                    if(albumListFb.size()>0){
                        if(albumListFb.get(albumListFb.size()-1).getSource()!="0"){
                        check=false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    }
                });
            }}}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
