package com.github.warmuuh.jedge;

public interface TypeDeserializer<T> {
    T deserialize(String typeId, byte[] data) throws DeserializationException;
}
