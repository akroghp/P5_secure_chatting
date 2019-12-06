package aau.itcom.group_2.p5_secure_chatting.adding_contacts;

import aau.itcom.group_2.p5_secure_chatting.ListUsersActivity;
import aau.itcom.group_2.p5_secure_chatting.R;
import aau.itcom.group_2.p5_secure_chatting.create_account.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {

    private final static String TAG = "AddContactActivity";
    FirebaseDatabase database;
    EditText editText_addContact;
    String email;
    String requestedID;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    User user;
    String userID;
    ContactRequest contactRequest;
    ArrayList<ContactRequest> contactRequests;
    User currentUser;
    String currentUserId;
    Contact contact;
    ListView requestList;
    ProgressDialog pd;
    int counter = 0;
    int totalUsers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editText_addContact = findViewById(R.id.editText_addContact);
        requestList = findViewById(R.id.requestList);

        contactRequests = new ArrayList<>();

        pd = new ProgressDialog(AddContactActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            database = FirebaseDatabase.getInstance();
            /**
             * Acessing user first names on the database and add them to arrayList
             */
            database.getReference("users").child(currentUserId).child("contactRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        contactRequest = postSnapshot.getValue(ContactRequest.class);

                        contactRequests.add(contactRequest);

                        Log.i(TAG, "Name of contact request: " + contactRequests.get(totalUsers).getMessage());

                        totalUsers++;
                    }
                    pd.dismiss();

                    /**
                     * Hide list if no users are found
                     */
                    if (totalUsers < 1) {
                        requestList.setVisibility(View.GONE);
                        totalUsers = 0;

                    } else {
                        requestList.setVisibility(View.VISIBLE);
                        requestList.setAdapter(new CustomAdapter(contactRequests, AddContactActivity.this));

                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }


    }

    public void sendContactRequest (View view){
        /**
         * First finding the id that is searched with the email
         */
        email = editText_addContact.getText().toString();

        if (!email.equals("")) {

            database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String emailDatabase = String.valueOf(postSnapshot.child("email").getValue());
                        currentUserId = mAuth.getUid();
                        currentUser = dataSnapshot.child(currentUserId).getValue(User.class);
                        counter ++;
                        Log.i(TAG, emailDatabase);
                        Log.i(TAG, "typed mail: " +  email);

                        if (email.equals(currentUser.getEmail())){
                            editText_addContact.setError("Error (You cannot send a request to yourself)");
                        } else if (email.equals(emailDatabase)) {
                            requestedID = String.valueOf(postSnapshot.child("id").getValue());


                            Log.i(TAG, "id: " + requestedID + " users name" + currentUser.getName());
                            if (currentUser!=null){
                                contact = new Contact(currentUser.getName(), currentUser.getLastName(), currentUser.getEmail(), currentUser.getPhoneNumber(), currentUser.getID());
                                contactRequest = new ContactRequest(contact);
                                database.getReference().child("users").child(requestedID).child("contactRequests")
                                        .child(contactRequest.getContactRequestID()).setValue(contactRequest);
                                Log.i(TAG, "Adding contact request to firebase");
                            }
                            editText_addContact.setText("");
                            break;
                        } else if (counter == dataSnapshot.getChildrenCount()){
                            editText_addContact.setError("No user found with this email");
                            counter = 0;
                        }
                    }



                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


        } else{
            editText_addContact.setError("Please enter an email");
        }


    }

    public void acceptRequest (final ContactRequest contactRequest){

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userID = mAuth.getUid();
        contact = null;


        database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(userID).getValue(User.class);
                if (user != null) {
                    contact = new Contact(user.getName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getID());
                    Log.i(TAG, "contact " + contact.getName());
                }
                // Adding to this users contacts
                database.getReference().child("users").child(userID).child("contacts").child(contactRequest.getContactRequestID()).setValue(contactRequest.getContact());
                // Adding to senders contacts
                database.getReference().child("users").child(contactRequest.getContactRequestID()).child("contacts").child(userID).setValue(contact);

                // Removing request
                database.getReference().child("users").child(userID).child("contactRequests").child(contactRequest.getContactRequestID()).removeValue();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void declineRequest (final ContactRequest contactRequest){
        // Removing request
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userID = mAuth.getUid();
        Log.i(TAG, userID);
        Log.i(TAG, contactRequest.getContactRequestID());


        database.getReference().child("users").child(userID).child("contactRequests").child(contactRequest.getContactRequestID()).removeValue();



    }
}