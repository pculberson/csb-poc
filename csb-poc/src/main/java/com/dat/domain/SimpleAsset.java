/*
 * $Id: $
 *
 * Copyright (C) 2018, TransCore LP. All Rights Reserved
 */
package com.dat.domain;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.dat.sync.SyncEvent;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsoniterSpi;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleAsset
{
    private String fmeId;
    private String sourceApplication;
    private int userId;
    private int officeId;
    private int companyId;
    private int groupId;
    private String whenIssued;
    private String postingType;
    private String equipmentType;
    private GeoLocation origin;
    private GeoLocation destination;
    private String startDate;
    private String endDate;
    private String earliestAvailability;
    private String latestAvailability;

    public SimpleAsset(final SyncEvent syncEvent)
    {
        fmeId = syncEvent.getFmeId();
        sourceApplication = syncEvent.getSourceApplication();
        userId = syncEvent.getOwnerId();
        officeId = syncEvent.getOfficeId();
        companyId = syncEvent.getCompanyId();
        groupId = syncEvent.getGroupId();

        final String payloadClassname = syncEvent.getProperty("__payloadClassname");

        if (StringUtils.isAllBlank(payloadClassname))
        {
            throw new IllegalArgumentException("No classname found");
        }

        final Any json = JsonIterator.deserialize(syncEvent.getPayload());

        final Any fme;
        if (payloadClassname.endsWith("FmCoreSyncNotificationDocument"))
        {
            fme = json.get("fmCoreSyncNotification", "insert", "fme");
        }
        else
        {
            fme = json.get("insert", "fme");
        }

        whenIssued = fme.get("whenIssued").toString();

        final Any defn = fme.get("posting", "defn");

        final Any basic = defn.get("basic");
        postingType = basic.get("postingType_schemaVal").toString();
        equipmentType = basic.get("equipmentType").toString();

        origin = GeoLocationHelper.parse(basic.get("origin"));
        destination = GeoLocationHelper.parse(basic.get("destination"));

        final Any exposure = defn.get("exposure");
        startDate = exposure.get("startDate").toString();
        endDate = exposure.get("endDate").toString();

        earliestAvailability = exposure.get("availability", "earliest").toString();
        latestAvailability = exposure.get("availability", "latest").toString();
    }

    public SimpleAsset(final String json)
    {
        final JsonIterator iter = JsonIterator.parse(json);

        try
        {
            for (String field = iter.readObject(); field != null; field = iter.readObject()) {
                switch (field) {
                    case "fmeId":
                        fmeId = iter.readString();
                        continue;
                    case "userId":
                        userId = iter.readInt();
                        continue;
                    case "officeId":
                        officeId = iter.readInt();
                        continue;
                    case "companyId":
                        companyId = iter.readInt();
                        continue;
                    case "groupId":
                        groupId = iter.readInt();
                        continue;
                    case "sourceApplication":
                        sourceApplication = iter.readString();
                        continue;
                    case "whenIssued":
                        whenIssued = iter.readString();
                        continue;
                    case "equipmentType":
                        equipmentType = iter.readString();
                        continue;
                    case "startDate":
                        startDate = iter.readString();
                        continue;
                    case "endDate":
                        endDate = iter.readString();
                        continue;
                    case "earliestAvailability":
                        earliestAvailability = iter.readString();
                        continue;
                    case "latestAvailability":
                        latestAvailability = iter.readString();
                        continue;
                    case "postingType":
                        postingType = iter.readString();
                        continue;
                    case "origin":
                        origin = GeoLocationHelper.parse(iter.readAny());
                        continue;
                    case "destination":
                        destination = GeoLocationHelper.parse(iter.readAny());
                        continue;
                    default:
                        System.out.printf("Unused field: '%s'\n", field);
                        iter.skip();
                }
            }
        }
        catch (final IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final Config PRETTY_PRINT = new Config.Builder().indentionStep(2).omitDefaultValue(true).build();
    private static final Config DEFAULT_PRINT = JsoniterSpi.getDefaultConfig();

    public String toJson(final boolean prettyPrint)
    {
        final Config cfg = prettyPrint ? PRETTY_PRINT : DEFAULT_PRINT;
        return JsonStream.serialize(cfg, this);
    }

    public String toJson()
    {
        return toJson(false);
    }
}
