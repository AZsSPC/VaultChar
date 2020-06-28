package com.azspc.vaultchar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static com.azspc.vaultchar.Property.multiProperties;

public class MainActivity extends AppCompatActivity {
    public static String[] targets;
    public static String[] characters;
    public static String[] properties;
    RecyclerView rv;
    public static final String separator = "=", splitter = ">";

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

    public void generateRandom(View v) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append((char) (Math.random() * targets.length + 65));
        stringBuilder.append((char) (Math.random() * characters.length + 65));
        for (int i = 0; i < 6; i++) {
            stringBuilder.append((char) (Math.random() * properties.length + 65));
        }
        ((EditText) findViewById(R.id.char_code)).setText(stringBuilder.toString());
        convert(v);
    }

    public void convert(View v) {
        String id = ((EditText) findViewById(R.id.char_code)).getText().toString();
        int[] arr = new int[8];
        try {
            for (int i = 0; i < 8; i++) arr[i] = id.charAt(i);
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
/*
    @SuppressLint("SetTextI18n")
    void setTexts(int[] c) {
        HashSet<String> data = new HashSet<>();

        for (String[] s : new String[][]{getStrArray(MainActivity.properties, c[2]), getStrArray(MainActivity.properties, c[3]), getStrArray(MainActivity.properties, c[4])})
            if (s[0].equals("-")) minus.add(s[1]);
            else if (s[0].equals("+")) plus.add(s[1]);
            else data.add(s[1]);

        for (String[] s : new String[][]{getStrArray(hidden, c[5]), getStrArray(hidden, c[6]), getStrArray(hidden, c[7])})
            if (s[0].equals("-"))
                minus.add(s[1].replaceAll(" > ",
                        " (Скрытое) > ") + "\n  ! Храни это в секрете");
            else if (s[0].equals("+"))
                plus.add(s[1].replaceAll(" > ",
                        " (Скрытое) > ") + "\n  ! Храни это в секрете");
            else data.add(s[1].replaceAll(" > ",
                        " (Скрытое) > ") + "\n  ! Храни это в секрете");

        ((TextView) findViewById(R.id.stat_info)).setText("Данные:"
                + "\n  Имя: " + character[3].split(" > ")[0]
                + "\n  Профессия: " + character[0].split(" > ")[0]
                + "\n  Возраст: " + character[1].split(" > ")[0]
                + "\n  Пол: " + character[2].split(" > ")[0]
                + "\n  Стиль игры: " + target.split(" > ")[0]);
        tab[0] = ""
                + "\nДобро пожаловать в команду, " + character[0].replaceAll(" > ", "\n   - ")
                + " " + character[3].replaceAll(" > ", "\n   - ")
                + "\nВозраст: " + character[1].replaceAll(" > ", "\n   - ")
                + "\n" + character[2].replaceAll(" > ", "\n   - ")
                + "\n" + target.replaceAll(" > ", "\n   - ")
                + "\n\n" + character[5];
        tab[1] = "";
        tab[2] = "";
        tab[3] = "";
        StringBuilder min = new StringBuilder();
        StringBuilder plu = new StringBuilder();
        StringBuilder exc = new StringBuilder();
        for (String s : minus) {
            tab[2] += ("\n\n" + s).replaceAll(" > ", "\n  - ");
            min.append("\n- ").append(s.split(" > ")[0]);
        }
        for (String s : plus) {
            tab[3] += ("\n\n" + s).replaceAll(" > ", "\n  - ");
            plu.append("\n+ ").append(s.split(" > ")[0]);
        }
        for (String s : data) {
            tab[1] += ("\n\n" + s).replaceAll(" > ", "\n  - ");
            exc.append("\n* ").append(s.split(" > ")[0]);
        }
        try {
            ((TextView) findViewById(R.id.stat_debuf)).setText(min.toString().substring(1));
        } catch (Exception ignored) {
            ((TextView) findViewById(R.id.stat_debuf)).setText("");
        }
        try {
            ((TextView) findViewById(R.id.stat_buf)).setText(plu.toString().substring(1));
        } catch (Exception ignored) {
            ((TextView) findViewById(R.id.stat_buf)).setText("");
        }
        try {
            ((TextView) findViewById(R.id.stat_exclusive)).setText(exc.toString().substring(1));
        } catch (Exception ignored) {
            ((TextView) findViewById(R.id.stat_exclusive)).setText("");
        }
    }*/

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
            while ((line = bread.readLine()) != null) if (line.length() >= 3) ret.add(line);
            return ret.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{"no data"};
    }
}
