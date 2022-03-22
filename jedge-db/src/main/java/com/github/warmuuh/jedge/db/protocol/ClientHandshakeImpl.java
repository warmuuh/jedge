package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;


public class ClientHandshakeImpl extends ClientHandshake {
    @Delegate
    private final StringReader stringReader = new StringReader();

    public static ClientHandshakeImpl of(String username, String database) {
        ClientHandshakeImpl handshake = new ClientHandshakeImpl();
        handshake.major_ver = 0;
        handshake.minor_ver = 13;
        CONNECTIONPARAM user = new CONNECTIONPARAM(handshake);
        user.name = new JBBPFieldString(null, "user");
        user.value = new JBBPFieldString(null, username);
        CONNECTIONPARAM db = new CONNECTIONPARAM(handshake);
        db.name = new JBBPFieldString(null, "database");
        db.value = new JBBPFieldString(null, database);
        handshake.num_extensions = 0;
        handshake.num_params = 2;
        handshake.connectionparam = new CONNECTIONPARAM[]{
            user, db
        };
        return handshake;
    }
}
