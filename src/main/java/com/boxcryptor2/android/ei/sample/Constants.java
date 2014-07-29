package com.boxcryptor2.android.ei.sample;

public class Constants {

    //encryption service connection
    static final String START_PERMISSION_ACTIVITY = "com.boxcryptor2.android.START_APP_PERMISSION";
    static final String START_ENCRYPTION_SERVICE = "com.boxcryptor2.android.START_ENCRYPTION_SERVICE";

    //encryption service communication
    static final int MSG_REGISTER_WITH_SERVICE = 0;
    static final int MSG_REGISTER_WITH_SERVICE_RESPONSE = 1;
    static final int MSG_ENCRYPT_FILE = 10;
    static final int MSG_ENCRYPT_FILE_RESPONSE = 11;
    static final int MSG_ENCRYPT_FILE_PROGRESS = 12;
    static final int MSG_DECRYPT_FILE = 20;
    static final int MSG_DECRYPT_FILE_RESPONSE = 21;
    static final int MSG_DECRYPT_FILE_PROGRESS = 22;
    static final int MSG_ABORT_FILE_ENCRYPTION = 30;
    static final int MSG_ABORT_FILE_ENCRYPTION_RESPONSE = 31;
    static final int MSG_ABORT_FILE_DECRYPTION = 40;
    static final int MSG_ABORT_FILE_DECRYPTION_RESPONSE = 41;
    static final int MSG_ENCRYPT_FILENAME = 50;
    static final int MSG_ENCRYPT_FILENAME_RESPONSE = 51;
    static final int MSG_DECRYPT_FILENAME = 60;
    static final int MSG_DECRYPT_FILENAME_RESPONSE = 61;
    static final int MSG_ENCRYPT_FOLDERNAME = 70;
    static final int MSG_ENCRYPT_FOLDERNAME_RESPONSE = 71;
    static final int MSG_DECRYPT_FOLDERNAME = 80;
    static final int MSG_DECRYPT_FOLDERNAME_RESPONSE = 81;
    static final int MSG_NO_ACCESS_RESPONSE = -1;
    static final String PROCESS_TOKEN = "PROCESS_TOKEN";
    static final String FILENAME = "FILENAME";
    static final String FOLDERNAME = "FOLDERNAME";

    //encryption service information exchange
    static final String STATUS = "STATUS";
    static final String PROGRESS = "PROGRESS";
    static final String IS_TRUSTED = "IS_TRUSTED";
    static final String SOURCE_FILE_PATH = "SOURCE_FILE_PATH";
    static final String TARGET_FOLDER_PATH = "TARGET_FOLDER_PATH";

    //permission activity information exchange
    static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    static final String NO_ACCESS = "NO_ACCESS";
    static final int GET_PERMISSION_REQUEST = 1;
    static final int IS_TRUSTED_REQUEST = 2;

    //error codes
    static final String ERROR_CODE = "ERROR_CODE";
    static final int ERROR_INPUT = 91;
    static final int ERROR_SESSION = 92;
    static final int ERROR_PASSWORD_REQUIRED = 93;
    static final int ERROR_FILENAME = 94;
    static final int ERROR_ENCRYPTION = 95;
    static final int ERROR_DECRYPTION = 96;
    static final int ERROR_ACCESS_DENIED = 97;
    static final int ERROR_UNKNOWN = 98;

    //internal constants
    static final String APP_DATA = "APP_DATA";

}
