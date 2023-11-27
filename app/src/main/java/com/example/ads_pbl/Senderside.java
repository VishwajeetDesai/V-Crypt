package com.example.ads_pbl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class Senderside extends AppCompatActivity {
   private Button send;
   private EditText sendermsg;
   private FirebaseFirestore db;
   private String msg;
   private Boolean isStringvalid;

   private TextView showmessage;
   Boolean uniquecodeword;
   private EditText codeword;
   private String codeWord;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.senderside);
        db=FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        showmessage=findViewById(R.id.textView2);
        registerForContextMenu(showmessage);

        send=findViewById(R.id.send);

        sendermsg=findViewById(R.id.Sendermsg);
        codeword = findViewById(R.id.codeWord);
        uniquecodeword = true;
        isStringvalid = true;

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeWord = codeword.getText().toString();
                msg=sendermsg.getText().toString();
                for (int i =0;i<msg.length();i++) {
                    if(Character.isLowerCase(msg.charAt(i)) || msg.charAt(i)==' '){
                    } else{
                        isStringvalid = false;
                        break;
                    }

                }
                if(isStringvalid){
                    int key=(int)(Math.random()*100);
                    db.collection("codewordkey").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot d : list){
                                Codekey c = d.toObject(Codekey.class);
                                if(c.codeword.equals(codeWord)){
                                    uniquecodeword = false;
                                    Toast.makeText(Senderside.this, "Codeword already in use", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Senderside.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if(uniquecodeword) {
                        CollectionReference dbcodewordkey = db.collection("codewordkey");
                        Codekey codekey = new Codekey(codeWord, key);
                        dbcodewordkey.add(codekey).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(Senderside.this, "Return successful", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Senderside.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });



                        Toast.makeText(Senderside.this, Integer.toString(key), Toast.LENGTH_SHORT).show();
                        String encryptedtext= encrypttext(sendermsg.getText().toString(),key);
                        Toast.makeText(Senderside.this, encryptedtext, Toast.LENGTH_SHORT).show();
                        showmessage.setText(encryptedtext);
//                    textView.setText(encryptedtext);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("*/*");
                        String[] addresses={"vishwajeetdesai315@gmail.com"};
                        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "My subject");
                        intent.putExtra(Intent.EXTRA_TEXT, encryptedtext);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }



                } else{
                    Toast.makeText(Senderside.this, "Only Lowercase please", Toast.LENGTH_SHORT).show();
                }
                }

        });




    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(),0, "Copy");
        menu.setHeaderTitle("Copy text"); //setting header title for menu
        TextView textView = (TextView) v;// calling our textView
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        manager.setPrimaryClip(clipData);
    }
    public static String encrypttext(String actualtext,int key){

        HashMap<Character,Character> hashtable=new HashMap<>();
        for (int i = 97; i < 123; i++) {

            hashtable.put((char)i,(char) ((((i+key)*11)%26)+97));

        }
        hashtable.put(' ', ' ');
        char [] actualtextarray = actualtext.toCharArray();
        String encryptedtext = "";
        for (int i = 0; i < actualtext.length(); i++) {

            encryptedtext += hashtable.get(actualtextarray[i]);

        }
        return encryptedtext;




    }

}
