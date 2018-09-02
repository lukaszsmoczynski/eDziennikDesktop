package com.hiero.elektronicznydziennik.Helpers.SharedPreferences;

import com.hiero.elektronicznydziennik.Helpers.Functions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by Hiero on 11.12.2017.
 */

public class PreferencesHelper {
    private static final String PACKAGE_NAME = "/com/hiero/elektronicznydziennik";

    static final String REG_LOGIN = "REG_LOGIN";

    private static final String USER_ID = "USER_ID";
    private static final String LOGIN = "LOGIN";

    private static final String IS_PARENT = "IS_PARENT";
    private static final String IS_STUDENT = "IS_STUDENT";
    private static final String IS_ADMIN = "IS_ADMIN";
    private static final String IS_TEACHER = "IS_TEACHER";

    private static final String NOTE_REASONS = "NOTE_REASONS";

    private static Preferences getPreferences() {
        return Preferences.userRoot().node(PACKAGE_NAME);
    }

    public static void setLastLogin(String login) {
        try {
            Preferences preferences = getPreferences();
            preferences.put(REG_LOGIN, login);
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static String getLastLogin() {
        return getPreferences().get(REG_LOGIN, "");
    }

    public static void setUserID(Integer userID) {
        try {
            Preferences preferences = getPreferences();
            preferences.putInt(USER_ID, userID);
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static Integer getUserID() {
        return getPreferences().getInt(USER_ID, -1);
    }

    public static void setUserName(String userName) {
        try {
            Preferences preferences = getPreferences();
            preferences.put(LOGIN, userName);
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static String getUserName() {
        return getPreferences().get(LOGIN, "");
    }

    public static void setIsParent(Boolean pIsParent) {
        try {
            Preferences preferences = getPreferences();
            preferences.putBoolean(IS_PARENT, pIsParent);
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static Boolean getIsParent() {
        return getPreferences().getBoolean(IS_PARENT, false);
    }

    public static void setIsStudent(Boolean pIsStudent) {
        try {
            Preferences preferences = getPreferences();
            preferences.putBoolean(IS_STUDENT, pIsStudent);
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static Boolean getIsStudent() {
        return getPreferences().getBoolean(IS_STUDENT, false);
    }

    public static void setIsAdmin(Boolean pIsAdmin) {
        try {
            Preferences preferences = getPreferences();
            preferences.putBoolean(IS_ADMIN, pIsAdmin);
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static Boolean getIsAdmin() {
        return getPreferences().getBoolean(IS_ADMIN, false);
    }

    public static void setIsTeacher(Boolean pIsTeacher) {
        try {
            Preferences preferences = getPreferences();
            preferences.putBoolean(IS_TEACHER, pIsTeacher);
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static Boolean getIsTeacher() {
        return getPreferences().getBoolean(IS_TEACHER, false);
    }

    public static Set<String> getNoteReasons() {
        try {
            Set<String> result;
            String pref = getPreferences().get(NOTE_REASONS, "");

            result = new HashSet<String>();
            for (String reason : CSVParser.parse(pref, CSVFormat.DEFAULT).getRecords().get(0)) {
                result.add(reason);
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
        return null;
    }

    public static void addNoteReasons(String... reasons) {
        Set<String> newReasons = getNoteReasons();
        Collections.addAll(newReasons, reasons);
        setNoteReasons(newReasons);
    }

    public static void addNoteReasons(Set<String> reasons) {
        Set<String> newReasons = getNoteReasons();
        newReasons.addAll(reasons);
        setNoteReasons(newReasons);
    }

    public static void setNoteReasons(String... reasons) {
        try {
            Preferences preferences = getPreferences();

            StringWriter stringWriter = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);
            csvPrinter.printRecord(reasons);
            csvPrinter.flush();

            preferences.put(NOTE_REASONS, stringWriter.toString());
            preferences.flush();
        } catch (BackingStoreException | IOException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }

    public static void setNoteReasons(Set<String> reasons) {
        try {
            Preferences preferences = getPreferences();

            StringWriter stringWriter = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);
            csvPrinter.printRecord(reasons);
            csvPrinter.flush();

            preferences.put(NOTE_REASONS, stringWriter.toString());
            preferences.flush();
        } catch (BackingStoreException | IOException e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }
    }
}
