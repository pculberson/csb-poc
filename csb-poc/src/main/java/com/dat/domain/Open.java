/*
 * $Id: $
 *
 * Copyright (C) 2018, TransCore LP. All Rights Reserved
 */
package com.dat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
final @EqualsAndHashCode(callSuper=false)
public class Open extends GeoLocation
{
    private final boolean isOpen = true;
}
