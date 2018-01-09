/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.sync;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Wrapper class for messages to the Sharing Agent. These messages consist of an arbitrary number of
 * properties followed by an XML document. This class (and in fact the Sharing Agent as a whole)
 * does not parse or interpret the XML contents in any way. This class does not enforce which
 * properties must be populated, but of course any agent wishing to send messages to the Sharing
 * Agent must pre-arrange an agreed-upon set. The Sharing Agent will likely simply ignore any
 * messages which don't include the properties it expects.
 *
 * @author Aaron Dunlop
 * @version $Revision: 23927 $ $Date: 2017-01-17 13:53:22 -0800 (Tue, 17 Jan 2017) $ $Author: timd $
 * @since Dec 9, 2004
 */
public class SharingMessage
{
    private final HashMap<String, String> m_properties = new HashMap<>();

    private String m_xmlContents = null;

    public SharingMessage()
    {}

    public SharingMessage(final String message)
    {
        this(message.getBytes());
    }

    public SharingMessage(final byte[] message)
    {
        final int len = message.length;
        int beginningOffset = 0;
        int currentOffset = 0;
        byte ch = 0;
        String name = null;
        String value = null;

        // Optimized property parser.
        // Expects optional "name=value" pairs separated by '\n' and/or '\r'.
        // The character that separates the properties from the XML content is the opening angle
        // bracket, '<', of the <?xml...> tag.
        while (currentOffset < len)
        {
            ch = message[currentOffset];

            // Any of these three characters are EOL for a property
            if (ch == '\r' || ch == '\n' || ch == '<')
            {
                // Only add a property if we have valid characters AND a name was found.
                if (beginningOffset <= currentOffset && name != null)
                {
                    value = new String(message, beginningOffset, (currentOffset - beginningOffset));
                    m_properties.put(name, value);
                }

                // If we are at the beginning of the XML body, extract the remainder of the
                // characters and break out of our parser.
                if (ch == '<')
                {
                    m_xmlContents = new String(message, currentOffset, len - currentOffset);
                    break;
                }

                // Not done yet. Reset our variables and offsets and continue.
                name = value = null;
                beginningOffset = ++currentOffset;
            }
            else if (ch == '=')
            {
                // Make sure we have characters.
                if (name != null)
                {
                    // we have already had one '=' on the line
                    // a second '=' should not trigger a change in the name
                    currentOffset++;
                }
                else if ((beginningOffset <= currentOffset))
                {
                    name = new String(message, beginningOffset, (currentOffset - beginningOffset));

                    // Set our offsets and continue.
                    beginningOffset = ++currentOffset;
                }
            }
            else
            {
                // Regular character... Keep going.
                currentOffset++;
            }
        }
    }

    public Map<String, String> getProperties()
    {
        return m_properties;
    }

    public String getProperty(final String name)
    {
        return m_properties.get(name);
    }

    /**
     * Add properties to this sharing message. Any existing properties with the same name will be
     * replaced. All other existing properties remain unchanged.
     *
     * @param properties The properties to add to this sharing message. If null or empty, then
     *            nothing happens.
     */
    public void setProperties(final Map<String, String> properties)
    {
        if (properties == null)
        {
            return;
        }

        m_properties.putAll(properties);
    }

    /**
     * Add or update a name value pair.
     *
     * @param name should not contain an '=', '<', LF, or CR
     * @param value should not contain a '<', LF, or CR - may contain an '='
     */
    public void setProperty(final String name, final String value)
    {
        m_properties.put(name, value);
    }

    public Collection<String> propertyNames()
    {
        return m_properties.keySet();
    }

    private String fromSafeXml(final String safeXml) throws UnsupportedEncodingException
    {
        if (StringUtils.isAllEmpty(safeXml))
        {
            return StringUtils.EMPTY;
        }

        return URLDecoder.decode(safeXml, "UTF-8");
    }

    private String toSafeXml(final String xml) throws UnsupportedEncodingException
    {
        if (StringUtils.isEmpty(xml))
        {
            return StringUtils.EMPTY;
        }

        return URLEncoder.encode(xml, "UTF-8");
    }

    public String getXmlProperty(final String name) throws UnsupportedEncodingException
    {
        // API
        return fromSafeXml(m_properties.get(name));
    }

    public void setXmlProperty(final String name, final String value) throws UnsupportedEncodingException
    {
        // API
        m_properties.put(name, toSafeXml(value));
    }

    public void setXmlContents(final String document)
    {
        m_xmlContents = document;
    }

    public String getXmlContents()
    {
        return m_xmlContents;
    }

    @Override
    public String toString()
    {
        // API
        int bufferSize = 1024; // a little room for properties
        if (!StringUtils.isEmpty(getXmlContents()))
        {
            bufferSize += getXmlContents().length();
        }

        final StringBuilder sb = new StringBuilder(bufferSize);
        for (final String propertyName : getProperties().keySet())
        {
            final String propertyValue = getProperties().get(propertyName);
            sb.append(propertyName);
            sb.append("=");
            sb.append(propertyValue);
            sb.append('\n');
        }

        if (!StringUtils.isEmpty(getXmlContents()))
        {
            sb.append(getXmlContents());
        }

        return sb.toString();
    }
}
