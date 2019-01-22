/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.geckostroll.common.utils;

/**
 *
 * @author yanhuai
 * @version $Id: MathUtils.java, v 0.1 2019年01月22日 21:01 yanhuai Exp $
 */
public class MathUtils {

    public static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static boolean isPowerOfTwo(int intValue) {
        if (intValue == 0) {
            return false;
        }
        while ((intValue & 1) == 0) {
            intValue = intValue >>> 1;
        }
        return intValue == 1;
    }

    public static boolean isPowerOfTwo(long longValue) {
        if (longValue == 0L) {
            return false;
        }
        while ((longValue & 1L) == 0L) {
            longValue = longValue >>> 1;
        }
        return longValue == 1L;
    }
}
