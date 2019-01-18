package io.pp.net_disk_demo.nebulas.crypto.keystore.secp256k1;

import io.pp.net_disk_demo.nebulas.crypto.keystore.Algorithm;
import io.pp.net_disk_demo.nebulas.crypto.keystore.PrivateKey;
import io.pp.net_disk_demo.nebulas.crypto.keystore.PublicKey;
import io.pp.net_disk_demo.nebulas.crypto.util.Utils;


public class ECPrivateKey implements PrivateKey {

    byte[] seckey;

    public static ECPrivateKey GeneratePrivateKey() {
     byte[] seckey = Secp256k1.GenerateECKey();
     return new ECPrivateKey(seckey);
    }

    public ECPrivateKey(byte[] data) {
        this.seckey = data;
    }

    @Override
    public Algorithm algorithm() {
        return Algorithm.SECP256K1;
    }

    @Override
    public byte[] encode() throws Exception {
        return this.seckey;
    }

    @Override
    public void decode(byte[] data) throws Exception {
        this.seckey = data;
    }

    @Override
    public void clear() {
        Utils.ClearBytes(this.seckey);
    }

    @Override
    public PublicKey publickey() {
        byte[] pub = Secp256k1.PublicFromPrivateKey(seckey);
        return new ECPublicKey(pub);
    }

    @Override
    public byte[] sign(byte[] data) throws Exception {
        return Secp256k1.Sign(data, seckey);
    }
}
