/*
 * $Id: $
 *
 * Copyright (C) 2018, TransCore LP. All Rights Reserved
 */
package com.dat.domain;

import com.jsoniter.ValueType;
import com.jsoniter.any.Any;

public class GeoLocationHelper
{
    public static GeoLocation parse(final Any element)
    {
        if (element.valueType() == ValueType.INVALID)
        {
            return null;
        }

        for (final String key : element.keys())
        {
            switch (key)
            {
                // From SyncEvent
                case "@class" :
                    continue;
                case "minimalPoint" :
                    return element.get(key).as(Point.class);
                case "area" :
                    return element.get(key).as(Area.class);
                case "open" :
                    return element.get(key).as(Open.class);


                // From SimpleAsset.toJson()
                case "city" :
                case "stateProvince" :
                case "latitude" :
                case "longitude" :
                case "county" :
                    return element.as(Point.class);

                case "zones" :
                case "stateProvinces" :
                    return element.as(Area.class);

                case "isOpen" :
                    return element.as(Open.class);

                default :
                    throw new IllegalArgumentException(String.format("Unknown GeoLocation element: '%s'", key));
            }
        }

        throw new IllegalArgumentException("Could not parse GeoLocation");
    }
}
