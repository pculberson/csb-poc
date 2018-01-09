/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.common.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.dat.sync.SyncEvent;

public class SyncEventDeserializer implements Deserializer<SyncEvent>
{
    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey)
    {}

    @Override
    public void close()
    {}

    @Override
    public SyncEvent deserialize(final String topic, final byte[] data)
    {
        return new SyncEvent(data, false);
    }
}
