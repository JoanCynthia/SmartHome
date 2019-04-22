package redeemsystems.com.home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;

public class MyDatabase {
    SQLiteDatabase sdb;
    MyHelper mh;
    public MyDatabase(Context c){
        mh=new MyHelper(c,"configure",null,1);

    }
    public void open(){
        sdb=mh.getWritableDatabase();
    }
    public  void insertdevices(String Rname,String Rid){
        ContentValues cv=new ContentValues();
        cv.put("Rname",Rname);
        cv.put("Rid",Rid);

        sdb.insert("devices",null,cv);

    }

    public void insertregister(String User_type,String User_name,String User_email,String User_pwd,String phone, byte[] image){
        ContentValues cv1=new ContentValues();
        cv1.put("User_type",User_type);
        cv1.put("User_name",User_name);
        cv1.put("User_email",User_email);
        cv1.put("User_pwd",User_pwd);
//        cv1.put("country",country);
        cv1.put("phone",phone);
        cv1.put("image", image);
        sdb.insert("register",null,cv1);
        Cursor c = sdb.query("register", null,null,null,null,null,null);
        while(c.moveToNext())
        System.out.println("~Swapnil " +c.getString(1)+"  "+c.getString(2)+"  "+c.getString(3)+"  "+c.getString(4)+"  "+c.getString(5)+"  "+c.getString(6));
    }



    public void updateAccount(String User_Name, String User_type,String User_name,String User_email,String User_pwd,String phone, byte[] image)
    {
        ContentValues cv1=new ContentValues();
        cv1.put("User_type",User_type);
        cv1.put("User_name",User_name);
        cv1.put("User_email",User_email);
        cv1.put("User_pwd",User_pwd);
//        cv1.put("country",country);
        cv1.put("phone",phone);
        cv1.put("image", image);
        sdb.update("register",cv1,"User_name = ?",new String[]{User_Name});

    }
    public void updatepassword(String phone, String password){
        ContentValues cv2=new ContentValues();
        cv2.put("User_pwd",password);
        sdb.update("register",cv2,"phone= ?",new String[]{phone});

    }

    public void updatedevices(int id,String Rname, String Rid){
        ContentValues cv=new ContentValues();
        cv.put("Rname",Rname);
        cv.put("Rid",Rid);

        sdb.update("devices",cv,"_id = ?",new String[]{(id+1)+""});
    }
    public  Cursor queryDevices(){
        Cursor cursor = sdb.query("devices",null,null,null,null,null,null);
        return cursor;
    }

    public Cursor queryRegister(){
        Cursor cursor1=sdb.query("register",null,null,null,null,null,null);
        return cursor1;
    }

    public Cursor queryRegisterMobile(String number)
    {
        Cursor cursor = null;
        cursor = sdb.query("register", null, "phone = ?", new String[]{number},
                null, null, null);
        return cursor;
    }
    public Cursor queryRegisterEmail(String email)
    {
        Cursor cursor = null;
        cursor = sdb.query("register", null, "User_email = ?", new String[]{email},
                null, null, null);
        return cursor;
    }


    protected void close(){
        sdb.close();
    }

   public Cursor queryByName(String roomName)
   {
       Cursor cursor = sdb.query("devices", null, "Rname = ?", new String[]{roomName}, null, null, null);
       return cursor;
   }
    public class MyHelper extends SQLiteOpenHelper {
        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table devices(_id INTEGER PRIMARY KEY,Rname TEXT,Rid TEXT,Switch1 TEXT,Switch2 TEXT,Switch3 TEXT,Switch4 TEXT,Switch5 TEXT,Switch6 TEXT,Switch7 TEXT,Switch8 TEXT);");

            sqLiteDatabase.execSQL("create table register(_id INTEGER PRIMARY KEY,User_type TEXT,User_name TEXT,User_email TEXT,User_pwd TEXT" +
                    ",phone TEXT, image BLOB);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
