package io.pp.net_disk_demo.nebulas.crypto;

import io.pp.net_disk_demo.nebulas.crypto.keystore.Algorithm;
import io.pp.net_disk_demo.nebulas.crypto.keystore.PrivateKey;
import io.pp.net_disk_demo.nebulas.crypto.keystore.Signature;
import io.pp.net_disk_demo.nebulas.crypto.keystore.secp256k1.ECPrivateKey;
import io.pp.net_disk_demo.nebulas.crypto.keystore.secp256k1.ECSignature;

public class Crypto {

    public static PrivateKey NewPrivateKey(Algorithm algorithm, byte[] data) throws Exception{
        PrivateKey privateKey;
        switch (algorithm) {
            case SECP256K1:
            {
                if (data != null && data.length != 0) {
                    privateKey = new ECPrivateKey(data);
                } else {
                    privateKey = ECPrivateKey.GeneratePrivateKey();
                }
                break;
            }
            default:
                throw new Exception("invalid algorithm");
        }
        return privateKey;
    }

    public static Signature NewSignature(Algorithm algorithm) throws Exception {
        Signature signature;
        switch (algorithm) {
            case SECP256K1:
            {
                signature = new ECSignature();
                break;
            }
            default:
                throw new Exception("invalid algorithm");
        }
        return signature;
    }
}
