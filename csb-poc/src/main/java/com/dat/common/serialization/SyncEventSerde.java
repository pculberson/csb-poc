/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.common.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.dat.sync.SyncEvent;

public class SyncEventSerde implements Serde<SyncEvent>
{
    final private Serializer<SyncEvent> m_serializer = new SyncEventSerializer();
    final private Deserializer<SyncEvent> m_deserializer = new SyncEventDeserializer();

    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey)
    {}

    @Override
    public void close()
    {}

    @Override
    public Serializer<SyncEvent> serializer()
    {
        return m_serializer;
    }

    @Override
    public Deserializer<SyncEvent> deserializer()
    {
        return m_deserializer;
    }
}
