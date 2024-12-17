package datasources;

import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Common tools for random data generation
 */

public class RandomDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RandomDataGenerator.class);
    public static final String DEFAULT_SEPARATOR = "\\.";
    public static final String[] SPECIAL_CHARS = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_"};


    /**
     * get string in format yyyyMMdd_HHmmss
     * @return date as string
     */
    public static String getCurDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }


    /**
     * get user email in format yyyyMMdd_HHmmss_*@gmail.com
     * @return random email
     */
    public static String getRandomUserEmail(String domain) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy_HHmmss");
        return sdf.format(new Date(System.currentTimeMillis())) + domain;
    }

    /**
     * get random number in range 0 - i
     * @param i upper limit
     * @return number as string
     */
    public static String getRandomNumber(int i) {
        Random r = new Random();
        return String.valueOf(r.nextInt(i));
    }

    /**
     * get random number in range min - max
     * @param max upper limit
     * @param min bottom limit
     * @return number as string
     */
    public static String getRandomNumberFromRange(int min, int max) {
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        return String.valueOf(random_int);
    }

    public static String getRandomDropdown(List<String> elements){
        if(elements.size()>=1)
        {
            Random random = new Random();
            int randomIndex = random.nextInt(elements.size());
            return  elements.get(randomIndex);
        }
        return "";
    }

    public static String getRandomBooleanAsString(){
      return   String.valueOf(Math.random() < 0.5);
    }

    public static boolean getRandomBoolean(){
        return Math.random() < 0.5;
    }


    /**
     * Generate random string using template
     * @param template for generation
     * @return random string
     */
    public static String getRandomField(String template) {
        return getRandomField(template, RandomDataGenerator.DEFAULT_SEPARATOR);
    }
    public static String getRandomField(String parameters, String parameterDelimiter) {

        String result = "";

        String DEEP_PAST_DATE_LABEL = "deep_past_date";
        String CURRENT_DATE_LABEL = "current_date";
        String EMAIL_LABEL = "email";
        String FUTURE_DATE_LABEL = "future_date";
        String PAST_DATE_LABEL = "past_date";
        String WI_USER_LABEL = "wi_user";
        String DEFAULT_TEMPLATE = "10c";
        String DROPDOWNS = "dropdown";
        String CHECKBOX="randomCheckBox";

        String template;
        if (parameters.equals("")){
            template = DEFAULT_TEMPLATE;
        } else {
            template = parameters;
        }

        if (parameters.toLowerCase().contains(DROPDOWNS)) {

            List<String> elements = Arrays.asList(parameters.split(parameterDelimiter));
            return getRandomDropdown(elements.subList(1,elements.size()));
        }

        if(parameters.contains(CHECKBOX)){
           return getRandomBooleanAsString();
        }

        //dates generation

        if (parameters.toLowerCase().contains(DEEP_PAST_DATE_LABEL)) {
            int days = 73000;
            return LocalDateTime.now().minusDays(days).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        }

        if (parameters.toLowerCase().contains(CURRENT_DATE_LABEL)) {
            String[] parts = parameters.split(parameterDelimiter);
            if(parts.length > 1) {
                if (parts[1].equals("ISO_OFFSET_DATE_TIME")) { // TODO generate from label
                    return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                } else {
                    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(parts[1]));
                }
            }
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        }

        if (parameters.toLowerCase().contains(FUTURE_DATE_LABEL)) {
            Random r = new Random();
            int days = r.nextInt(10);
            String[] parts = parameters.split(parameterDelimiter);
            if(parts.length > 1) {
                if (parts[1].equals("ISO_OFFSET_DATE_TIME")) { // TODO generate from label
                    return ZonedDateTime.now().plusDays(days).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                } else {
                    return LocalDateTime.now().plusDays(days).format(DateTimeFormatter.ofPattern(parts[1]));
                }
            }
            return LocalDateTime.now().plusDays(days).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        }
        if (parameters.toLowerCase().contains(PAST_DATE_LABEL)) {
            Random r = new Random();
            int days = r.nextInt(10)+1;
            String[] parts = parameters.split(parameterDelimiter);
            if(parts.length > 1) {
                if (parts[1].equals("ISO_OFFSET_DATE_TIME")) { // TODO generate from label
                    return ZonedDateTime.now().minusDays(days).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                } else {
                    return LocalDateTime.now().minusDays(days).format(DateTimeFormatter.ofPattern(parts[1]));
                }
            }
            return LocalDateTime.now().minusDays(days).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        }


        // email generation
        if (parameters.toLowerCase().contains(EMAIL_LABEL))
            return getRandomStringByTemplate(DEFAULT_TEMPLATE) + "@email.com";

        // WI user generation
        if (parameters.toLowerCase().contains(WI_USER_LABEL)) {
            String[] parts = parameters.split(parameterDelimiter);
            if(parts.length > 1) {
                return String.format("WI_%s_%s", parts[1], getRandomStringByTemplate(DEFAULT_TEMPLATE));
            }
            return String.format("WI_notSpecified_%s", getRandomStringByTemplate(DEFAULT_TEMPLATE));
        }

        // string generation
        result = getRandomStringByTemplate(template);

        return result;
    }


    /**
     * Simple random string generator <br>
     *     Based on simple template: {number}{type} <br>
     *   Available types: <ol>
     *       <li>d - digit</li>
     *       <li>c - chars</li>
     *       <li>s - chars + digits</li>
     *       <li>a - any printable</li>
     *   </ol>
     *   Example: Single char: c, <br> SSN: 3d-2d-4d, <br>Name: 10c, <br>Password: 10a
     * @param template template
     * @return random string
     */
    public static  String getRandomStringByTemplate(String template) {
        String resultString = "";
        String counter = "";
        for(int i =0; i<template.length(); i++){
            char b = template.toCharArray()[i];
            switch (b){
                case '0' :
                case '1' :
                case '2' :
                case '3' :
                case '4' :
                case '5' :
                case '6' :
                case '7' :
                case '8' :
                case '9' :
                    counter = counter + b;
                    break;
                //digit
                case 'd': resultString = resultString + (counter.equals("")? "d" : RandomStringUtils.random(Integer.parseInt(counter), false, true)); counter = ""; break;
                //char
                case 'c': resultString = resultString + (counter.equals("")? "c" : RandomStringUtils.random(Integer.parseInt(counter), true, false)); counter = ""; break;
                //alphanumeric
                case 's': resultString = resultString + (counter.equals("")? "s" : RandomStringUtils.random(Integer.parseInt(counter), true, true)); counter = ""; break;
                //any
                case 'a': resultString = resultString + (counter.equals("") ? "a" : RandomStringUtils.randomPrint(Integer.valueOf(counter))); counter = ""; break;
                default: resultString = resultString + b;
            }
        }
        return resultString;
    }

    /** Get random number in range from 0 to defined border
     *
     * @param maxValue max
     * @return random integer
     */
        public static int getRandomIntInRange(int maxValue){
            Random random = new Random();
            return random.ints(0,maxValue).findFirst().getAsInt();
        }
    /** Get random number from defined range
     *
     * @param minValue min
     * @param maxValue max
     * @return randim int
     */
    public static int getRandomIntInRange(int minValue, int maxValue){
        Random random = new Random();
        return random.ints(minValue,maxValue).findFirst().getAsInt();
    }

    public static String getRandomSpecialChar() {
        int index = getRandomIntInRange(SPECIAL_CHARS.length);
        return SPECIAL_CHARS[index];
    }
}
