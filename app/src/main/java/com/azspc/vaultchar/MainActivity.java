package com.azspc.vaultchar;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;

import static com.azspc.vaultchar.Property.multiProperties;

public class MainActivity extends AppCompatActivity {
    String[] targets;
    String[] characters;
    String[] properties;
    RecyclerView rv;
    public static final String separator = "=", splitter = ">";
    boolean turnUp = false;

    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        rv = findViewById(R.id.prop);
        rv.setLayoutManager(new LinearLayoutManager(this));
        initData();
        generateRandom(null);
    }

    private void initData() {
        ArrayList<String> t = new ArrayList<>();
        ArrayList<String> c = new ArrayList<>();
        ArrayList<String> p = new ArrayList<>();
        for (String s : getFromCloud(getString(R.string.data_url), "savedData"))
            try {
                String type = s.split(separator)[0];
                if ("t".equals(type)) t.add(s.substring(2));
                if ("c".equals(type)) c.add(s.substring(2));
                if ("p".equals(type)) p.add(s.substring(2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        targets = t.toArray(new String[0]);
        characters = c.toArray(new String[0]);
        properties = p.toArray(new String[0]);
    }

    public void turnUpDown(View v) {
        findViewById(R.id.fablay).setVisibility((turnUp = !turnUp) ? View.VISIBLE : View.INVISIBLE);
    }

    String generateProperties(HashSet<Integer> set) {
        set.add((int) (Math.random() * properties.length));
        if (set.size() < 6)
            generateProperties(set);
        StringBuilder ret = new StringBuilder();
        for (Integer i : set) ret.append(" ").append(i);
        return ret.toString();
    }

    @SuppressLint("SetTextI18n")
    public void generateRandom(View v) {
        ((EditText) findViewById(R.id.char_code)).setText("" +
                ((int) (Math.random() * targets.length)) + " " +
                ((int) (Math.random() * characters.length)) +
                generateProperties(new HashSet<Integer>()));
        convert(null);
    }

    public void convert(View v) {
        try {
            String[] id = ((EditText) findViewById(R.id.char_code)).getText().toString().split(" ");
            int[] arr = new int[8];
            for (int i = 0; i < 8; i++) arr[i] = Integer.parseInt(id[i]);
            rv.setAdapter(new ProretyAdapter(this, initProperties(arr)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Ошибка", Toast.LENGTH_SHORT).show();
        }
    }

    ArrayList<Property> initProperties(int[] code) {
        return new ArrayList<>(multiProperties(getResources(), (""
                + getData(targets, code[0]) + separator
                + getData(characters, code[1]) + separator
                + getData(properties, code[2]) + separator
                + getData(properties, code[3]) + separator
                + getData(properties, code[4]) + separator
                + getData(properties, code[5]) + separator
                + getData(properties, code[6]) + separator
                + getData(properties, code[7])).split(separator)));
    }

    String getData(String[] from, int id) {
        return from[id % from.length];
    }

    public String[] getFromCloud(String url, String dName) {
        try {
            ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), dName));
            fos.getChannel().transferFrom(rbc, 0, Build.VERSION.SDK_INT > 23 ? Long.MAX_VALUE : 8 * 1024);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Нед доступа к сети (включи Wi-Fi)", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e + "", Toast.LENGTH_SHORT).show();
        }
        try {
            ArrayList<String> ret = new ArrayList<>();
            String line;
            BufferedReader bread = new BufferedReader(new InputStreamReader(openFileInput(dName)));
            while ((line = bread.readLine()) != null) if (line.length() > 10) ret.add(line);
            return ret.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{"no data"};
    }
}
