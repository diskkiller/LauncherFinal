package com.zhongti.huacailauncher.app.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * 处理数字相关工具类
 * Created by shuheming on 2018/2/27 0027.
 */

public class NumUtils {

    /**
     * 转换钱数为 1,231,212.02这种格式
     */
    public static String getMoneyDou(String fromNum) {
        String result;
        try {
            StringBuilder sb = new StringBuilder();
            char[] chars = fromNum.toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                sb.append(chars[i]);
                if ((chars.length - i) % 3 == 0 && i != chars.length - 1) {
                    if (i == 0) continue;
                    sb.append(',');
                }
            }
            result = sb.reverse().toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = fromNum;
        }
        return result;
    }

    /**
     * 转换钱数为 1,231,212.02这种格式
     */
    public static String getMoneyNum(String fromNum) {
//        boolean isDou
        String result;
        try {
            String rightNum = getRightDouble(twoDouble(fromNum));
            String newNum;
            String[] numArray = null;
            if (rightNum.contains(".")) {
                numArray = rightNum.split("\\.");
                newNum = numArray[0];
            } else {
                newNum = rightNum;
            }
            StringBuilder sb = new StringBuilder();
            char[] chars = newNum.toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                sb.append(chars[i]);
                if ((chars.length - i) % 3 == 0 && i != chars.length - 1) {
                    if (i == 0) continue;
                    if (i == 1 && chars[0] == '-') continue;
                    sb.append(',');
                }
            }
            result = numArray == null ? sb.reverse().toString() : sb.reverse().append(".").append(numArray[1]).toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = fromNum;
        }
        return result;
    }

    /**
     * 获取整数
     */
    public static String getMoneyIntNum(String fromNum) {
//        boolean isDou
        String result;
        try {
            String rightNum = String.valueOf((int) Double.parseDouble(fromNum));
            String newNum;
            String[] numArray = null;
            if (rightNum.contains(".")) {
                numArray = rightNum.split("\\.");
                newNum = numArray[0];
            } else {
                newNum = rightNum;
            }
            StringBuilder sb = new StringBuilder();
            char[] chars = newNum.toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                sb.append(chars[i]);
                if ((chars.length - i) % 3 == 0 && i != chars.length - 1) {
                    if (i == 0) continue;
                    if (i == 1 && chars[0] == '-') continue;
                    sb.append(',');
                }
            }
            result = numArray == null ? sb.reverse().toString() : sb.reverse().append(".").append(numArray[1]).toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = fromNum;
        }
        return result;
    }


    /**
     * 转换钱数为 1,231,212.02这种格式
     */
    public static String getListMoneyNum(String fromNum) {
//        boolean isDou
        String result;
        try {
            String rightNum = getRightDouble(twoDouble(fromNum));
            String newNum;
            String[] numArray = null;
            if (rightNum.contains(".")) {
                numArray = rightNum.split("\\.");
                newNum = numArray[0];
            } else {
                newNum = rightNum;
            }
            StringBuilder sb = new StringBuilder();
            char[] chars = newNum.toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                sb.append(chars[i]);
                if ((chars.length - i) % 3 == 0 && i != chars.length - 1) {
                    if (i == 0 || i == 1) continue;
                    sb.append(',');
                }
            }
            result = getPlusNum(numArray == null ? sb.reverse().toString() : sb.reverse().append(".").append(numArray[1]).toString());
        } catch (Exception e) {
            e.printStackTrace();
            result = fromNum;
        }
        return result;
    }

    public static String getListMoneyIntNum(String fromNum) {
//        boolean isDou
        String result;
        try {
            String rightNum = String.valueOf((int) Double.parseDouble(fromNum));
            String newNum;
            String[] numArray = null;
            if (rightNum.contains(".")) {
                numArray = rightNum.split("\\.");
                newNum = numArray[0];
            } else {
                newNum = rightNum;
            }
            StringBuilder sb = new StringBuilder();
            char[] chars = newNum.toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                sb.append(chars[i]);
                if ((chars.length - i) % 3 == 0 && i != chars.length - 1) {
                    if (i == 0 || i == 1) continue;
                    sb.append(',');
                }
            }
            result = getPlusNum(numArray == null ? sb.reverse().toString() : sb.reverse().append(".").append(numArray[1]).toString());
        } catch (Exception e) {
            e.printStackTrace();
            result = fromNum;
        }
        return result;
    }

    private static String getPlusNum(String num) {
        if (TextUtils.isEmpty(num)) return "";
        if (!num.contains("-")) {
            return "+" + num;
        }
        return num;
    }

    /**
     * 保留两位小数
     *
     * @param num double 类型 数字
     * @return 两位小数的字符串
     */
    public static String twoDouble(String num) {
//        return String.format(Locale.getDefault(), "%.2f", num); //四舍五入
//        BigDecimal decimal1 = new BigDecimal(num);
//        BigDecimal decimal2 = new BigDecimal(0.005);
//        double v = decimal1.subtract(decimal2).doubleValue();
//        return String.format(Locale.getDefault(), "%.2f", v);//不四舍五入
//        DecimalFormat formatter = new DecimalFormat("#0.##");
//        formatter.setRoundingMode(RoundingMode.FLOOR);
//        return formatter.format(num);
        String result;
        if (num.contains(".")) {
            String[] arrNum = num.split("\\.");
            if (arrNum[1].length() > 2) {
                result = TextUtils.concat(arrNum[0], ".", arrNum[1].substring(0, 2)).toString();
            } else if (arrNum[1].length() == 2) {
                result = num;
            } else if (arrNum[1].length() == 1) {
                result = TextUtils.concat(arrNum[0], ".", arrNum[1], "0").toString();
            } else {
                result = num;
            }
        } else {
            result = TextUtils.concat(num, ".00").toString();
        }
        return result;
    }

    /**
     * 整数后面拼接两位小数
     */
    public static String getTwoDoubleNum(int num) {
        return TextUtils.concat(String.valueOf(num), ".00").toString();
    }

    /**
     * 小数部分为 0 取整
     * 不为0 , 返回
     */
    public static String getRightDouble(String num) {
        double f = Double.parseDouble(num);
        int intF = (int) f;
        if (f - intF == 0) {
            return String.valueOf(intF);
        } else {
            return num;
        }
    }

    /**
     * 判断一个字符串是否是整数
     */
    public static boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

}
