package me.ziningzhu.basicchattingbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText mInputText;
    private Button mSendButton;
    private String mSentence;
    private ArrayList<String> mMessages;
    private ListView mListView;
    private MyMessagesAdapter mListAdapter;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputText = (EditText)findViewById(R.id.user_input);
        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSentence = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mMessages = new ArrayList<String>();
        mListView = (ListView)findViewById(R.id.my_list_view);
        mListAdapter = new MyMessagesAdapter(getApplicationContext(), mMessages);
        mListView.setAdapter(mListAdapter);

        mSendButton = (Button)findViewById(R.id.submit_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // read file, append to end of its arrayList, and write back
                if (mMessages == null)
                    mMessages = new ArrayList<String>();
                mMessages.add("me:"+mSentence);
                mMessages.add(mSentence);
                Log.d(TAG, "length of mMessages is " + mMessages.size());

                try {
                    // Starts appending to the new file.
                    File file = new File(getFilesDir(), "message_history.ser");
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream out = new ObjectOutputStream(fos);
                    out.writeObject(mMessages);
                    out.close();
                    fos.close();
                } catch (IOException i) {
                    i.printStackTrace();
                    Log.e(TAG, "IOException when writing memory file.");
                }

                // update the ListView. Already runs on UI Thread so no need to specify
                mListAdapter.notifyDataSetChanged();
                //mListView.refreshDrawableState();



            }
        });


        
    }

    private void cleanUpPreviousMessages() {
        try {
            // Starts appending to the new file.
            File file = new File(getFilesDir(), "message_history.ser");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(null);
            out.close();
            fos.close();
        } catch (IOException i) {
            i.printStackTrace();
            Log.e(TAG, "IOException when cleaning up memory file.");
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Read file to get mMessages. Should be placed in onResume()
        try {
            File file = new File(getFilesDir(), "message_history.ser");
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            mMessages = (ArrayList<String>)oin.readObject();
        } catch(IOException i) {
            i.printStackTrace();
            Log.e(TAG, "Error when reading memory!");
        } catch(ClassNotFoundException c) {
            c.printStackTrace();
            Log.e(TAG, "ClassNotFoundException when reading memory!");
        }
        if (mMessages != null && mMessages.size() > 0) {
            mListAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("EditArea", mSentence);
        editor.apply();

    }
}
