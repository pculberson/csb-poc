/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.sync;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dat.domain.AssetType;
import com.dat.domain.DomainException;

/**
 * Abstract class for processing SyncAlarm, SyncAsset and SyncSearch events.
 *
 * <p>
 * <em>Note that all required properties in the sharing message must be extracted in the
 * {@link #init} method of this class as the underlying properties Map is cleared at the end if init
 * to conserve space.</em>
 *
 * @author Philip Culberson
 * @since 11/2011
 *
 * @version $Revision: 24304 $ $Date:: 2017-03-17 11:37:39 -0700 #$ $Author: marks $
 */
public class SyncEvent extends MetaDomainObject
{
    private String m_fmeId;

    private String m_environment;

    private AssetType m_assetType;

    private int m_companyId;

    private int m_officeId;

    private int m_ownerId;

    private int m_groupId;

    private Action m_action;

    private char m_siteCode;

    private boolean m_fromSb2;

    private String m_sourceApplication;

    private int m_equipmentApiVersion;

    private boolean m_truckstops;

    private boolean m_extendedNetwork;

    private boolean m_doNotForwardSyncEvent;

    private boolean m_ignoreLocalPersistence;

    private int m_actualBusinessDays;

    private String m_basisAssetId;

    public enum Action
    {
        insert, cancel, update, refresh, delete, archive, sb2_export,
        selectSearch, syncStatus, markMatchWorked, takeMatch, unknown, associateAlarm
    }

    /**
     * Extract all properties of interest from the underlying {@link SharingMessage} into local
     * variables, converting to intrinsics if possible.
     *
     * @param clearProperties if true, clear the SharingMessage properties map once properties of
     *            interest have been extracted
     */
    private void init(final boolean clearProperties)
    {
        final Map<String, String> properties = getProperties();

        Action action = null;
        try
        {
            action = Action.valueOf(StringUtils.defaultString(properties.get("action")));
        }
        catch (final IllegalArgumentException e)
        {
            action = Action.unknown;
        }
        m_action = action;

        m_fmeId = StringUtils.defaultString(properties.get("fmeId"));

        // These two strings have very low cardinality, so use intern() to conserve space
        m_environment = StringUtils.defaultString(properties.get("environment")).intern();
        m_sourceApplication = StringUtils.defaultString(properties.get("sourceApplication")).intern();

        m_assetType = Boolean.parseBoolean(properties.get("isEquipment")) ? AssetType.Equipment : AssetType.Shipment;
        m_companyId = safeInt(properties.get("companyId"));
        m_officeId = safeInt(properties.get("officeId"));
        m_ownerId = safeInt(properties.get("ownerId"));
        m_groupId = safeInt(properties.get("groupId"));
        m_siteCode = StringUtils.isEmpty(properties.get("siteCode")) ? '?' : properties.get("siteCode").charAt(0);
        m_fromSb2 = Boolean.parseBoolean(properties.get("fromSb2"));
        m_equipmentApiVersion = safeInt(properties.get("eqTypeApiVersion"));
        m_truckstops = Boolean.parseBoolean(properties.get("hasTruckstops"));
        m_extendedNetwork = Boolean.parseBoolean(properties.get("isExtendedNetwork"));
        m_doNotForwardSyncEvent = Boolean.parseBoolean(properties.get("doNotForwardSyncEvent"));
        m_ignoreLocalPersistence = Boolean.parseBoolean(properties.get("ignoreLocalPersistence"));
        m_actualBusinessDays = Math.max(1, safeInt(properties.get("actualBusinessDays")));
        m_basisAssetId = StringUtils.defaultString(properties.get("basisAssetId"));

        // We have all the properties we are interested in, most of which have now been translated
        // to intrinsics, so clear the underlying map to save space.
        if (clearProperties)
        {
            properties.clear();
        }
    }

    public SyncEvent(final byte[] bytes, final boolean clearProperties) throws Exception
    {
        super(bytes);
        init(clearProperties);
    }

    public SyncEvent(final byte[] bytes) throws Exception
    {
        this(bytes, false);
    }

    public SyncEvent(final String data) throws Exception
    {
        this(data.getBytes(), false);
    }

    /**
     * Safely convert a string to an int. If <code>str</code> cannot be parsed to an int, return
     * {@link CSB#NULL_INTEGER}.
     *
     * @param str the string to convert
     * @return the int representation of <code>str</code>, or {@link CSB#NULL_INTEGER} if not
     *         parseable
     */
    private int safeInt(final String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (final Exception ignore)
        {
            return Integer.MIN_VALUE;
        }
    }

    public String getFmeId()
    {
        return m_fmeId;
    }

    protected void setFmeId(final String fmeId)
    {
        m_fmeId = fmeId;
    }

    public String getEnvironment()
    {
        return m_environment;
    }

    public AssetType getAssetType()
    {
        return m_assetType;
    }

    public int getCompanyId()
    {
        return m_companyId;
    }

    public int getOfficeId()
    {
        return m_officeId;
    }

    public int getOwnerId()
    {
        return m_ownerId;
    }

    public int getGroupId()
    {
        return m_groupId;
    }

    public char getSiteCode()
    {
        return m_siteCode;
    }

    public Action getAction()
    {
        return m_action;
    }

    public boolean isFromSb2()
    {
        return m_fromSb2;
    }

    public String getSourceApplication()
    {
        return m_sourceApplication;
    }

    public int getEquipmentApiVersion()
    {
        return m_equipmentApiVersion;
    }

    public boolean hasTruckstops()
    {
        return m_truckstops;
    }

    public boolean isExtendedNetwork()
    {
        return m_extendedNetwork;
    }

    public boolean doNotForwardSyncEvent()
    {
        return m_doNotForwardSyncEvent;
    }

    public boolean ignoreLocalPersistence()
    {
        return m_ignoreLocalPersistence;
    }

    public int getActualBusinessDays()
    {
        return m_actualBusinessDays;
    }

    public String getBasisAssetId()
    {
        return m_basisAssetId;
    }

    /**
     * Retrieve the legacy order id.
     *
     * @return the legacy order id
     * @throws DomainException if no Sb2Export element is found in the SyncAsset notification
     */
    public int getLegacyOrderId() throws DomainException
    {
        throw new DomainException(unsupportedActionException(getAction()));
    }

    public String unsupportedActionException(final Action action)
    {
        return actionException("Unsupported action", action);
    }

    public String nonFmeActionException(final Action action)
    {
        return actionException("Actions of this type do not specifiy an FME", action);
    }

    public String missingFmeException(final Action action)
    {
        return actionException("Sync event does not specify an FME", action);
    }

    public String actionException(final String msg, final Action action)
    {
        return actionException(msg, action, null);
    }

    public String actionException(final String msg, final Action action, final Exception e)
    {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(StringUtils.defaultString(msg));
        sb.append(" for action ");
        sb.append("'").append(action.name()).append("'");
        sb.append(" in ");
        sb.append("'").append(getClass().getName()).append("'");

        if (e != null)
        {
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            sb.append(": ").append(e.getMessage()).append("\n").append(StringUtils.defaultString(sw.toString()));
        }

        return sb.toString();
    }
}
