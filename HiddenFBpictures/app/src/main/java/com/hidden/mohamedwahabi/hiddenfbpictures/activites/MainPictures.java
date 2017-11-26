package com.hidden.mohamedwahabi.hiddenfbpictures.activites;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hidden.mohamedwahabi.hiddenfbpictures.classes.Pagination;
import com.hidden.mohamedwahabi.hiddenfbpictures.R;
import com.hidden.mohamedwahabi.hiddenfbpictures.classes.PicturesClass;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.hidden.mohamedwahabi.hiddenfbpictures.activites.Main2Activity.albumPic;

public class MainPictures extends AppCompatActivity {
    //arraylist of the pictures in one page
    public static ArrayList<PicturesClass> albumPage = new ArrayList<PicturesClass>();
    //grid for display pictures in album
    private GridView gridView;
    private Button btnPrev;
    private Button btnNext;
    // count the current numbers of albums in all albums
    private int count = 0;
    //number of item in one page this variable can changed in string
    private int num_item_picturs;
    private Pagination page;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pictures);
        gridView = (GridView) findViewById(R.id.pictlist);
        //initialization the some attributs
        num_item_picturs = getResources().getInteger(R.integer.name_of_item_in_page_pictures);
        btnPrev = (Button) findViewById(R.id.prev);
        btnNext = (Button) findViewById(R.id.next);
        page = new Pagination(albumPic, num_item_picturs);
        page.charge(count, num_item_picturs, albumPage, btnNext, btnPrev);
        //change actionbar name to name the album
        getSupportActionBar().setTitle(getIntent().getStringExtra("nameAlbum"));
        //get first group of pictures
        allListItems();
    }
    //oncreate fin



    @Override
    public void onBackPressed() {
        //clear my albumPic list when the user back to Mai2Activity
        albumPic.clear();
        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.getItem(0).setIcon(R.drawable.upload);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.niceicone) {
            //progress for count the progressing of upload
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage("Upload to Firebase");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setProgress(0);
            progress.show();
            progress.setMax(albumPic.size());
            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://hiddenfbpictures.appspot.com/" + Profile.getCurrentProfile().getId() + "/" + getIntent().getStringExtra("nameAlbum") + "/");
            ImageView loadimage = (ImageView) findViewById(R.id.uploadImg);
            for (int i = 0; i < albumPic.size(); i++) {
                StorageReference mapice = storageReference.child(albumPic.get(i).getId() + ".jpg");
                Picasso.with(getApplicationContext()).load(albumPic.get(i).getUrlpic()).into(loadimage);
                loadimage.setDrawingCacheEnabled(true);
                loadimage.buildDrawingCache();
                // Get the data from an ImageView as bytes
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
                        if (finalI == albumPic.size() - 2 ||albumPic.size()==1 ) {
                            Toast.makeText(MainPictures.this, getResources().getString(R.string.upload_finish), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progress.setProgress(finalI + 1);
                    }
                });
            }
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(MainPictures.this, ""+getResources().getString(R.string.upload_prob), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainPictures.this,Main2Activity.class));
                        }
            });
        }
        return super.onOptionsItemSelected(item);
    }
    //itemselected fin



    //button next click
    public void next(View view) {
        count = page.newtpage(count, albumPage, btnNext, btnPrev);
        allListItems();
    }



    //button preview click
    public void prev(View view) {
        count = page.prevpage(count, albumPage, btnNext, btnPrev);
        allListItems();
    }



    //AlbumAdabter for display my pictures in picturerow and get this layouts to layout of MainPicture
    public class picAdapter extends BaseAdapter {
        ArrayList<PicturesClass> picList = new ArrayList<PicturesClass>();

        public picAdapter(ArrayList<PicturesClass> picList) {
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
                    Dialog dialog = new Dialog(MainPictures.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.setContentView(R.layout.imageone);
                    dialog.setCancelable(true);
                    dialog.show();
                    ImageView imageone = (ImageView) dialog.findViewById(R.id.imafeFullScreen);
                    Picasso.with(getApplicationContext()).load(picList.get(position).getUrlpic()).into(imageone);
                }
            });
            return view;
        }
    }
    //adapter fin



    //charge my adapter from mylist
    public void allListItems() {
        gridView.invalidateViews();
        picAdapter ml = new picAdapter(albumPage);
        gridView.setAdapter(ml);
    }
}
