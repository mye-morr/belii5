package com.better_computer.habitaid.navigation;

public class DrawerItem {
    private int name;
    private int icon;

    public DrawerItem(int name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public int getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
