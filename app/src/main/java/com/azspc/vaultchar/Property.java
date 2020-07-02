package com.azspc.vaultchar;

import android.content.res.Resources;

import java.util.ArrayList;

import static com.azspc.vaultchar.MainActivity.key_icnot;
import static com.azspc.vaultchar.MainActivity.key_icon;
import static com.azspc.vaultchar.MainActivity.s_item;
import static com.azspc.vaultchar.MainActivity.sp;

class Property {
    private String text;
    private int type;
    private int color;

    private Property(Resources r, String[] data) {
        try {
            this.type = initType(data[0]);
            this.text = data[1] + initSubtitle(data);
            if (!(sp.getBoolean(key_icnot, false) && sp.getBoolean(key_icon, true)))
                this.text += ((type == 1 || type == 4 || type == 7) ? "\n\n • Очевидное свойство!" :
                        ((type == 2 || type == 5 || type == 8) ? "\n\n • Секретное свойство!" : ""));
            this.color = initColor(r, type);
        } catch (Exception e) {
            e.printStackTrace();
            this.type = -1;
            this.text = "%error%" + initSubtitle(data);
            this.color = initColor(r, type);
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
                    .replaceAll("%p", "Профессия:")
                    .split(s_item)));
        return ret;
    }

    static int getMultiLevel(String[] data) {
        int lvl = 0;
        for (String s : data) {
            int type = initType(s.split(s_item)[0]);
            lvl += (type == 3 || type == 4 || type == 5) ? 1 : ((type == 6 || type == 7 || type == 8) ? -1 : 0);
        }
        return lvl;
    }

    int getType() {
        return this.type;
    }

    int getTypeIcon() {
        switch (this.type) {
            default:
                return R.drawable.lic_vis;
            case 1:
            case 4:
            case 7:
                return R.drawable.lic_vis_yes;
            case 2:
            case 5:
            case 8:
                return R.drawable.lic_vis_no;
        }
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

    private static int initColor(Resources r, int type) {
        switch (type) {
            default:
            case 11:
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
            case 10:
                return r.getColor(R.color.bag);
        }
    }

    private static int initType(String s) {
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
            case "#":
                return 9;
            case "$":
                return 10;
            case "@":
                return 11;
        }
    }
}
