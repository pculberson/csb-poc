/*
 * $Id: $
 *
 * Copyright (C) 2018, TransCore LP. All Rights Reserved
 */
package com.dat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class Point extends GeoLocation
{
    private String city;
    private String stateProvince;
    private double latitude;
    private double longitude;
    private String county;
}
