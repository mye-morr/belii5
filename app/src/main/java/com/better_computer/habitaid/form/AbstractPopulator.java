package com.better_computer.habitaid.form;

import android.content.Context;
import android.view.View;

public abstract class AbstractPopulator {
    protected Context context;
    protected View rootView;
    protected String category;

    public AbstractPopulator(Context context){
        this.context = context;
    }

    public void setup(View rootView, String category) {
        this.rootView = rootView;
        this.category = category;
    }

    public void resetup(){
            setup(rootView, category);
    }
}
