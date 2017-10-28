package com.dp.LogSupport;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by whf on 8/29/15.
 */
public class SimpleTest {

    @Test
    public void testScanWithAnnotation() throws Exception {
        List<String> pkgNames = new ArrayList<>();
        pkgNames.add("com.dp");
        Map<String, List<String>> loggingBeans = PackageUtils.getLoggingBeans(pkgNames);
        System.out.println();
    }
}
