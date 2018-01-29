package com.dat.poc;
/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dat.common.serialization.SyncEventSerde;
import com.dat.domain.SimpleAsset;
import com.dat.sync.SyncEvent;
import com.dat.sync.SyncEvent.Action;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

/* Creates the topic for Kafka
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic tfsprd.syncAssetInsert
*/

@SuppressWarnings("unused")
public class Siphon
{
    private static final String APP_NAME = "Siphon";

    private static final String FROM_TOPIC = "tfsprd.syncAsset_nx_j";
    private static final String TO_TOPIC = "tfsprd.syncAssetInsert";

    private static final Logger log = LoggerFactory.getLogger(Siphon.class);

    private static void printit(final SyncEvent e)
    {
        final Any json = JsonIterator.deserialize(e.getPayload());

        System.out.printf("\n%s\n  encoding=%s\n  classname=%s\n  payload=%s\n", e.getFmeId(),
            e.getProperty("__encoding"), e.getProperty("__payloadClassname"), e.getPayload());
    }

    private static void printSimpleAsset(final SyncEvent e)
    {
        final SimpleAsset asset = new SimpleAsset(e);
        System.out.println(asset.toJson());
    }

    public static void main(final String[] args)
    {
        log.info("Starting up");

        final StreamsBuilder builder = new StreamsBuilder();

        log.info("Creating syncEvents stream");
        final KStream<String, SyncEvent> syncEvents = builder.stream(FROM_TOPIC,
            Consumed.with(Serdes.String(), new SyncEventSerde()));

        // Only pass along every 100th FME with an action of 'insert'
//        syncEvents
//            .filter(new SyncEventPredicate(Action.insert, 100))
//            .map((key, event) -> KeyValue.pair(event.getFmeId(), event))
//            .to(TO_TOPIC, Produced.valueSerde(new SyncEventSerde()));

        syncEvents
            .filter((k, v) -> v.getAction() == Action.insert)
            .foreach((k, v) -> printSimpleAsset(v));

        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "pdxcsbdev02:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);

        streams.cleanUp();

        final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler to catch control-c or interrupt.
        // Note that this isn't really useful when running from Eclipse.
        Runtime.getRuntime().addShutdownHook(new Thread("siphon-shutdown-hook")
        {
            @Override
            public void run()
            {
                log.warn("Shutting down");
                streams.close();
                latch.countDown();
            }
        });

        try
        {
            log.info("Starting stream processor");
            streams.start();
            latch.await();
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }
}
