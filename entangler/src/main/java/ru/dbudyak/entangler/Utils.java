package ru.dbudyak.entangler;

import javafx.scene.image.Image;

/**
 * Created by dbudyak on 19.05.14.
 */
public class Utils {
    public Image getImageByElId(String id) {
        if (id.startsWith("el")) {
            return new Image(getClass().getClassLoader().getResourceAsStream(id.substring(2).toLowerCase() + ".png"));
        } else if (id.startsWith("w")) {
            return new Image(getClass().getClassLoader().getResourceAsStream(id.substring(1).toLowerCase() + ".png"));
        }
        return null;
    }

    public static void print(Object s) {
        System.out.println(s);
    }

    public static void print(Object s, Object t) {
        System.out.println(s + " : " + t);
    }

    public static void printData(double[][] data) {
        for (double[] aData : data) {
            for (int j = 0; j < data[0].length; j++) {
                System.out.print(aData[j] + " ");
            }
            System.out.println();
        }
    }
}
