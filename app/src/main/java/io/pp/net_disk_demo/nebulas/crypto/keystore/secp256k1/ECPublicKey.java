package io.pp.net_disk_demo.nebulas.crypto.keystore.secp256k1;
//
import io.pp.net_disk_demo.nebulas.crypto.keystore.Algorithm;
import io.pp.net_disk_demo.nebulas.crypto.keystore.PublicKey;
import io.pp.net_disk_demo.nebulas.crypto.util.Utils;


public class ECPublicKey implements PublicKey {
    byte[] pubKey;

    public ECPublicKey(byte[] pub) {
        this.pubKey = pub;
    }

    @Override
    public Algorithm algorithm() {
        return Algorithm.SECP256K1;
    }

    @Override
    public byte[] encode() throws Exception {
        return this.pubKey;
    }

    @Override
    public void decode(byte[] data) throws Exception {
        this.pubKey = data;
    }

    @Override
    public void clear() {
        Utils.ClearBytes(this.pubKey);
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) throws Exception {
        return Secp256k1.Verify(data, signature, pubKey);
    }
}
