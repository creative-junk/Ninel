package com.crysoft.me.pichat.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.crysoft.me.pichat.Network.AHttpRequest;
import com.crysoft.me.pichat.Network.AHttpResponse;
import com.crysoft.me.pichat.Network.RequestCallback;
import com.crysoft.me.pichat.Network.Urls;
import com.crysoft.me.pichat.R;
import com.crysoft.me.pichat.RecentChats;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.helpers.Utilities;
import com.crysoft.me.pichat.models.UserDetails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterStepThree extends BaseRegisterFragment implements RequestCallback {
    private static final String TAG = "Step 3";

    private static final String IMAGE_DIRECTORY_NAME = Constants.ROOT_FOLDER_NAME;

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int RESULT_LOAD_IMAGE = 102;
    private static final int MOBILE_WIDTH = 500;

    private ImageView ivUserImage;
    private EditText etName;

    private UserDetails myUserDetails;
    private String image;

    private static String filePath;

    private Bitmap photoBitmap;

    private UserDetails userDetails;

    private ProgressDialog progressDialog;

    public RegisterStepThree() {
        // Required empty public constructor
    }

    private Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_register_step_three, container, false);

        File file = new File(Utilities.getBasePath());

        AQUtility.setCacheDir(file);

        MyPreferences myPreferences = new MyPreferences(getActivity());
        myUserDetails = myPreferences.getUserDetails();

        ivUserImage = (ImageView) findViewById(R.id.iv_user_picture);
        ivUserImage.setOnClickListener(this);
        etName = (EditText) findViewById(R.id.et_display_name);
        findViewById(R.id.btn_save).setOnClickListener(this);

        if (myUserDetails.getName() != "") {
            etName.setText(myUserDetails.getName());
        }

        AQuery aQuery = new AQuery(getActivity());
        aQuery.id(ivUserImage).image(Urls.BASE_IMAGE + myUserDetails.getImage());

        return contentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CAMERA_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    resizeBitmap(filePath);
                    image = Utilities.encodeToBase64(photoBitmap);

                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivUserImage.setImageBitmap(photoBitmap);
                            }
                        });
                    } else if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivUserImage.setImageBitmap(photoBitmap);
                            }
                        });
                    }

                }
            }).start();
        } else if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            final Uri contentURI = data.getData();
            filePath = Utilities.getRealPathFromURI(contentURI, getActivity());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    resizeBitmap(filePath);
                    image = Utilities.encodeToBase64(photoBitmap);

                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivUserImage.setImageBitmap(photoBitmap);
                            }
                        });
                    } else if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivUserImage.setImageBitmap(photoBitmap);
                            }
                        });
                    } else {
                        ivUserImage.setImageBitmap(photoBitmap);
                    }
                }
            }).start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            onSaveClick();
        } else if (v.getId() == R.id.iv_user_picture) {
            imageAlertDialog().show();
        }
    }

    private Uri getTempUri() {
        File file = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME);

        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME + "/" + Constants.PROFILE_PICTURES);
        if (!file2.exists()) {
            file2.mkdir();
        }
        filePath = Environment.getExternalStorageDirectory() + "/" + IMAGE_DIRECTORY_NAME
                + Constants.PROFILE_PICTURES + "/" + "Camera_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(filePath);

        if (imageFile.exists()) {
            imageFile.delete();
        }

        if (!imageFile.exists()) {
            try {
                new File(imageFile.getParent()).mkdirs();
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(imageFile);
    }

    private Dialog imageAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity()).setAdapter(new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_list_item_1,
                        new String[]{"Camera", "Gallery"}),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

                        } else if (which == 1) {
                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, RESULT_LOAD_IMAGE);
                        }
                    }
                }
        );
        return builder.create();
    }

    private void resizeBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        int outWidth, outHeight;

        if (filePath != null) {
            BitmapFactory.decodeFile(filePath, options);
            outWidth = options.outWidth;
            outHeight = options.outHeight;
        } else {
            if (photoBitmap != null) {
                outWidth = photoBitmap.getWidth();
                outHeight = photoBitmap.getHeight();
            } else {
                return;
            }
        }

        int ratio = (int) ((((float) outWidth) / MOBILE_WIDTH) + 0.5f);

        if (ratio == 0) {
            ratio = 1;
        }

        if (filePath != null) {
            options.inJustDecodeBounds = false;
            options.inSampleSize = ratio;
            photoBitmap = BitmapFactory.decodeFile(filePath, options);
        } else {
            outWidth = outWidth / ratio;
            outHeight = outHeight / ratio;

            photoBitmap = Bitmap.createScaledBitmap(photoBitmap, outWidth, outHeight, true);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                File file = new File(Environment.getExternalStorageDirectory()
                        + File.separator + IMAGE_DIRECTORY_NAME + File.separator
                        + myUserDetails.getUserId() + ".jpg");
                createIfNotDirectory();

                if (file.exists()){
                    file.delete();
                }
                try{
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bos.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void createIfNotDirectory() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + IMAGE_DIRECTORY_NAME);

        if (file.exists()){
            return;
        } else{
            file.mkdir();
        }
    }
    private void onSaveClick(){
        String name = etName.getText().toString().trim().length() == 0 ?
                myUserDetails.getName() : etName.getText().toString().trim();

        if (name.length() ==0){
            etName.setError("Display Name");
            return;
        }

        progressDialog = ProgressDialog.show(getActivity(), "Loading...","Please Wait");
        AHttpRequest aHttpRequest = new AHttpRequest(getActivity(),this);
        aHttpRequest.editProfile(myUserDetails.getUserId()+"",etName.getText().toString().trim(),
                myUserDetails.getStatus(),image,myUserDetails.getPhoneCode(),myUserDetails.getPhoneNumber());

    }
    @Override
    public void onRequestComplete(AHttpResponse response) {
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        if (response != null && response.isSuccess){
            setStepAsComplete(3);
            startActivity(new Intent(getActivity(), RecentChats.class));
            getActivity().finish();
        }
    }
}
