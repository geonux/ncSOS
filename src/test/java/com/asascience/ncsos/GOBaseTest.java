/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asascience.ncsos;

import com.asascience.ncsos.outputformatter.OutputFormatter;
import com.asascience.ncsos.service.Parser;
import com.asascience.ncsos.util.XMLDomUtils;
import java.io.*;
import java.util.HashMap;
import org.apache.log4j.BasicConfigurator;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.jdom.*;
import ucar.nc2.dataset.NetcdfDataset;

/**
 *
 * @author SCowan
 */
public class GOBaseTest {
    
    private static String base = null;
    private static String exampleOutputDir = null;
    private static final String datasets = "resources/datasets/";
    private static final String defaultAuthority = "ncsos";
   
    // final strings
    private static final String baseRequest = "request=GetObservation&version=1.0.0&service=sos&responseFormat=text%2Fxml%3Bsubtype%3D%22om%2F1.0.0%22";
    private static final String IoosSosRequest = "request=GetObservation&version=1.0.0&service=sos&responseFormat=text%2Fxml%3Bsubtype%3D%22om%2F1.0.0%2Fprofiles%2Fioos_sos%2F1.0%22";
    
    private static final String imeds1 = "resources/datasets/sura/Hsig_UNDKennedy_IKE_VIMS_3D_WAVEONLY.nc";
    private static final String imeds1Req = baseRequest + String.format("&observedProperty=hs&offering=network-all&procedure=urn:ioos:station:%1$s:UNDKennedy_S,urn:ioos:station:%1$s:UNDKennedy_X,urn:ioos:station:%1$s:UNDKennedy_Z&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds4 = "resources/datasets/sura/hs_USACE-CHL.nc";
    private static final String imeds4Req = baseRequest + String.format("&observedProperty=hs&offering=network-all&procedure=urn:ioos:station:%1$s:USACE-CHL_2410508B,urn:ioos:station:%1$s:USACE-CHL_2410513B,urn:ioos:station:%1$s:USACE-CHL_2410510B&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);

    private static final String imeds5 = "resources/datasets/sura/hwm_TCOON_NAVD88.nc";
    private static final String imeds5Req = baseRequest + String.format("&observedProperty=hwm&offering=network-all&procedure=urn:ioos:station:%1$s:TCOON_87747701,urn:ioos:station:%1$s:TCOON_87705701,urn:ioos:station:%1$s:TCOON_87705201,urn:ioos:station:%1$s:TCOON_87704751,urn:ioos:station:%1$s:TCOON_87708221&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);

    private static final String imeds6 = "resources/datasets/sura/tm_CSI.nc";
    private static final String imeds6Req = baseRequest + String.format("&observedProperty=tp&offering=network-all&procedure=urn:ioos:station:%1$s:CSI_15,urn:ioos:station:%1$s:CSI_06,urn:ioos:station:%1$s:CSI_09&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    private static final String imeds6IoosReq = IoosSosRequest + String.format("&observedProperty=tp&offering=CSI_03&procedure=urn:ioos:station:%1$s:CSI_03", defaultAuthority);
    
    private static final String imeds7 = "resources/datasets/sura/tm_IKE.nc";
    private static final String imeds7Req = baseRequest + String.format("&observedProperty=tp&offering=network-all&procedure=urn:ioos:station:%1$s:CSI_06,urn:ioos:station:%1$s:CSI_09,urn:ioos:station:%1$s:NDBC_42020,urn:ioos:station:%1$s:NDBC_42019,urn:ioos:station:%1$s:USACE-CHL_2410513B,urn:ioos:station:%1$s:NDBC_42059&eventtime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    private static final String imeds7ReqIoos = IoosSosRequest + String.format("&observedProperty=tp&offering=network-all&procedure=urn:ioos:station:%1$s:CSI_06,urn:ioos:station:%1$s:CSI_09,urn:ioos:station:%1$s:NDBC_42020,urn:ioos:station:%1$s:NDBC_42019,urn:ioos:station:%1$s:USACE-CHL_2410513B,urn:ioos:station:%1$s:NDBC_42059&eventtime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds8 = "resources/datasets/sura/watlev_CRMS.nc";
    private static final String imeds8Req = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:ioos:station:%1$s:CRMS_CS20-106,urn:ioos:station:%1$s:CRMS_CS20-15R,urn:ioos:station:%1$s:CRMS_DCPBS03,urn:ioos:station:%1$s:CRMS_DCPBS04&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds9 = "resources/datasets/sura/watlev_CRMS_2005.nc";
    private static final String imeds9Req = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:ioos:station:%1$s:CRMS_CS20-106,urn:ioos:station:%1$s:CRMS_CS20-15R,urn:ioos:station:%1$s:CRMS_DCPBS03,urn:ioos:station:%1$s:CRMS_DCPBS04&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds10 = "resources/datasets/sura/watlev_CRMS_2008.F.C_IKE_VIMS_3D_NOWAVE.nc";
    private static final String imeds10Req = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:ioos:station:%1$s:CRMS_CRMS0161-H01,urn:ioos:station:%1$s:CRMS_DCPBA07,urn:ioos:station:%1$s:CRMS_CRMS0174-H01,&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds11 = "resources/datasets/sura/watlev_CRMS_2008.F.C__IKE_VIMS_3D_WITHWAVE.nc";
    private static final String imeds11Req = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:ioos:station:%1$s:CRMS_CRMS0161-H01,urn:ioos:station:%1$s:CRMS_DCPBA07,urn:ioos:station:%1$s:CRMS_CRMS0174-H01,&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds12 = "resources/datasets/sura/watlev_CSI.nc";
    private static final String imeds12Req = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:ioos:station:%1$s:CSI_03,urn:ioos:station:%1$s:CSI_06,urn:ioos:station:%1$s:CSI_09,urn:ioos:station:%1$s:CSI_15&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds13 = "resources/datasets/sura/watlev_IKE.nc";
    private static final String imeds13Req = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:ioos:station:%1$s:CSI_06,urn:ioos:station:%1$s:CSI_09&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String imeds14 = "resources/datasets/sura/watlev_IKE.P.UL-Ike2Dh.61.nc";
    
    private static final String imeds15 = "resources/datasets/sura/watlev_NOAA_NAVD_PRE.nc";
    private static final String imeds15Req = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:ioos:station:%1$s:NOAA_8727235,urn:ioos:station:%1$s:NOAA_8729501&eventTime=1990-01-01T00:00:00Z/2009-01-01T00:00:00Z", defaultAuthority);
    
    private static final String tsIncompleteMultiDimensionalMultipleStations = "resources/datasets/timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2/timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2.nc";
    private static final String timeSeriesIncomplete = baseRequest + String.format("&observedProperty=temperature&offering=Station-9&procedure=urn:ioos:station:%1$s:Station-9", defaultAuthority);
    private static final String timeSeriesIncompleteWithTime = baseRequest + String.format("&observedProperty=temperature&offering=Station-9&eventTime=1990-01-01T00:00:00Z&procedure=urn:ioos:station:%1$s:Station-9", defaultAuthority);
    private static final String timeSeriesIncompleteMulti = baseRequest + String.format("&observedProperty=temperature&offering=Station-9&eventTime=1990-01-01T00:00:00Z/1990-01-01T10:00:00Z&procedure=urn:ioos:station:%1$s:Station-9", defaultAuthority);
    private static final String timeSeriesIncompleteMultiInvalid = baseRequest + String.format("&observedProperty=temperature&offering=Station-9&eventTime=1990-01-01T05:00:00Z/1990-01-01T06:00:00Z&procedure=urn:ioos:station:%1$s:Station-9", defaultAuthority);
    private static final String timeSeriesIncompleteMultiStation = baseRequest + String.format("&observedProperty=temperature&offering=network-all&eventTime=1990-01-01T00:00:00Z/1990-01-01T8:00:00Z&procedure=urn:ioos:station:%1$s:Station-9,urn:ioos:station:%1$s:Station-8",defaultAuthority);
    private static final String timeSeriesIncompleteMultiStationx3 = baseRequest + String.format("&observedProperty=temperature&procedure=urn:ioos:station:%1$s:Station-9,urn:ioos:station:%1$s:Station-8,urn:ioos:station:%1$s:Station-7&offering=network-all&eventTime=1990-01-01T00:00:00Z/1990-01-01T8:00:00Z", defaultAuthority);
    
    private static final String tsOrthogonalMultidimenstionalMultipleStations = "resources/datasets/timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1/timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.nc";
    private static final String timeSeriestOrth = baseRequest + String.format("&observedProperty=alt&offering=Station-1&procedure=urn:ioos:station:%1$s:Station-1&eventTime=1990-01-01T00:00:00Z", defaultAuthority);
    
    private static final String RaggedMultiConventions = "resources/datasets/timeSeriesProfile-Ragged-MultipeStations-H.5.3/timeSeriesProfile-Ragged-MultipeStations-H.5.3.nc";
    private static final String MultiDimensionalSingleStations = "resources/datasets/timeSeriesProfile-Multidimensional-SingleStation-H.5.2/timeSeriesProfile-Multidimensional-SingleStation-H.5.2.nc";
    private static final String MultiDimensionalMultiStations = "resources/datasets/timeSeriesProfile-Multidimensional-MultipeStations-H.5.1/timeSeriesProfile-Multidimensional-MultipeStations-H.5.1.nc";
    
    private static final String ContiguousRaggedMultipleProfiles = "resources/datasets/profile-Contiguous-Ragged-MultipleProfiles-H.3.4/profile-Contiguous-Ragged-MultipleProfiles-H.3.4.nc";
    private static final String profileRequest = baseRequest + String.format("&observedProperty=temperature,humidity&offering=Profile0&procedure=urn:ioos:station:%1$s:Profile0&eventTime=1990-01-01T00:00:00Z", defaultAuthority);
    private static final String profileRequestMultiTime = baseRequest + String.format("&observedProperty=temperature,humidity&offering=network-all&procedure=urn:ioos:station:%1$s:Profile1,urn:ioos:station:%1$s:Profile2,urn:ioos:station:%1$s:Profile3&eventTime=1990-01-01T00:00:00Z/1990-01-01T02:00:00Z", defaultAuthority);
    private static final String profileRequestMultiTime2 = baseRequest + String.format("&observedProperty=temperature,humidity&offering=network-all&procedure=urn:ioos:station:%1$s:Profile1,urn:ioos:station:%1$s:Profile2&eventTime=1990-01-01T00:00:00Z/1990-01-01T02:00:00Z", defaultAuthority);
    private static final String profileRequestMultiTime3 = baseRequest + String.format("&service=sos&observedProperty=temperature,humidity&offering=Profile3&procedure=urn:ioos:station:%1$s:Profile3&eventTime=1990-01-01T00:00:00Z/1990-01-01T03:00:00Z", defaultAuthority);
    
    private static final String IncompleteMultiDimensionalMultipleProfiles = "resources/datasets/profile-Incomplete-MultiDimensional-MultipleProfiles-H.3.2/profile-Incomplete-MultiDimensional-MultipleProfiles-H.3.2.nc";
    
    private static final String OrthogonalMultiDimensionalMultipleProfiles = "resources/datasets/profile-Orthogonal-MultiDimensional-MultipleProfiles-H.3.1/profile-Orthogonal-MultiDimensional-MultipleProfiles-H.3.1.nc";
    
    private static final String OrthogonalSingleDimensionalSingleProfile = "resources/datasets/profile-Orthogonal-SingleDimensional-SingleProfile-H.3.3/profile-Orthogonal-SingleDimensional-SingleProfile-H.3.3.nc";
    
    private static final String IndexedRaggedMultipleProfiles = "resources/datasets/profile-Indexed-Ragged-MultipleProfiles-H.3.5/profile-Indexed-Ragged-MultipleProfiles-H.3.5.nc";
    private static final String profileRequestIndexed = baseRequest + String.format("&observedProperty=temperature,humidity&procedure=urn:ioos:station:%1$s:Profile1&offering=Profile1&eventTime=1990-01-01T01:00:00Z/1990-01-01T04:00:00Z", defaultAuthority);
    
    private static final String timeSeriesProfileRequestSingle = baseRequest + String.format("&observedProperty=temperature&procedure=urn:ioos:station:%1$s:Station1&offering=Station1&eventTime=1990-01-01T00:00:00Z", defaultAuthority);
    private static final String timeSeriesProfileRequestMulti = baseRequest + String.format("&observedProperty=temperature&procedure=urn:ioos:station:%1$s:Station1&offering=Station1&eventTime=1990-01-01T00:00:00Z/1990-01-01T02:00:00Z", defaultAuthority);
    private static final String timeSeriesProfileRequestMultiStation = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:station:%1$s:Station1,urn:ioos:station:%1$s:Station2&eventTime=1990-01-01T00:00:00Z/1990-01-01T05:00:00Z", defaultAuthority);

    private static final String timeSeriesProfileRequestMultiInvalidDates = baseRequest + String.format("&observedProperty=temperature&procedure=urn:ioos:station:%1$s:Station1&offering=Station1&eventTime=1990-04-01T00:00:00Z/1990-08-01T02:00:00Z", defaultAuthority);
    private static final String timeSeriesProfileRequest2 = baseRequest + String.format("&observedProperty=temperature&offering=Station1&procedure=urn:ioos:station:%1$s:Station1&eventTime=1990-01-01T00:00:00Z", defaultAuthority);
    private static final String timeSeriesTimeRequestT2 = baseRequest + String.format("&observedProperty=temperature&offering=Station1&procedure=urn:ioos:station:%1$s:Station1&eventTime=1990-01-01T02:00:00Z", defaultAuthority);
    private static final String timeSeriesTimeRequestT1 = baseRequest + String.format("&observedProperty=temperature&offering=Station1&procedure=urn:ioos:station:%1$s:Station1&eventTime=1990-01-01T00:00:00Z", defaultAuthority);
    private static final String timeSeriesProfileRequest3 = baseRequest + String.format("&observedProperty=temperature&offering=Station2&procedure=urn:ioos:station:%1$s:Station2&eventTime=1990-01-01T04:00:00Z", defaultAuthority);
    
    private static final String trajectoryIncompleteMultidimensionalMultipleTrajectories = "resources/datasets/trajectory-Incomplete-Multidimensional-MultipleTrajectories-H.4.1/trajectory-Incomplete-Multidimensional-MultipleTrajectories-H.4.1.nc";
    private static final String trajectoryIncompleteRequest1 = baseRequest + String.format("&observedProperty=temperature&offering=Trajectory1&procedure=urn:ioos:station:%1$s:Trajectory1&eventTime=1970-01-01T00:00:00Z/2012-07-11T00:00:00", defaultAuthority);
    
    private static final String trajectoryContiguousRaggedMultipleTrajectories = "resources/datasets/trajectory-Contiguous-Ragged-MultipleTrajectories-H.4.3/trajectory-Contiguous-Ragged-MultipleTrajectories-H.4.3.nc";
    private static final String trajectoryContiguousRequest1 = baseRequest + String.format("&observedProperty=temperature&procedure=urn:ioos:station:%1$s:Trajectory1&offering=Trajectory1&eventTime=1970-01-01T00:00:00Z/2012-07-11T00:00:00", defaultAuthority);
    
    private static final String trajectoryProfileMultidimensionalMultipleTrajectories = "resources/datasets/trajectoryProfile-Multidimensional-MultipleTrajectories-H.6.1/trajectoryProfile-Multidimensional-MultipleTrajectories-H.6.1.nc";
    private static final String sectionRequest1 = baseRequest + String.format("&observedProperty=salinity&procedure=urn:ioos:station:%1$s:Trajectory2&offering=Trajectory2&eventTime=1990-01-01T00:00:00Z", defaultAuthority);
    
    private static final String externalHawaiiStation = "resources/datasets/sura/wqbkn_2012_08_01.nc";
    private static final String externalHawaiiRequest1 = baseRequest + String.format("&observedProperty=temp&offering=WQBKN&procedure=urn:ioos:station:%1$s:WQBKN", defaultAuthority);
    
    private static final String watlevNOAANavdPre = "resources/datasets/sura/watlev_NOAA_NAVD_PRE.nc";
    private static final String watlevNOAANavdRequest = baseRequest + String.format("&observedProperty=watlev&offering=NOAA_8767961&procedure=urn:ioos:station:%1$s:NOAA_8767961", defaultAuthority);
    private static final String watlevNOAANavdRequestIoos = IoosSosRequest + String.format("&observedProperty=watlev&offering=NOAA_8767961&procedure=urn:ioos:station:%1$s:NOAA_8767961", defaultAuthority);
    
    // test some exceptions (using imeds8)
    private static final String testBadProcedure = baseRequest + String.format("&observedProperty=watlev&offering=network-all&procedure=urn:station:%1$s:CRMS_CS20-106", defaultAuthority);
    private static final String testBadOffering = baseRequest + String.format("&observedProperty=watlev&offering=badOffering&procedure=urn:ioos:station:%1$s:CRMS_CS20-106", defaultAuthority);
    private static final String testBadProcOffPair = baseRequest + String.format("&observedProperty=watlev&offering=CRMS20_106&procedure=urn:ioos:station:%1$s:CRMS20_15R", defaultAuthority);
    
    //network all time series
    private static final String networkAllTimeSeries1 = datasets + "timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2/timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2.nc";
    private static final String networkAllTimeSeries1Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTimeSeries2 = datasets + "timeSeries-Orthogonal-Multidimensional-MultipeStations-H.2.1/timeSeries-Orthogonal-Multidimensional-MultipeStations-H.2.1.nc";
    private static final String networkAllTimeSeries2Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    
    // network all trajectory
    private static final String networkAllTrajectory1 = datasets + "trajectory-Contiguous-Ragged-MultipleTrajectories-H.4.3/trajectory-Contiguous-Ragged-MultipleTrajectories-H.4.3.nc";
    private static final String networkAllTrajectory1Request1 = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTrajectory1Request2 = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all&eventTime=1970-01-01T00:00:00Z/2012-07-11T00:00:00", defaultAuthority);
    private static final String networkAllTrajectory1Request3 = baseRequest + String.format("&observedProperty=humidity&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTrajectory2 = datasets + "trajectory-Incomplete-Multidimensional-MultipleTrajectories-H.4.1/trajectory-Incomplete-Multidimensional-MultipleTrajectories-H.4.1.nc";
    private static final String networkAllTrajectory2Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTrajectory3 = datasets + "trajectory-Incomplete-Multidimensional-SingleTrajectory-H.4.2/trajectory-Incomplete-Multidimensional-SingleTrajectory-H.4.2.nc";
    private static final String networkAllTrajectory3Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTrajectory4 = datasets + "trajectory-Indexed-Ragged-MultipleTrajectories-H.4.4/trajectory-Indexed-Ragged-MultipleTrajectories-H.4.4.nc";
    private static final String networkAllTrajectory4Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    
    // network all time series profile
    private static final String networkAllTimeSeriesProfile1 = datasets + "timeSeriesProfile-Multidimensional-MultipeStations-H.5.1/timeSeriesProfile-Multidimensional-MultipeStations-H.5.1.nc";
    private static final String networkAllTimeSeriesProfile1Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all&eventTime=1990-01-01T04:00:00Z", defaultAuthority);
    private static final String networkAllTimeSeriesProfile2 = datasets + "timeSeriesProfile-Multidimensional-SingleStation-H.5.2/timeSeriesProfile-Multidimensional-SingleStation-H.5.2.nc";
    private static final String networkAllTimeSeriesProfile2Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTimeSeriesProfile3 = datasets + "timeSeriesProfile-Orthogonal-Multidimensional-MultipeStations-H.5.1/timeSeriesProfile-Orthogonal-Multidimensional-MultipeStations-H.5.1.nc";
    private static final String networkAllTimeSeriesProfile3Request = baseRequest + String.format("&observedProperty=station_info&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTimeSeriesProfile4 = datasets + "timeSeriesProfile-Ragged-MultipeStations-H.5.3/timeSeriesProfile-Ragged-MultipeStations-H.5.3.nc";
    private static final String networkAllTimeSeriesProfile4Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTimeSeriesProfile5 = datasets + "timeSeriesProfile-Ragged-SingleStation-H.5.3/timeSeriesProfile-Ragged-SingleStation-H.5.3.nc";
    private static final String networkAllTimeSeriesProfile5Request = baseRequest + String.format("&observedProperty=time&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    
    
    // network all profile
    private static final String networkAllProfile1 = "resources/datasets/profile-Orthogonal-MultiDimensional-MultipleProfiles-H.3.1/profile-Orthogonal-MultiDimensional-MultipleProfiles-H.3.1.nc";
    private static final String networkAllProfile1Request = baseRequest + String.format("&observedProperty=temperature,humidity&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllProfile2 = datasets + "profile-Contiguous-Ragged-MultipleProfiles-H.3.4/profile-Contiguous-Ragged-MultipleProfiles-H.3.4.nc";
    private static final String networkAllProfile2Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllProfile3 = datasets + "profile-Incomplete-MultiDimensional-MultipleProfiles-H.3.2/profile-Incomplete-MultiDimensional-MultipleProfiles-H.3.2.nc";
    private static final String networkAllProfile3Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllProfile4 = datasets + "profile-Indexed-Ragged-MultipleProfiles-H.3.5/profile-Indexed-Ragged-MultipleProfiles-H.3.5.nc";
    private static final String networkAllProfile4Request = baseRequest + String.format("&observedProperty=temperature&offering=network-allprocedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllProfile5 = datasets + "profile-Orthogonal-SingleDimensional-SingleProfile-H.3.3/profile-Orthogonal-SingleDimensional-SingleProfile-H.3.3.nc";
    private static final String networkAllProfile5Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    
    // network all section
    private static final String networkAllTrajectoryProfile1 = "resources/datasets/trajectoryProfile-Multidimensional-MultipleTrajectories-H.6.1/trajectoryProfile-Multidimensional-MultipleTrajectories-H.6.1.nc";
    private static final String networkAllTrajectoryProfile1Request = baseRequest + String.format("&observedProperty=salinity&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTrajectoryProfile2 = datasets + "trajectoryProfile-Multidimensional-SingleTrajectory-H.6.2/trajectoryProfile-Multidimensional-SingleTrajectory-H.6.2.nc";
    private static final String networkAllTrajectoryProfile2Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String networkAllTrajectoryProfile3 = datasets + "trajectoryProfile-Ragged-MultipleTrajectories-H.6.3/trajectoryProfile-Ragged-MultipleTrajectories-H.6.3.nc";
    private static final String networkAllTrajectoryProfile3Request = baseRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    
    // IoosSos1.0 response format
    private static final String kachemakBay = datasets + "nodc/KachemakBay.nc";
    private static final String seaMapPoint = datasets + "SEAMAPdataCStructs7.nc";
    private static final String seaMapPointRequest = IoosSosRequest + String.format("&observedProperty=Air_Temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String timeSeriesIncompIoosSosRequest1 = IoosSosRequest + String.format("&observedProperty=temperature,humidity&offering=network-all&eventTime=1990-01-01T00:00:00Z&procedure=urn:ioos:station:%1$s:Station-1,urn:ioos:station:%1$s:Station-3", defaultAuthority);
    private static final String pointIoosSosRequest1 = IoosSosRequest + String.format("&observedProperty=fluorene&offering=network-all&eventTime=1990-01-01T00:00:48Z&procedure=urn:ioos:network:%1$s:all", defaultAuthority);
    private static final String bodegaMarineLabBuoy = datasets + "nodc/BodegaMarineLabBuoy.nc";
    private static final String bodegaIoosRequest1 = IoosSosRequest + String.format("&observedProperty=density,temperature,salinity&offering=Cordell Bank Buoy&eventTime=140835-01-26T00:00:00Z/140835-12-31T23:59:59&procedure=urn:ioos:station:%1$s:Cordell Bank Buoy", defaultAuthority);
    // TODO: below are currently unsupported by IOOS response format, once they are
    // tests will need to be re-written for them to reflect that
    
    // uses networkAllTrajectory1 dataset
    private static final String ioosTrajectoryRequest = IoosSosRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:station:%1$s:Trajectory1,urn:ioos:station:%1$s:Trajectory2,urn:ioos:station:%1$s:Trajectory4", defaultAuthority);
    // uses networkAllTimeSeriesProfile1 dataset
    private static final String ioosTimeSeriesProfileRequest = IoosSosRequest + String.format("&observedProperty=temperature&offering=network-all&procedure=urn:ioos:network:%1$s:all&eventTime=1990-01-01T04:00:00Z", defaultAuthority);
    // uses networkAllProfile1 dataset
    private static final String ioosProfileRequest = IoosSosRequest + String.format("&observedProperty=temperature,humidity&offering=network-all&procedure=urn:ioos:station:%1$s:Profile10,urn:ioos:station:%1$s:Profile42", defaultAuthority);
    // uses networkAllTrajectoryProfile1 dataset
    private static final String ioosTrajectoryProfileRequest = IoosSosRequest + String.format("&observedProperty=salinity&offering=network-all&procedure=urn:ioos:station:%1$s:Trajectory0,urn:ioos:station:%1$s:Trajectory5", defaultAuthority);
    
    @BeforeClass
    public static void SetupEnviron() {
        // early return if the vars we are setting are already set
        if (base != null) {
            return;
        }
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        String container = "testConfiguration";
        InputStream templateInputStream = null;
        try {
            File configFile = new File("resources/tests_config.xml");
            templateInputStream = new FileInputStream(configFile);
            Document configDoc = XMLDomUtils.getTemplateDom(templateInputStream);
            base = XMLDomUtils.getNodeValue(configDoc, container, "outputDirectory");
            // add get_obs to output dir
            base += "get_obs/";
            exampleOutputDir = XMLDomUtils.getNodeValue(configDoc, container, "outputDirectory");
            // add examples to output dir
            exampleOutputDir += "examples/";
        } catch (FileNotFoundException fnfex) {
            System.out.println(fnfex.getMessage());
        } finally {
            if (templateInputStream != null) {
                try {
                    templateInputStream.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        
        File file = new File(base);
        file.mkdirs();
        
        file = new File(exampleOutputDir);
        file.mkdirs();
    }
    
    /******************************
     * Private, Non-Junit Methods *
     ******************************/
    
    private static void dataAvailableInOutputFile(Writer write) {
        assertTrue("error no values in output", write.toString().contains("<swe:values>") || write.toString().contains("<swe2:values>"));
        assertFalse("error no values: ERROR string in values", write.toString().contains("<swe:values>ERROR!</swe:values>"));
    }
    
    private static void fileWriter(String base, String fileName, Writer write) {
        try {
            File file = new File(base + fileName);
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(write.toString());
            output.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private static void writeOutput(HashMap<String, Object> outMap, Writer writer) throws IOException {
        OutputFormatter output = (OutputFormatter)outMap.get("outputHandler");
        assertNotNull("got null output", output);
        output.writeOutput(writer);
    }
    
    private static String getCurrentMethod() {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i=0; i<ste.length; i++) {
            if (ste[i].getMethodName().contains(("test")))
                return ste[i].getMethodName();
        }
        return "could not find test name";
    }
    
    @Test
    public void testEnhanceIMEDS1() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds1);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds1Req, imeds1),write);
            write.flush();
            write.close();String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMDEDS4() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds4);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds4Req, imeds4),write);
            write.flush();
            write.close();
            String fileName = "imeds4.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS5() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds5);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds5Req, imeds5),write);
            write.flush();
            write.close();
            String fileName = "imeds5.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS6() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds6);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds6Req, imeds6),write);
            write.flush();
            write.close();
            String fileName = "imeds6.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosEnhanceIMEDS6() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds6);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds6IoosReq, imeds6),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            
            int lastMemberIdx = write.toString().lastIndexOf("</om:member>");
            int metaIdx = write.toString().lastIndexOf("<gml:metaDataProperty");
            
            assertTrue("invalid meta data property order",lastMemberIdx>metaIdx);
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS7() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds7);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds7Req, imeds7),write);
            write.flush();
            write.close();
            String fileName = "imeds7.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS7IOOS() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds7);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds7ReqIoos, imeds7),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            //must contain dimension in CompositePhenomenon
            assertTrue("Invalid CompositePhenomenon", write.toString().contains("<swe:CompositePhenomenon dimension=\"1\" gml:id=\"observedProperties\">"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS8() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds8);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds8Req, imeds8),write);
            write.flush();
            write.close();
            String fileName = "imeds8.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS9() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds9);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds9Req, imeds9),write);
            write.flush();
            write.close();
            String fileName = "imeds9.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS10() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds10);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds10Req, imeds10),write);
            write.flush();
            write.close();
            String fileName = "imeds10.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS11() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds11);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds11Req, imeds11),write);
            write.flush();
            write.close();
            String fileName = "imeds11.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS12() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds12);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds12Req, imeds12),write);
            write.flush();
            write.close();
            String fileName = "imeds12.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS13() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds13);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds13Req, imeds13),write);
            write.flush();
            write.close();
            String fileName = "imeds13.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS14() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds14);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds13Req, imeds14),write);
            write.flush();
            write.close();
            String fileName = "imeds14.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            
            
            int idxUnit = write.toString().indexOf("<swe:uom code=\"m\"/>");
            int idxNil = write.toString().indexOf("<swe:nilValues>");
            
            assertTrue("invalid unit/nill order",idxUnit>idxNil);
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testEnhanceIMEDS15() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds15);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, imeds15Req, imeds15),write);
            write.flush();
            write.close();
            String fileName = "imeds15.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTimeSeries3Stations() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsIncompleteMultiDimensionalMultipleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesIncompleteMultiStationx3, tsIncompleteMultiDimensionalMultipleStations),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);

            //check dates
            assertTrue("Time1 entered", write.toString().contains("1990-01-01T00:00:00Z,"));
            assertTrue("Time2 entered", write.toString().contains("1990-01-01T07:00:00Z,"));
            assertTrue("Time3 entered", write.toString().contains("1990-01-01T06:00:00Z,"));
            //check data
            assertTrue("Station8 Data T0", write.toString().contains("1990-01-01T00:00:00Z,32.0"));
            assertTrue("Station8 Data T2", write.toString().contains("1990-01-01T02:00:00Z,36.0"));
            assertTrue("Station9 Data T0", write.toString().contains("1990-01-01T00:00:00Z,37.0"));
            assertTrue("Station9 Data T2", write.toString().contains("1990-01-01T02:00:00Z,6.0"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTimeSeriesCreateDataStruct() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsIncompleteMultiDimensionalMultipleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesIncompleteMultiStation, tsIncompleteMultiDimensionalMultipleStations),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);

            //check dates
            assertTrue("Time1 entered", write.toString().contains("1990-01-01T00:00:00Z,"));
            assertTrue("Time2 entered", write.toString().contains("1990-01-01T07:00:00Z,"));
            assertTrue("Time3 entered", write.toString().contains("1990-01-01T06:00:00Z,"));
            //check data
            assertTrue("Station8 Data T0", write.toString().contains("1990-01-01T00:00:00Z,32.0"));
            assertTrue("Station8 Data T2", write.toString().contains("1990-01-01T02:00:00Z,36.0"));
            assertTrue("Station9 Data T0", write.toString().contains("1990-01-01T00:00:00Z,37.0"));
            assertTrue("Station9 Data T2", write.toString().contains("1990-01-01T02:00:00Z,6.0"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTimeSeriesIncompleteDataMultiTimeInvalid() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsIncompleteMultiDimensionalMultipleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesIncompleteMultiInvalid, tsIncompleteMultiDimensionalMultipleStations),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            //dataAvailableInOutputFile(write);

            assertFalse(write.toString().contains("1990-01-01T00:00:00Z,"));
            assertFalse(write.toString().contains("1990-01-01T11:00:00Z,"));
            assertFalse(write.toString().contains("1990-01-01T12:00:00Z,"));
            assertFalse(write.toString().contains("1990-01-01T13:00:00Z,"));
            assertFalse(write.toString().contains("1990-01-01T14:00:00Z,"));
            assertFalse(write.toString().contains("1990-01-01T17:00:00Z,"));
            assertFalse(write.toString().contains("1990-01-01T18:00:00Z,"));

            assertTrue(write.toString().contains("1990-01-01T05:00:00Z,"));
            assertTrue(write.toString().contains("1990-01-01T06:00:00Z,"));

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTimeSeriesIncompleteDataMultiTime() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsIncompleteMultiDimensionalMultipleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesIncompleteMulti, tsIncompleteMultiDimensionalMultipleStations),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);

            assertTrue("Time1 not entered", write.toString().contains("1990-01-01T00:00:00Z,"));
            assertTrue("Time2 not entered", write.toString().contains("1990-01-01T10:00:00Z,"));

            assertFalse("Time3 entered when not!", write.toString().contains("1990-01-01T11:00:00Z,"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTimeSeriesIncomplete() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsIncompleteMultiDimensionalMultipleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesIncomplete, tsIncompleteMultiDimensionalMultipleStations),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTimeSeriesIncompleteTime() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsIncompleteMultiDimensionalMultipleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesIncompleteWithTime, tsIncompleteMultiDimensionalMultipleStations),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTimeSeriesOrthogonalMultidimenstionalMultipleStations() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsOrthogonalMultidimenstionalMultipleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriestOrth, tsOrthogonalMultidimenstionalMultipleStations),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiTimeSeriesProfileRequest() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(RaggedMultiConventions);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesProfileRequestMulti, RaggedMultiConventions),write);
            write.flush();
            write.close();
            String fileName = "RaggedMultiConventionsMultiTime.xml";
            fileWriter(base, fileName, write);
            // write as an example
            fileWriter(exampleOutputDir, "GetObservation-TimeSeriesProfile-om1.0.0.xml", write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"height\">"));

            assertTrue("data missing", write.toString().contains("1990-01-01T00:00:00Z,6.7"));
            assertTrue("data missing", write.toString().contains("1990-01-01T00:00:00Z,6.9"));


            assertTrue("data missing", write.toString().contains("1990-01-01T02:00:00Z,6.7"));
            assertTrue("data missing", write.toString().contains("1990-01-01T02:00:00Z,7.0"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiTimeSeriesProfileRequestInvalidDates() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(RaggedMultiConventions);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesProfileRequestMultiInvalidDates, RaggedMultiConventions),write);
            write.flush();
            write.close();
            String fileName = "RaggedMultiConventionsMultiTimeInvalidDates.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in outptu", write.toString().contains("Exception"));
            //dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"height\">"));

            assertFalse("data missing", write.toString().contains("1990-01-01T00:00:00Z,6.7,0.5"));
            assertFalse("data missing", write.toString().contains("1990-01-01T00:00:00Z,6.9,1.5"));


            assertFalse("data missing", write.toString().contains("1990-01-01T02:00:00Z,6.7,0.5"));
            assertFalse("data missing", write.toString().contains("1990-01-01T02:00:00Z,7.0,1.5"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testenhanceMultiRaggedDataset() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(RaggedMultiConventions);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesProfileRequestSingle, RaggedMultiConventions),write);
            write.flush();
            write.close();
            String fileName = "RaggedMultiConventions.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"height\">"));

            assertTrue("data missing", write.toString().contains("1990-01-01T00:00:00Z,6.7"));
            assertTrue("data missing", write.toString().contains("1990-01-01T00:00:00Z,6.9"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiDimensionalSingleStations() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(MultiDimensionalSingleStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesProfileRequest2, MultiDimensionalSingleStations),write);
            write.flush();
            write.close();
            String fileName = "MultiDimensionalSingleStations.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"alt\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiDimensionalSingleStationsTimeTestT2() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(RaggedMultiConventions);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesTimeRequestT2, RaggedMultiConventions),write);
            write.flush();
            write.close();
            String fileName = "MultiDimensionalSingleStationsT2.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"height\">"));

            assertFalse("too much data", write.toString().contains("1990-01-01T00:00:00Z,6.7"));
            assertFalse("too much data", write.toString().contains("1990-01-01T00:00:00Z,6.9"));


            assertTrue("data missing", write.toString().contains("1990-01-01T02:00:00Z,6.7"));
            assertTrue("data missing", write.toString().contains("1990-01-01T02:00:00Z,7.0"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiDimensionalSingleStationsTimeTestT1() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(RaggedMultiConventions);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesTimeRequestT1, RaggedMultiConventions),write);
            write.flush();
            write.close();
            String fileName = "MultiDimensionalSingleStationsT1.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"height\">"));

            assertTrue("too much data", write.toString().contains("1990-01-01T00:00:00Z,6.7"));
            assertTrue("too much data", write.toString().contains("1990-01-01T00:00:00Z,6.9"));


            assertFalse("data missing", write.toString().contains("1990-01-01T02:00:00Z,6.7"));
            assertFalse("data missing", write.toString().contains("1990-01-01T02:00:00Z,7.0"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiDimensionalMultiStations() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(MultiDimensionalMultiStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesProfileRequest2, MultiDimensionalMultiStations),write);
            write.flush();
            write.close();
            String fileName = "MultiDimensionalMultiStations.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"alt\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiDimensionalMultiStationsStation2() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(MultiDimensionalMultiStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesProfileRequest3, MultiDimensionalMultiStations),write);
            write.flush();
            write.close();
            String fileName = "MultiDimensionalMultiStations2.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"alt\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testMultiDimensionalMultiStation() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(MultiDimensionalMultiStations);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesProfileRequestMultiStation, MultiDimensionalMultiStations),write);
            write.flush();
            write.close();
            String fileName = "MultiDimensionalMultiStationRequest.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"alt\">"));

            assertTrue("Station 1 T0A0", write.toString().contains("1990-01-01T00:00:00Z,0.0"));
            assertTrue("Station 1 T0A1", write.toString().contains("1990-01-01T00:00:00Z,0.7"));
            assertTrue("Station 1 T0A2", write.toString().contains("1990-01-01T00:00:00Z,1.6"));
            //
            assertTrue("Station 1 T3A0", write.toString().contains("1990-01-01T03:00:00Z,9.0"));
            assertTrue("Station 1 T3A1", write.toString().contains("1990-01-01T03:00:00Z,9.3"));
            assertTrue("Station 1 T3A2", write.toString().contains("1990-01-01T03:00:00Z,11.4"));
            assertTrue("Station 1 T3A3", write.toString().contains("1990-01-01T03:00:00Z,11.8"));

            assertTrue("Station 2 T0A0", write.toString().contains("1990-01-01T04:00:00Z,12.0"));
            assertTrue("Station 2 T0A1", write.toString().contains("1990-01-01T04:00:00Z,13.5"));
            assertTrue("Station 2 T0A2", write.toString().contains("1990-01-01T04:00:00Z,14.0"));
            assertTrue("Station 2 T3A0", write.toString().contains("1990-01-01T04:00:00Z,14.9"));
            //
            assertTrue("Station 2 T3A1", write.toString().contains("1990-01-01T05:00:00Z,15.0"));
            assertTrue("Station 2 T3A2", write.toString().contains("1990-01-01T05:00:00Z,16.3"));
            assertTrue("Station 2 T3A3", write.toString().contains("1990-01-01T05:00:00Z,17.8"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testContiguousRaggedMultipleProfilesMultiTime3() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(ContiguousRaggedMultipleProfiles);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequestMultiTime3, ContiguousRaggedMultipleProfiles), write);
            write.flush();
            write.close();
            String fileName = "ContiguousRaggedMultipleProfilesMultiTime3.xml";
            fileWriter(base, fileName, write);
            //dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertFalse(write.toString().contains("Exception"));
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
            assertTrue("data missing - feature of interest", write.toString().contains("<om:featureOfInterest xlink:href=\"urn:ioos:station:" + defaultAuthority + ":Profile3\"/>"));
            assertFalse("bad data included - time stamp", write.toString().contains("1990-01-01T01:00:00Z,"));
            assertFalse("bad data included - time stamp", write.toString().contains("1990-01-01T02:00:00Z,"));
            // write as an example
            fileWriter(exampleOutputDir, "GetObservation-Profile-om1.0.0.xml", write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testContiguousRaggedMultipleProfilesMultiTime() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(ContiguousRaggedMultipleProfiles);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequestMultiTime, ContiguousRaggedMultipleProfiles),write);
            write.flush();
            write.close();
            assertFalse(write.toString().contains("Exception"));
            String fileName = "ContiguousRaggedMultipleProfilesMultiTime.xml";
            fileWriter(base, fileName, write);
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
            assertFalse("invalid foi", write.toString().contains("<om:featureOfInterest xlink:href=\"urn:ioos:station:" + defaultAuthority + ":PROFILE_0\"/>"));
            assertFalse("invalid foi", write.toString().contains("<om:featureOfInterest xlink:href=\"urn:ioos:station:" + defaultAuthority + ":PROFILE_3\"/>"));
            assertTrue("foi missing", write.toString().contains("<om:featureOfInterest xlink:href=\"urn:ioos:station:" + defaultAuthority + ":Profile1\"/>"));
            assertTrue("foi missing", write.toString().contains("<om:featureOfInterest xlink:href=\"urn:ioos:station:" + defaultAuthority + ":Profile2\"/>"));
            assertTrue("data missing", write.toString().contains("1990-01-01T01:00:00Z,"));
            assertTrue("data missing", write.toString().contains("1990-01-01T02:00:00Z,"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testContiguousRaggedMultipleProfilesMultiTime2() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(ContiguousRaggedMultipleProfiles);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequestMultiTime2, ContiguousRaggedMultipleProfiles),write);
            write.flush();
            write.close();
            String fileName = "ContiguousRaggedMultipleProfilesMultiTime2.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
            assertFalse("data missing", write.toString().contains("1990-01-01T00:00:00Z,"));
            assertTrue("data missing", write.toString().contains("1990-01-01T01:00:00Z,"));
            assertTrue("data missing", write.toString().contains("1990-01-01T02:00:00Z,"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testContiguousRaggedMultipleProfiles() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(ContiguousRaggedMultipleProfiles);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequest, ContiguousRaggedMultipleProfiles),write);
            write.flush();
            write.close();
            String fileName = "ContiguousRaggedMultipleProfiles.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIncompleteMultiDimensionalMultipleProfiles() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(IncompleteMultiDimensionalMultipleProfiles);

            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequest, IncompleteMultiDimensionalMultipleProfiles),write);
            write.flush();
            write.close();
            String fileName = "IncompleteMultiDimensionalMultipleProfiles.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"alt\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIndexedRaggedMultipleProfiles() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(IndexedRaggedMultipleProfiles);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequestIndexed, IndexedRaggedMultipleProfiles),write);
            write.flush();
            write.close();
            String fileName = "IndexedRaggedMultipleProfiles.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testOrthogonalMultiDimensionalMultipleProfiles() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(OrthogonalMultiDimensionalMultipleProfiles);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequest, OrthogonalMultiDimensionalMultipleProfiles),write);
            write.flush();
            write.close();
            String fileName = "OrthogonalMultiDimensionalMultipleProfiles.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testOrthogonalSingleDimensionalSingleProfile() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(OrthogonalSingleDimensionalSingleProfile);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, profileRequest, OrthogonalSingleDimensionalSingleProfile),write);
            write.flush();
            write.close();
            String fileName = "OrthogonalSingleDimensionalSingleProfile.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTrajectoryContiguousRaggedMultipleTrajectories() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(trajectoryContiguousRaggedMultipleTrajectories);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, trajectoryContiguousRequest1, trajectoryContiguousRaggedMultipleTrajectories),write);
            write.flush();
            write.close();
            String fileName = "trajectoryContiguousRaggedMultipleTrajectories.xml";
            fileWriter(base, fileName, write);
            // write as an example
            fileWriter(exampleOutputDir, "GetObservation-Trajectory-om1.0.0.xml", write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTrajectoryIncompleteMultidimensionalMultipleTrajectories() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(trajectoryIncompleteMultidimensionalMultipleTrajectories);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, trajectoryIncompleteRequest1, trajectoryIncompleteMultidimensionalMultipleTrajectories),write);
            write.flush();
            write.close();
            String fileName = "trajectoryIncompleteMultidimensionalMultipleTrajectories_request1.xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testTrajectoryProfileMultidimensionalMultipleTrajectories1() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(trajectoryProfileMultidimensionalMultipleTrajectories);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, sectionRequest1, trajectoryProfileMultidimensionalMultipleTrajectories),write);
            write.flush();
            write.close();
            String fileName = "trajectoryProfileMultidimensionalMultipleTrajectories_request1.xml";
            fileWriter(base, fileName, write);
            // write as an example
            fileWriter(exampleOutputDir, "GetObservation-Section-om1.0.0.xml", write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            //check depth was entered auto
//            assertTrue("depth not added", write.toString().contains("<swe:field name=\"z\">"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testExternalHawaiiGetObs() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(externalHawaiiStation);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, externalHawaiiRequest1, externalHawaiiStation),write);
            write.flush();
            write.close();
            String fileName = "external_Hawaii_request1.xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllIncompleteTimeSeries() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeries1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTimeSeries1Request, networkAllTimeSeries1),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllOrthogonalTimeSeries() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeries2);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTimeSeries2Request, networkAllTimeSeries2),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryContiguousRagged() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectory1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectory1Request1, networkAllTrajectory1),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryContiguousRaggedTimeConstraint() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectory1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectory1Request2, networkAllTrajectory1),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryContiguousRaggedHumidity() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectory1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectory1Request3, networkAllTrajectory1),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryIncompleteMultiple() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectory2);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectory2Request, networkAllTrajectory2),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    // dataset has some issues which prevent it from being successfully read
//    @Ignore
    @Test
    public void testNetworkAllTrajectoryIncompleteSingle() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectory3);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectory3Request, networkAllTrajectory3),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryIndexedRagged() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectory4);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectory4Request, networkAllTrajectory4),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTimeSeriesProfileMultiDimMultiStation() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeriesProfile1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTimeSeriesProfile1Request, networkAllTimeSeriesProfile1),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTimeSeriesProfileMultiDimSingleStation() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeriesProfile2);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTimeSeriesProfile2Request, networkAllTimeSeriesProfile2),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    // no valid observed properties
    @Ignore
    @Test
    public void testNetworkAllTimeSeriesProfileOrthoMultiDimMultiStation() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeriesProfile3);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTimeSeriesProfile3Request, networkAllTimeSeriesProfile3),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTimeSeriesProfileRaggedMultiStations() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeriesProfile4);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTimeSeriesProfile4Request, networkAllTimeSeriesProfile4),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTimeSeriesProfileRaggedSingleStation() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeriesProfile5);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTimeSeriesProfile5Request, networkAllTimeSeriesProfile5),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllProfileOrthoMultiDimMultiProfile() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllProfile1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllProfile1Request, networkAllProfile1),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllProfileContRaggedMultiProfiles() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllProfile2);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllProfile2Request, networkAllProfile2),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllProfileIncompleteMultiDimMultiProfiles() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllProfile3);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllProfile3Request, networkAllProfile3),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Ignore
    @Test
    public void testNetworkAllProfileIndexedRaggedMultiProfiles() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllProfile4);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllProfile4Request, networkAllProfile4),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllProfileSingleDimSingleProfile() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllProfile5);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllProfile5Request, networkAllProfile5),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryProfileMultiDimMultiTrajectories() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectoryProfile1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectoryProfile1Request, networkAllTrajectoryProfile1),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryProfileMultiDimSingleTrajectory() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectoryProfile2);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectoryProfile2Request, networkAllTrajectoryProfile2),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testNetworkAllTrajectoryProfileRaggedMultiTrajectories() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectoryProfile3);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, networkAllTrajectoryProfile3Request, networkAllTrajectoryProfile3),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // write as an example
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testWatlevNOAANAVDPRE() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(watlevNOAANavdPre);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, watlevNOAANavdRequest, watlevNOAANavdPre),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            // write as an example
            fileWriter(exampleOutputDir, "GetObservation-TimeSeries-om1.0.0.xml", write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testExampleTimeSeriesIoos() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(watlevNOAANavdPre);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, watlevNOAANavdRequestIoos, watlevNOAANavdPre),write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
            dataAvailableInOutputFile(write);
            // write as an example
            fileWriter(exampleOutputDir, "GetObservation-TimeSeries-om1.0.0-IOOS1.0.xml", write);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosSosTimeSeries() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(tsIncompleteMultiDimensionalMultipleStations);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, timeSeriesIncompIoosSosRequest1, tsIncompleteMultiDimensionalMultipleStations), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosSosPointKachemak() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(kachemakBay);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, pointIoosSosRequest1, kachemakBay), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            // point
            assertTrue("no exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosBodegaMarineLabBuoy() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(bodegaMarineLabBuoy);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, bodegaIoosRequest1, bodegaMarineLabBuoy), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertFalse("exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosTrajectory() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectory1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, ioosTrajectoryRequest, networkAllTrajectory1), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosProfile() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllProfile1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, ioosProfileRequest, networkAllProfile1), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosTimeSeriesProfile() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTimeSeriesProfile1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, ioosTimeSeriesProfileRequest, networkAllTimeSeriesProfile1), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosTrajectoryProfile() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(networkAllTrajectoryProfile1);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, ioosTrajectoryProfileRequest, networkAllTrajectoryProfile1), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testIoosSeaMapPoint() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(seaMapPoint);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, seaMapPointRequest, seaMapPoint), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testInvalidRequestBadOffering() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds8);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, testBadOffering, imeds8), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
            assertTrue("exception unrelated to bad offering", write.toString().contains("offer"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testInvalidRequestBadProcedure() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds8);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, testBadProcedure, imeds8), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
            assertTrue("exception not related to invalid procedure", write.toString().contains("procedure"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    @Test
    public void testInvalidRequestProcedureNotInOffering() {
        System.out.println("\n------" + getCurrentMethod() + "------");
        
        try {
            NetcdfDataset dataset = NetcdfDataset.openDataset(imeds8);
            Parser md = new Parser();
            Writer write = new CharArrayWriter();
            writeOutput(md.enhanceGETRequest(dataset, testBadProcOffPair, imeds8), write);
            write.flush();
            write.close();
            String fileName = getCurrentMethod() + ".xml";
            fileWriter(base, fileName, write);
            assertTrue("no exception in output", write.toString().contains("Exception"));
            assertTrue("exception not related to procedure and offering", write.toString().contains("Procedure") && write.toString().contains("offering"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("------END " + getCurrentMethod() + "------");
        }
    }
    
    
    
//    @Test
//    public void testInsertClassNameHere() {
//        System.out.println("\n------" + getCurrentMethod() + "------");
//        
//        try {
//            
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        } finally {
//            System.out.println("------END " + getCurrentMethod() + "------");
//        }
//    }
}
