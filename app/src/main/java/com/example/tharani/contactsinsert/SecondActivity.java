package com.example.tharani.contactsinsert;
/*import is libraries imported for writing the code
* AppCompatActivity is base class for activities
* Bundle handles the orientation of the activity
*/
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    /*onCreate is the first method in the life cycle of an activity
   savedInstance passes data to super class,data is pull to store state of application
 * setContentView is used to set layout for the activity
 *R is a resource and it is auto generate file
 * activity_main assign an integer value*/
    MyAdapter myAdapter;
    // The ListView
    //declaring variables
    private ListView lstNames;
    private ArrayList<String> al_contactName,al_contactNumber;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    //taking permissions to read contacts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //creating new ArrayList object for al_contactName,al_contactNumber
        al_contactName = new ArrayList<>();
        al_contactNumber = new ArrayList<>();
        showContacts();//shows contacts
        myAdapter = new MyAdapter();//created new MyAdapter
        lstNames =  findViewById(R.id.lstNames);
        lstNames.setAdapter(myAdapter);
    }
/**here displays all contacts*/
    private void displayAllContacts(){
        // The ContentResolver object communicates with the provider object, an instance of a class that implements ContentProvider
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount()>0){//if statement tests the condition. It executes the if block if condition is true.
            while (cursor.moveToNext()){//The while loop executes a block of code while a boolean expression evaluates to true
                //taking id,name to by giving string
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
                    //if statement tests the condition. It executes the if block if condition is true.
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                            new String[]{id},null);
                    while (pCur.moveToNext()){
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //here by using add method taking name and phoneNo
                        al_contactName.add(name);
                        al_contactNumber.add(phoneNo);
                    }
                }

            }
        }
    }
    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            displayAllContacts();//displaysAllContacts

        }
    }
    /**Void is Callback for the result from requesting permissions.
     * onRequestPermissionsResult Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //if statement tests the condition. It executes the if block if condition is true.
                // Permission is granted
                showContacts();//shows contact
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_LONG).show();
                 /*A toast is a view containing a quick little message
                LENGTH_LONG Show the view or text notification for a long period of time*/
            }
        }
    }

/**creating class MyAdapter which extends BaseAdapter
 * using getter method to gets count,item and item id*/
    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {//gets count
            return al_contactName.size();//returns al_contactName size
        }

        @Override
        public Object getItem(int position) {//gets item
            return null;//returns null
        }

        @Override
        public long getItemId(int position) {//gets item id
            return 0;//returns 0
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                //if statement tests the condition. It executes the if block if condition is true.
                convertView = getLayoutInflater().inflate(R.layout.name_and_contacts,parent,false);
                holder = new ViewHolder();//created new ViewHolder
                holder.bindView(convertView);
                convertView.setTag(holder);

            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
            //here setText for al_contactName,al_contactNumber
            holder.contactName.setText(al_contactName.get(position));
            holder.contactNumber.setText(al_contactNumber.get(position));
            return convertView;
        }
    }
    //created class ViewHolder
    public class ViewHolder{
        //declaring variables
        TextView contactName, contactNumber;
        void bindView(View convertView){//void does not return any method and binds view
            contactName = convertView.findViewById(R.id.contact_name);
            contactNumber = convertView.findViewById(R.id.contact_number);
        }

    }
}