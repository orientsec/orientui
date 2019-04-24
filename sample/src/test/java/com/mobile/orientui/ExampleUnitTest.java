package com.mobile.orientui;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Integer i = 128;
        Integer j = 128;
        System.out.println(i == j);
        System.out.println(i.equals(j));
        Integer i1 = 150;
        Integer j1 = 150;
        System.out.println(i1 == j1);
        System.out.println(i1.equals(j1));

    }
}