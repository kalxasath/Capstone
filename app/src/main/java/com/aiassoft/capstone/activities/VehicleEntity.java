package com.aiassoft.capstone.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aiassoft.capstone.R;
import com.rany.albeg.wein.springfabmenu.SpringFabMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VehicleEntity extends AppCompatActivity {

    private static final boolean USER_IS_GOING_TO_EXIT = false;

    private Context mContext;
    private Toolbar mToolbar;
    private ViewGroup mRootLayout;
    private ViewGroup mLayoutContainer;
    private Toast mBacktoast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.app_coordinator_container);

        // Root Content Layout
        mRootLayout = this.findViewById(R.id.root_layout);
        // Layout Content
        mLayoutContainer = this.findViewById(R.id.layout_container);
        // Entity
        View.inflate(this, R.layout.activity_vehicle_entity, mLayoutContainer);
        // Fab
        View.inflate(this, R.layout.fab_image, mRootLayout);

        // toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SpringFabMenu sfm = (SpringFabMenu)findViewById(R.id.fab);

        sfm.setOnSpringFabMenuItemClickListener(new SpringFabMenu.OnSpringFabMenuItemClickListener() {
            @Override
            public void onSpringFabMenuItemClick(View view) {
                switch (view.getId()) {
                    case R.id.fab_camera:
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri uri  = Uri.fromFile(getTempFile(mContext));
                        takePicture.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(takePicture, 0);//PICK_IMAGE_CAMERA
                        break;
                    case R.id.fab_gallery:
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);//PICK_IMAGE_GALLERY
                        break;
                }
            }
        });

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getPickImageIntent(mContext);
                startActivityForResult(intent, 1);
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(takePicture, 0);//zero can be replaced with any action code
//                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                //selectImage((Activity) mContext);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        */


        setTitle("Peugeot 307sw");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vehicle_entiry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();

        switch (selectedMenuItem) {
            case android.R.id.home :
                //TODO ask for cancel if the are data in the views
                //finish();
                //return true;
                onBackPressed();
                return true;
                //return false; // calls the onSupportNavigateUp

            case R.id.action_done :
                //TODO check data validity
                //TODO save the data
                //NavUtils.navigateUpFromSameTask(this); Did you forget to add the android.support.PARENT_ACTIVITY <meta-data>  element in your manifest?
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected

    /*
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    */


    /*
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        VehicleEntity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        if(USER_IS_GOING_TO_EXIT) {
            if(mBacktoast !=null&& mBacktoast.getView().getWindowToken()!=null) {
                finish();
            } else {
                mBacktoast = Toast.makeText(this, "Press back to exit", Toast.LENGTH_SHORT);
                mBacktoast.show();
            }
        } else {
            //other stuff...
            super.onBackPressed();
        }
    }
    */

    // (resultCode == RESULT_CANCELED)
    protected void onActivityResult(int requestCode, int resultCode, Intent identData) {
        super.onActivityResult(requestCode, resultCode, identData);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = identData.getData();
                    if (selectedImage == null) {
                        Snackbar.make(mLayoutContainer, "NULL RETURN", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(mLayoutContainer, selectedImage.toString(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    //imageview.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = identData.getData();
                    Snackbar.make(mLayoutContainer, selectedImage.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //imageview.setImageURI(selectedImage);
                }
                break;
        }
    }

    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.select_image_source));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), "tempImage");
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    /**
     * Image Handling Functions
     * https://stackoverflow.com/questions/10165302/dialog-to-pick-image-from-gallery-or-from-camera
     */
    /*
    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    // Select image from camera and gallery
    private void selectImage(Activity activity) {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                imageview.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                imageview.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    */


}
