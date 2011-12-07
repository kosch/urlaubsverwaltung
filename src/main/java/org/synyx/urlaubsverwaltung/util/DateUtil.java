package org.synyx.urlaubsverwaltung.util;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;


/**
 * @author  Aljona Murygina
 */
public class DateUtil {

    private static final int CHRISTMAS_EVE = 24;
    private static final int LAST_DAY_OF_MONTH = 31;
    private static final int FIRST_DAY_OF_MONTH = 1;
    private static final int SATURDAY = DateTimeConstants.SATURDAY;
    private static final int SUNDAY = DateTimeConstants.SUNDAY;

    /**
     * checks if the given date is a work day
     *
     * @param  date
     *
     * @return
     */
    public static boolean isWorkDay(DateMidnight date) {

        return !(date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY);
    }


    /**
     * checks if the given period is before April
     *
     * @param  month
     *
     * @return
     */
    public static boolean isBeforeApril(int startMonth, int endMonth) {

        return (DateTimeConstants.JANUARY <= startMonth && startMonth <= DateTimeConstants.MARCH)
            && (DateTimeConstants.JANUARY <= endMonth && endMonth <= DateTimeConstants.MARCH);
    }


    /**
     * checks if the given period is after April
     *
     * @param  month
     *
     * @return
     */
    public static boolean isAfterApril(int startMonth, int endMonth) {

        return (DateTimeConstants.APRIL <= startMonth && startMonth <= DateTimeConstants.DECEMBER)
            && (DateTimeConstants.APRIL <= endMonth && endMonth <= DateTimeConstants.DECEMBER);
    }


    /**
     * checks if the given period is between March and April
     *
     * @param  monthStart
     * @param  monthEnd
     *
     * @return
     */
    public static boolean isBetweenMarchAndApril(int startMonth, int endMonth) {

        return (startMonth == DateTimeConstants.MARCH && endMonth == DateTimeConstants.APRIL);
    }


    /**
     * checks if given period is between December and January
     *
     * @param  startMonth
     * @param  endMonth
     *
     * @return
     */
    public static boolean isBetweenDecemberAndJanuary(int startMonth, int endMonth) {

        return (startMonth == DateTimeConstants.DECEMBER && endMonth == DateTimeConstants.JANUARY);
    }


    /**
     * checks if given date is before April
     *
     * @param  date
     *
     * @return
     */
    public static boolean isBeforeApril(DateMidnight date) {

        return (date.getMonthOfYear() < DateTimeConstants.APRIL);
    }


    /**
     * checks if given date is after April
     *
     * @param  date
     *
     * @return
     */
    public static boolean isAfterApril(DateMidnight date) {

        return (date.getMonthOfYear() >= DateTimeConstants.APRIL);
    }


    /**
     * this method checks if a given date is on Christmas Eve or on New Year's Eve
     *
     * @param  date
     *
     * @return  true if date is on Christmas Eve or on New Year's Eve, else false
     */
    public static boolean isChristmasEveOrNewYearsEve(DateMidnight date) {

        if ((date.getDayOfMonth() == CHRISTMAS_EVE && date.getMonthOfYear() == DateTimeConstants.DECEMBER)
                || (date.getDayOfMonth() == LAST_DAY_OF_MONTH && date.getMonthOfYear() == DateTimeConstants.DECEMBER)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * this method checks if a given date is on first January (returns true) or not (returns false).
     *
     * @param  date
     *
     * @return
     */
    public static boolean isFirstJanuary(DateMidnight date) {

        return ((date.getDayOfMonth() == FIRST_DAY_OF_MONTH) && (date.getMonthOfYear() == DateTimeConstants.JANUARY));
    }
}