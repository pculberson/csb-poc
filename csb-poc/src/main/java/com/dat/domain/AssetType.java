/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.domain;

public enum AssetType
{
    Shipment('S'), Equipment('E');

    private final char m_abbreviation;

    AssetType(final char abbreviation)
    {
        m_abbreviation = abbreviation;
    }

    public char abbreviation()
    {
        return m_abbreviation;
    }
}
