package com.assignment.users.src.helper;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.assignment.users.src.helper.DistinctMobCountUtil.MonthEnum.resolveToLiteral;
import static java.util.Collections.singletonList;

public class DistinctMobCountUtilTests {

    private HashMap<Object, Object> janCount;
    private HashMap<Object, Object> febCount;
    private HashMap<Object, Object> decCount;

    @Before
    public void setUp() {
        janCount = new HashMap<>();
        janCount.put("_id", 1);
        janCount.put("count", 14);

        febCount = new HashMap<>();
        febCount.put("_id", 2);
        febCount.put("count", 999);

        decCount = new HashMap<>();
        decCount.put("_id", 12);
        decCount.put("count", 87654);
    }


    @Test
    public void testParse() {
        List<Map> resultList = Arrays.asList(janCount, febCount, decCount);
        Map<String, Integer> mobCountResults = new DistinctMobCountUtil().parse(resultList);

        TestCase.assertEquals(12, mobCountResults.size());
        mobCountResults.forEach((k, v) -> {
            switch (k) {
                case "January":
                    TestCase.assertEquals("value", 14, v.intValue());
                    break;
                case "February":
                    TestCase.assertEquals("value", 999, v.intValue());
                    break;
                case "December":
                    TestCase.assertEquals("value", 87654, v.intValue());
                    break;
                default:
                    TestCase.assertEquals(0, v.intValue());
                    break;
            }
        });
    }

    @Test
    public void testInvalidData() {
        Map<Object, Object> data = new HashMap<>();
        data.put("_id", 19); //19 month is not valid
        data.put("count", 100);

        List<Map> resultList = singletonList(data);
        Map<String, Integer> mobCountResults = DistinctMobCountUtil.parse(resultList);

        TestCase.assertEquals(12, mobCountResults.size());
        mobCountResults.forEach((k, v) -> TestCase.assertEquals(0, v.intValue())
        );
    }

    @Test
    public void testMonthEnum() {
        TestCase.assertNull("Not a valid month", resolveToLiteral(100));
        TestCase.assertEquals("January", resolveToLiteral(1));
    }

}