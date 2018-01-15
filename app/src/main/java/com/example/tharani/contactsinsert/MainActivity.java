package com.example.tharani.contactsinsert;
/*import is libraries imported for writing the code
* AppCompatActivity is base class for activities
* Bundle handles the orientation of the activity
*/
import android.Manifest;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /*onCreate is the first method in the life cycle of an activity
   savedInstance passes data to super class,data is pull to store state of application
 * setContentView is used to set layout for the activity
 *R is a resource and it is auto generate file
 * activity_main assign an integer value*/
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 100;
    //declarig variables
    ContentResolver contentResolver;
    Button buttonAdd, buttonViewAll;
    Intent intent;
    EditText ev_diag_contactName,ev_diag_contactNumber;
    String diag_contactName,diag_contactNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //providing id
        buttonAdd = (Button) findViewById(R.id.buttonADD);
        buttonViewAll = (Button) findViewById(R.id.buttonViewAll);
        /*Setting OnClickListener for two button*/
        buttonAdd.setOnClickListener(this);
        buttonViewAll.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {//here taking onClick method
        switch(view.getId()){
            /* A switch statement allows a variable to be tested for equality against a list of values
            *  Each value is called a case*/
            case R.id.buttonADD://taking case for buttonADD
                addItem();//adding Item
                Toast.makeText(MainActivity.this,"buttonADD Clicked",Toast.LENGTH_LONG).show();
                /*A toast is a view containing a quick little message
                LENGTH_LONG Show the view or text notification for a long period of time*/
                break;//terminates statement
            case R.id.buttonViewAll://taking case for buttonViewAll
                intent = new Intent(MainActivity.this,SecondActivity.class);
                //An intent is an abstract description of an operation to be performed. It can be used with startActivity to launch an Activity
                startActivity(intent);//starts activity for intent
                Toast.makeText(MainActivity.this,"buttonViewAll Clicked",Toast.LENGTH_LONG).show();
                /*A toast is a view containing a quick little message
                LENGTH_LONG Show the view or text notification for a long period of time*/
                break;//terminates statement
            default:
                break;//terminates statement
        }
    }

    public void addItem(){
        /** AlertDialog can be used to display the dialog message with OK and Cancel buttons
         * creating new object alert dialog using new
         * and giving reference to MainActivity using this keyword*/
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        alertDialog.setView(inflater.inflate(R.layout.dialog_contact, null))
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog diag = (Dialog) dialog;
                        ev_diag_contactName =  diag.findViewById(R.id.diag_contact_name);
                        ev_diag_contactNumber =  diag.findViewById(R.id.diag_phone_number);
                        //here using get method to get text
                        diag_contactName = ev_diag_contactName.getText().toString();
                        diag_contactNumber = ev_diag_contactNumber.getText().toString();
                         /*here taking Log.e method to write logs and displaying
            * tag Used to identify the source of a log message, usually identifies the class or activity where the log call occurs.*/
                        Log.e("addItem ",diag_contactName);
                        writeContacts();//writes contacts
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();//dialog message for cancel buttons
                    }
                });
        alertDialog.show();//shows alertDialog
    }

   //using private access modifier taking writeContacts
    private void writeContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_WRITE_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            //createContact(diag_contactName,diag_contactNumber);
            insertContact(diag_contactName, diag_contactNumber);
        }
    }
    /**Void is Callback for the result from requesting permissions.
     * onRequestPermissionsResult Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            //taking  if statement tests the condition. It executes the if block if condition is true.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                writeContacts();
            } else {//else is followed by if statement
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_LONG).show();
            /*A toast is a view containing a quick little message
                LENGTH_LONG Show the view or text notification for a long period of time*/
            }
        }
    }
    //inserting Contact
    public void insertContact(String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        //creating new arrayList object
        int rawContactInsertIndex = ops.size();//taking integer for rawContactInsertIndex
        /*taking add method to add ACCOUNT_TYPE,ACCOUNT_NAME*/
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        /*taking add method to DISPLAT_NAME*/
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, firstName) // Name of the person
                .build());
        /*taking add method to add NUMBER,TYPE*/
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number

        try {//taking try block
            ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
     // contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {//exceptions not handled in try block handled by catch block
            Toast.makeText(this, " we canot insert the names", Toast.LENGTH_SHORT).show();
            /*A toast is a view containing a quick little message
                LENGTH_SHORT Show the view or text notification for a short period of time*/
            Log.e("C",e.toString());
        }
        Toast.makeText(this, "Contact Added.", Toast.LENGTH_SHORT).show();
        /*A toast is a view containing a quick little message
                LENGTH_SHORT Show the view or text notification for a short period of time*/
    }
}
