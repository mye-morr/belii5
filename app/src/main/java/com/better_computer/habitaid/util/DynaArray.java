package com.better_computer.habitaid.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.better_computer.habitaid.data.core.Content;

public class DynaArray {

    private InternalItem[] internalArray = {};
    private int lenInternalArray = 0;
    private double totalWight = 0;
    private Random rand = new Random();

    public void init() {
        this.internalArray = new InternalItem[] {};
        lenInternalArray = 0;
        totalWight = 0;
    }

    private static class ContributingArray {
        List<Content> array;
        int weight;
        String arrayId;
        double percentExtinguish;
        double percentRemove;
        double removeBoundary;
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
            return "";
        }

        double dRand = rand.nextDouble();
        dRand *= totalWight;

        double fSum = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItem item = internalArray[i];
            fSum += (double) item.calWeight;
            if(dRand < fSum
                    && item.calWeight > item.contributingArray.removeBoundary) {

                String sResult = (String) item.name;

                // apply percentExtinquish
                double newWeight = item.calWeight * (1 - item.contributingArray.percentExtinguish);
                totalWight -= (item.calWeight - newWeight);
                // assign new cal weight
                item.calWeight = newWeight;

                return sResult;
            }
        }

        // may have changed!
        // since some items have been removed
        totalWight = 0;

        for (int i = 0; i < lenInternalArray; i++) {
            InternalItem item = internalArray[i];
            item.calWeight = item.originalWeight * item.contributingArray.weight;
            totalWight += item.calWeight;
        }
        return getRandomElement();
    }

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

    public void addContributingArray(List<Content> listContent, String arrayId, int weight, double percentExtinguish, double percentRemove) {
        ContributingArray contributingArray = new ContributingArray();
        contributingArray.array = listContent;
        contributingArray.weight = weight;
        contributingArray.arrayId = arrayId;
        contributingArray.percentExtinguish = percentExtinguish;
        contributingArray.percentRemove = percentRemove;

        int iTotWeightArray = 0;

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
            iTotWeightArray += item.calWeight;
        }

        contributingArray.removeBoundary = iTotWeightArray * contributingArray.percentRemove;
        totalWight += iTotWeightArray;
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

                // if item removed, remove its contribution to extinguish threshold
                item.contributingArray.removeBoundary -= item.originalWeight * item.contributingArray.weight * item.contributingArray.percentRemove;

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

        //Object[][] array1 = {{"aaa", 0.1}, {"bbb", 0.4}, {"ccc", 0.5}};
        //Object[][] array2 = {{"xxx", 0.2}, {"yyy", 0.3}, {"zzz", 0.5}};
        //Object[][] array3 = {{"111", 0.1}, {"222", 0.4}, {"333", 0.1}};

        DynaArray dynaArray = new DynaArray();

        List<Content> listContent1 = new ArrayList<Content>();

        Content c1_1 = new Content();
        c1_1.setPlayerid("1");
        c1_1.setWeight(1);
        c1_1.setContent("aaa");

        Content c1_2 = new Content();
        c1_2.setPlayerid("1");
        c1_2.setWeight(4);
        c1_2.setContent("bbb");

        Content c1_3 = new Content();
        c1_3.setPlayerid("1");
        c1_3.setWeight(5);
        c1_3.setContent("ccc");

        listContent1.add(c1_1);
        listContent1.add(c1_2);
        listContent1.add(c1_3);

        dynaArray.addContributingArray(listContent1, "ID1", 3, 0.5, 0.01);

        List<Content> listContent2 = new ArrayList<Content>();

        Content c2_1 = new Content();
        c2_1.setPlayerid("1");
        c2_1.setWeight(2);
        c2_1.setContent("xxx");

        Content c2_2 = new Content();
        c2_2.setPlayerid("1");
        c2_2.setWeight(3);
        c2_2.setContent("yyy");

        Content c2_3 = new Content();
        c2_3.setPlayerid("1");
        c2_3.setWeight(5);
        c2_3.setContent("zzz");

        listContent2.add(c2_1);
        listContent2.add(c2_2);
        listContent2.add(c2_3);

        dynaArray.addContributingArray(listContent2, "ID2", 5, 0.5, 0.01);

        //dynaArray.addContributingArray(array2, 5, "ID2", 0.6, 0.05);
        String[] foo;
        foo = dynaArray.currentStringArray();

        String o1 = dynaArray.getRandomElement();
        System.out.print("1: " + o1 + "\n");

        String o2 = dynaArray.getRandomElement();
        System.out.print("2: " + o2 + "\n");

        String o3 = dynaArray.getRandomElement();
        System.out.print("3: " + o3 + "\n");

//        dynaArray.addContributingArray(array3, 6, "ID3", 0.7, 0.1);

        String o4 = dynaArray.getRandomElement();
        System.out.print("4: " + o4 + "\n");

        String o5 = dynaArray.getRandomElement();
        System.out.print("5: " + o5 + "\n");

        String o6 = dynaArray.getRandomElement();
        System.out.print("6: " + o6 + "\n");

        String o7 = dynaArray.getRandomElement();
        System.out.print("7: " + o7 + "\n");

        String o8 = dynaArray.getRandomElement();
        System.out.print("8: " + o8 + "\n");

        String o9 = dynaArray.getRandomElement();
        System.out.print("9: " + o9 + "\n");

        dynaArray.removeContributingArray("ID2");

        foo = dynaArray.currentStringArray();

        dynaArray.removeArrayItem("111");

        dynaArray.removeContributingArrayStartWith("ID");
    }
}
