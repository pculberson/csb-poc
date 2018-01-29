/*
 * $Id: $
 *
 * Copyright (C) 2018, TransCore LP. All Rights Reserved
 */
package com.dat.sync;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.dat.domain.DomainException;

/**
 * Container for a {@code DomainObject} with associated metadata. Next-generation replacement for TFS's
 * "SharingMessage".
 * <p>
 * The metadata is a set of simple name+value properties.
 * <ul>
 * <li>Properties with no name will be tacitly ignored.</li>
 * <li>Properties need not have a value (i.e., they merely exist).</li>
 * <li>Property names must be printable alphanumeric US-ASCII.</li>
 * <li>There are no restrictions on the values, except that they may not contain '\n' (newline) or '\r' or (carriage
 * return). If this is needed, be sure to quote them (e.g., "\\n") or encode them (e.g., via {@code URLEncoder.encode}.
 * Consumers will be responsible for proper encoding/decoding.</li>
 * <li>Non-compliant property names or values will be tacitly ignored. Ideally, an exception would be thrown, but there
 * is already a huge body of existent code.</li>
 * </ul>
 * </p>
 * When serialized, the metadata is first, and appears as a set of string name-value pairs terminated by newline.
 * Internal properties, denoted by a leading "__" appear afterwards. The {@code DomainObject} payload is itself
 * serialized as a property, and is last. The payload may have multiple newlines in it, to support pretty-printed XML,
 * JSON, and other textual encodings:
 *
 * <pre>
 * <code>
 * foo=Something
 * bar=something else
 * userId=50001
 * companyId=234
 * officeId=1238876
 * pi=3.14159
 * groupMembersUserIds=12345,67890,223344
 * bunch=of other metadata
 * __payloadClassname=com.tcore.csb.domain.tcoreTypes.MinimalPoint
 * __encoding=JSON
 * __payload={
 * "@class" : "com.tcore.csb.domain.tcoreTypes.MinimalPoint",
 * "city" : "Canby",
 * "stateProvince" : "OR",
 * "latitude" : 45.262264,
 * "longitude" : -122.6921,
 * "county" : "Clackamas"
 * }
 * </code>
 * </pre>
 *
 * The user can specify any implemented encoding for the payload. If it's a binary encoding, it will be internally
 * rendered as a {@String}, via {@code Base64}. However, normally, CSB developers should not concern themselves with the
 * encoding used, and instead rely on system policy. External consumers, such as application developers, will of course
 * need to deal with the encoding used.
 *
 * @author Tim Dale (re-developed into current form, with selectable encoding, everything-is-a-property, etc.) January
 *         2017.
 *
 * @author Aaron Dunlop (original {@code SharingMessage})
 *
 * @version $Revision: 23128 $ $Date: 2016-09-16 09:48:08 -0700 (Fri, 16 Sep 2016) $ $Author: timd $
 *
 * @since Dec 9, 2004
 */
public class MetaDomainObject {

    /**
     * Supported internal encodings for the payload. CSB developers should not concern themselves with this, and let
     * system policy select the encoding.
     */
    public enum Encoding {
        XML, //
        JSON //
        // JS, //
        // BSON
    }



    /**
     * Construct with domain object payload, and metadata.
     *
     * @param payload The domain object to be contained.
     *
     * @param properties The metadata properties associated with the domain object payload.
     */
    public MetaDomainObject (final String payload, final Map<String, String> properties) {

        setPayload(payload);
        setProperties(properties);
    }



    /**
     * Copy constructor.
     *
     * @param mdo The original to copy.
     */
    public MetaDomainObject (final MetaDomainObject mdo) {

        this(mdo.m_payload, mdo.m_properties);
    }



    /**
     * Construct empty. Metadata and payload will need to be manually set.
     */
    public MetaDomainObject () {

        // we always want to have the encoding set
        setEncoding(DEFAULT_ENCODING);
    }



    /**
     * Construct from serialized string. It is assumed that this class performed the serialization.
     *
     * @param serialized The string that represents a serialized instance of this class.
     *
     * @throws DomainException if cannot instantiate from the supplied bytes.
     */
    public MetaDomainObject (final String serialized) throws Exception {

        this(serialized.getBytes());
    }



    /**
     * Construct from serialized bytes. It is assumed that this class performed the serialization.
     *
     * @param serialized The bytes that represent a serialized instance of this class.
     *
     * @throws DomainException if cannot instantiate from the supplied bytes.
     */
    public MetaDomainObject (final byte[] serialized) throws Exception {

        this();

        if (!MetaDomainObject.is(serialized)) {
            throw new Exception("Serialized data does not define a " + this.getClass().getName());
        }


        // clear the encoding; we rely on the serialization to tell us what it should be
        setEncoding(null);

        boolean inPayload = false;
        final StringBuilder payload = new StringBuilder(10 * 1024);
        for (final String line : toLines(serialized)) {

            if (inPayload) {
                if (payload.length() > 0) {
                    // dealing with multi-line payload (e.g., XML, JSON)
                    payload.append('\n');
                }
                payload.append(line);
                continue;
            }

            // regular property
            final Entry<String, String> property = extractProperty(line);
            final String name = property.getKey();
            final String value = StringUtils.defaultString(property.getValue());
            if (PROPERTY_NAME_PAYLOAD.equals(name)) {
                inPayload = true;
                // value is whatever value we have on this line, plus the contents of all the following lines.
                //
                // Temporary hack for XML: the applications are expecting to see the opening bracket "<" of XML as the
                // first character of a line. If this is the first line, and it's empty, ignore it.
                if (doSupportOldXmlEncoding() && (payload.length() == 0) && StringUtils.isEmpty(value)) {
                    continue;
                }
                payload.append(property.getValue());
            }
            else {
                // bypass the extra safeties and processing provided by setProperties(); we are trusting that we're
                // the ones that emitted this byte stream
                setRawProperty(name, property.getValue());
            }
        }

        // now decode the payload
        m_payload = null;
        final String payloadString = payload.toString();
        if (StringUtils.isEmpty(payloadString) || StringUtils.isAllBlank(payloadString)) {
            return;
        }

        m_payload = payloadString;
    }



    private Collection<String> toLines(final byte[] bytes) {

        final Collection<String> lines = new LinkedList<>();
        final StringBuilder line = new StringBuilder(120); // usually big enough for a line
        for (int offset = 0; offset < bytes.length; offset++) {
            final char ch = (char)bytes[offset];
            if ((ch == '\n') || (ch == '\r')) {
                lines.add(line.toString());
                line.setLength(0);
            }
            else {
                line.append(ch);
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines;
    }



    private Map.Entry<String, String> extractProperty(final String line) {

        final int equalsAt = line.indexOf('=');
        if (equalsAt < 0) {
            // nameless value
            return new AbstractMap.SimpleEntry<>(StringUtils.EMPTY, line);
        }
        if (equalsAt == 0) {
            // we consider this to be a nameless value that includes the '='
            return new AbstractMap.SimpleEntry<>(StringUtils.EMPTY, line);
        }
        if (equalsAt == line.length() - 1) {
            // this is a name with no value
            return new AbstractMap.SimpleEntry<>(line.substring(0, line.length() - 1), StringUtils.EMPTY);
        }

        // everything else is typical "name=value"
        final String name = line.substring(0, equalsAt);
        final String value = line.substring(equalsAt + 1, line.length());
        return new AbstractMap.SimpleEntry<>(name, value);
    }



    /**
     * Extract a named property from a serialized {@link MetaDomainObject} without a full de-serialization. This is
     * useful when meta information is needed, but the cost of a full de-serialization is too expensive at that time.
     *
     * @param serialized The serialized {@link MetaDomainObject}
     *
     * @param propertyName The desired property's name. If null or empty, and null property value is returned.
     *
     * @return The value of the property. If the property is not found, null is returned.
     */
    public static String extractPropertyFromRawSerialization(final String serialized,
                                                             final String propertyName) {

        if (StringUtils.isEmpty(propertyName)) return null;

        // scan the raw serialized string, looking for the property name
        final String propertyTag = "\n" + propertyName + "=";
        final int propertyNameStart = serialized.indexOf(propertyTag);
        if (propertyNameStart < 0) return null;

        // found the property; now slurp in everything else until EOL
        final int propertyValueStart = propertyNameStart + propertyTag.length();
        int propertyValueEnd = propertyValueStart;
        while (propertyValueEnd < serialized.length()) {
            final char ch = serialized.charAt(propertyValueEnd);
            if ((ch == '\n') || (ch == '\r')) break;
            ++propertyValueEnd;
        }

        return serialized.substring(propertyValueStart, propertyValueEnd);
    }



    /**
     * Test if data appears to be a serialized {@code MetaDomainObject}.
     *
     * @param data the data to test.
     *
     * @return true if the data appears to be a serialized {@code MetaDomainObject}
     */
    public static boolean is(final byte[] data) {

        return (data == null) ? false : is(new String(data));
    }




    /**
     * Test if data appears to be a serialized {@code MetaDomainObject}.
     *
     * @param data the data to test.
     *
     * @return true if the data appears to be a serialized {@code MetaDomainObject}
     */
    public static boolean is(final String data) {

        return (data == null) ? false : data.startsWith(MAGIC_NUMBER_NAME + "=" + MAGIC_NUMBER_VALUE);
    }



    /**
     * Specify the encoding to be used when serializing.
     *
     * @param encoding The encoding to be used when serializing. If set to null, will default to system policy.
     */
    public void setEncoding(final Encoding encoding) {

        if (encoding == null) {
            removeProperty(PROPERTY_NAME_PAYLOAD_ENCODING);
        }
        else {
            setRawProperty(PROPERTY_NAME_PAYLOAD_ENCODING, encoding.name());
        }
    }



    /**
     * Get the encoding that will be used when serializing.
     *
     * @return the encoding that will be used when serializing.
     */
    public Encoding encoding() {

        // it should always be set, but if not, default
        final String encodingName = getProperty(PROPERTY_NAME_PAYLOAD_ENCODING);
        return (StringUtils.isEmpty(encodingName)) ? DEFAULT_ENCODING : Encoding.valueOf(encodingName);
    }



    /**
     * Set the domain object payload. The current payload (if any) is replaced and metadata is not affected.
     *
     * @param payload The domain object payload. Can be null, but why bother?
     */
    public void setPayload(final String payload) {

        m_payload = payload;
        if (payload == null) {
            removeProperty(PROPERTY_NAME_PAYLOAD_CLASSNAME);
        }
        else {
            setProperty(PROPERTY_NAME_PAYLOAD_CLASSNAME, payload.getClass().getName());
        }
    }



    /**
     * Get the domain object payload.
     *
     * @return The domain object payload. If not yet set, then null is returned.
     */
    public String getPayload() {

        return m_payload;
    }



    /**
     * Get the class name of the domain object payload.
     *
     * @return The class name of the domain object payload. If the payload has not yet been set, then an empty string is
     *         returned.
     */
    public String payloadClassName() {

        return (m_payload == null) ? StringUtils.EMPTY : m_payload.getClass().getName();
    }



    /**
     * Get the metadata properties associated with the domain object payload.
     *
     * @return The metadata properties. At least an empty map is always returned. The returned map is copy-safe, so
     *         changes to it will NOT be reflected in this {@code MetaDomainObject}.
     */
    public Map<String, String> getProperties() {

        final Map<String, String> copy = getAllProperties();

        // Remove the internal properties; we want the user to only see their own properties, and not be able to
        // directly mess with internal properties.
        copy.remove(MAGIC_NUMBER_NAME);
        copy.remove(PROPERTY_NAME_PAYLOAD);
        copy.remove(PROPERTY_NAME_PAYLOAD_CLASSNAME);
        copy.remove(PROPERTY_NAME_PAYLOAD_ENCODING);

        return copy;
    }



    private Map<String, String> getAllProperties() {

        return new TreeMap<>(m_properties);
    }



    /**
     * Get the metadata properties associated with the domain object payload, where the name of the each property starts
     * with the specified prefix.
     *
     * @param startsWith Only properties whose name starts with this prefix will be returned.
     *
     * @return The metadata properties whose name starts with the specified prefix. At least an empty map is always
     *         returned.
     */
    public Map<String, String> getProperties(final String startsWith) {

        final Map<String, String> extracted = new TreeMap<>();
        if (StringUtils.isEmpty(startsWith)) return extracted;

        m_properties.entrySet()
                    .stream()
                    .filter(property -> property.getKey().startsWith(startsWith))
                    .forEach(property -> extracted.put(property.getKey(), property.getValue()));

        return extracted;
    }



    /**
     * Get a specified metadata property's value.
     *
     * @param name The name of the metadata property associated with the value.
     *
     * @return The value of the specified metadata property. If no value is associated with the specified property name,
     *         then null is returned.
     */
    public String getProperty(final String name) {

        return m_properties.get(name);
    }



    /**
     * Add metadata properties to this sharing message. Any existing properties with the same name will be replaced. All
     * other existing properties remain unchanged.
     *
     * @param properties The metadata properties to add to this sharing message. If null or empty, then nothing happens.
     */
    public void setProperties(final Map<String, String> properties) {

        if (properties == null) return;

        // copy-safe
        properties.entrySet().forEach(property -> setProperty(property.getKey(), property.getValue()));
    }



    /**
     * Add or update a property.
     *
     * @param name Name of the property. Must be US-ASCII printable alphanumeric. If not, this property will be ignored.
     *
     * @param value Value of the property. Should not contain a '<', LF, or CR - may contain an '='
     */
    public void setProperty(final String name,
                            final String value) {

        if (StringUtils.isEmpty(name)) return;
        if (!isSafePropertyName(name)) return;
        if (!isSafePropertyValue(value)) return;
        setRawProperty(name, value);
    }



    private void setRawProperty(final String name,
                                final String value) {

        m_properties.put(name, value);
    }



    /**
     * Remove a property.
     *
     * @param name The name of the property.
     */
    public void removeProperty(final String name) {

        m_properties.remove(name);
    }



    /**
     * Remove all metadata properties whose name starts with the specified prefix.
     *
     * @param startsWith Only properties whose name starts with this prefix will be removed.
     */
    public void removeProperties(final String startsWith) {

        if (StringUtils.isEmpty(startsWith)) return;

        final Set<String> victimKeys = new HashSet<>();
        m_properties.keySet()
                    .stream()
                    .filter(name -> name.startsWith(startsWith))
                    .forEach(name -> victimKeys.add(name));

        victimKeys.stream().forEach(victimKey -> removeProperty(victimKey));
    }



    private String fromSafeXml(final String safeXml) throws UnsupportedEncodingException {

        if (StringUtils.isEmpty(safeXml)) {
            return StringUtils.EMPTY;
        }

        return URLDecoder.decode(safeXml, "UTF-8");
    }



    private String toSafeXml(final String xml) throws UnsupportedEncodingException {

        if (StringUtils.isEmpty(xml)) {
            return StringUtils.EMPTY;
        }

        return URLEncoder.encode(xml, "UTF-8");
    }



    /**
     * Get the value of a property. The value is assumed to be XML and have been internally stored as URL encoded.
     *
     * @param name The name of the property.
     *
     * @return The value of the property, as an XML doclit. If no such property exists, then null is returned.
     *
     * @throws UnsupportedEncodingException If the XML property value cannot be decoded from URL encoding.
     */
    public String getXmlProperty(final String name) throws UnsupportedEncodingException {

        return fromSafeXml(getProperty(name));
    }



    /**
     * Set a property whose value is explicitly XML. This is independent of the payload's encoding.
     *
     * @param name The name of the property.
     *
     * @param value The value of the property; is assumed to be XML. It will be URL encoded internally for storage.
     *
     * @throws UnsupportedEncodingException If the XML property value cannot be URL encoded.
     */
    public void setXmlProperty(final String name,
                               final String value) throws UnsupportedEncodingException {

        setProperty(name, toSafeXml(value));
    }



    @Override
    public String toString() {

        try {
            return serialize();
        } catch (final Exception dex) {
            return "DomainException: " + dex.toString();
        }
    }



    /**
     * Serialize this container into a string. See class javadoc for implementation details, if you care (and you
     * probably don't, or shouldn't).
     *
     * @return {@code String} serialization of this container.
     *
     * @throws DomainException If the serialization failed.
     */
    public String serialize() throws Exception {

        final StringBuilder sb = new StringBuilder(10 * 1024);

        // magic number first
        sb.append(MAGIC_NUMBER_LITERAL);

        // put user properties up front
        getProperties().keySet()
                       .stream()
                       .filter(name -> !isInternalProperty(name))
                       .forEach(name -> serializeProperty(name, sb));

        // now the internal properties at the end
        serializeProperty(PROPERTY_NAME_PAYLOAD_ENCODING, encoding().name(), sb);
        serializeProperty(PROPERTY_NAME_PAYLOAD_CLASSNAME, payloadClassName(), sb);

        // payload must be last of all
        serializeProperty(PROPERTY_NAME_PAYLOAD, m_payload, sb);

        return sb.toString();
    }



    private StringBuilder serializeProperty(final String propertyName,
                                            final StringBuilder sb) {

        return serializeProperty(propertyName, getProperty(propertyName), sb);
    }



    private static StringBuilder serializeProperty(final String propertyName,
                                                   final String propertyValue,
                                                   final StringBuilder sb) {

        sb.append(propertyName);
        sb.append("=");
        if (!StringUtils.isEmpty(propertyValue)) {
            sb.append(propertyValue);
        }
        return sb.append('\n');
    }



    private boolean isInternalProperty(final String propertyName) {

        return StringUtils.defaultString(propertyName).startsWith(INTERNAL_PROPERTY_NAME_PREFIX);
    }



    private boolean isSafePropertyName(final String name) {

        // simple rule: must be US ASCII, can't have a "=" (that's our name-value separator), and can't look like an
        // internal property
        if (StringUtils.isEmpty(name)) return false;
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(name)) return false;
        if (name.startsWith(INTERNAL_PROPERTY_NAME_PREFIX)) return false;
        for (int i = 0; i < name.length(); i++) {
            final char ch = name.charAt(i);
            if ((ch >= '!') && (ch <= '~') && (ch != '=')) continue;
            return false;
        }

        return true;
    }



    private boolean isSafePropertyValue(final String value) {

        // null is permissible; we can have value-less properties
        if (value == null) return true;

        // no properties with EOL embedded
        return (!value.contains("\n") && !value.contains("\r"));
    }



    /**
     * Set true to support the XML encoding (serializing) in the style of SharingMessage.
     *
     * @param state whether to support SharingMessage's style of XML encoding.
     */
    public void doSupportOldXmlEncoding(final boolean state) {

        m_supportOldXmlEncoding = state;
    }



    private boolean doSupportOldXmlEncoding() {

        return (m_supportOldXmlEncoding && (encoding() == Encoding.XML));
    }



    // we go to the expense of a TreeMap so the properties always appear in sorted order; much easier on humans when
    // viewing serialized instances, and for writing test code
    private final Map<String, String> m_properties = new TreeMap<>(); // new HashMap<>();

    private String m_payload = null;

    private static final String INTERNAL_PROPERTY_NAME_PREFIX = "__";

    private static final String PROPERTY_NAME_PAYLOAD = INTERNAL_PROPERTY_NAME_PREFIX + "payload";

    private static final String PROPERTY_NAME_PAYLOAD_CLASSNAME = INTERNAL_PROPERTY_NAME_PREFIX + "payloadClassname";

    private static final String PROPERTY_NAME_PAYLOAD_ENCODING = INTERNAL_PROPERTY_NAME_PREFIX + "encoding";

    private static final Encoding DEFAULT_ENCODING = Encoding.JSON;

    private boolean m_supportOldXmlEncoding = true;

    private static final String MAGIC_NUMBER_NAME = INTERNAL_PROPERTY_NAME_PREFIX + "magicNumber";

    private static final String MAGIC_NUMBER_VALUE = "Xyzzy0xfeedbeef1990";

    private static final String MAGIC_NUMBER_LITERAL = serializeProperty(MAGIC_NUMBER_NAME,
                                                                         MAGIC_NUMBER_VALUE,
                                                                         new StringBuilder()).toString();
}
