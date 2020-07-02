package com.azspc.vaultchar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
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

import static com.azspc.vaultchar.Property.getMultiLevel;
import static com.azspc.vaultchar.Property.multiProperties;

public class MainActivity extends AppCompatActivity {
    String[] targets;
    String[] characters;
    String[] properties;
    RecyclerView rv;
    public static final String
            s_data = "=", s_item = ">", s_incode = " ",
            key_icon = "show_ic", key_generation = "smart_gen", key_color = "fill_color", key_icnot = "icon_no_text";
    boolean turnUp = false;
    public static SharedPreferences sp;
    String static_code = "";
    int static_loop = 0;

    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Switch) findViewById(R.id.cb_icon)).setChecked(sp.getBoolean(key_icon, true));
        ((Switch) findViewById(R.id.cb_smartgen)).setChecked(sp.getBoolean(key_generation, false));
        ((Switch) findViewById(R.id.cb_color)).setChecked(sp.getBoolean(key_color, true));
        ((Switch) findViewById(R.id.cb_icnot)).setChecked(sp.getBoolean(key_icnot, false));
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        rv = findViewById(R.id.prop);
        rv.setLayoutManager(new LinearLayoutManager(this));
        initData();
        generateRandom(null);
    }

    @SuppressLint("SetTextI18n")
    public void generateRandom(View v) {
        if (sp.getBoolean(key_generation, false)) {
            static_loop = 0;
            ((EditText) findViewById(R.id.char_code)).setText(genPropSmart());
            Toast.makeText(getBaseContext(), "Попыток генерации ключа: " + static_loop, Toast.LENGTH_SHORT).show();
        } else ((EditText) findViewById(R.id.char_code)).setText("" +
                ((int) (Math.random() * targets.length)) + s_incode +
                ((int) (Math.random() * characters.length)) +
                genProp(new HashSet<Integer>()));
        convert(null);
    }

    String genProp(HashSet<Integer> set) {
        set.add((int) (Math.random() * properties.length));
        if (set.size() < 6)
            genProp(set);
        StringBuilder ret = new StringBuilder();
        for (Integer i : set) ret.append(s_incode).append(i);
        return ret.toString();
    }

    String genPropSmart() {
        ++static_loop;
        static_code = (((int) (Math.random() * targets.length)) + s_incode +
                ((int) (Math.random() * characters.length)) + genProp(new HashSet<Integer>()));
        int[] arr = new int[8];
        for (int i = 0; i < 8; i++) arr[i] = Integer.parseInt(static_code.split(s_incode)[i]);
        int lvl = getMultiLevel(initProperties(arr));
        if (!(lvl == 0)) genPropSmart();
        return static_code;
    }

    public void convert(View v) {
        try {
            String[] id = ((EditText) findViewById(R.id.char_code)).getText().toString().split(s_incode);
            int[] arr = new int[8];
            for (int i = 0; i < 8; i++) arr[i] = Integer.parseInt(id[i]);
            rv.setAdapter(new ProretyAdapter(this,
                    new ArrayList<>(multiProperties(getResources(), initProperties(arr)))));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Ошибка", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkBox(View v) {
        Switch cb = (Switch) v;
        if (cb.getId() == R.id.cb_icon) {
            sp.edit().putBoolean(key_icon, cb.isChecked()).apply();
            convert(null);
        }
        if (cb.getId() == R.id.cb_smartgen) {
            sp.edit().putBoolean(key_generation, cb.isChecked()).apply();
        }
        if (cb.getId() == R.id.cb_color) {
            sp.edit().putBoolean(key_color, cb.isChecked()).apply();
            convert(null);
        }
        if (cb.getId() == R.id.cb_icnot) {
            sp.edit().putBoolean(key_icnot, cb.isChecked()).apply();
            convert(null);
        }
    }

    private void initData() {
        ArrayList<String> t = new ArrayList<>();
        ArrayList<String> c = new ArrayList<>();
        ArrayList<String> p = new ArrayList<>();
        for (String s : getFromCloud(getString(R.string.data_url), "savedData"))
            try {
                String type = s.split(s_data)[0];
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

    String[] initProperties(int[] code) {
        return (""
                + getData(targets, code[0]) + s_data
                + getData(characters, code[1]) + s_data
                + getData(properties, code[2]) + s_data
                + getData(properties, code[3]) + s_data
                + getData(properties, code[4]) + s_data
                + getData(properties, code[5]) + s_data
                + getData(properties, code[6]) + s_data
                + getData(properties, code[7])).split(s_data);
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
