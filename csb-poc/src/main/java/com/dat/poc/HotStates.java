/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.poc;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dat.common.serialization.SyncEventSerde;
import com.dat.sync.SyncEvent;
import com.dat.sync.SyncEvent.Action;
import com.dat.util.Base56;

public class HotStates
{
    private static final Logger log = LoggerFactory.getLogger(HotStates.class);

    public static void main(final String[] args)
    {
        log.info("Starting up");

        final Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "HotStates");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "pdxcsbdev02:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        final SyncEventSerde saSerde = new SyncEventSerde();

        final KStream<String, SyncEvent> syncEvents = builder.stream("tfsprd.SyncEvent_nx_j",
            Consumed.with(Serdes.String(), saSerde));

        // Only pass along every 100th event with an action of insert
        syncEvents
            .filter((k, e) -> e.getAction() == Action.insert && ((Base56.toDecimalValue(e.getFmeId()) % 100) == 0))
            .map((key, event) -> KeyValue.pair(event.getFmeId(), event))
            .to("tfsprd.SyncEventInsert", Produced.with(Serdes.String(), saSerde));

        final Topology t = new Topology();
        new KafkaStreams(t, config);
    }
}
