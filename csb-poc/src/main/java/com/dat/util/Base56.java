/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.util;

/**
 * This class implements base-56 notation using a restricted ASCII alphabet for numerals.
 * <p>
 * The alphabet (in ordinal order) is "0123456789ABCDEFGHJKLMNPRSTUVWXYZabcdefghjkmnpqrstuvwxyz".
 * 'I', 'O', 'Q', 'i', 'l', and 'o' are excluded because they are too easily confused with '1' and
 * '0'.
 *
 * @author Tim Dale
 * @version $Id: Base56.java 16544 2013-10-25 20:52:53Z philipc $
 *
 */
public class Base56
{
    /**
     * Given a decimal value in the range 0..55, return its representative numeral.
     *
     * @param decimalValue The decimal value to convert to a base-56 numeral.
     *
     * @return The representative numeral for decimal values in the range 0..55.
     *
     * @throws java.lang.IllegalArgumentException if the decimal value is outside of 0..55.
     */
    public static char numeral(final int decimalValue)
    {
        ArgCheck.inRange(decimalValue, 0, 55);
        return alphabet[decimalValue];
    }

    /**
     * Given a base-56 numeral, return it's decimal value.
     *
     * @param base56Numeral The base-56 numeral to convert into a decimal value.
     *
     * @return the decimal value (0..55) of the base-56 numeral.
     *
     * @throws java.lang.IllegalArgumentException If the base-56 numeral is not a valid base-56
     *             numeral.
     */
    public static int toDecimal(final char base56Numeral)
    {
        switch (base56Numeral)
        {
            case '0' :
                return 0;
            case '1' :
                return 1;
            case '2' :
                return 2;
            case '3' :
                return 3;
            case '4' :
                return 4;
            case '5' :
                return 5;
            case '6' :
                return 6;
            case '7' :
                return 7;
            case '8' :
                return 8;
            case '9' :
                return 9;
            case 'A' :
                return 10;
            case 'B' :
                return 11;
            case 'C' :
                return 12;
            case 'D' :
                return 13;
            case 'E' :
                return 14;
            case 'F' :
                return 15;
            case 'G' :
                return 16;
            case 'H' :
                return 17;
            case 'J' :
                return 18;
            case 'K' :
                return 19;
            case 'L' :
                return 20;
            case 'M' :
                return 21;
            case 'N' :
                return 22;
            case 'P' :
                return 23;
            case 'R' :
                return 24;
            case 'S' :
                return 25;
            case 'T' :
                return 26;
            case 'U' :
                return 27;
            case 'V' :
                return 28;
            case 'W' :
                return 29;
            case 'X' :
                return 30;
            case 'Y' :
                return 31;
            case 'Z' :
                return 32;
            case 'a' :
                return 33;
            case 'b' :
                return 34;
            case 'c' :
                return 35;
            case 'd' :
                return 36;
            case 'e' :
                return 37;
            case 'f' :
                return 38;
            case 'g' :
                return 39;
            case 'h' :
                return 40;
            case 'j' :
                return 41;
            case 'k' :
                return 42;
            case 'm' :
                return 43;
            case 'n' :
                return 44;
            case 'p' :
                return 45;
            case 'q' :
                return 46;
            case 'r' :
                return 47;
            case 's' :
                return 48;
            case 't' :
                return 49;
            case 'u' :
                return 50;
            case 'v' :
                return 51;
            case 'w' :
                return 52;
            case 'x' :
                return 53;
            case 'y' :
                return 54;
            case 'z' :
                return 55;
            default :
                throw new IllegalArgumentException("base56Numeral is not in base-56 alphabet");
        }
    }

    /**
     * Convert a decimal value to base-56 numerals.
     *
     * @param decimalValue The decimal value to convert to base-56 encoding.
     *
     * @param minimumDigits The minimum number of digits. This allows for leading zeros. Must be >=
     *            1, otherwise will default to 1.
     *
     * @return String encoding of the decimal value in base-56.
     */
    public static String toString(long decimalValue, int minimumDigits)
    {
        if (minimumDigits < 1)
        {
            minimumDigits = 1;
        }

        final boolean negative = decimalValue < 0;
        if (negative)
        {
            decimalValue = -decimalValue;
        }

        final StringBuilder sb = new StringBuilder();
        int digitsProcessed = 0;
        boolean done = false;
        while (!done)
        {
            final int digit = (int) (decimalValue % 56);
            sb.append(numeral(digit));
            ++digitsProcessed;

            decimalValue /= 56;
            done = decimalValue == 0 && digitsProcessed >= minimumDigits;
        }

        if (negative)
        {
            sb.append('-');
        }

        return sb.reverse().toString();
    }

    /**
     * Convert a base 56 encoded string back into its decimal value.
     *
     * @param base56String a string as returned by {@link #toString(long, int)}
     *
     * @return the decimal value represented by {@code base56String}
     *
     * @throws IllegalArgumentException if the base56String is null or empty
     */
    public static long toDecimalValue(final String base56String)
    {

        ArgCheck.notEmpty(base56String);

        final int len = base56String.length();

        long decimalValue = 0L;

        for (int index = 0; index < len; index++)
        {
            decimalValue = decimalValue * 56 + Base56.toDecimal(base56String.charAt(index));
        }

        return decimalValue;
    }

    //
    // Base-56 alphabet "0123456789ABCDEFGHJKLMNPRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    //
    private static final char alphabet[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
                                            'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'V', 'W',
                                            'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
                                            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

}
