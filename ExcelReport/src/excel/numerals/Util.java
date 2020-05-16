package excel.numerals;

import java.math.*;

/**
 * Utility-class for internal usage only
 * @author vit
 */
class Util {

    private Util() {
    }

    static byte[][] groups(String txt, int groupSize) {
    	
        int length = txt.length();
        int groupCount = length / groupSize;
        int remainder = length % groupSize;

        int j = 0;
        int k = 0;
        if (remainder > 0) {
            groupCount++;
            k = groupSize - remainder;
        }
        byte[][] nn = new byte[groupCount][groupSize];
        // 1234567 - 1,2,3,4,5,6,7 -- [0,0,1],[2,3,4],[5,6,7]
        for (int i = 0; i < txt.length(); ++i) {
            byte x = (byte) (txt.charAt(i) - '0');
            if (x < 0 || x > 9) {
                throw new IllegalArgumentException("Wrong string:" + txt);
            }
            nn[j][k] = x;
            if (k == groupSize - 1) {
                k = 0;
                j++;
            } else {
                k++;
            }
        }
        return nn;
    }

    /**
     * @return -1 = not found, else index
     */
    static int search(String[] array, String token) {
        int x = -1;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(token)) {
                x = i;
                break;
            }
        }
        return x;
    }

    static void toUpperCaseFirstLetter(StringBuilder sb) {
        // to upper case
        char first = sb.charAt(0);
        first = Character.toUpperCase(first);
        sb.setCharAt(0, first);
    }

    static BigDecimal toBigDecimal(Number n) {
        BigDecimal bd;
        if (n == null) {
            throw new IllegalArgumentException("Your number is null");
        } else if (n instanceof BigDecimal) {
            bd = (BigDecimal) n;
        } else if (n instanceof BigInteger) {
            bd = new BigDecimal((BigInteger) n);
        } else if (n instanceof Short
                || n instanceof Long
                || n instanceof Integer) {
            bd = BigDecimal.valueOf(n.longValue());
        } else if (n instanceof Float
                || n instanceof Double) {
            bd = BigDecimal.valueOf(n.doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported type:" + n);
        }
        return bd;
    }
}
