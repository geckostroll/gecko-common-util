/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.geckostroll.common.utils;

/**
 * 字符串操作工具类
 *
 * @author yanhuai
 * @version $Id: StringUtils.java, v 0.1 2019年01月22日 20:53 yanhuai Exp $
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";

    public static String defaultIfNull(String str) {
        return (str == null) ? EMPTY_STRING : str;
    }

    public static String defaultIfNull(String str, String defaultStr) {
        return (str == null) ? defaultStr : str;
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equalsIgnoreCase(str2);
    }

    public static String reverse(String str) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }
        return new StringBuffer(str).reverse().toString();
    }

    public static int getLevenshteinDistance(String s, String t) {
        s     = defaultIfNull(s);
        t     = defaultIfNull(t);

        int[][] d; // matrix
        char    s_i; // ith character of s
        char    t_j; // jth character of t
        int     cost; // cost

        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        // 初始化
        d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        // 动态规划
        for (int i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                t_j = t.charAt(j - 1);
                cost = (s_i == t_j) ? 0 : 1;
                d[i][j] = MathUtils.min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }
        return d[n][m];
    }
}
