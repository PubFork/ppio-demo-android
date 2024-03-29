package io.pp.net_disk_demo.nebulas.crypto.keystore;

public interface PrivateKey extends Key {

    Algorithm algorithm();

    byte[] encode() throws Exception;

    void decode(byte[] data) throws Exception;

    void clear();

    PublicKey publickey();

    byte[] sign(byte[] data) throws Exception;
}
