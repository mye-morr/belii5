package com.better_computer.habitaid.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.better_computer.habitaid.data.core.Content;

/**
 * Created by tedwei on 10/14/16.
 * Version 0.3
 */
public class DynaArray {

    private InternalItem[] internalArray = {};
    private int lenInternalArray = 0;
    private double totalWight = 0;
    private Random rand = new Random();

    private static class ContributingArray implements Parcelable {
        List<Content> array;
        int weight;
        String arrayId;
        double percentExtinguish;
        double percentRemove;
        double removeBoundary;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(this.array);
            dest.writeInt(this.weight);
            dest.writeString(this.arrayId);
            dest.writeDouble(this.percentExtinguish);
            dest.writeDouble(this.percentRemove);
            dest.writeDouble(this.removeBoundary);
        }

        public ContributingArray() {
        }

        protected ContributingArray(Parcel in) {
            this.array = new ArrayList<Content>();
            in.readList(this.array, Content.class.getClassLoader());
            this.weight = in.readInt();
            this.arrayId = in.readString();
            this.percentExtinguish = in.readDouble();
            this.percentRemove = in.readDouble();
            this.removeBoundary = in.readDouble();
        }

        public static final Creator<ContributingArray> CREATOR = new Creator<ContributingArray>() {
            @Override
            public ContributingArray createFromParcel(Parcel source) {
                return new ContributingArray(source);
            }

            @Override
            public ContributingArray[] newArray(int size) {
                return new ContributingArray[size];
            }
        };
    }

    public static class InternalItem {
        String name;
        double originalWeight;
        double calWeight;
        ContributingArray contributingArray;
        Content content;

        public String getName() {
            return name;
        }

        public String get_state() {
            return content.get_state();
        }

        public String getArrayId() {
            return content.getPlayerid();
        }
    }

    public String getRandomElement() {
        if (lenInternalArray == 0) {
            return null;
        }

        double dRand = rand.nextDouble();
        dRand *= totalWight;

        double fSum = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItem item = internalArray[i];
            fSum += (double) item.calWeight;
            if(dRand < fSum) {
                String sResult = (String) item.name;

                // apply percentExtinquish
                double newWeight = item.calWeight * item.contributingArray.percentExtinguish;

                if (newWeight < item.contributingArray.removeBoundary) {
                    // swap item to the last to simulate remove
                    swapWithLastItem(i);
                } else {
                    // adjust total weight
                    totalWight -= (item.calWeight - newWeight);
                    // assign new cal weight
                    item.calWeight = newWeight;
                }

                return sResult;
            }
        }

        return null;
    }

    /*
    public Object[] currentStringArray() {
        Object[] result = new Object[lenInternalArray];
        for (int i = 0 ; i < lenInternalArray ; i++) {
            InternalItem item = internalArray[i];
            result[i] = new Object[]{item.name, item.originalWeight};
        }
        return result;
    }
    */
    public String[] currentStringArray() {
        String[] result = new String[lenInternalArray];
        for (int i = 0 ; i < lenInternalArray ; i++) {
            InternalItem item = internalArray[i];
            result[i] = item.name;
        }
        return result;
    }

    public InternalItem[] currentInternalItemArray() {
        InternalItem[] result = new InternalItem[lenInternalArray];
        for (int i = 0 ; i < lenInternalArray ; i++) {
            InternalItem item = internalArray[i];
            result[i] = item;
        }
        return result;
    }

    //public void addContributingArray(Object[][] array, int weight, String arrayId, double percentExtinguish, double percentRemove) {
    public void addContributingArray(List<Content> listContent, int weight, String arrayId, double percentExtinguish, double percentRemove) {
        ContributingArray contributingArray = new ContributingArray();
        contributingArray.array = listContent;
        contributingArray.weight = weight;
        contributingArray.arrayId = arrayId;
        contributingArray.percentExtinguish = percentExtinguish;
        contributingArray.percentRemove = percentRemove;
        contributingArray.removeBoundary = 0;

        InternalItem[] tempInternalArray = new InternalItem[listContent.size()];
        for (int i=0 ; i<tempInternalArray.length ; i++) {
            Content content = listContent.get(i);
            InternalItem item = new InternalItem();
            item.contributingArray = contributingArray;
            item.content = content;
            tempInternalArray[i] = item;

            item.name = (String) content.getContent();
            item.originalWeight = (double) content.getWeight();
            item.calWeight = item.originalWeight * item.contributingArray.weight;
            totalWight += item.calWeight;
            contributingArray.removeBoundary += item.calWeight;
        }
        contributingArray.removeBoundary *= contributingArray.weight;
        contributingArray.removeBoundary *= contributingArray.percentRemove;
        internalArray = concat(internalArray, lenInternalArray, tempInternalArray, tempInternalArray.length);
        lenInternalArray = internalArray.length;
    }

    public boolean containsContributingArray(String arrayId) {
        if (lenInternalArray == 0) {
            return false;
        }

        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.contributingArray.arrayId.equals(arrayId)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public void removeContributingArray(String arrayId) {
        if (lenInternalArray == 0) {
            return;
        }

        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.contributingArray.arrayId.equals(arrayId)) {
                swapWithLastItem(i);
            } else {
                i++;
            }
        }
    }

    public void removeContributingArrayStartWith(String arrayId) {
        if (lenInternalArray == 0) {
            return;
        }

        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.contributingArray.arrayId.startsWith(arrayId)) {
                swapWithLastItem(i);
            } else {
                i++;
            }
        }
    }

    public void removeArrayItem(String itemName) {
        int i = 0;
        while (i < lenInternalArray) {
            InternalItem item = internalArray[i];
            if (item.name.equals(itemName)) {
                swapWithLastItem(i);
            } else {
                i++;
            }
        }
    }

    private InternalItem[] concat(InternalItem[] a, int aLen, InternalItem[] b, int bLen) {
        InternalItem[] c= new InternalItem[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private void swapWithLastItem(int index) {
        InternalItem item = internalArray[index];
        swap(internalArray, index, lenInternalArray - 1);
        // decrease totalWight
        totalWight -= item.calWeight;
        lenInternalArray--;
    }

    private void swap(Object[] array, int index1, int index2) {
        Object temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }


    public static void main(String[] args) {
        System.out.println("Test");

        Object[][] array1 = {{"aaa", 0.1}, {"bbb", 0.4}, {"ccc", 0.5}};
        Object[][] array2 = {{"xxx", 0.2}, {"yyy", 0.3}, {"zzz", 0.5}};
        Object[][] array3 = {{"111", 0.1}, {"222", 0.4}, {"333", 0.1}};

        DynaArray dynaArray = new DynaArray();
//        dynaArray.addContributingArray(array1, 3, "ID1", 0.5, 0.01);
//        dynaArray.addContributingArray(array2, 5, "ID2", 0.6, 0.05);
        dynaArray.currentStringArray();

        String a = dynaArray.getRandomElement();
        System.out.print("a = " + a + "\n");

        String b = dynaArray.getRandomElement();
        System.out.print("b = " + b + "\n");

        String c = dynaArray.getRandomElement();
        System.out.print("c = " + c + "\n");

//        dynaArray.addContributingArray(array3, 6, "ID3", 0.7, 0.1);

        String d = dynaArray.getRandomElement();
        System.out.print("d = " + d + "\n");

        dynaArray.getRandomElement();
        dynaArray.getRandomElement();
        dynaArray.getRandomElement();
        dynaArray.getRandomElement();
        dynaArray.getRandomElement();

        dynaArray.removeContributingArray("ID2");

        dynaArray.currentStringArray();

        dynaArray.removeArrayItem("111");

        dynaArray.removeContributingArrayStartWith("ID");

    }

}
