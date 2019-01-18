package io.pp.net_disk_demo.nebulas.crypto.keystore;

public interface Key {

    Algorithm algorithm();

    byte[] encode() throws Exception;

    void decode(byte[] data) throws Exception;

    void clear();

}
