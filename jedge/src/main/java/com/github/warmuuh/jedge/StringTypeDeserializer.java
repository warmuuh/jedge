package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.types.ScalarType;

public class StringTypeDeserializer implements TypeDeserializer<String> {
    @Override
    public String deserialize(String typeId, byte[] data) {
        if (ScalarType.STR != ScalarType.fromTypeId(typeId).orElse(null)) {
            throw new UnsupportedOperationException("Type " + typeId + " not supported for deserialization");
        }
        return new String(data);
    }
}
