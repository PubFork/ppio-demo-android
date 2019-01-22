package io.pp.net_disk_demo.ppio;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.nebulas.account.KeyJSON;
import io.pp.net_disk_demo.nebulas.crypto.Crypto;
import io.pp.net_disk_demo.nebulas.crypto.cipher.Cipher;
import io.pp.net_disk_demo.nebulas.crypto.cipher.CryptoJSON;
import io.pp.net_disk_demo.nebulas.crypto.keystore.Algorithm;
import io.pp.net_disk_demo.nebulas.crypto.keystore.Key;
import io.pp.net_disk_demo.nebulas.crypto.keystore.Keystore;
import io.pp.net_disk_demo.nebulas.util.JSONUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_APPEND;

public class KeyStoreUtil {

    private static final String TAG = "KeyStoreUtil";

    private static final Algorithm encryptAlg = Algorithm.SCRYPT;
    private static final Algorithm signatureAlg = Algorithm.SECP256K1;

    public static String logInByKeyStore(String keyStoreStr, String keyStorePassPhrase) {
        try {
            KeyJSON keyJSON = JSONUtils.Parse(keyStoreStr, KeyJSON.class);
            keyJSON.getCrypto().setVersion(keyJSON.getVersion());

            Cipher cipher = new Cipher(Algorithm.SCRYPT);
            byte[] key = cipher.decrypt(keyJSON.getCrypto(), keyStorePassPhrase.getBytes());

            String privateKeyStr = bytesToHexString(key);

            String address = PpioAccountUtil.generatePpioAddressStr(privateKeyStr);

            return privateKeyStr;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static boolean checkKeyStoreAndPassPhrase(String keyStoreStr, String keyStorePassPhrase) {
        try {
            KeyJSON keyJSON = JSONUtils.Parse(keyStoreStr, KeyJSON.class);
            String keyJSONAddress = keyJSON.getAddress();
            keyJSON.getCrypto().setVersion(keyJSON.getVersion());

            Cipher cipher = new Cipher(Algorithm.SCRYPT);

            String address = PpioAccountUtil.generatePpioAddressStr(bytesToHexString(cipher.decrypt(keyJSON.getCrypto(),
                    keyStorePassPhrase.getBytes())));

            if (!TextUtils.isEmpty(keyJSONAddress) &&
                    keyJSONAddress.equals(address) &&
                    PpioAccountUtil.checkPpioAddress(address)) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static boolean checkHasRememberKeyStore(Context context, CheckHasKeyStoreListener checkHasKeyStoreListener) {
        try {
            String[] files = context.fileList();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    //
                    Log.e(TAG, "checkHasRememberKeyStore() file = " + files[i]);
                    //
                    if (Constant.PPIO_File.PRIVATE_KEYSOTR_FILE.equals(files[i])) {
                        FileInputStream fileInputStream = context.openFileInput(Constant.PPIO_File.PRIVATE_KEYSOTR_FILE);
                        byte[] keyStoreBytes = new byte[fileInputStream.available()];
                        fileInputStream.read(keyStoreBytes);
                        fileInputStream.close();

                        return JSONUtils.Parse(new String(keyStoreBytes), KeyJSON.class) != null;
                    }
                }

                Log.e(TAG, "checkHasRememberKeyStore() don't have private keystore file");

                return false;
            } else {
                if (checkHasKeyStoreListener != null) {
                    checkHasKeyStoreListener.onCheckFail("app has 0 private files");
                }

                Log.e(TAG, "checkHasRememberKeyStore() don't have private  file");

                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "checkHasRememberKeyStore() error: " + e.getMessage());

            if (checkHasKeyStoreListener != null) {
                checkHasKeyStoreListener.onCheckFail(e.getMessage());
            }

            e.printStackTrace();
            return false;
        }
    }

    //return keyStore data
    public static String autoLogInByKeyStore(Context context) {
        try {
            String[] files = context.fileList();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (Constant.PPIO_File.PRIVATE_KEYSOTR_FILE.equals(files[i])) {
                        FileInputStream fileInputStream = context.openFileInput(Constant.PPIO_File.PRIVATE_KEYSOTR_FILE);
                        byte[] keyStoreBytes = new byte[fileInputStream.available()];
                        fileInputStream.read(keyStoreBytes);
                        fileInputStream.close();

                        KeyJSON keyJSON = JSONUtils.Parse(new String(keyStoreBytes), KeyJSON.class);

                        PossUtil.setKeyStoreStr(new String(keyStoreBytes));
                        PossUtil.setAddressStr(keyJSON.getAddress());

                        return JSONUtils.Stringify(keyJSON);
                    }
                }

                return null;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteKeyStore(Context context) {
        try {
            File keyStoreFile = new File(context.getApplicationContext().getFilesDir().getPath()
                    + "/" + Constant.PPIO_File.PRIVATE_KEYSOTR_FILE);

            if (keyStoreFile.exists()) {
                return keyStoreFile.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean rememberFromKeyStore(Context context, String keyStoreStr) {
        if (context != null) {
            try {
                KeyJSON keyJSON = JSONUtils.Parse(new String(keyStoreStr), KeyJSON.class);

                FileOutputStream fileOutputStream = context.getApplicationContext().
                        openFileOutput(Constant.PPIO_File.PRIVATE_KEYSOTR_FILE, MODE_APPEND);//use append mode
                fileOutputStream.write(keyStoreStr.getBytes());
                fileOutputStream.close();

                PossUtil.setKeyStoreStr(keyStoreStr);
                PossUtil.setAddressStr(keyJSON.getAddress());

                return true;
            } catch (Exception e) {
                Log.e(TAG, "rememberKeyStore() error: " + e.getMessage());

                e.printStackTrace();

                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean rememberFromPrivateKey(Context context, String privateKey, String passPhrase) {
        if (context != null) {
            try {
                String address = PpioAccountUtil.generatePpioAddressStr(privateKey);
                Keystore keystore = new Keystore(Algorithm.SCRYPT);

                byte[] bytePrivateKey = parseHexBinary(privateKey);
                keystore.setKey(address,
                        Crypto.NewPrivateKey(signatureAlg, bytePrivateKey),
                        passPhrase.getBytes());
                Key key = keystore.getKey(address, passPhrase.getBytes());
                Cipher cipher = new Cipher(encryptAlg);
                CryptoJSON cryptoJSON = cipher.encrypt(key.encode(), passPhrase.getBytes());

                KeyJSON keyJSON = new KeyJSON(address, cryptoJSON);

                FileOutputStream fileOutputStream = context.getApplicationContext().
                        openFileOutput(Constant.PPIO_File.PRIVATE_KEYSOTR_FILE, MODE_APPEND);//use append mode
                fileOutputStream.write(JSONUtils.Stringify(keyJSON).getBytes());
                fileOutputStream.close();

                //
                PossUtil.setKeyStoreStr(JSONUtils.Stringify(keyJSON));
                PossUtil.setPasswordStr(passPhrase);
                PossUtil.setPrivateKeyStr(privateKey);
                PossUtil.setAddressStr(keyJSON.getAddress());
                //

                return true;
            } catch (Exception e) {
                Log.e(TAG, "rememberKeyStore() error: " + e.getMessage());

                e.printStackTrace();

                return false;
            }
        } else {
            return false;
        }
    }

    public static String exportKeyStoreStr(Context context, String passPhrase, String newPassPhrase) {
        try {
            String[] files = context.fileList();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (Constant.PPIO_File.PRIVATE_KEYSOTR_FILE.equals(files[i])) {
                        Cipher cipher = new Cipher(Algorithm.SCRYPT);

                        FileInputStream fileInputStream = context.openFileInput(Constant.PPIO_File.PRIVATE_KEYSOTR_FILE);
                        byte[] keyStoreBytes = new byte[fileInputStream.available()];
                        fileInputStream.read(keyStoreBytes);
                        fileInputStream.close();

                        KeyJSON keyJSON = JSONUtils.Parse(new String(keyStoreBytes), KeyJSON.class);

                        //get the private key
                        String privateKey = bytesToHexString(cipher.decrypt(keyJSON.getCrypto(),
                                passPhrase.getBytes()));
                        //get the address
                        String address = PpioAccountUtil.generatePpioAddressStr(privateKey);

                        //export the new keystore
                        Keystore newKeystore = new Keystore(Algorithm.SCRYPT);
                        byte[] bytePrivateKey = parseHexBinary(privateKey);
                        newKeystore.setKey(address,
                                Crypto.NewPrivateKey(signatureAlg, bytePrivateKey),
                                newPassPhrase.getBytes());
                        Key key = newKeystore.getKey(address, newPassPhrase.getBytes());

                        CryptoJSON cryptoJSON = cipher.encrypt(key.encode(), newPassPhrase.getBytes());

                        return JSONUtils.Stringify(new KeyJSON(address, cryptoJSON));
                    }
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String exportKeyStoreFile(Context context, String passPhrase, String newPassPhrase) {
        try {
            String[] files = context.fileList();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (Constant.PPIO_File.PRIVATE_KEYSOTR_FILE.equals(files[i])) {
                        FileInputStream fileInputStream = context.openFileInput(Constant.PPIO_File.PRIVATE_KEYSOTR_FILE);
                        byte[] keyStoreBytes = new byte[fileInputStream.available()];
                        fileInputStream.read(keyStoreBytes);
                        fileInputStream.close();

                        KeyJSON keyJSON = JSONUtils.Parse(new String(keyStoreBytes), KeyJSON.class);
                        Cipher cipher = new Cipher(Algorithm.SCRYPT);
                        String privateKey = bytesToHexString(cipher.decrypt(keyJSON.getCrypto(),
                                passPhrase.getBytes()));
                        String address = PpioAccountUtil.generatePpioAddressStr(privateKey);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("'PPIO-UTC-'yyyy_MM_dd'T'HH:mm:ss.SSS'-'");
                        final String keyStoreFilePath = Environment.getExternalStorageDirectory().getPath() + "/" +
                                dateFormat.format(new Date()) + address + ".json";

                        return generateKeyStore(privateKey, newPassPhrase, keyStoreFilePath);
                    }
                }
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "exportKeyStoreFile() error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public static String generateKeyStore(String privateKey, String passPhrase,
                                          final String keyStoreFilePath) {
        try {
            String address = PpioAccountUtil.generatePpioAddressStr(privateKey);

            Keystore keystore = new Keystore(Algorithm.SCRYPT);

            byte[] bytePrivateKey = parseHexBinary(privateKey);
            keystore.setKey(address,
                    Crypto.NewPrivateKey(signatureAlg, bytePrivateKey),
                    passPhrase.getBytes());
            Key key = keystore.getKey(address, passPhrase.getBytes());
            Cipher cipher = new Cipher(encryptAlg);
            CryptoJSON cryptoJSON = cipher.encrypt(key.encode(), passPhrase.getBytes());

            KeyJSON keyJSON = new KeyJSON(address, cryptoJSON);

            new ObjectMapper().writeValue(new File(keyStoreFilePath), keyJSON);

            return keyStoreFilePath;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String bytesToHexString(byte[] bytesArr) {
        if (bytesArr != null) {
            StringBuilder stringBuilder = new StringBuilder(bytesArr.length);
            String stringTemp;

            for (int i = 0; i < bytesArr.length; i++) {
                stringTemp = Integer.toHexString(0xFF & bytesArr[i]);
                if (stringTemp.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(stringTemp.toUpperCase());
            }

            return stringBuilder.toString();
        } else {
            return "";
        }
    }


    public static byte[] parseHexBinary(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        } else {
            byte[] out = new byte[len / 2];

            for (int i = 0; i < len; i += 2) {
                int h = hexToBin(s.charAt(i));
                int l = hexToBin(s.charAt(i + 1));
                if (h == -1 || l == -1) {
                    throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
                }

                out[i / 2] = (byte) (h * 16 + l);
            }

            return out;
        }
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - 48;
        } else if ('A' <= ch && ch <= 'F') {
            return ch - 65 + 10;
        } else {
            return 'a' <= ch && ch <= 'f' ? ch - 97 + 10 : -1;
        }
    }

    /**
     * Determine if the string contains only alphanumeric characters and Chinese characters
     * The range of unicode encoding for various characters:
     * Chinese character：[0x4e00,0x9fa5]（or decimal[19968,40869]）
     * digital：[0x30,0x39]（or decimal[48, 57]）
     * Lower case letters：[0x61,0x7a]（or decimal[97, 122]）
     * uppercase letter：[0x41,0x5a]（or decimal[65, 90]）
     */
    public static boolean isLetterDigitOrChinese(String str) {
        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    private static boolean isHexStr(String str) {
        String regex = "^[A-Fa-f0-9]+$";
        return str.matches(regex);
    }

    public interface CheckHasKeyStoreListener {
        void onCheckFail(String errMsg);
    }
}