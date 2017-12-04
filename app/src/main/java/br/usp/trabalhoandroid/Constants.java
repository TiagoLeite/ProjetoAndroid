package br.usp.trabalhoandroid;

public class Constants {

    // Webhost Constants
    public static final String DB_URL = "https://bd-android.000webhostapp.com/";

    public static final String REGISTER_URL = DB_URL + "register.php";
    public static final String LOGIN_URL = DB_URL + "login.php";
    public static final String UPDATE_URL = DB_URL + "update.php";

    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BIRTH = "birth";
    public static final String KEY_USERNAME = "username";

    // User constants... we can create a User class later, if necessary.
    public static String NAME;
    public static String EMAIL;
    public static String PASSWORD;
    public static String GENDER;
    public static String BIRTH;
    public static String USERNAME;

    public static final String LOGIN_PREFS = "LoginPrefs";
}
