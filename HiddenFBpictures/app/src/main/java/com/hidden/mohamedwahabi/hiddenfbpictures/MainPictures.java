package com.hidden.mohamedwahabi.hiddenfbpictures;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.util.ArrayList;

import static android.R.attr.data;
import static com.hidden.mohamedwahabi.hiddenfbpictures.Main2Activity.albumPic;
import static com.hidden.mohamedwahabi.hiddenfbpictures.MainActivity.albumListFb;

public class MainPictures extends AppCompatActivity {
GridView gridView;
     Button btn_prev;
     Button btn_next;
    int count=0;
    public static ArrayList<picturesClass> albumPage=new ArrayList<picturesClass>();
    int num_item_picturs= 0;
      Pagination page;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.getItem(0).setIcon(R.drawable.upload);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pictures);
        gridView=(GridView)findViewById(R.id.pictlist);
        getSupportActionBar().setTitle(getIntent().getStringExtra("nameofalbum"));
        num_item_picturs= Integer.parseInt(getResources().getString(R.string.name_of_item_in_page_pictures));
        btn_prev     = (Button)findViewById(R.id.prev);
        btn_next     = (Button)findViewById(R.id.next);
        page=new Pagination(albumPic,num_item_picturs);
        page.charge(count,num_item_picturs,albumPage,btn_next,btn_prev);
        allListItems();



    }

    @Override
    public void onBackPressed() {
        albumPic.clear();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.niceicone) {
            final ProgressDialog progress=new ProgressDialog(this);
            progress.setMessage("Upload to Firebase");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setProgress(0);
            progress.show();
            progress.setMax(albumPic.size());
            final int totalProgressTime = albumPic.size();

            FirebaseStorage  storage = FirebaseStorage.getInstance();
            StorageReference   storageReference=storage.getReferenceFromUrl("gs://hiddenfbpictures.appspot.com/"+Profile.getCurrentProfile().getId()+"/"+getIntent().getStringExtra("nameofalbum")+"/");
            ImageView loadimage = (ImageView)findViewById(R.id.uploadImg);
            Toast.makeText(this, ""+albumPic.size(), Toast.LENGTH_SHORT).show();
            for (int i=0;i<albumPic.size();i++) {
                StorageReference  mapice=storageReference.child(albumPic.get(i).getId()+".jpg");
                Picasso.with(getApplicationContext()).load(albumPic.get(i).getUrlpic()).into(loadimage);
                loadimage.setDrawingCacheEnabled(true);
                loadimage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) loadimage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mapice.putBytes(data);
                final int finalI = i;
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(MainPictures.this, getResources().getString(R.string.upload_prob) + exception, Toast.LENGTH_SHORT).show();
                        progress.dismiss();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if(finalI==albumPic.size()-2)
                        {
                            Toast.makeText(MainPictures.this, getResources().getString(R.string.upload_finish), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        progress.setProgress(finalI +1);



                    }
                });
            }
               }
        return super.onOptionsItemSelected(item);
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

    public void Next(View view) {
      count=  page.newtpage(count,albumPage,btn_next,btn_prev);
        allListItems();
    }

    public void Prev(View view) {
        count=  page.prevpage(count,albumPage,btn_next,btn_prev);
        allListItems();
    }



}
