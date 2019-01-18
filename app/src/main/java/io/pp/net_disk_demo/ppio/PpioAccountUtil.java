package io.pp.net_disk_demo.ppio;

import android.text.TextUtils;

import org.web3j.crypto.Credentials;

import java.security.SecureRandom;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;
import io.pp.net_disk_demo.nebulas.crypto.hash.Hash;
import io.pp.net_disk_demo.nebulas.util.Base58;
import io.pp.net_disk_demo.nebulas.util.ByteUtils;

public class PpioAccountUtil {

    private static final String TAG = "PpioAccountUtil";

    public static String generateMnemonics() {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, stringBuilder::append);

        return stringBuilder.toString();//mnemonic words
    }

    public static String generate64PrivateKeyStr(String mnemonics, String passphrase) {
        byte[] originalKey = new SeedCalculator().calculateSeed(mnemonics, passphrase);
        String privateKeyStr = bytesToHexString(originalKey);

        //64 byte private key
        StringBuilder shortPrivateKeyBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(privateKeyStr)) {
            for (int i = 0; i < privateKeyStr.length(); i++) {
                if (i % 2 == 1) {
                    shortPrivateKeyBuilder = shortPrivateKeyBuilder.append(privateKeyStr.charAt(i));
                }
            }
        }

        return shortPrivateKeyBuilder.toString();
    }

    public static String generatePpioAddressStr(String shortPrivateKeyStr) {
        String addressStr = "";

        if (TextUtils.isEmpty(shortPrivateKeyStr)) {
            return addressStr;
        }

        try {
            //Ethereum address
            Credentials credentials = Credentials.create(shortPrivateKeyStr);
            String originalAddressStr = credentials.getAddress();

            //Ethereum public key
            byte[] publicKey = credentials.getEcKeyPair().getPublicKey().toByteArray();
            String publicKeyStr = bytesToHexString(publicKey);

            /*
             * Ethereum public key should transform
             * if public key is 64byte, add "04", if is 65 byte, change the header with "04"
             **/
            if (publicKeyStr.length() == 130) {
                publicKeyStr = "04" + publicKeyStr.substring(2);
            } else if (publicKeyStr.length() == 128) {
                publicKeyStr = "04" + publicKeyStr;
            } else {
                publicKeyStr = "";
            }

            publicKey = toBytes(publicKeyStr);

            //ppio address
            addressStr = generatePpioAddress(publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addressStr;
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

    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static String generatePpioAddress(byte[]... args) throws Exception {
        if (args != null) {
            byte[] addressBytes = new byte[28];

            byte[] headerBytes = toBytes("502A8D78");

            byte[] sha = Hash.Sha3256(args);
            byte[] content = Hash.Ripemd160(sha);
            System.arraycopy(headerBytes, 0, addressBytes, 0, 4);
            System.arraycopy(content, 0, addressBytes, 4, 20);

            byte[] checkData = ByteUtils.SubBytes(addressBytes, 0, 24);
            byte[] checkSum = checkSum(checkData);
            System.arraycopy(checkSum, 0, addressBytes, 24, 4);

            return Base58.encode(addressBytes);
        }

        return "";
    }

    private static byte[] checkSum(byte[] data) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }

        byte[] checkData = Hash.Sha3256(data);
        return ByteUtils.SubBytes(checkData, 0, 4);
    }

}