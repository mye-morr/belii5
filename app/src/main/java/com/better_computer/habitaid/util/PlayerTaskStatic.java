package com.better_computer.habitaid.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerTaskStatic extends AsyncTask<Void, Void, Integer> {
    private Context context;
    private String[] sxItems;
    private Integer[] ixRandIdx;
    private int len;
    private int nxt;
    private int iMinBreak;

    public PlayerTaskStatic(Context context, String[] sxItems, String sRate) {

        switch(sRate) {
            case "SUPER":
                this.iMinBreak = 1;
                break;
            case "HIGH":
                this.iMinBreak = 2;
                break;
            case "MEDIUM":
                this.iMinBreak = 3;
                break;
            case "LOW":
                this.iMinBreak = 4;
                break;
            default:
                this.iMinBreak = 2;
                break;
        }

        this.context = context;
        this.sxItems = sxItems;
        this.len = sxItems.length;
        this.ixRandIdx = genRandIdx(this.len);
        this.nxt = -1;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(Void... params) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        String sAction;
        String[] fc_output;

        while(!isCancelled()) {

            String sKeyVal = sxItems[ixRandIdx[++nxt]];

            if (nxt == (len-1)) {
                nxt = -1;
            }

            int iBuf = sKeyVal.indexOf("-=");
            int iBufColon = sKeyVal.indexOf(":");

            if(iBuf > 0) {
                if (iBufColon > 0) {
                    sAction = "com.example.SendBroadcast.fc_cat_4";
                    fc_output = new String[9];

                    for(int i=0; i<fc_output.length; i++)
                        fc_output[i] = "";

                    String sKey = sKeyVal.substring(0, iBuf).trim();
                    String sParamVal = sKeyVal.substring(iBuf + 2).trim();

                    fc_output[0] = sKey;

                    String[] sxParamVal = sParamVal.split(";");
                    switch (sxParamVal.length) {
                        case 4:
                            sxParamVal[3] = sxParamVal[3].trim();
                            iBuf = sxParamVal[3].indexOf(":");
                            fc_output[7] = sxParamVal[3].substring(0, Math.min(iBuf, 2));
                            fc_output[8] = sxParamVal[3].substring(iBuf + 1).trim();
                        case 3:
                            sxParamVal[2] = sxParamVal[2].trim();
                            iBuf = sxParamVal[2].indexOf(":");
                            fc_output[5] = sxParamVal[2].substring(0, Math.min(iBuf, 2));
                            fc_output[6] = sxParamVal[2].substring(iBuf + 1).trim();
                        case 2:
                            sxParamVal[1] = sxParamVal[1].trim();
                            iBuf = sxParamVal[1].indexOf(":");
                            fc_output[3] = sxParamVal[1].substring(0, Math.min(iBuf, 2));
                            fc_output[4] = sxParamVal[1].substring(iBuf + 1).trim();
                        case 1:
                            sxParamVal[0] = sxParamVal[0].trim();
                            iBuf = sxParamVal[0].indexOf(":");
                            fc_output[1] = sxParamVal[0].substring(0, Math.min(iBuf, 2));
                            fc_output[2] = sxParamVal[0].substring(iBuf + 1).trim();
                        default:
                            break;
                    }
                }
                else {
                    sAction = "com.example.SendBroadcast.fc2";
                    fc_output = new String[2];

                    String sKey = sKeyVal.substring(0, iBuf).trim();
                    String sVal = sKeyVal.substring(iBuf + 2).trim();

                    fc_output[0] = sKey;
                    fc_output[1] = sVal;

                }

                String sOutput = "";
                for(int i=0; i < fc_output.length; i++)
                    sOutput += fc_output[i] + " |";

                Intent i1 = new Intent();
                i1.setAction(sAction);
                i1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                i1.putExtra("STRING_FC", sOutput);
                i1.putExtra("BOOL_SHOW_ANSWERS", false);

                context.sendBroadcast(i1);

                try {
                    Thread.sleep(30 * 1000);

                    Intent i2 = new Intent();
                    i2.setAction(sAction);
                    i2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    i2.putExtra("STRING_FC", sOutput);
                    i2.putExtra("BOOL_SHOW_ANSWERS", true);
                    context.sendBroadcast(i2);

                    Thread.sleep(iMinBreak * 60 * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }

            else { // no -= delimiter

                Intent i3 = new Intent();
                i3.setAction("com.example.SendBroadcast.fc1");
                i3.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                i3.putExtra("STRING_FC", sKeyVal);
                i3.putExtra("BOOL_SHOW_ANSWERS", false);

                context.sendBroadcast(i3);

                try {
                    Thread.sleep(iMinBreak * 60 * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        wl.release();
        return 1;
    }

    @Override
    protected void onCancelled(Integer id) {
    }

    @Override
    protected void onPostExecute(Integer id) {
    }

    protected Integer[] genRandIdx(int iSize) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < iSize; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list.toArray(new Integer[list.size()]);
    }
}

