package br.usp.trabalhoandroid;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final int CAMERA_PIC_REQUEST = 1;
    ImageView imgProfilePic;
    Button btnChangePic, btnEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle(getResources().getString(R.string.profile));

        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        TextView tvGender = (TextView) view.findViewById(R.id.tvSex);
        TextView tvBirth = (TextView) view.findViewById(R.id.tvBirth);
        TextView tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        TextView tvPassword = (TextView) view.findViewById(R.id.tvPassword);

        tvName.setText(Constants.NAME);
        tvEmail.setText(Constants.EMAIL);
        tvGender.setText(Constants.GENDER);
        tvBirth.setText(Constants.BIRTH);
        tvUsername.setText(Constants.USERNAME);
        tvPassword.setText(Constants.PASSWORD);

        imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
        btnEditProfile = (Button) view.findViewById(R.id.btnEditProfile);
        btnChangePic = (Button) view.findViewById(R.id.btnChangeProfilePic);

        loadImageFromStorage(Environment.getExternalStorageDirectory());

        btnChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CAMERA_PIC_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    saveToInternalStorage(thumbnail);
                    imgProfilePic.setImageBitmap(thumbnail);
                    ((MainActivity)getActivity()).loadImageFromStorage();
                }
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage)
    {
        File mypath = new File(Environment.getExternalStorageDirectory(),Constants.USERNAME + ".jpg");

        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImageFromStorage(File path)
    {

        try {
            File f=new File(path, Constants.USERNAME + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imgProfilePic.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {

        }

    }
}



