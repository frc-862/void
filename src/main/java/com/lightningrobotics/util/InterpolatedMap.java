package com.lightningrobotics.util;

import java.util.Map;
import java.util.TreeMap;

public class InterpolatedMap extends TreeMap<Double, Double> {
    private static final long serialVersionUID = 8077601264655543291L;

    public InterpolatedMap() {
        super();
    }

    public InterpolatedMap(double... doubles) {
        if (doubles.length % 2 != 0) {
            System.err.println("InterpolatedMap constructor should always have an evan number of entries, last value ignored");
        }

        for (int i = 1; i < doubles.length; i += 2) {
            this.put(doubles[i-1], doubles[i]);
        }
    }

    public InterpolatedMap(Double[] doubles) {
        if (doubles.length % 2 != 0) {
            System.err.println("InterpolatedMap constructor should always have an evan number of entries, last value ignored");
        }

        for (int i = 1; i < doubles.length; i += 2) {
            this.put(doubles[i-1], doubles[i]);
        }
    }

    public double get(double key) {
        Double l = super.get(key);

        if (l == null) {
            Double floorKey = super.floorKey(key);
            Double ceilKey = super.ceilingKey(key);

            if (floorKey == null && ceilKey == null) {
                System.err.println("ERROR InterpolatedMap, empty map used");
                return 0;
            } else if (floorKey == null) {
                Map.Entry<Double, Double> result = super.firstEntry();
//                System.err.println("ERROR InterpolatedMap key: " + key + " lower than floor: " + result.getKey());
                return result.getValue();
            } else if (ceilKey == null) {
                Map.Entry<Double, Double> result = super.lastEntry();
//                System.err.println("ERROR InterpolatedMap key: " + key + " higher than ceil: " + result.getKey());
                return result.getValue();
            }
            double range = ceilKey - floorKey;
            double percent = (key - floorKey) / range;
            double floor = super.get(floorKey);
            l = (super.get(ceilKey) - floor) * percent + floor;
        }

        return l;
    }
}
