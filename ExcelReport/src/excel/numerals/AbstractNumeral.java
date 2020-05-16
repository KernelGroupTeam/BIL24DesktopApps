package excel.numerals;

import java.math.*;

/**
 * 
 */
public abstract class AbstractNumeral {

    public static final BigInteger MAX_SUPPORTED = new BigInteger("1000000000000000000000000000000000000").subtract(BigInteger.ONE);
    //10^33
    
    protected void checkSupported(Number number) {
        if (number instanceof Integer
                || number instanceof Long
                || number instanceof Short
                ||number instanceof Byte) {
        } else if (number instanceof BigInteger) {
            BigInteger bi = (BigInteger) number;
            if (bi.abs().compareTo(MAX_SUPPORTED) > 0) {
                throw new IllegalArgumentException("Max supported number:" + MAX_SUPPORTED);
            }
        } else {
            throw new IllegalArgumentException("Support only Integer numbers: BigInteger, Integer, Long and Short."
                    + "Floating-point is not supported");
        }
    }

    public abstract String format(Number number);

    public abstract String amount(BigDecimal amount);

    public String amount(Number amount) {
        return amount(Util.toBigDecimal(amount));
    }
}
