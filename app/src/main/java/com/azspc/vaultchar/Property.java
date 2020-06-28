package com.azspc.vaultchar;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.azspc.vaultchar.MainActivity.separator;
import static com.azspc.vaultchar.MainActivity.splitter;

class Property {
    private String text;
    private int type;
    private int color;

    private Property(Resources r, String[] data) {
        if (data.length > 2) {
            this.type = initType(data[0]);
            this.text = data[1]
                    + initSubtitle(data)
                    + ((type == 0 || type == 4 || type == 7) ? "\n • Очевидное свойство!" :
                    ((type == 1 || type == 5 || type == 8) ? "\n • Секретное свойство!" : ""));
            this.color = initColor(r, this.type);
        } else {
            this.type = -1;
            this.text = "null";
            this.color = initColor(r, this.type);
        }
    }

    static ArrayList<Property> multiProperties(Resources r, String[] data) {
        ArrayList<Property> ret = new ArrayList<>();
        for (String s : data)
            ret.add(new Property(r, s
                    .replaceAll("%d", "Справка")
                    .replaceAll("%s", "Пол:")
                    .replaceAll("%n", "Имя:")
                    .replaceAll("%a", "Возраст:")
                    .split(splitter)));
        return ret;
    }

    int getType() {
        return this.type;
    }

    String getText() {
        return this.text;
    }

    int getColor() {
        return this.color;
    }

    private String initSubtitle(String[] s) {
        StringBuilder ret = new StringBuilder();
        for (int i = 2; i < s.length; i++)
            ret.append(i + 1 == s.length ? "\n└ " : "\n├ ").append(s[i]);
        return ret.toString();
    }

    int initColor(Resources r, int type) {
        switch (this.type) {
            default:
                return r.getColor(R.color.basic);
            case 0:
            case 1:
            case 2:
                return r.getColor(R.color.normal);
            case 3:
            case 4:
            case 5:
                return r.getColor(R.color.plus);
            case 6:
            case 7:
            case 8:
                return r.getColor(R.color.minus);
            case 9:
                return r.getColor(R.color.target);
        }
    }

    private int initType(String s) {
        switch (s) {
            default:
                return -1;
            case "**":
                return 0;
            case "!*":
                return 1;
            case "?*":
                return 2;
            case "*+":
                return 3;
            case "!+":
                return 4;
            case "?+":
                return 5;
            case "*-":
                return 6;
            case "!-":
                return 7;
            case "?-":
                return 8;
            case "$":
                return 9;
        }
    }
}
