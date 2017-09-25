package floormapping;

import java.util.HashMap;

/**
 * Created by martin on 2017-05-16.
 */

public class FloorToFloorMapPositionTranslator {

    private static HashMap<Integer, Integer> createHashMap(){

        HashMap hashMap = new HashMap();
        hashMap.put(0, 0);
        hashMap.put(1, 2);
        hashMap.put(2, 4);
        hashMap.put(3, 5);
        hashMap.put(4, 1);
        hashMap.put(5, 0);
        hashMap.put(6, 3);
        hashMap.put(7, 4);
        hashMap.put(8, 5);
        hashMap.put(9, 2);

        return hashMap;
    }

    public static int getCorrectFloor(Integer floor){

        HashMap hashMap = createHashMap();

        Integer correctFloor = (Integer) hashMap.get(floor);

        if (correctFloor == null) {
            correctFloor = floor;
        }

        return correctFloor;
    }


}
