package redeemsystems.com.home;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Account extends Fragment
{
    CircleImageView profilePic;
    TextView userName, changePassword;
    EditText fullName, phoneNumber, email;
    MainActivity mainActivity;
    Cursor cursor;
    String phone, emailId, name, profile_pic;
    public static final int PICK_IMAGE = 1;
    Uri imageUri;
    Bitmap bitmap;
    byte[] profile;
    Button save;

    public Account() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        profilePic = view.findViewById(R.id.profile_pic);
        userName = view.findViewById(R.id.userName);
        changePassword = view.findViewById(R.id.changepwd);
        fullName = view.findViewById(R.id.fullname);
        phoneNumber = view.findViewById(R.id.mobilenum);
        email = view.findViewById(R.id.email);
        save = view.findViewById(R.id.save);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Profile");
        userName.setText(MainActivity.loginName);
        fullName.setText(MainActivity.loginName);
        phoneNumber.setText(MainActivity.loginPhone);
        email.setText(MainActivity.loginEmail);
        if(mainActivity.myDatabase != null)
        {
            Cursor cursor = mainActivity.myDatabase.queryRegisterMobile(MainActivity.loginPhone);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToNext();
                byte[] image = cursor.getBlob(6);
                if(image != null && image.length > 0) {
                    Bitmap photo = getImage(image);
                    profilePic.setImageBitmap(Bitmap.createScaledBitmap(photo, 200, 200, false));
                }
            }
        }
//        if(MainActivity.loginImage != null && MainActivity.loginImage.length > 5){
//            Bitmap photo = getImage(MainActivity.loginImage);
//            profilePic.setImageBitmap(Bitmap.createScaledBitmap(photo, 200, 200, false));
//        }
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intentPickImage = new Intent();
                    intentPickImage.setType("image/*");
                    intentPickImage.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intentPickImage, "Select Picture"), PICK_IMAGE);
                }
                catch (Exception e)
                {
                    Log.d("pic", e.toString());
                }
                catch (OutOfMemoryError e)
                {
                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity= (MainActivity) getActivity();
                mainActivity.loadchangepassword();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = fullName.getText().toString().trim();
                phone = phoneNumber.getText().toString().trim();
                emailId = email.getText().toString().trim();
                if(bitmap != null) {
                    //                        profile = getStringFromBitmap(bitmap);
                    profile = getBytes(bitmap);
                    Log.d("joan", profile.length+"");
//                    if(profile.length > 1120182)
//                    {
//                        Toast.makeText(mainActivity, "Please select some other picture", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                }
                else {
                    profile = MainActivity.loginImage;
                }
                MainActivity.loginImage = profile;
                mainActivity.myDatabase.updateAccount(MainActivity.loginName, MainActivity.loginUser,
                        name, emailId, MainActivity.loginPassword, phone, profile);
                Toast.makeText(mainActivity, "Updated", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE)
        {
            if(resultCode == RESULT_OK && data != null)
            {
                imageUri = data.getData();//data store in image uri
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
//                        bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(),imageUri);
                    }
                    profilePic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //NEWLY ADDED
                } catch (OutOfMemoryError e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                catch (Throwable e)
                {

                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
                //HANDLE MEMORY EXCEPTION - DONE
                //OTHER POSSIBLE EXCEPTION - DONE
            }
        }
    }
//    public String getStringFromBitmap(Bitmap bitmapPicture) throws UnsupportedEncodingException {
//
//        try {
//            final int COMPRESSION_QUALITY = 1000;
//            String encodedImage;
//            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
//            bitmapPicture.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY,
//                    byteArrayBitmapStream);
//            byte[] b = byteArrayBitmapStream.toByteArray();
//            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//            //WRITE EXCEPTION HANDLING FOR MEMORY ISSUES - DONE
//            return encodedImage;
//        }
//        catch (OutOfMemoryError e){
//            e.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        catch (Throwable e)
//        {
//            e.printStackTrace();
//            Log.d("exception", e.toString());
//        }
//        return null;
//    }

//    public Bitmap getBitmapFromString(String jsonString) {
//        try {
//            byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            return decodedByte;
//        }catch (NumberFormatException exception)
//        {
//            Log.d("bitmap-exception", exception.toString());
//            return null;
//        }
//        catch (OutOfMemoryError e)
//        {
//            e.printStackTrace();
//            Log.d("exception", e.toString());
//            return null;
//        }
//        catch (Throwable e)
//        {
//            e.printStackTrace();
//            Log.d("exception", e.toString());
//            return null;
//        }
//    }
//
//    // convert from bitmap to byte array
    public byte[] getBytes(Bitmap bitmap) {
        final int COMPRESSION_QUALITY = 100;
        profilePic.setDrawingCacheEnabled(true);
        profilePic.buildDrawingCache();
        Bitmap bitmap1 = profilePic.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
