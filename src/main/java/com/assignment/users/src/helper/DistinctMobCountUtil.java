package com.assignment.users.src.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistinctMobCountUtil {

    public static Map<String, Integer> parse(List<Map> results) {
        Map<Integer, Integer> serialized = serialize(results);
        Map<String, Integer> parsedResults = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            parsedResults.put(MonthEnum.resolveToLiteral(i), serialized.getOrDefault(i, 0));
        }
        return parsedResults;
    }

    private static Map<Integer, Integer> serialize(List<Map> results) {
        Map<Integer, Integer> serialized = new HashMap<>();
        for (Map result : results) {
            Integer mob = (Integer) result.getOrDefault("_id", 0);
            Integer count = (Integer) result.getOrDefault("count", 0);
            serialized.put(mob, count);
        }
        return serialized;
    }


    public enum MonthEnum {

        JAN("January", 1),
        FEB("February", 2),
        MAR("March", 3),
        APR("April", 4),
        MAY("May", 5),
        JUN("June", 6),
        JUL("July", 7),
        AUG("August", 8),
        SEP("September", 9),
        OCT("October", 10),
        NOV("November", 11),
        DEC("December", 12);

        private final String monthLiteral;
        private final int monthNum;

        MonthEnum(String monthLiteral, int monthNum) {
            this.monthLiteral = monthLiteral;
            this.monthNum = monthNum;
        }

        public static String resolveToLiteral(int num) {
            MonthEnum[] values = values();
            for (MonthEnum monthEnum : values) {
                if (monthEnum.monthNum == num) {
                    return monthEnum.monthLiteral;
                }
            }
            return null;
        }
    }

}
