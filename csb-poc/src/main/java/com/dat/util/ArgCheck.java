/*
 * $Id: ArgCheck.java 18617 2014-09-04 15:38:41Z philipc $
 *
 * Copyright (C) 2004-2017, TransCore LP. All Rights Reserved
 */

package com.dat.util;


import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

/**
 * This class is used to check the values of arguments (parameters) to methods to insure expected pre-conditions. If the
 * pre-condition is violated, a <code>java.lang.IllegalArgument</code> exception (with explanatory message) is thrown.
 *
 * @author Tim Dale
 * @since 01/2004
 *
 * @version $Revision: 18617 $ $Date: 2014-09-04 08:38:41 -0700 (Thu, 04 Sep 2014) $ $Author: philipc $
 */

public final class ArgCheck {

    //
    // Common Messages
    //
    private static String OUT_OF_ORDER = "lower must be less than or equal to upper";

    private static String OUT_OF_RANGE = "pre-condition: argument must be in range [";

    private static String NOT_ZERO = "argument cannot be zero";

    private static String NOT_NEGATIVE = "argument cannot be negative";

    private static String NOT_POSITIVE = "argument cannot be positive";

    private static String LESS_THAN = "argument cannot be less than ";

    private static String GREATER_THAN = "argument cannot be greater than ";

    private static String NOT_NULL = "argument cannot be null";



    /**
     * Test if an expression is true.
     *
     * @param expression Boolean expression to test
     *
     * @param message a message to provide to <code>IllegalArgumentException</code> if expression is NOT true.
     *
     * @throws java.lang.IllegalArgumentException if <code>expression</code> is not true, embedding
     *         <code>message</code> in the exception message.
     */
    public static final void test(final boolean expression, final String message) {

        if (!expression) {
            throw iax(message);
        }
    }



    /**
     * Test if argument is null.
     *
     * @param o Object to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>o</code> is null.
     */
    public static final void notNull(final Object o) {

        // coded for speed; the null check has to be fast...
        if (o == null) {
            throw iax(NOT_NULL);
        }
    }



    /**
     * Test if argument is null.
     *
     * @param o Object to test
     *
     * @param msg Optional message to include in the IllegalArgumentException. If null or empty, a default message will
     *        be automatically supplied.
     *
     * @throws java.lang.IllegalArgumentException exception if <code>o</code> is null.
     */
    public static final void notNull(final Object o, String msg) {

        // coded for speed; only take extra time if the null check fails...
        if (o == null) {
            if (StringUtils.isEmpty(msg)) {
                msg = NOT_NULL;
            }
            throw iax(msg);
        }
    }



    /**
     * Test if String argument is null or empty ("").
     *
     * @param s String to test.
     *
     * @param message a message to provide to <code>IllegalArgumentException</code> if <code>s</code> is null or
     *        empty.
     *
     * @throws java.lang.IllegalArgumentException exception if <code>s</code> is null or empty.
     */
    public static final void notEmpty(final CharSequence s, final String message) {

        test((s != null) && !StringUtils.isEmpty(s.toString()), message);
    }



    /**
     * Test if String argument is null or empty ("").
     *
     * @param s String to test.
     *
     * @throws java.lang.IllegalArgumentException exception if <code>s</code> is null or empty.
     */
    public static final void notEmpty(final CharSequence s) {

        notEmpty(s, "string argument cannot be null or empty");
    }



    /**
     * Test is a collection argument is null or empty.
     *
     * @param c Collection to test.
     *
     * @param message a message to provide to <code>IllegalArgumentException</code> if <code>c</code> is null or
     *        empty.
     *
     * @throws IllegalArgumentException if <code>c</code> is null or empty.
     */
    public static final void notEmpty(final Collection<? extends Object> c, final String message) {

        test((c != null) && (c.size() > 0), message);
    }



    /**
     * Test is a collection argument is null or empty.
     *
     * @param c Collection to test.
     *
     * @throws IllegalArgumentException if <code>c</code> is null or empty.
     */
    public static final void notEmpty(final Collection<? extends Object> c) {

        notEmpty(c, "collection argument cannot be null or empty");
    }



    /**
     * Test if argument is zero.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is zero.
     */
    public static final void notZero(final int value) {

        test((value != 0), NOT_ZERO);
    }



    /**
     * Test if argument is zero.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is zero.
     */
    public static final void notZero(final double value) {

        test((value != 0.0), NOT_ZERO);
    }



    /**
     * Test if argument is zero.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is zero.
     */
    public static final void notZero(final float value) {

        test((value != 0.0), NOT_ZERO);
    }



    /**
     * Test if argument is zero.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is zero.
     */
    public static final void notZero(final char value) {

        test((value != '\u0000'), NOT_ZERO);
    }



    /**
     * Test if argument is negative.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is negative.
     */
    public static final void notNegative(final int value) {

        test((value >= 0), NOT_NEGATIVE);
    }



    /**
     * Test if argument is negative.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is negative.
     */
    public static final void notNegative(final double value) {

        test((value >= 0.0), NOT_NEGATIVE);
    }



    /**
     * Test if argument is negative.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is negative.
     */
    public static final void notNegative(final float value) {

        test((value >= 0.0), NOT_NEGATIVE);
    }



    /**
     * Test if argument is positive.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is positive.
     */
    public static final void notPositive(final int value) {

        test((value < 0), NOT_POSITIVE);
    }



    /**
     * Test if argument is positive.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is positive.
     */
    public static final void notPositive(final double value) {

        test((value < 0.0), NOT_POSITIVE);
    }



    /**
     * Test if argument is positive.
     *
     * @param value Value to test
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is positive.
     */
    public static final void notPositive(final float value) {

        test((value < 0.0), NOT_POSITIVE);
    }



    /**
     * Test if argument is in an inclusive range.
     *
     * @param value Value to test
     *
     * @param lower Lower inclusive range value
     *
     * @param upper Upper inclusive range value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is not in the range [
     *         <code>lower</code>..<code>upper</code>], or <code>lower</code> is not less than or equal to
     *         <code>upper</code>.
     */
    public static final void inRange(final int value, final int lower, final int upper) {

        ArgCheck.test((lower <= upper), OUT_OF_ORDER);
        if ((value < lower) || (value > upper)) {
            throw iax(OUT_OF_RANGE + lower + ".." + upper + "]");
        }
    }



    /**
     * Test if argument is in an inclusive range.
     *
     * @param value Value to test
     *
     * @param lower Lower inclusive range value
     *
     * @param upper Upper inclusive range value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is not in the range [
     *         <code>lower</code>..<code>upper</code>], or <code>lower</code> is not less than or equal to
     *         <code>upper</code>.
     */
    public static final void inRange(final double value, final double lower, final double upper) {

        ArgCheck.test((lower <= upper), OUT_OF_ORDER);
        if ((value < lower) || (value > upper)) {
            throw iax(OUT_OF_RANGE + lower + ".." + upper + "]");
        }
    }



    /**
     * Test if argument is in an inclusive range.
     *
     * @param value Value to test
     *
     * @param lower Lower inclusive range value
     *
     * @param upper Upper inclusive range value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is not in the range [
     *         <code>lower</code>..<code>upper</code>], or <code>lower</code> is not less than or equal to
     *         <code>upper</code>.
     */
    public static final void inRange(final float value, final float lower, final float upper) {

        ArgCheck.test((lower <= upper), OUT_OF_ORDER);
        if ((value < lower) || (value > upper)) {
            throw iax(OUT_OF_RANGE + lower + ".." + upper + "]");
        }
    }



    /**
     * Test if argument is in an inclusive range.
     *
     * @param value Value to test
     *
     * @param lower Lower inclusive range value
     *
     * @param upper Upper inclusive range value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is not in the range [
     *         <code>lower</code>..<code>upper</code>], or <code>lower</code> is not less than or equal to
     *         <code>upper</code>.
     */
    public static final void inRange(final char value, final char lower, final char upper) {

        ArgCheck.test((lower <= upper), OUT_OF_ORDER);
        if ((value < lower) || (value > upper)) {
            throw iax(OUT_OF_RANGE + "(char)" + (int)lower + "..(char)" + (int)upper + "]");
        }
    }



    /**
     * Test if the first argument is less than the second argument.
     *
     * @param lower The lower value
     *
     * @param upper The upper value
     *
     * @throws java.lang.IllegalArgumentException if <code>lower</code> is not <=<code>upper</code>.
     */
    public static final void inOrder(final long lower, final long upper) {

        ArgCheck.test((lower <= upper), OUT_OF_ORDER);
    }



    /**
     * Test if argument is less than a boundary.
     *
     * @param value Value to test
     *
     * @param lower Lower bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is less than <code>lower</code>.
     */
    public static final void notLessThan(final int value, final int lower) {

        test((value >= lower), LESS_THAN + lower);
    }



    /**
     * Test if argument is less than a boundary.
     *
     * @param value Value to test
     * @param lower Lower bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is less than <code>lower</code>.
     */
    public static final void notLessThan(final double value, final double lower) {

        test((value >= lower), LESS_THAN + lower);
    }



    /**
     * Test if argument is less than a boundary.
     *
     * @param value Value to test
     *
     * @param lower Lower bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is less than <code>lower</code>.
     */
    public static final void notLessThan(final float value, final float lower) {

        test((value >= lower), LESS_THAN + lower);
    }



    /**
     * Test if argument is less than a boundary.
     *
     * @param value Value to test
     *
     * @param lower Lower bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is less than <code>lower</code>.
     */
    public static final void notLessThan(final char value, final char lower) {

        test((value >= lower), LESS_THAN + "(char)" + (int)lower);
    }



    /**
     * Test if argument is greater than a boundary.
     *
     * @param value Value to test
     * @param upper Upper bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is greater than <code>upper</code>.
     */
    public static final void notGreaterThan(final int value, final int upper) {

        test((value <= upper), GREATER_THAN + upper);
    }



    /**
     * Test if argument is greater than a boundary.
     *
     * @param value Value to test
     * @param upper Upper bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is greater than <code>upper</code>.
     */
    public static final void notGreaterThan(final double value, final double upper) {

        test((value <= upper), GREATER_THAN + upper);
    }



    /**
     * Test if argument is greater than a boundary.
     *
     * @param value Value to test
     *
     * @param upper Upper bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is greater than <code>upper</code>.
     */
    public static final void notGreaterThan(final float value, final float upper) {

        test((value <= upper), GREATER_THAN + upper);
    }



    /**
     * Test if argument is greater than a boundary.
     *
     * @param value Value to test
     *
     * @param upper Upper bounds value
     *
     * @throws java.lang.IllegalArgumentException exception if <code>value</code> is greater than <code>upper</code>.
     */
    public static final void notGreaterThan(final char value, final char upper) {

        test((value <= upper), GREATER_THAN + "(char)" + (int)upper);
    }

    /**
     * Dynamic form of "instanceof" operator: test if an object is an instance
     * of the specified class.
     *
     * @param o Object to test.
     *
     * @param superClass <code>Class</code> to test if <code>o</code> is an
     *            instance of.
     *
     * @return <code>true</code> if <code>o</code> is an instance of the
     *         class <code>superClass</code>. If either parameter is null,
     *         then <code>false</code> is returned. The default class loader
     *         is used.
     */
    private static boolean isInstanceOf(final Object o, final Class<?> superClass)
    {
        // safeties
        if ((o == null) || (superClass == null))
        {
            return false;
        }

        return superClass.isInstance(o);
    }

    /**
     * Dynamic form of "instanceof" operator: test if an object is an instance
     * of the specified class.
     *
     * @param o Object to test.
     *
     * @param superClassName Name of the class to test if <code>o</code> is an
     *            instance of.
     *
     * @return <code>true</code> if <code>o</code> is an instance of the
     *         class named <code>superClassName</code>. If either parameter
     *         is null, or a class cannot be found that is named
     *         <code>superClassName</code>, then <code>false</code> is
     *         returned. The default class loader is used.
     */
    private static boolean isInstanceOf(final Object o, final String superClassName)
    {
        // safeties
        if ((o == null) || StringUtils.isEmpty(superClassName))
        {
            return false;
        }

        // interrogate the rts
        Class<?> superClass;
        try
        {
            superClass = Class.forName(superClassName);
        }
        catch (final ClassNotFoundException e)
        {
            return false;
        }

        return isInstanceOf(o, superClass);
    }

    /**
     * Test if an object is of a particular class type.
     *
     * @param className Fully qualified name of the class (e.g., "dat.base.DateTime") to test if <code>o</code> is an
     *        instance.
     *
     * @param o the object to check
     *
     * @throws java.lang.IllegalArgumentException if <code>o</code> is not an instance of the class named
     *         <code>className</code>.
     */
    public static final void isClass(final String className, final Object o) {

        if (!isInstanceOf(o, className)) {
            // build up a message only if needed; calling test()
            // would have required building msg every time this
            // method was invoked... way too slow...
            final String msg = "pre-condition: argument must be instance of " + className;
            throw iax(msg);
        }
    }



    /**
     * Test if a collection contains only objects of a particular type.
     *
     * @param className Fully qualified name of the class of objects being tested for (e.g., "dat.base.DateTime").
     *
     * @param collection Collection of objects to test.
     *
     * @throws java.lang.IllegalArgumentException if <code>collection</code> contains any objects which are not
     *         instances of <code>className</code>.
     */
    public static final void containsOnly(final String className, final Collection<? extends Object> collection) {

        final Iterator<? extends Object> iter = collection.iterator();
        while (iter.hasNext()) {
            if (!isInstanceOf(iter.next(), className)) {

                // build up a message only if needed; calling test()
                // would have required building msg every time this
                // method was invoked... way too slow...
                final String msg = "pre-condition: collection must contain only objects " + "which are instances of "
                                + className;
                throw iax(msg);
            }
        }
    }



    /**
     * This method always throws {@link UnsupportedOperationException} and is nothing more than a convenient way for
     * callers to do so.
     *
     * @param o The object that is immutable. It's fully qualified class name will be included in the exception message,
     *        e.g., "Object of class com.myco.foo.MyClass is immutable". If null, message will be "Object is immutable".
     *
     * @throws UnsupportedOperationException Always.
     */
    public static final void immutable(final Object o) throws UnsupportedOperationException {

        String msg = "Object";
        if (o != null) {
            msg += " of " + o.getClass().toString();
        }
        msg += " is immutable";

        immutable(msg);
    }



    /**
     * This method always throws {@link UnsupportedOperationException} and is nothing more than a convenient way for
     * callers to do so.
     *
     * @param msg Message to include in the {@link UnsupportedOperationException}. Can be null or empty.
     *
     * @throws UnsupportedOperationException Always.
     */
    public static final void immutable(final String msg) throws UnsupportedOperationException {

        throw new UnsupportedOperationException(StringUtils.defaultString(msg));
    }



    // Note: this method is not "final"; we don't want to encourage the compiler to inline this rarely/never-called
    // method
    private static IllegalArgumentException iax(String message) {

        // safe the message (yes, should probably use a StringBuffer, but calls to this method SHOULD be exceedingly
        // rare...
        message = StringUtils.defaultString(message);

        // now attempt to add a stack trace to the message
        message += "   where: ";
        try {
            // figure out our name; no sense reporting the stack trace elements that are in this class
            final String thisClass = ArgCheck.class.getName();

            // get the stack trace
            final Throwable current = new Throwable();
            final StackTraceElement[] stackTrace = current.getStackTrace();

            // element 0 is us, element 1 is our caller; so start at element 2
            for (int i = 2; i < stackTrace.length; i++) {

                // skip this class
                if (stackTrace[i].getClassName().equals(thisClass)) {
                    continue;
                }

                message += stackTrace[i].toString() + "\n";
            }
        } catch (final Throwable t) {
            message += " <unable to generate a complete stacktrace>";
        }


        // finally, throw our IAX, complete with message and stack trace
        throw new IllegalArgumentException(message);
    }
}
