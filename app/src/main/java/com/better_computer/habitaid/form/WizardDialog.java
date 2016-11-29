package com.better_computer.habitaid.form;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.better_computer.habitaid.R;

abstract public class WizardDialog{

    protected List<DialogStep> steps;
    protected int currentStep;
    protected Context context;
    protected Dialog dialog;

    protected Integer iconRes;
    protected String titleText;

    protected ImageView icon;
    protected TextView title;
    protected Button rightBtn;
    protected Button leftBtn;
    protected FrameLayout displayFrame;
    protected LinearLayout buttonContainer;

    protected LayoutInflater inflater;

    public WizardDialog(Context context) {
        this.context = context;
        this.steps = new ArrayList<DialogStep>();

        this.inflater = ((Activity)context).getLayoutInflater();

        this.dialog = new Dialog(context, R.style.DialogWizard);
        this.dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //this.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        this.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        this.dialog.setContentView(R.layout.wizard_layout);

        this.displayFrame = (FrameLayout)dialog.findViewById(R.id.wizard_frame);
        this.rightBtn = (Button)dialog.findViewById(R.id.wizard_rigth_button);
        this.leftBtn = (Button)dialog.findViewById(R.id.wizard_left_button);
        this.buttonContainer = (LinearLayout)dialog.findViewById(R.id.wizard_button_container);
        this.icon = (ImageView)dialog.findViewById(R.id.wizard_icon);
        this.title = (TextView)dialog.findViewById(R.id.wizard_heading);
        initialize();
    }

    public void initialize(){
        currentStep = 0;
    }

    public void setIcon(int icon) {
        this.icon.setImageResource(icon);
    }

    public abstract class DialogStep{
        public abstract int getResource();

        public void setup(){
            displayFrame.removeAllViews();
            inflater.inflate(getResource(), displayFrame);
            if(titleText != null) {
                title.setText(titleText);
            }
            if(iconRes != null) {
                icon.setImageResource(iconRes);
            }
            ((TextView)findViewById(R.id.wizard_steps)).setText((currentStep + 1) + "/" + steps.size());
        }
    }

    public View findViewById(int id) {
        return dialog.findViewById(id);
    }

    public void setTitle(String titleText) {
        this.titleText = titleText;
    }

    public void setIcon(Integer iconRes) {
        this.iconRes = iconRes;
    }

    public void setRightButton(CharSequence text, View.OnClickListener listener){
        this.rightBtn.setText(text);
        this.rightBtn.setOnClickListener(listener);
    }

    public void setLeftButton(CharSequence text, View.OnClickListener listener){
        this.leftBtn.setText(text);
        this.leftBtn.setOnClickListener(listener);
    }

    public void show(){
        steps.get(currentStep).setup();
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    /* for potential multi-step layout, in case needed for display compatibility
    public void nextStep(){
        if(steps.size() > currentStep){
            currentStep ++;
            steps.get(currentStep).setup();
        }
    }

    public void previousStep(){
        if(currentStep > 0){
            currentStep --;
            steps.get(currentStep).setup();
        }
    }
    */
}