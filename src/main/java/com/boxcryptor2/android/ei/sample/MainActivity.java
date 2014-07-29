package com.boxcryptor2.android.ei.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

public class MainActivity extends Activity {

    //service connection fields
    private final Messenger incomingMessenger = new Messenger(new IncomingHandler());
    private Messenger outgoingMessenger = null;
    private boolean hasServiceConnection = false;
    private String accessToken;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            hasServiceConnection = true;
            outgoingMessenger = new Messenger(binder);

            registerAppWithService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            hasServiceConnection = false;
            outgoingMessenger = null;
            accessToken = Constants.NO_ACCESS;
        }
    };

    //ui
    private EditText sourceFilePathEditText;
    private EditText targetFolderPathEditText;
    private EditText decryptedFilenameEditText;
    private EditText encryptedFilenameEditText;
    private EditText decryptedFoldernameEditText;
    private EditText encryptedFoldernameEditText;
    private Button encryptButton;
    private Button encryptAbortButton;
    private Button decryptButton;
    private Button decryptAbortButton;
    private Button encryptFilenameButton;
    private Button decryptFilenameButton;
    private Button encryptFoldernameButton;
    private Button decryptFoldernameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        connectToService();

        sourceFilePathEditText = (EditText) findViewById(R.id.source_file_path_edittext);
        sourceFilePathEditText.setEnabled(false);

        targetFolderPathEditText = (EditText) findViewById(R.id.target_folder_path_edittext);
        targetFolderPathEditText.setEnabled(false);

        decryptedFilenameEditText = (EditText) findViewById(R.id.decrypted_filename_edittext);
        decryptedFilenameEditText.setEnabled(false);

        encryptedFilenameEditText = (EditText) findViewById(R.id.encrypted_filename_edittext);
        encryptedFilenameEditText.setEnabled(false);

        decryptedFoldernameEditText = (EditText) findViewById(R.id.decrypted_foldername_edittext);
        decryptedFoldernameEditText.setEnabled(false);

        encryptedFoldernameEditText = (EditText) findViewById(R.id.encrypted_foldername_edittext);
        encryptedFoldernameEditText.setEnabled(false);

        encryptButton = (Button) findViewById(R.id.encrypt_button);
        encryptButton.setEnabled(false);
        encryptButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                encryptFile(sourceFilePathEditText.getText().toString(), targetFolderPathEditText.getText().toString());
            }
        });

        decryptButton = (Button) findViewById(R.id.decrypt_button);
        decryptButton.setEnabled(false);
        decryptButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                decryptFile(sourceFilePathEditText.getText().toString(), targetFolderPathEditText.getText().toString());
            }
        });

        encryptAbortButton = (Button) findViewById(R.id.encrypt_abort_button);
        encryptAbortButton.setEnabled(false);
        encryptAbortButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                abortFileEncryption();
            }
        });

        decryptAbortButton = (Button) findViewById(R.id.decrypt_abort_button);
        decryptAbortButton.setEnabled(false);
        decryptAbortButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                abortFileDecryption();
            }
        });

        encryptFilenameButton = (Button) findViewById(R.id.encrypt_filename_button);
        encryptFilenameButton.setEnabled(false);
        encryptFilenameButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                encryptFileName(decryptedFilenameEditText.getText().toString());
            }
        });

        decryptFilenameButton = (Button) findViewById(R.id.decrypt_filename_button);
        decryptFilenameButton.setEnabled(false);
        decryptFilenameButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                decryptFileName(encryptedFilenameEditText.getText().toString());
            }
        });

        encryptFoldernameButton = (Button) findViewById(R.id.encrypt_foldername_button);
        encryptFoldernameButton.setEnabled(false);
        encryptFoldernameButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                encryptFolderName(decryptedFoldernameEditText.getText().toString());
            }
        });

        decryptFoldernameButton = (Button) findViewById(R.id.decrypt_foldername_button);
        decryptFoldernameButton.setEnabled(false);
        decryptFoldernameButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                decryptFolderName(encryptedFoldernameEditText.getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindFromService();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IS_TRUSTED_REQUEST && resultCode == RESULT_OK) {
            accessToken = loadAccessToken();
            if (data.getBooleanExtra(Constants.IS_TRUSTED, false) && !Constants.NO_ACCESS.equals(accessToken)) {
                bindToService();
            }
            else {
                getPermission();
            }
        }
        else if (requestCode == Constants.GET_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            accessToken = data.getStringExtra(Constants.ACCESS_TOKEN);
            saveAccessToken(accessToken);
            if (!Constants.NO_ACCESS.equals(accessToken)) {
                bindToService();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message incomingMessage) {
            Bundle dataBundle = incomingMessage.getData();
            switch (incomingMessage.what) {
                case Constants.MSG_REGISTER_WITH_SERVICE_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "registration successful");
                        sourceFilePathEditText.setEnabled(true);
                        targetFolderPathEditText.setEnabled(true);
                        decryptedFilenameEditText.setEnabled(true);
                        encryptedFilenameEditText.setEnabled(true);
                        decryptedFoldernameEditText.setEnabled(true);
                        encryptedFoldernameEditText.setEnabled(true);
                        encryptButton.setEnabled(true);
                        encryptAbortButton.setEnabled(true);
                        decryptButton.setEnabled(true);
                        decryptAbortButton.setEnabled(true);
                        encryptFilenameButton.setEnabled(true);
                        decryptFilenameButton.setEnabled(true);
                        encryptFoldernameButton.setEnabled(true);
                        decryptFoldernameButton.setEnabled(true);
                    }
                    break;
                case Constants.MSG_ENCRYPT_FILE_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "encryption successful");
                    }
                    else {
                        Log.i(this.getClass().getName(), "encryption not successful: " + dataBundle.getInt(Constants.ERROR_CODE));
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_ENCRYPT_FILE_PROGRESS:
                    Log.i(this.getClass().getName(), "encryption process: " + dataBundle.getString(Constants.PROCESS_TOKEN) + " done: " + dataBundle.getLong(Constants.PROGRESS));
                    break;
                case Constants.MSG_ABORT_FILE_ENCRYPTION_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "successfully aborted encryption");
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_DECRYPT_FILE_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "decryption successful");
                    }
                    else {
                        Log.i(this.getClass().getName(), "decryption not successful: " + dataBundle.getInt(Constants.ERROR_CODE));
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_DECRYPT_FILE_PROGRESS:
                    Log.i(this.getClass().getName(), "decryption process: " + dataBundle.getString(Constants.PROCESS_TOKEN) + " done: " + dataBundle.getLong(Constants.PROGRESS));
                    break;
                case Constants.MSG_ABORT_FILE_DECRYPTION_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "successfully aborted decryption");
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_ENCRYPT_FILENAME_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "filename encryption successful");
                        encryptedFilenameEditText.setText(dataBundle.getString(Constants.FILENAME));
                    }
                    else {
                        Log.i(this.getClass().getName(), "filename encryption not successful: " + dataBundle.getInt(Constants.ERROR_CODE));
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_DECRYPT_FILENAME_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "filename decryption successful");
                        decryptedFilenameEditText.setText(dataBundle.getString(Constants.FILENAME));
                    }
                    else {
                        Log.i(this.getClass().getName(), "filename decryption not successful: " + dataBundle.getInt(Constants.ERROR_CODE));
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_ENCRYPT_FOLDERNAME_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "foldername encryption successful");
                        encryptedFoldernameEditText.setText(dataBundle.getString(Constants.FOLDERNAME));
                    }
                    else {
                        Log.i(this.getClass().getName(), "foldername encryption not successful: " + dataBundle.getInt(Constants.ERROR_CODE));
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_DECRYPT_FOLDERNAME_RESPONSE:
                    if (dataBundle.getBoolean(Constants.STATUS)) {
                        Log.i(this.getClass().getName(), "foldername decryption successful");
                        decryptedFoldernameEditText.setText(dataBundle.getString(Constants.FOLDERNAME));
                    }
                    else {
                        Log.i(this.getClass().getName(), "foldername decryption not successful: " + dataBundle.getInt(Constants.ERROR_CODE));
                    }
                    removeCurrentProcessToken();
                    break;
                case Constants.MSG_NO_ACCESS_RESPONSE:
                    Log.i(this.getClass().getName(), "no access");
                    removeCurrentProcessToken();
                    break;
                default:
                    super.handleMessage(incomingMessage);
            }
        }
    }

    private void connectToService() {
        try {
            Intent intent = new Intent();
            intent.setAction(Constants.START_PERMISSION_ACTIVITY);
            intent.putExtra(Constants.IS_TRUSTED, Constants.IS_TRUSTED_REQUEST);

            startActivityForResult(intent, Constants.IS_TRUSTED_REQUEST);
        }
        catch (Exception e) {
            //is Boxcryptor installed?
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
    }

    private void getPermission() {
        try {
            Intent intent = new Intent();
            intent.setAction(Constants.START_PERMISSION_ACTIVITY);

            startActivityForResult(intent, Constants.GET_PERMISSION_REQUEST);
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
    }

    private void bindToService() {
        if (!hasServiceConnection) {
            Intent intent = new Intent(Constants.START_ENCRYPTION_SERVICE);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void registerAppWithService() {
        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);

        sendMessageToService(dataBundle, Constants.MSG_REGISTER_WITH_SERVICE);
    }

    private void sendMessageToService(Bundle data, int message) {
        if (hasServiceConnection && !Constants.NO_ACCESS.equals(accessToken)) {
            try {
                Message outgoingMessage = Message.obtain(null, message);
                outgoingMessage.setData(data);
                outgoingMessage.replyTo = incomingMessenger;

                outgoingMessenger.send(outgoingMessage);
            }
            catch (RemoteException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    private void encryptFile(String sourcePath, String targetPath) {
        if (loadCurrentProcessToken() != null) {
            return;
        }

        String processToken = UUID.randomUUID().toString();

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, processToken);
        dataBundle.putString(Constants.SOURCE_FILE_PATH, sourcePath);
        dataBundle.putString(Constants.TARGET_FOLDER_PATH, targetPath);

        saveCurrentProcessToken(processToken);

        sendMessageToService(dataBundle, Constants.MSG_ENCRYPT_FILE);
    }

    private void decryptFile(String sourcePath, String targetPath) {
        if (loadCurrentProcessToken() != null) {
            return;
        }

        String processToken = UUID.randomUUID().toString();

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, processToken);
        dataBundle.putString(Constants.SOURCE_FILE_PATH, sourcePath);
        dataBundle.putString(Constants.TARGET_FOLDER_PATH, targetPath);

        saveCurrentProcessToken(processToken);

        sendMessageToService(dataBundle, Constants.MSG_DECRYPT_FILE);
    }

    private void abortFileEncryption() {
        String currentProcessToken = loadCurrentProcessToken();

        if (currentProcessToken == null) {
            return;
        }

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, currentProcessToken);

        sendMessageToService(dataBundle, Constants.MSG_ABORT_FILE_ENCRYPTION);
    }

    private void abortFileDecryption() {
        String currentProcessToken = loadCurrentProcessToken();

        if (currentProcessToken == null) {
            return;
        }

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, currentProcessToken);

        sendMessageToService(dataBundle, Constants.MSG_ABORT_FILE_DECRYPTION);
    }

    private void encryptFileName(String filename) {
        if (loadCurrentProcessToken() != null) {
            return;
        }

        String processToken = UUID.randomUUID().toString();

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, processToken);
        dataBundle.putString(Constants.FILENAME, filename);

        saveCurrentProcessToken(processToken);

        sendMessageToService(dataBundle, Constants.MSG_ENCRYPT_FILENAME);
    }

    private void decryptFileName(String filename) {
        if (loadCurrentProcessToken() != null) {
            return;
        }

        String processToken = UUID.randomUUID().toString();

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, processToken);
        dataBundle.putString(Constants.FILENAME, filename);

        saveCurrentProcessToken(processToken);

        sendMessageToService(dataBundle, Constants.MSG_DECRYPT_FILENAME);
    }

    private void encryptFolderName(String foldername) {
        if (loadCurrentProcessToken() != null) {
            return;
        }

        String processToken = UUID.randomUUID().toString();

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, processToken);
        dataBundle.putString(Constants.FOLDERNAME, foldername);

        saveCurrentProcessToken(processToken);

        sendMessageToService(dataBundle, Constants.MSG_ENCRYPT_FOLDERNAME);
    }

    private void decryptFolderName(String foldername) {
        if (loadCurrentProcessToken() != null) {
            return;
        }

        String processToken = UUID.randomUUID().toString();

        Bundle dataBundle = new Bundle();
        dataBundle.putString(Constants.ACCESS_TOKEN, accessToken);
        dataBundle.putString(Constants.PROCESS_TOKEN, processToken);
        dataBundle.putString(Constants.FOLDERNAME, foldername);

        saveCurrentProcessToken(processToken);

        sendMessageToService(dataBundle, Constants.MSG_DECRYPT_FOLDERNAME);
    }

    private void unbindFromService() {
        if (hasServiceConnection) {
            unbindService(serviceConnection);
            hasServiceConnection = false;
            outgoingMessenger = null;
            serviceConnection = null;
        }
    }

    private void saveAccessToken(String accessToken) {
        SharedPreferences appData = getSharedPreferences(Constants.APP_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = appData.edit();

        editor.putString(Constants.ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    private String loadAccessToken() {
        SharedPreferences appData = getSharedPreferences(Constants.APP_DATA, MODE_PRIVATE);
        if (appData != null) {
            return appData.getString(Constants.ACCESS_TOKEN, Constants.NO_ACCESS);
        }
        return Constants.NO_ACCESS;
    }

    private void saveCurrentProcessToken(String processToken) {
        SharedPreferences appData = getSharedPreferences(Constants.APP_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = appData.edit();

        editor.putString(Constants.PROCESS_TOKEN, processToken);
        editor.commit();
    }

    private void removeCurrentProcessToken() {
        SharedPreferences appData = getSharedPreferences(Constants.APP_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = appData.edit();

        editor.remove(Constants.PROCESS_TOKEN);
        editor.commit();
    }

    private String loadCurrentProcessToken() {
        SharedPreferences appData = getSharedPreferences(Constants.APP_DATA, 0);
        if (appData != null) {
            return appData.getString(Constants.PROCESS_TOKEN, null);
        }
        return null;
    }

}
