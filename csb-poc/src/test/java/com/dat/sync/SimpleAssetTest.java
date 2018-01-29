/*
 * $Id: $
 *
 * Copyright (C) 2018, TransCore LP. All Rights Reserved
 */
package com.dat.sync;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.dat.domain.Area;
import com.dat.domain.GeoLocation;
import com.dat.domain.Open;
import com.dat.domain.Point;
import com.dat.domain.SimpleAsset;

public class SimpleAssetTest
{
    private static final Point ORIGIN_POINT = new Point("Newark", "DE", 39.68361, -75.75, "New Castle");
    private static final Point DESTINATION_POINT = new Point("Laredo", "TX", 27.50611, -99.50722, "Webb");
    private static final Area AREA = new Area(new String[] { "GA", "NC", "SC" }, null);
    private static final Open OPEN = new Open();

    private void assertGeoEquals(final GeoLocation expected, final GeoLocation actual)
    {
        assertThat(expected.getClass(), instanceOf(actual.getClass().getClass()));
        assertThat(expected.getClass().getName(), equalTo(actual.getClass().getName()));
        assertThat(expected, is(actual));
    }

    @Test
    public void p2pJsonToAsset() throws Exception
    {
        final SimpleAsset sa = new SimpleAsset(p2p);
        assertThat(sa.getFmeId(), is("DS1TAZR0"));
        assertThat(sa.getSourceApplication(), is("ftp"));
        assertGeoEquals(ORIGIN_POINT, sa.getOrigin());
        assertGeoEquals(DESTINATION_POINT, sa.getDestination());
    }

    @Test
    public void p2AreaJsonToAsset() throws Exception
    {
        final SimpleAsset sa = new SimpleAsset(p2area);
        assertThat(sa.getFmeId(), is("DS1TAZS0"));
        assertThat(sa.getSourceApplication(), is("dat.any"));
        assertGeoEquals(ORIGIN_POINT, sa.getOrigin());
        assertGeoEquals(AREA, sa.getDestination());
    }

    @Test
    public void p2openJsonToAsset() throws Exception
    {
        final SimpleAsset sa = new SimpleAsset(p2open);
        assertThat(sa.getFmeId(), is("DS1TAZT0"));
        assertThat(sa.getSourceApplication(), is("csb"));
        assertGeoEquals(ORIGIN_POINT, sa.getOrigin());
        assertGeoEquals(OPEN, sa.getDestination());
    }

    @Test
    public void assetToJson() throws Exception
    {
        final SyncEvent syncEvent = new SyncEvent(SYNC_EVENT_STRING);
        final SimpleAsset asset = new SimpleAsset(syncEvent);
        final SimpleAsset reconstituted = new SimpleAsset(asset.toJson());
        assertThat(asset, equalTo(reconstituted));
    }

    @Test
    public void assetToString() throws Exception
    {
        final SyncEvent syncEvent = new SyncEvent(SYNC_EVENT_STRING);
        final SimpleAsset asset = new SimpleAsset(syncEvent);
        final String str = asset.toString();
        assertThat(str, containsString("fmeId=LS3SM9dx"));
        assertThat(str, containsString("sourceApplication=dat.any"));
        assertThat(str, containsString("userId=69386"));
        assertThat(str, containsString("officeId=49942"));
        assertThat(str, containsString("companyId=34979"));
        assertThat(str, containsString("groupId=4276"));
        assertThat(str, containsString("postingType=Shipment"));
        assertThat(str, containsString("equipmentType=VR"));
        assertThat(str, containsString("origin=" + ORIGIN_POINT.toString()));
        assertThat(str, containsString("destination=" + DESTINATION_POINT.toString()));
    }

    private static final String p2p = "{"
        + "\"fmeId\":\"DS1TAZR0\","
        + "\"origin\":{\"county\":\"New Castle\",\"stateProvince\":\"DE\",\"latitude\":39.68361,\"longitude\":-75.75,\"city\":\"Newark\"},"
        + "\"companyId\":36345,"
        + "\"groupId\":16624,"
        + "\"officeId\":52080,"
        + "\"sourceApplication\":\"ftp\","
        + "\"earliestAvailability\":\"2018-01-25T08:00:00.000Z\","
        + "\"latestAvailability\":\"2018-01-26T08:00:00.000Z\","
        + "\"userId\":75643,"
        + "\"whenIssued\":\"2018-01-22T22:51:40.381Z\","
        + "\"equipmentType\":\"V\","
        + "\"destination\":{\"county\":\"Webb\",\"stateProvince\":\"TX\",\"latitude\":27.50611,\"longitude\":-99.50722,\"city\":\"Laredo\"},"
        + "\"startDate\":\"2018-01-22T22:51:40.377Z\","
        + "\"endDate\":\"2018-01-23T08:15:00.375Z\","
        + "\"postingType\":\"Shipment\"}";

    private static final String p2area = "{"
        + "\"fmeId\":\"DS1TAZS0\","
        + "\"origin\":{\"county\":\"New Castle\",\"stateProvince\":\"DE\",\"latitude\":39.68361,\"longitude\":-75.75,\"city\":\"Newark\"},"
        + "\"companyId\":36345,"
        + "\"groupId\":16624,"
        + "\"officeId\":52080,"
        + "\"sourceApplication\":\"dat.any\","
        + "\"earliestAvailability\":\"2018-01-25T08:00:00.000Z\","
        + "\"latestAvailability\":\"2018-01-26T08:00:00.000Z\","
        + "\"userId\":75643,"
        + "\"whenIssued\":\"2018-01-22T22:51:40.381Z\","
        + "\"equipmentType\":\"V\","
        + "\"destination\":{\"stateProvinces\":[\"GA\",\"NC\",\"SC\"],\"zones\":null},"
        + "\"startDate\":\"2018-01-22T22:51:40.377Z\","
        + "\"endDate\":\"2018-01-23T08:15:00.375Z\","
        + "\"postingType\":\"Shipment\"}";

    private static final String p2open = "{"
        + "\"fmeId\":\"DS1TAZT0\","
        + "\"origin\":{\"county\":\"New Castle\",\"stateProvince\":\"DE\",\"latitude\":39.68361,\"longitude\":-75.75,\"city\":\"Newark\"},"
        + "\"companyId\":36345,"
        + "\"groupId\":16624,"
        + "\"officeId\":52080,"
        + "\"sourceApplication\":\"csb\","
        + "\"earliestAvailability\":\"2018-01-25T08:00:00.000Z\","
        + "\"latestAvailability\":\"2018-01-26T08:00:00.000Z\","
        + "\"userId\":75643,"
        + "\"whenIssued\":\"2018-01-22T22:51:40.381Z\","
        + "\"equipmentType\":\"V\","
        + "\"destination\":{\"isOpen\":true},"
        + "\"startDate\":\"2018-01-22T22:51:40.377Z\","
        + "\"endDate\":\"2018-01-23T08:15:00.375Z\","
        + "\"postingType\":\"Shipment\"}";

    private static final String SYNC_EVENT_STRING;

    static
    {
        final StringBuilder sb = new StringBuilder(10*1024);

        sb.append("__magicNumber=Xyzzy0xfeedbeef1990").append("\n");
        sb.append("action=insert").append("\n");
        sb.append("causerGroupMemberUserIds=65931,69386,69388").append("\n");
        sb.append("causerId=69386").append("\n");
        sb.append("companyId=34979").append("\n");
        sb.append("doNotForwardSyncEvent=false").append("\n");
        sb.append("environment=tfsprd").append("\n");
        sb.append("eqTypeApiVersion=1").append("\n");
        sb.append("fmCoreId=533").append("\n");
        sb.append("fmeId=LS3SM9dx").append("\n");
        sb.append("fmeType=Asset").append("\n");
        sb.append("fromSb2=false").append("\n");
        sb.append("groupId=4276").append("\n");
        sb.append("groupMemberUserIds=65931,69386,69388").append("\n");
        sb.append("hasTruckstops=false").append("\n");
        sb.append("ignoreLocalPersistence=false").append("\n");
        sb.append("isEquipment=false").append("\n");
        sb.append("isExtendedNetwork=false").append("\n");
        sb.append("isMatchable=true").append("\n");
        sb.append("isPrivate=false").append("\n");
        sb.append("isShipment=true").append("\n");
        sb.append("officeId=49942").append("\n");
        sb.append("ownerId=69386").append("\n");
        sb.append("registryLookupId=S.418702.RD").append("\n");
        sb.append("sendToGetLoaded=false").append("\n");
        sb.append("siteCode=L").append("\n");
        sb.append("sourceApplication=dat.any").append("\n");
        sb.append("__encoding=JSON").append("\n");
        sb.append("__payloadClassname=java.lang.String").append("\n");
        sb.append("__payload={").append("\n");
        sb.append("  \"@class\" : \"com.tcore.tfs.domain.fmCoreInternal.FmCoreSyncNotification\",").append("\n");
        sb.append("  \"when\" : \"2018-01-22T17:43:43.168Z\",").append("\n");
        sb.append("  \"action\" : \"INSERT\",").append("\n");
        sb.append("  \"action_schemaVal\" : \"insert\",").append("\n");
        sb.append("  \"fmeId\" : \"LS3SM9dx\",").append("\n");
        sb.append("  \"who\" : {").append("\n");
        sb.append("    \"@class\" : \"com.tcore.csb.domain.tcoreTypes.UserTimeStamp\",").append("\n");
        sb.append("    \"user\" : 69386,").append("\n");
        sb.append("    \"date\" : \"2018-01-22T17:43:43.168Z\" ").append("\n");
        sb.append("  },").append("\n");
        sb.append("  \"updateCount\" : 1,").append("\n");
        sb.append("  \"ignoreLocalPersistence\" : false,").append("\n");
        sb.append("  \"groupId\" : 4276,").append("\n");
        sb.append("  \"insert\" : {").append("\n");
        sb.append("    \"@class\" : \"com.tcore.tfs.domain.fmCoreInternal.FmcInsert\",").append("\n");
        sb.append("    \"fme\" : {").append("\n");
        sb.append("      \"@class\" : \"com.tcore.tfs.domain.fmCoreInternal.SyncedFme\",").append("\n");
        sb.append("      \"whenIssued\" : \"2018-01-22T17:43:43.168Z\",").append("\n");
        sb.append("      \"posting\" : {").append("\n");
        sb.append("        \"@class\" : \"com.tcore.tfs.domain.fmCoreInternal.CorePosting\",").append("\n");
        sb.append("        \"fmeId\" : \"LS3SM9dx\",").append("\n");
        sb.append("        \"csbSequenceId\" : 1,").append("\n");
        sb.append("        \"postingId\" : \"LS3SM9dx\",").append("\n");
        sb.append("        \"defn\" : {").append("\n");
        sb.append("          \"@class\" : \"com.tcore.tfs.domain.tcoreServices.PostingDefinition\",").append("\n");
        sb.append("          \"basic\" : {").append("\n");
        sb.append("            \"@class\" : \"com.tcore.tfs.domain.tcoreServices.BasicPostingDefinition\",").append("\n");
        sb.append("            \"postingType\" : \"SHIPMENT\",").append("\n");
        sb.append("            \"postingType_schemaVal\" : \"Shipment\",").append("\n");
        sb.append("            \"equipmentType\" : \"VR\",").append("\n");
        sb.append("            \"origin\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreServices.PostingOrigin\",").append("\n");
        sb.append("              \"minimalPoint\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.csb.domain.tcoreTypes.MinimalPoint\",").append("\n");
        sb.append("                \"city\" : \"Newark\",").append("\n");
        sb.append("                \"stateProvince\" : \"DE\",").append("\n");
        sb.append("                \"stateProvince_schemaVal\" : \"DE\",").append("\n");
        sb.append("                \"latitude\" : 39.68361,").append("\n");
        sb.append("                \"longitude\" : -75.75,").append("\n");
        sb.append("                \"county\" : \"New Castle\" ").append("\n");
        sb.append("              } ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"destination\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreServices.PostingDestination\",").append("\n");
        sb.append("              \"minimalPoint\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.csb.domain.tcoreTypes.MinimalPoint\",").append("\n");
        sb.append("                \"city\" : \"Laredo\",").append("\n");
        sb.append("                \"stateProvince\" : \"TX\",").append("\n");
        sb.append("                \"stateProvince_schemaVal\" : \"TX\",").append("\n");
        sb.append("                \"latitude\" : 27.50611,").append("\n");
        sb.append("                \"longitude\" : -99.50722,").append("\n");
        sb.append("                \"county\" : \"Webb\" ").append("\n");
        sb.append("              } ").append("\n");
        sb.append("            } ").append("\n");
        sb.append("          },").append("\n");
        sb.append("          \"optional\" : {").append("\n");
        sb.append("            \"@class\" : \"com.tcore.tfs.domain.tcoreServices.OptionalPostingDefinition\",").append("\n");
        sb.append("            \"ltl\" : true,").append("\n");
        sb.append("            \"generalNotes\" : \"\",").append("\n");
        sb.append("            \"comments\" : [  ],").append("\n");
        sb.append("            \"count\" : 1,").append("\n");
        sb.append("            \"dimensions\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreFreightMatching.Dimensions\",").append("\n");
        sb.append("              \"length\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.csb.domain.tcoreTypes.Length\",").append("\n");
        sb.append("                \"amount\" : 16.0,").append("\n");
        sb.append("                \"unit\" : \"FT\",").append("\n");
        sb.append("                \"unit_schemaVal\" : \"ft\" ").append("\n");
        sb.append("              },").append("\n");
        sb.append("              \"weight\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.csb.domain.tcoreTypes.Weight\",").append("\n");
        sb.append("                \"amount\" : 3000.0,").append("\n");
        sb.append("                \"unit\" : \"LB\",").append("\n");
        sb.append("                \"unit_schemaVal\" : \"lb\" ").append("\n");
        sb.append("              } ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"tripMileage\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.csb.domain.tcoreTypes.Mileage\",").append("\n");
        sb.append("              \"distance\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.csb.domain.tcoreTypes.Distance\",").append("\n");
        sb.append("                \"amount\" : 1117.0,").append("\n");
        sb.append("                \"unit\" : \"MI\",").append("\n");
        sb.append("                \"unit_schemaVal\" : \"mi\" ").append("\n");
        sb.append("              },").append("\n");
        sb.append("              \"method\" : \"ROAD\",").append("\n");
        sb.append("              \"method_schemaVal\" : \"Road\" ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"displayEnhancements\" : \"\",").append("\n");
        sb.append("            \"isFavorite\" : false,").append("\n");
        sb.append("            \"kept\" : false,").append("\n");
        sb.append("            \"shipmentSpecific\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreServices.OptionalShipmentDefinition\",").append("\n");
        sb.append("              \"quickPay\" : false,").append("\n");
        sb.append("              \"loadAdvanceFuel\" : false,").append("\n");
        sb.append("              \"exclusive\" : false,").append("\n");
        sb.append("              \"intermodal\" : false,").append("\n");
        sb.append("              \"commodity\" : \"\",").append("\n");
        sb.append("              \"stopCount\" : 1 ").append("\n");
        sb.append("            } ").append("\n");
        sb.append("          },").append("\n");
        sb.append("          \"aux\" : {").append("\n");
        sb.append("            \"@class\" : \"com.tcore.tfs.domain.tcoreServices.PostingAuxiliaryInfo\",").append("\n");
        sb.append("            \"creditScore\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreRegistry.CreditScoreInfo\",").append("\n");
        sb.append("              \"score\" : 99,").append("\n");
        sb.append("              \"daysToPay\" : 36,").append("\n");
        sb.append("              \"scoreTimeStamp\" : \"2018-01-17T02:42:36.000Z\" ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"thirdPartyInfo\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreRegistry.ThirdPartyInfo\",").append("\n");
        sb.append("              \"rmisGreenLight\" : true,").append("\n");
        sb.append("              \"nmftaMember\" : false,").append("\n");
        sb.append("              \"ooidaMember\" : false,").append("\n");
        sb.append("              \"tiaP3Member\" : false,").append("\n");
        sb.append("              \"assurable\" : true,").append("\n");
        sb.append("              \"rivieraGreenLight\" : false,").append("\n");
        sb.append("              \"factorable\" : true,").append("\n");
        sb.append("              \"abcFactorable\" : true,").append("\n");
        sb.append("              \"abcCustomer\" : false,").append("\n");
        sb.append("              \"tiaMember\" : false,").append("\n");
        sb.append("              \"p3Level\" : 0,").append("\n");
        sb.append("              \"triumphPay\" : false ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"dotIds\" : [ {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreRegistry.DotIds\",").append("\n");
        sb.append("              \"dotNumber\" : 167896,").append("\n");
        sb.append("              \"brokerMcNumber\" : 153865,").append("\n");
        sb.append("              \"carrierMcNumber\" : 153865 ").append("\n");
        sb.append("            }   ] ").append("\n");
        sb.append("          },").append("\n");
        sb.append("          \"exposure\" : {").append("\n");
        sb.append("            \"@class\" : \"com.tcore.tfs.domain.tcoreServices.PostingExposure\",").append("\n");
        sb.append("            \"businessDays\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreFreightMatching.BusinessDays\",").append("\n");
        sb.append("              \"days\" : 1,").append("\n");
        sb.append("              \"locale\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.csb.domain.tcoreTypes.Locale\",").append("\n");
        sb.append("                \"language\" : \"en\",").append("\n");
        sb.append("                \"country\" : \"US\",").append("\n");
        sb.append("                \"country_schemaVal\" : \"US\",").append("\n");
        sb.append("                \"utcOffset\" : -28800000 ").append("\n");
        sb.append("              } ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"startDate\" : \"2018-01-22T17:43:43.165Z\",").append("\n");
        sb.append("            \"endDate\" : \"2018-01-23T07:59:59.999Z\",").append("\n");
        sb.append("            \"availability\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreFreightMatching.Availability\",").append("\n");
        sb.append("              \"earliest\" : \"2018-01-22T16:00:00.000Z\",").append("\n");
        sb.append("              \"latest\" : \"2018-01-23T07:59:59.999Z\" ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"isMatchable\" : true,").append("\n");
        sb.append("            \"isPrivate\" : false,").append("\n");
        sb.append("            \"callback\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreFreightMatching.CallbackDefinition\",").append("\n");
        sb.append("              \"preferredCallbackMethod\" : \"PRIMARY_PHONE\",").append("\n");
        sb.append("              \"preferredCallbackMethod_schemaVal\" : \"PrimaryPhone\" ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"callbackOverride\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreServices.CallbackOverride\",").append("\n");
        sb.append("              \"overriderId\" : 69386,").append("\n");
        sb.append("              \"preferredCallbackMethod\" : \"PRIMARY_PHONE\",").append("\n");
        sb.append("              \"preferredCallbackMethod_schemaVal\" : \"PrimaryPhone\" ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"extendedNetwork\" : false,").append("\n");
        sb.append("            \"allCallbacks\" : {").append("\n");
        sb.append("              \"@class\" : \"com.tcore.tfs.domain.tcoreServices.AllPostingCallbackContacts\",").append("\n");
        sb.append("              \"primaryPhone\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.tfs.domain.tcoreFreightMatching.CallbackPhoneNumber\",").append("\n");
        sb.append("                \"phone\" : {").append("\n");
        sb.append("                  \"@class\" : \"com.tcore.csb.domain.tcoreTypes.PhoneNumber\",").append("\n");
        sb.append("                  \"number\" : \"8017855400\" ").append("\n");
        sb.append("                } ").append("\n");
        sb.append("              },").append("\n");
        sb.append("              \"alternatePhone\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.tfs.domain.tcoreFreightMatching.CallbackPhoneNumber\",").append("\n");
        sb.append("                \"phone\" : {").append("\n");
        sb.append("                  \"@class\" : \"com.tcore.csb.domain.tcoreTypes.PhoneNumber\",").append("\n");
        sb.append("                  \"number\" : \"8013763366\" ").append("\n");
        sb.append("                } ").append("\n");
        sb.append("              },").append("\n");
        sb.append("              \"email\" : {").append("\n");
        sb.append("                \"@class\" : \"com.tcore.tfs.domain.tcoreFreightMatching.CallbackEmailAddress\",").append("\n");
        sb.append("                \"email\" : \"driggsinc@gmail.com\" ").append("\n");
        sb.append("              } ").append("\n");
        sb.append("            },").append("\n");
        sb.append("            \"getLoaded\" : false ").append("\n");
        sb.append("          } ").append("\n");
        sb.append("        },").append("\n");
        sb.append("        \"stats\" : {").append("\n");
        sb.append("          \"@class\" : \"com.tcore.tfs.domain.tcoreServices.PostingStatistics\",").append("\n");
        sb.append("          \"refreshCount\" : 0,").append("\n");
        sb.append("          \"editCount\" : 0,").append("\n");
        sb.append("          \"aggregateRepostCount\" : 0,").append("\n");
        sb.append("          \"aggregateBusinessDays\" : 0,").append("\n");
        sb.append("          \"aggregateRolloverCount\" : 0,").append("\n");
        sb.append("          \"aggregateCancelCount\" : 0,").append("\n");
        sb.append("          \"lookCount\" : 0,").append("\n");
        sb.append("          \"takeCount\" : 0,").append("\n");
        sb.append("          \"exactMatchCount\" : 0,").append("\n");
        sb.append("          \"similarMatchCount\" : 0 ").append("\n");
        sb.append("        },").append("\n");
        sb.append("        \"status\" : {").append("\n");
        sb.append("          \"@class\" : \"com.tcore.tfs.domain.tcoreServices.PostingStatus\",").append("\n");
        sb.append("          \"fmCoreId\" : 533,").append("\n");
        sb.append("          \"userId\" : 69386,").append("\n");
        sb.append("          \"groupId\" : 4276,").append("\n");
        sb.append("          \"sourceApplication\" : \"dat.any\",").append("\n");
        sb.append("          \"sourceApplicationVersion\" : \"7.0\",").append("\n");
        sb.append("          \"startDate\" : \"2018-01-22T17:43:43.165Z\",").append("\n");
        sb.append("          \"endDate\" : \"2018-01-23T07:59:59.999Z\",").append("\n");
        sb.append("          \"booked\" : \"2018-01-22T17:43:43.168Z\",").append("\n");
        sb.append("          \"created\" : {").append("\n");
        sb.append("            \"@class\" : \"com.tcore.csb.domain.tcoreTypes.UserTimeStamp\",").append("\n");
        sb.append("            \"user\" : 69386,").append("\n");
        sb.append("            \"date\" : \"2018-01-22T17:43:43.168Z\" ").append("\n");
        sb.append("          },").append("\n");
        sb.append("          \"updated\" : {").append("\n");
        sb.append("            \"@class\" : \"com.tcore.csb.domain.tcoreTypes.UserTimeStamp\",").append("\n");
        sb.append("            \"user\" : 69386,").append("\n");
        sb.append("            \"date\" : \"2018-01-22T17:43:43.168Z\" ").append("\n");
        sb.append("          },").append("\n");
        sb.append("          \"registryLookupId\" : \"S.418702.RD\",").append("\n");
        sb.append("          \"customerDirectoryId\" : \"S.418702.224630\",").append("\n");
        sb.append("          \"priceClass\" : \"FH\",").append("\n");
        sb.append("          \"correlationId\" : \"20646810188\",").append("\n");
        sb.append("          \"lastModified\" : {").append("\n");
        sb.append("            \"@class\" : \"com.tcore.csb.domain.tcoreTypes.UserTimeStamp\",").append("\n");
        sb.append("            \"user\" : 69386,").append("\n");
        sb.append("            \"date\" : \"2018-01-22T17:43:43.168Z\" ").append("\n");
        sb.append("          },").append("\n");
        sb.append("          \"flags\" : \"1\",").append("\n");
        sb.append("          \"crmPosId\" : 3241400,").append("\n");
        sb.append("          \"updateCounter\" : 1,").append("\n");
        sb.append("          \"tcsiOfficeId\" : \"S.418702.224630\",").append("\n");
        sb.append("          \"combinedOfficeId\" : 23836,").append("\n");
        sb.append("          \"updateCount\" : 0,").append("\n");
        sb.append("          \"isRecurring\" : false,").append("\n");
        sb.append("          \"legacyOrderId\" : 0,").append("\n");
        sb.append("          \"fromLegacySystem\" : false,").append("\n");
        sb.append("          \"isKept\" : false,").append("\n");
        sb.append("          \"serviced\" : \"2018-01-22T17:43:43.168Z\",").append("\n");
        sb.append("          \"ownerInitials\" : \"RD\" ").append("\n");
        sb.append("        } ").append("\n");
        sb.append("      } ").append("\n");
        sb.append("    } ").append("\n");
        sb.append("  } ").append("\n");
        sb.append("}").append("\n");

        SYNC_EVENT_STRING = sb.toString();
    }
}
