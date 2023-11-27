package com.example.ads_pbl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Receiverside extends AppCompatActivity {
    private Button decrypt;
    EditText encryptedtext;
    EditText codeword;
    private FirebaseFirestore db;
    private int key;
    private TextView showencryptedmessage;
    String codeWord;
    String decryptedtext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiverside);
        Intent intent=getIntent();
        decrypt=findViewById(R.id.decrypt);
        encryptedtext=findViewById(R.id.encryptedtext);
        codeword=findViewById(R.id.codeword);
        showencryptedmessage=findViewById(R.id.textView3);
        decryptedtext = "";
        key = 0;
        db=FirebaseFirestore.getInstance();



        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeWord = codeword.getText().toString();
                db.collection("codewordkey").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()  {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot d : list){
                            Codekey codekey = d.toObject(Codekey.class);
                            if(codekey.codeword.equals(codeWord)){
                                key = codekey.key;
//                                Toast.makeText(Receiverside.this, Integer.toString(key), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Receiverside.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                decryptedtext= decrypt(encryptedtext.getText().toString(),key);
//                                Toast.makeText(Receiverside.this, decryptedtext, Toast.LENGTH_SHORT).show();
                                showencryptedmessage.setText(decryptedtext);
                            }
                        }
                );


            }
        });
    }
    String decrypt(String encryptedtext,int key){
        HashMap<Character,Character> hashtable=new HashMap<>();
        for (int i = 97; i < 123; i++) {
            hashtable.put((char)i,(char) ((((i+key)*11)%26)+97));

        }
        hashtable.put(' ', ' ');
        char[]encryptedtextarray=encryptedtext.toCharArray();
        String decryptedtext="";
        for (int i = 0; i < encryptedtextarray.length; i++) {
            for(Map.Entry<Character,Character> entry : hashtable.entrySet()){
                if(entry.getValue()==encryptedtextarray[i]){
                    decryptedtext += entry.getKey();
                }
            }

        }
        return decryptedtext;
    }


}
