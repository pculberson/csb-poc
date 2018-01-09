/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.common.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.dat.sync.SyncEvent;

public class SyncEventSerializer implements Serializer<SyncEvent>
{
    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey)
    {}

    @Override
    public void close()
    {}

    @Override
    public byte[] serialize(final String topic, final SyncEvent data)
    {
        return data.toString().getBytes();
    }
}
