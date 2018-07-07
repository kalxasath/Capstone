package com.aiassoft.capstone.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiassoft.capstone.BuildConfig;
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.utilities.FileUtils;
import com.rany.albeg.wein.springfabmenu.SpringFabMenu;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicleEntity extends AppCompatActivity {

    private static final String LOG_TAG = MyApp.APP_TAG + VehicleEntity.class.getSimpleName();

    private static final boolean USER_IS_GOING_TO_EXIT = false;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 1;

    private static final int TYPE_OF_DEFAULT = 0;
    private static final int TYPE_OF_NAME = 1;
    private static final int TYPE_OF_MAKE = 2;
    private static final int TYPE_OF_MODEL = 3;

    private static String mTempPhotoPath = null;
    private static File mTempPhotoFile = null;

    private static TextWatcher mTextWatcher = null;

    private Context mContext;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private View mTitleView;
    private TextView mTitle;
    private ViewGroup mRootLayout;
    private ViewGroup mLayoutContainer;
    private ImageView mToolbarPhoto;
    private Toast mBacktoast;

    @BindView(R.id.name_wrapper) TextInputLayout mNameWrapper;
    @BindView(R.id.name) TextInputEditText mName;
    @BindView(R.id.make_wrapper) TextInputLayout mMakeWrapper;
    @BindView(R.id.make) TextInputEditText mMake;
    @BindView(R.id.model_wrapper) TextInputLayout mModelWrapper;
    @BindView(R.id.model) TextInputEditText mModel;

//    TextInputLayout mNameWrapper;
//    TextInputEditText mName;
//    TextInputLayout mMakeWrapper;
//    TextInputEditText mMake;
//    TextInputLayout mModelWrapper;
//    TextInputEditText mModel;


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
        // Toolbar layout, to set the title
        mCollapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        // Layout Content
        mLayoutContainer = this.findViewById(R.id.layout_container);
        // Entity
        View.inflate(this, R.layout.activity_vehicle_entity, mLayoutContainer);
        // Fab
        View.inflate(this, R.layout.fab_image_menu, mRootLayout);

        // toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // toolbar photo
        mToolbarPhoto = findViewById(R.id.toolbar_photo);

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
                        startCamera();
                        break;
                    case R.id.fab_gallery:
                        startGallery();
                        break;
                }
            }
        });

        ButterKnife.bind(this);

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEntityTitle(s.toString());
            }
        };

        mName.addTextChangedListener(mTextWatcher);
        mMake.addTextChangedListener(mTextWatcher);
        mModel.addTextChangedListener(mTextWatcher);

        setEntityTitle("Peugeot 307sw");
    }

    private void setEntityTitle(String s) {
        String title = "";
        String name = mName.getText().toString();
        String make = mMake.getText().toString();
        String model = mModel.getText().toString();

        if (!name.isEmpty()) {
            title = name;
        } else {

            if (make.isEmpty() && model.isEmpty()) {
                if (s.isEmpty()) {
                    title = getString(R.string.title_activity_vehicle_entity);
                } else {
                    title = s;
                }
            }

            if (title.isEmpty()) {
                if (!s.isEmpty() && name.equals(s)) {
                    title = name;
                } else {
                    if (!make.isEmpty() && model.isEmpty()) {
                        title = make;
                    } else if (make.isEmpty() && !model.isEmpty()) {
                        title = model;
                    } else {
                        title = make + " " + model;
                    }
                }
            }
        }

        mCollapsingToolbarLayout.setTitle(title);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTempPhotoFile != null) {
            mTempPhotoFile.delete();
        }
    }

    /**
     * Camera & Gallery Handling
     */
    void startCamera() {
        try {
            dispatchTakePictureIntent();
        } catch (IOException e) {
            Log.e(LOG_TAG, getString(R.string.error_while_creating_file));
            Snackbar.make(mRootLayout, getString(R.string.error_while_creating_file), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // delete previous created temp file
            if (mTempPhotoFile != null) {
                mTempPhotoFile.delete();
            }

            // Create the Temp File where the photo should go
            try {
                mTempPhotoFile = FileUtils.createTempFile(mContext, "ve_", ".jpg");
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }

            // Continue only if the File was successfully created
            if (mTempPhotoFile != null) {
                //Uri photoURI = Uri.fromFile(photoFile);
                mTempPhotoPath = "file:" + mTempPhotoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(mContext,BuildConfig.APPLICATION_ID + ".provider", mTempPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    void startGallery() {
        try {
            dispatchPickPictureIntent();
        } catch (IOException e) {
        }
    }

    private void dispatchPickPictureIntent() throws IOException {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Ensure that there's a gallery activity to handle the intent
        if (pickPhoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent identData) {
        super.onActivityResult(requestCode, resultCode, identData);
        switch(requestCode) {
            case REQUEST_TAKE_PHOTO:
                switch(resultCode) {
                    case RESULT_OK:
                        mToolbarPhoto.setImageURI(Uri.parse(mTempPhotoPath));
                        return;
                    case RESULT_CANCELED:
                        // TODO remove temp file
                        return;
                }
                break;
            case REQUEST_PICK_PHOTO:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = identData.getData();
                    Snackbar.make(mLayoutContainer, selectedImage.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mToolbarPhoto.setImageURI(selectedImage);
                }
                break;
        }
    }


    /**
     *
     */
    /** @param root usually Activity.getWindow().getDecorView() or your custom Toolbar */
    public static @Nullable View findActionBarTitle(@NonNull View root) {
        return findActionBarItem(root, "action_bar_title", "mTitleTextView");
    }
    /** @param root usually Activity.getWindow().getDecorView() or your custom Toolbar */
    public static @Nullable View findActionBarSubTitle(@NonNull View root) {
        return findActionBarItem(root, "action_bar_subtitle", "mSubtitleTextView");
    }

    private static @Nullable View findActionBarItem(@NonNull View root,
                                                    @NonNull String resourceName, @NonNull String toolbarFieldName) {
        View result = findViewSupportOrAndroid(root, resourceName);

        if (result == null) {
            View actionBar = findViewSupportOrAndroid(root, "action_bar");
            if (actionBar != null) {
                result = reflectiveRead(actionBar, toolbarFieldName);
            }
        }
        if (result == null && root.getClass().getName().endsWith("widget.Toolbar")) {
            result = reflectiveRead(root, toolbarFieldName);
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private static @Nullable View findViewSupportOrAndroid(@NonNull View root, @NonNull String resourceName) {
        Context context = root.getContext();
        View result = null;
        if (result == null) {
            int supportID = context.getResources().getIdentifier(resourceName, "id", context.getPackageName());
            result = root.findViewById(supportID);
        }
        if (result == null) {
            int androidID = context.getResources().getIdentifier(resourceName, "id", "android");
            result = root.findViewById(androidID);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T reflectiveRead(@NonNull Object object, @NonNull String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T)field.get(object);
        } catch (Exception ex) {
            Log.w("HACK", "Cannot read " + fieldName + " in " + object, ex);
        }
        return null;
    }

}
