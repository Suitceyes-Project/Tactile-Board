package com.be.better.tactileboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShowDict extends AppCompatActivity {

    private Button delete;
    private TextView wordList;
    private HashMap<String, String> dict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.show_dict);

        dict = new HashMap<String, String >();
        Intent i = getIntent();
        dict = (HashMap<String, String>) i.getSerializableExtra("dict");

        wordList = (TextView) findViewById(R.id.wordList);

        createWordList();

        delete = (Button) findViewById(R.id.delete);
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(ShowDict.this, "Deleted",
                        Toast.LENGTH_SHORT).show();

                MainActivity.dict.setHashMap(new HashMap<String, String>());
                MainActivity.dict.saveDict(MainActivity.dict.getPrefs().edit());

                Intent i = new Intent(ShowDict.this, MainActivity.class);
                i.putExtra("dict", MainActivity.dict);
                startActivity(i);

                return true;
            }
        });

    }

    private void createWordList() {
        Set<String> words = dict.keySet();
        List<String> tmp = new ArrayList<>(words);
        Collections.sort(tmp);

        for (String key : tmp) {
            String existing = wordList.getText().toString();
            wordList.setText(existing + key + "\n");
        }
    }

}
