package msys.client.optimization;


import javafx.scene.control.Label;

import java.util.Comparator;

class PListComperator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof Label){
            return -1;
        }
        if (o2 instanceof Label){
            return 1;
        }
        if (o1 instanceof PListElement && o2 instanceof PListElement){
            double d1 = ((PListElement) o1).fitness;
            double d2 = ((PListElement) o2).fitness;
            if (d1 < d2){
                return -1;
            }
            if (d1 > d2){
                return 1;
            }
            return 0;
        }
        return 0;
    }
}
