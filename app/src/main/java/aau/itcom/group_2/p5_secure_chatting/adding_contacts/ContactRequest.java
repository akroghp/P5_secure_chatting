package aau.itcom.group_2.p5_secure_chatting.adding_contacts;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.UUID;

import aau.itcom.group_2.p5_secure_chatting.create_account.User;
import aau.itcom.group_2.p5_secure_chatting.key_creation.Keys;
import androidx.annotation.NonNull;

public class ContactRequest {



    String contactRequestID;
    Contact contact;
    String message;
    String userID;
    User user;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;




    public ContactRequest(Contact contact) {
        this.contactRequestID = contact.getId();
        this.contact = contact;
        this.message = contact.getName() + " " + contact.getLastName() + " would like to add you to his contacts";
    }
    public ContactRequest(){

    }


    public void updateUI(){

    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getContactRequestID() {
        return contactRequestID;
    }

    public void setContactRequestID(String contactRequestID) {
        this.contactRequestID = contactRequestID;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
