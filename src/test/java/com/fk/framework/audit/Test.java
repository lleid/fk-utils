package com.fk.framework.audit;

import org.apache.commons.lang3.RandomStringUtils;

public class Test {
    public static void main(String[] args) {
        String originFileName = "a.aba.png";
        String[] nameArr = originFileName.split("\\.");
        String fileName = RandomStringUtils.randomAlphabetic(6) + "." + nameArr[nameArr.length - 1];
        System.out.println(fileName);
    }
}
