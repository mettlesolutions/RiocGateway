package com.mettles.rioc.core;

import com.mettles.rioc.*;
import com.mettles.rioc.BHTBeginningOfHierarchicalTransaction;
import com.mettles.rioc.HLRequesterLevel2000B;
import com.mettles.rioc.HLSubscriberLevel2000C;
import com.mettles.rioc.HLUtilizationManagementOrganizationUMOLevel2000A;
import com.mettles.rioc.Loop2000A;
import com.mettles.rioc.Loop2000B;
import com.mettles.rioc.Loop2000C;
import com.mettles.rioc.Loop2010A;
import com.mettles.rioc.Loop2010B;
import com.mettles.rioc.N3RequesterAddress2010B;
import com.mettles.rioc.N4RequesterCityStateZIPCode2010B;
import com.mettles.rioc.NM1RequesterName2010B;
import com.mettles.rioc.NM1UtilizationManagementOrganizationUMOName2010A;
import com.mettles.rioc.PERRequesterContactInformation2010B;
import com.mettles.rioc.STTransactionSetHeader;
import com.mettles.rioc.X12005010X217278A1;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import gov.cms.esmd.schemas.v2.serviceregistration.ServiceRegistrationRequest;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;


import org.w3c.dom.*;
import javax.xml.parsers.*;


//import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.databene.commons.xml.XMLUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

@Component(PDFReadWriter.NAME)
public class PDFReadWriter {
    public static final String NAME = "rioc_PDFReadWriter";
    private static String  XML2EDI = "";

    public Loop2010C SetSubscriberInfo(){
        Loop2010C subNM1Loop = new Loop2010C();
        NM1SubscriberName2010C subNM1Segment = new NM1SubscriberName2010C();
        subNM1Segment.setNM101EntityIdentifierCode("IL");
        subNM1Segment.setNM102EntityTypeQualifier("1");
        subNM1Segment.setNM103SubscriberLastName("Mirmovich"); // replace with subscriber last name
        subNM1Segment.setNM104SubscriberFirstName("Mikhail"); // replace with subscriber first name
        subNM1Segment.setNM105SubscriberMiddleNameOrInitial(""); // replace with sub middle name
        subNM1Segment.setNM106SubscriberNamePrefix(""); // replace with sub name prefix
        subNM1Segment.setNM107SubscriberNameSuffix(""); // replace with sub name suffix
        subNM1Segment.setNM108IdentificationCodeQualifier("MI");
        subNM1Segment.setNM109SubscriberPrimaryIdentifier("524897298M");// replace with Sub ID
        subNM1Loop.setNM1SubscriberName2010C(subNM1Segment);
        N3SubscriberAddress2010C subAddress = new N3SubscriberAddress2010C();
        subAddress.setN301SubscriberAddressLine("1763 Pitkin Cir"); // replace with sub addr line 1
        subAddress.setN302SubscriberAddressLine(""); // replace with sub addr line 2
        subNM1Loop.setN3SubscriberAddress2010C(subAddress);
        N4SubscriberCityStateZIPCode2010C subcitystate = new N4SubscriberCityStateZIPCode2010C();
        subcitystate.setN401SubscriberCityName("Aurora"); // replace with sub city name
        subcitystate.setN402SubscriberStateCode("CO");  // replace with sub state code
        subcitystate.setN403SubscriberPostalZoneOrZIPCode("80017"); // replace with sub zip cpde
        subNM1Loop.setN4SubscriberCityStateZIPCode2010C(subcitystate);
        DMGSubscriberDemographicInformation2010C subperinfo = new DMGSubscriberDemographicInformation2010C();
        subNM1Loop.setDMGSubscriberDemographicInformation2010C(subperinfo);
        subperinfo.setDMG01DateTimePeriodFormatQualifier("D8");
        subperinfo.setDMG02SubscriberBirthDate("19380811"); // replace with sub DOB
        subperinfo.setDMG03SubscriberGenderCode("M"); // replace with sub gender
        subNM1Loop.setDMGSubscriberDemographicInformation2010C(subperinfo);
        return subNM1Loop;
    }
    public Loop2000F setServiceLevelInfo(String ProcedureCode, String Quantity, String hiearachialIDNumber, String parentID, boolean bSV1,String pwk){
        Loop2000F loop2000f = new Loop2000F();
        HLServiceLevel2000F hlServiceLevel2000F = new HLServiceLevel2000F();
        hlServiceLevel2000F.setHL01HierarchicalIDNumber(hiearachialIDNumber);
        hlServiceLevel2000F.setHL02HierarchicalParentIDNumber(parentID);
        hlServiceLevel2000F.setHL03HierarchicalLevelCode("SS");
        hlServiceLevel2000F.setHL04HierarchicalChildCode("0");
        loop2000f.setHLServiceLevel2000F(hlServiceLevel2000F);
        if(bSV1) {
            SV1ProfessionalService2000F sv1ProfessionalService2000F = new SV1ProfessionalService2000F();
            SV1ProfessionalService2000F.SV101CompositeMedicalProcedureIdentifier2000F sv1procedureinfo = new  SV1ProfessionalService2000F.SV101CompositeMedicalProcedureIdentifier2000F();
            sv1procedureinfo.setSV10101ProductOrServiceIDQualifier("HC");
            sv1procedureinfo.setSV10102ProcedureCode(ProcedureCode);
            sv1ProfessionalService2000F.setSV101CompositeMedicalProcedureIdentifier2000F(sv1procedureinfo);
            sv1ProfessionalService2000F.setSV102ServiceLineAmount("");
            sv1ProfessionalService2000F.setSV103UnitOrBasisForMeasurementCode("UN");
            sv1ProfessionalService2000F.setSV104ServiceUnitCount(Quantity);
            loop2000f.setSV1ProfessionalService2000F(sv1ProfessionalService2000F);
        }
        else {
            SV2InstitutionalServiceLine2000F sv2InstitutionalServiceLine2000F = new SV2InstitutionalServiceLine2000F();
            sv2InstitutionalServiceLine2000F.setSV201ServiceLineRevenueCode(""); //replace with revenue code
            SV2InstitutionalServiceLine2000F.SV202CompositeMedicalProcedureIdentifier2000F sv202CompositeMedicalProcedureIdentifier2000F = new SV2InstitutionalServiceLine2000F.SV202CompositeMedicalProcedureIdentifier2000F();
            sv202CompositeMedicalProcedureIdentifier2000F.setSV20201ProductOrServiceIDQualifier("HC");
            sv202CompositeMedicalProcedureIdentifier2000F.setSV20202ProcedureCode(ProcedureCode);
            sv2InstitutionalServiceLine2000F.setSV202CompositeMedicalProcedureIdentifier2000F(sv202CompositeMedicalProcedureIdentifier2000F);
            sv2InstitutionalServiceLine2000F.setSV203ServiceLineAmount("");
            sv2InstitutionalServiceLine2000F.setSV204UnitOrBasisForMeasurementCode("UN");
            sv2InstitutionalServiceLine2000F.setSV205ServiceUnitCount(Quantity);
            loop2000f.setSV2InstitutionalServiceLine2000F(sv2InstitutionalServiceLine2000F);
        }
        PWKAdditionalServiceInformation2000F pwkinfo = new PWKAdditionalServiceInformation2000F();
        pwkinfo.setPWK01AttachmentReportTypeCode("77"); //replace with attachment report type code
        pwkinfo.setPWK02ReportTransmissionCode("FX"); // replace with transmission code
        pwkinfo.setPWK03("");
        pwkinfo.setPWK04("");
        pwkinfo.setPWK05IdentificationCodeQualifier("AC");
        pwkinfo.setPWK06AttachmentControlNumber(pwk);
        loop2000f.getPWKAdditionalServiceInformation2000F().add(pwkinfo);

        return loop2000f;
    }
    public Loop2000E setPatientEventLevelInfo(){
        Loop2000E pateventloop = new Loop2000E();
        HLPatientEventLevel2000E HLPatientEvent = new HLPatientEventLevel2000E();
        HLPatientEvent.setHL01HierarchicalIDNumber("4");
        HLPatientEvent.setHL02HierarchicalParentIDNumber("3");
        HLPatientEvent.setHL03HierarchicalLevelCode("EV");
        HLPatientEvent.setHL04HierarchicalChildCode("1");
        pateventloop.setHLPatientEventLevel2000E(HLPatientEvent);
        UMHealthCareServicesReviewInformation2000E UMEventInfo = new UMHealthCareServicesReviewInformation2000E();
        UMEventInfo.setUM01RequestCategoryCode("HS");
        UMEventInfo.setUM02CertificationTypeCode("I");
        UMEventInfo.setUM03ServiceTypeCode(""); // replace service type code
        UMHealthCareServicesReviewInformation2000E.UM04HealthCareServiceLocationInformation2000E UM04Info = new UMHealthCareServicesReviewInformation2000E.UM04HealthCareServiceLocationInformation2000E();
        UM04Info.setUM0401FacilityTypeCode("32"); //replace with bill type POS
        UM04Info.setUM0402FacilityCodeQualifier("A");
       // UM04HealthCareServiceLocationInformation2000E UM04info = new UMHealthCareServicesReviewInformation2000E.UM04HealthCareServiceLocationInformation2000E();
        UMEventInfo.setUM04HealthCareServiceLocationInformation2000E(UM04Info);
        pateventloop.setUMHealthCareServicesReviewInformation2000E(UMEventInfo);
        DTPEventDate2000E dtpEventDate2000E = new DTPEventDate2000E();
        dtpEventDate2000E.setDTP01DateTimeQualifier("AAH");
        dtpEventDate2000E.setDTP02DateTimePeriodFormatQualifier("RD8"); // replace with D8 if it is not range
        dtpEventDate2000E.setDTP03ProposedOrActualEventDate("20190801-20190305"); // replace with event start and end date
        pateventloop.setDTPEventDate2000E(dtpEventDate2000E);
        HIPatientDiagnosis2000E hiPatientDiagnosis2000E = new HIPatientDiagnosis2000E();
        HIPatientDiagnosis2000E.HI01HealthCareCodeInformation2000E HI01DiagCode = new HIPatientDiagnosis2000E.HI01HealthCareCodeInformation2000E();
        HI01DiagCode.setHI0101DiagnosisTypeCode("ABK"); // replace with primary diagnosise code type
        HI01DiagCode.setHI0102DiagnosisCode("G1221"); // replace with primary diagnosis code
        hiPatientDiagnosis2000E.setHI01HealthCareCodeInformation2000E(HI01DiagCode);
        pateventloop.setHIPatientDiagnosis2000E(hiPatientDiagnosis2000E);
        PWKAdditionalPatientInformation2000E pwkInfo = new PWKAdditionalPatientInformation2000E();
        pwkInfo.setPWK01AttachmentReportTypeCode("77"); //replace with attachment report type code
        pwkInfo.setPWK02ReportTransmissionCode("FX"); // replace with transmission code
        pwkInfo.setPWK03("");
        pwkInfo.setPWK04("");
        pwkInfo.setPWK05IdentificationCodeQualifier("AC");
        String uniquestr = "CGS" + "HBOSPRACHATACHCNTLNJL1VA"; // replace with RC name and PA type
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSS");
        String strDate = dateFormat.format(date);
        uniquestr =  uniquestr+strDate;
        pwkInfo.setPWK06AttachmentControlNumber(uniquestr);
        pateventloop.getPWKAdditionalPatientInformation2000E().add(pwkInfo);
        //Replace Ordering Provider Info
        ProviderIdentityInfo prov = new ProviderIdentityInfo();
        prov.setProviderEntityIdentCode("DK"); // ordering provider code
        prov.setProviderEntityTypeQualifier("1"); //replace with ordering provider qualifier
        prov.setProviderLastName("Weber");
        prov.setProviderFirstName("Kathleen");
        prov.setProviderMiddleName("");
        prov.setProviderNPI("1285612259");
        prov.setProviderAddressLine1("610 S Maple Ave");
        prov.setProviderCity("Oak Park");
        prov.setProviderState("IL");
        prov.setProviderZIPCode("60304");
        pateventloop.getLoop2010EA().add(setProviderInfo(prov));
        ProviderIdentityInfo atteprov = new ProviderIdentityInfo();
        atteprov.setProviderEntityIdentCode("71"); // Attending provider code
        atteprov.setProviderEntityTypeQualifier("1"); //replace with Attending provider qualifier
        atteprov.setProviderLastName("");
        atteprov.setProviderFirstName("");
        atteprov.setProviderMiddleName("");
        atteprov.setProviderNPI("");
        atteprov.setProviderAddressLine1("");
        atteprov.setProviderCity("");
        atteprov.setProviderState("");
        atteprov.setProviderZIPCode("");
        pateventloop.getLoop2010EA().add(setProviderInfo(atteprov));
        ProviderIdentityInfo facilityprov = new ProviderIdentityInfo();
        facilityprov.setProviderEntityIdentCode("71"); // Facility provider code
        facilityprov.setProviderEntityTypeQualifier("1"); //replace with Facility provider qualifier
        facilityprov.setProviderLastName("");
        facilityprov.setProviderFirstName("");
        facilityprov.setProviderMiddleName("");
        facilityprov.setProviderNPI("");
        facilityprov.setProviderAddressLine1("");
        facilityprov.setProviderCity("");
        facilityprov.setProviderState("");
        facilityprov.setProviderZIPCode("");
        pateventloop.getLoop2010EA().add(setProviderInfo(facilityprov));
        if(false){ //service provider code != null
            ProviderIdentityInfo serviceprov = new ProviderIdentityInfo();
            serviceprov.setProviderEntityIdentCode("SJ"); // service provider code
            serviceprov.setProviderEntityTypeQualifier("1"); //replace with service provider qualifier
            serviceprov.setProviderLastName("");
            serviceprov.setProviderFirstName("");
            serviceprov.setProviderMiddleName("");
            serviceprov.setProviderNPI("");
            serviceprov.setProviderAddressLine1("");
            serviceprov.setProviderCity("");
            serviceprov.setProviderState("");
            serviceprov.setProviderZIPCode("");
            pateventloop.getLoop2010EA().add(setProviderInfo(serviceprov));
        }
        if(false){ //referring provider code != null
            ProviderIdentityInfo refrov = new ProviderIdentityInfo();
            refrov.setProviderEntityIdentCode("DN"); // referring provider code
            refrov.setProviderEntityTypeQualifier("1"); //replace with referring provider qualifier
            refrov.setProviderLastName("");
            refrov.setProviderFirstName("");
            refrov.setProviderMiddleName("");
            refrov.setProviderNPI("");
            refrov.setProviderAddressLine1("");
            refrov.setProviderCity("");
            refrov.setProviderState("");
            refrov.setProviderZIPCode("");
            pateventloop.getLoop2010EA().add(setProviderInfo(refrov));
        }
        if(false){ //operating provider code != null
            ProviderIdentityInfo operprov = new ProviderIdentityInfo();
            operprov.setProviderEntityIdentCode("72"); // operating provider code
            operprov.setProviderEntityTypeQualifier("1"); //replace with operating provider qualifier
            operprov.setProviderLastName("");
            operprov.setProviderFirstName("");
            operprov.setProviderMiddleName("");
            operprov.setProviderNPI("");
            operprov.setProviderAddressLine1("");
            operprov.setProviderCity("");
            operprov.setProviderState("");
            operprov.setProviderZIPCode("");
            pateventloop.getLoop2010EA().add(setProviderInfo(operprov));
        }
        pateventloop.getLoop2000F().add(setServiceLevelInfo("G0299","5", "5","4",false,uniquestr)); // replace iterate through service lines
        return pateventloop;
    }
    public Loop2010EA setProviderInfo(ProviderIdentityInfo prov){
        Loop2010EA loop2010ea = new Loop2010EA();
        NM1PatientEventProviderName2010EA nm1PatientEventProviderName2010EA = new NM1PatientEventProviderName2010EA();
        nm1PatientEventProviderName2010EA.setNM101EntityIdentifierCode(prov.getProviderEntityIdentCode()); // replace with OrderingProviderEntityIdentCode
        nm1PatientEventProviderName2010EA.setNM102EntityTypeQualifier(prov.getProviderEntityTypeQualifier()); //replace with size of the org
        nm1PatientEventProviderName2010EA.setNM103PatientEventProviderLastOrOrganizationName(prov.getProviderLastName());// replace with provider last name
        nm1PatientEventProviderName2010EA.setNM104PatientEventProviderFirstName(prov.getProviderFirstName());
        if(prov.getProviderMiddleName() != null)
        nm1PatientEventProviderName2010EA.setNM105PatientEventProviderMiddleName(prov.getProviderMiddleName());
        else
            nm1PatientEventProviderName2010EA.setNM105PatientEventProviderMiddleName("");
        if(prov.getProviderPrefix() != null)
            nm1PatientEventProviderName2010EA.setNM106PatientEventProviderNamePrefix(prov.getProviderPrefix());
        else
            nm1PatientEventProviderName2010EA.setNM106PatientEventProviderNamePrefix("");
        if(prov.getProviderSuffix() != null)
            nm1PatientEventProviderName2010EA.setNM107PatientEventProviderNameSuffix(prov.getProviderSuffix());
        else
            nm1PatientEventProviderName2010EA.setNM107PatientEventProviderNameSuffix("");

        nm1PatientEventProviderName2010EA.setNM108IdentificationCodeQualifier("XX");
        nm1PatientEventProviderName2010EA.setNM109PatientEventProviderIdentifier(prov.getProviderNPI());
        loop2010ea.setNM1PatientEventProviderName2010EA(nm1PatientEventProviderName2010EA);
        N3PatientEventProviderAddress2010EA patientEventProviderAddress2010EA = new N3PatientEventProviderAddress2010EA();
        patientEventProviderAddress2010EA.setN301PatientEventProviderAddressLine(prov.getProviderAddressLine1());
        if(prov.getProviderAddressLine2() != null){
            patientEventProviderAddress2010EA.setN302PatientEventProviderAddressLine(prov.getProviderAddressLine2());
        }else{
            patientEventProviderAddress2010EA.setN302PatientEventProviderAddressLine("");
        }
        loop2010ea.setN3PatientEventProviderAddress2010EA(patientEventProviderAddress2010EA);
        N4PatientEventProviderCityStateZIPCode2010EA patientEventProviderCityStateZIPCode2010EA = new N4PatientEventProviderCityStateZIPCode2010EA();
        patientEventProviderCityStateZIPCode2010EA.setN401PatientEventProviderCityName(prov.getProviderCity());
        patientEventProviderCityStateZIPCode2010EA.setN402PatientEventProviderStateCode(prov.getProviderState());
        patientEventProviderCityStateZIPCode2010EA.setN403PatientEventProviderPostalZoneOrZIPCode(prov.getProviderZIPCode());
        loop2010ea.setN4PatientEventProviderCityStateZIPCode2010EA(patientEventProviderCityStateZIPCode2010EA);
        return loop2010ea;
    }
    public void writeEDItoXML(){
        X12005010X217278A1 subelement = new X12005010X217278A1();
        STTransactionSetHeader stHeader = new STTransactionSetHeader();
        stHeader.setST01TransactionSetIdentifierCode("278");
        stHeader.setST02TransactionSetControlNumber("121901");
        stHeader.setST03ImplementationGuideVersionName("005010X217");
        subelement.setSTTransactionSetHeader(stHeader);
        BHTBeginningOfHierarchicalTransaction bhtelement = new BHTBeginningOfHierarchicalTransaction();
        bhtelement.setBHT01HierarchicalStructureCode("0007");
        bhtelement.setBHT02TransactionSetPurposeCode("13");
        bhtelement.setBHT03SubmitterTransactionIdentifier("000121901");
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String strDate = dateFormat.format(date);
        bhtelement.setBHT04TransactionSetCreationDate(strDate);
        DateFormat timeFormat = new SimpleDateFormat("HHmmss");
        String strtime = timeFormat.format(date);
        bhtelement.setBHT05TransactionSetCreationTime(strtime);
        subelement.setBHTBeginningOfHierarchicalTransaction(bhtelement);
        Loop2000A rcloopelement = new Loop2000A();
        HLUtilizationManagementOrganizationUMOLevel2000A hlrcelemet = new HLUtilizationManagementOrganizationUMOLevel2000A();
        hlrcelemet.setHL01HierarchicalIDNumber("1");
        hlrcelemet.setHL02("");
        hlrcelemet.setHL03HierarchicalLevelCode("20");
        hlrcelemet.setHL04HierarchicalChildCode("1");
        rcloopelement.setHLUtilizationManagementOrganizationUMOLevel2000A(hlrcelemet);
        Loop2010A loop2010a = new Loop2010A();
        NM1UtilizationManagementOrganizationUMOName2010A nmrcelement = new NM1UtilizationManagementOrganizationUMOName2010A();
        nmrcelement.setNM101EntityIdentifierCode("X3");
        nmrcelement.setNM102EntityTypeQualifier("2");
        nmrcelement.setNM103UtilizationManagementOrganizationUMOLastOrOrganizationName("CGS");// replaceRC Name
        nmrcelement.setNM104UtilizationManagementOrganizationUMOFirstName("");
        nmrcelement.setNM105UtilizationManagementOrganizationUMOMiddleName("");
        nmrcelement.setNM106("");
        nmrcelement.setNM107UtilizationManagementOrganizationUMONameSuffix("");
        nmrcelement.setNM108IdentificationCodeQualifier("PI");
        nmrcelement.setNM109UtilizationManagementOrganizationUMOIdentifier("15004"); // replace workload number
        loop2010a.setNM1UtilizationManagementOrganizationUMOName2010A(nmrcelement);
        rcloopelement.setLoop2010A(loop2010a);
        Loop2000B requesterloopelement = new Loop2000B();
        HLRequesterLevel2000B hlrequester = new HLRequesterLevel2000B();
        hlrequester.setHL01HierarchicalIDNumber("2");
        hlrequester.setHL02HierarchicalParentIDNumber("1");
        hlrequester.setHL03HierarchicalLevelCode("21");
        hlrequester.setHL04HierarchicalChildCode("1");
        requesterloopelement.setHLRequesterLevel2000B(hlrequester);
        Loop2010B reqnameelement = new Loop2010B();
        NM1RequesterName2010B nm1reqnameelement = new NM1RequesterName2010B();
        nm1reqnameelement.setNM101EntityIdentifierCode("1P"); // replace requester 1P or FA
        nm1reqnameelement.setNM102EntityTypeQualifier("2"); // replace with size of org
        nm1reqnameelement.setNM103RequesterLastOrOrganizationName("Unitypoint at Home"); //replace requester last org name
        nm1reqnameelement.setNM104RequesterFirstName("");
        nm1reqnameelement.setNM105RequesterMiddleName("");
        nm1reqnameelement.setNM106("");
        nm1reqnameelement.setNM107RequesterNameSuffix("");
        nm1reqnameelement.setNM108IdentificationCodeQualifier("XX");
        nm1reqnameelement.setNM109RequesterIdentifier("142351956"); //replace requester NPI
        reqnameelement.setNM1RequesterName2010B(nm1reqnameelement);
        N3RequesterAddress2010B n3reqaddrelement = new N3RequesterAddress2010B();
        n3reqaddrelement.setN301RequesterAddressLine("106 19th Ave 101"); //replace requester addr line1
        ///replace if req addr line 2 presetn

        reqnameelement.setN3RequesterAddress2010B(n3reqaddrelement);
        N4RequesterCityStateZIPCode2010B n4reqaddrcityzip = new N4RequesterCityStateZIPCode2010B();
        n4reqaddrcityzip.setN401RequesterCityName("Moline");  // replace requester city name
        n4reqaddrcityzip.setN402RequesterStateOrProvinceCode("IL"); // replace requester
        n4reqaddrcityzip.setN403RequesterPostalZoneOrZIPCode("61265"); // replace requester
        reqnameelement.setN4RequesterCityStateZIPCode2010B(n4reqaddrcityzip);
        //PER*IC*Carrie Smith*TE*8035555555~
        PERRequesterContactInformation2010B reqperinfo = new PERRequesterContactInformation2010B();
        reqperinfo.setPER01ContactFunctionCode("IC");
        reqperinfo.setPER02RequesterContactName("Jon Dow"); //replace requester contact name
        reqperinfo.setPER03CommunicationNumberQualifier("TE");
        reqperinfo.setPER04RequesterContactCommunicationNumber("5555555555"); // repkace requester ph number
        reqnameelement.setPERRequesterContactInformation2010B(reqperinfo);
        requesterloopelement.setLoop2010B(reqnameelement);
        Loop2000C subloopelement = new Loop2000C();
        HLSubscriberLevel2000C hlsubelemet = new HLSubscriberLevel2000C();
        hlsubelemet.setHL01HierarchicalIDNumber("3");
        hlsubelemet.setHL02HierarchicalParentIDNumber("2");
        hlsubelemet.setHL03HierarchicalLevelCode("22");
        hlsubelemet.setHL04HierarchicalChildCode("1");
        subloopelement.setHLSubscriberLevel2000C(hlsubelemet);

        subloopelement.setLoop2010C(SetSubscriberInfo());
        subloopelement.setLoop2000E(setPatientEventLevelInfo());

        requesterloopelement.setLoop2000C(subloopelement);

        rcloopelement.setLoop2000B(requesterloopelement);

        subelement.setLoop2000A(rcloopelement);
        SETransactionSetTrailer  seTransTrailer = new SETransactionSetTrailer();
        seTransTrailer.setSE01TransactionSegmentCount("32");
        seTransTrailer.setSE02TransactionSetControlNumber("121901");
        subelement.setSETransactionSetTrailer(seTransTrailer);
        try {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(X12005010X217278A1.class);

            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //Print XML String to Console
            StringWriter sw = new StringWriter();
            //subelement
            //Write XML to StringWriter
            jaxbMarshaller.marshal(subelement, sw);
            String xmlContent = sw.toString();
            System.out.println("XML string is"+xmlContent);
           /* DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(
                    xmlContent.getBytes("UTF-8"));
            Document doc = builder.parse(input);*/
          //  doc.getDocumentElement().normalize();
            Document doc = XMLUtil.parseString(xmlContent);
            Element root = doc.getDocumentElement();
            Element[] childNodes = XMLUtil.getChildElements(root);
            XML2EDI = "";
            for (int i = 0; i < childNodes.length; i++) {
                Element childList = childNodes[i];

                    if (childList.getTagName().startsWith("Loop")) {
                        iterateRecursive(childList);
                    }else{

                        Element[] attrNodeMap =  XMLUtil.getChildElements(childList);

                        System.out.println("Node name is"+childList.getTagName());
                        String elemTagName = childList.getTagName().substring(0,childList.getTagName().indexOf("_"));
                        XML2EDI = XML2EDI + elemTagName + "*";
                        for (int k = 0; k < attrNodeMap.length; k++){
                            Element childelement = attrNodeMap[k];
                           // String temp = XMLUtil.getAttribute(childList,childelement.getTagName(),true);
                            NodeList templist = childList.getElementsByTagName(childelement.getTagName());
                            System.out.println("Size of templist "+templist.getLength());
                            String temp = templist.item(0).getTextContent();
                            System.out.println("attr name is"+childelement.getTagName());
                            XML2EDI = XML2EDI + temp + "*";
                        }
                        XML2EDI = XML2EDI + "~\n";
                    }

            }
            System.out.println("EDI String is "+XML2EDI);
        }catch(Exception e){
            e.printStackTrace();
        }
        //Loop2010C hlloop
        //subloopelement.setLoop2010C();
        //.setLoop2000C();


    }
    public void parseEDI(String Text){
        StringTokenizer st1 =
                new StringTokenizer(Text, "\n");
        String prevSegment = null;
        boolean bRequesterSeg = false, bSubscriberSeg = false, bRecvSeg = false , bEventSeg = false , bProviderSeg = false, bServicelvlSeg = false ;
        while (st1.hasMoreTokens()) {
            String currSegment = st1.nextToken();
            if(currSegment.startsWith("ISA") || currSegment.startsWith("GS") || currSegment.startsWith("ST") || currSegment.startsWith("SE") || currSegment.startsWith("GE") || currSegment.startsWith("IEA")){

            }else if(currSegment.startsWith("BHT")){

            }else if(currSegment.startsWith("HL*1")){
                bRecvSeg = true;
            }else if(currSegment.startsWith("HL*2")){
                bRecvSeg = false;
                bRequesterSeg = true;
            }else if(currSegment.startsWith("HL*3")){
                bRequesterSeg = false;
                bSubscriberSeg = true;
            }else if(currSegment.startsWith("HL*4")){
                bSubscriberSeg = false;
                bEventSeg = true;
            }else if(bEventSeg && currSegment.startsWith("NM1")){
                bEventSeg = false;
                bProviderSeg = true;
            } else if(currSegment.startsWith("HL*5")){
                bProviderSeg = false;
                bServicelvlSeg = true;
            }else{
                if(bEventSeg){
                    if(currSegment.startsWith("TRN*")){
                        int len = currSegment.length();
                        String trnStr = currSegment.substring(0,len-1);
                        StringTokenizer trnStrTokenizer = new StringTokenizer(Text, "*");
                        int count = 0;
                        while(trnStrTokenizer.hasMoreTokens()){
                             String trnToken = trnStrTokenizer.nextToken();
                             count++;
                             if(count == 3){
                                 System.out.println("Transaction ID is"+trnToken);
                                 break;
                             }else if(count == 2){
                                 if(trnToken.equals("1")){
                                     System.out.println("esMD transaction ID Sent");
                                 }else if(trnToken.equals("2")){
                                     break;
                                 }
                             }
                        }
                    }else if(currSegment.startsWith("AAA*")){
                        int len = currSegment.length();
                        String trnStr = currSegment.substring(0,len-1);
                        StringTokenizer AAAStrTokenizer = new StringTokenizer(trnStr, "*");
                        int count = 0;
                        while(AAAStrTokenizer.hasMoreTokens()){
                            String trnToken = AAAStrTokenizer.nextToken();
                            count++;
                            if(count == 4){
                                System.out.println("Receiver Loop AAA error reject reason code "+trnToken);
                            }else if(count == 5){
                                System.out.println("Receiver Loop AAA error follow up action code"+trnToken);
                            }
                        }
                    }else if(currSegment.startsWith("HCR*")){
                        int len = currSegment.length();
                        String trnStr = currSegment.substring(0,len-1);
                        StringTokenizer HCRStrTokenizer = new StringTokenizer(trnStr, "*");
                        int count = 0;
                        while(HCRStrTokenizer.hasMoreTokens()){
                            String trnToken = HCRStrTokenizer.nextToken();
                            count++;
                            if(count != 1){
                              System.out.println("HCR segment elemts are"+trnToken);
                            }

                        }
                    }else if(currSegment.startsWith("REF*")){
                        int len = currSegment.length();
                        String trnStr = currSegment.substring(0,len-1);
                        StringTokenizer REFStrTokenizer = new StringTokenizer(trnStr, "*");
                        int count = 0;
                        while(REFStrTokenizer.hasMoreTokens()){
                            String trnToken = REFStrTokenizer.nextToken();
                            count++;
                            if(count == 3){
                                System.out.println("UTN value is"+trnToken);
                            }

                        }
                    }else if(currSegment.startsWith("MSG*")){
                        int len = currSegment.length();
                        String trnStr = currSegment.substring(0,len-1);
                        StringTokenizer MSGStrTokenizer = new StringTokenizer(trnStr, "*");
                        int count = 0;
                        while(MSGStrTokenizer.hasMoreTokens()){
                            String trnToken = MSGStrTokenizer.nextToken();
                            count++;
                            if(count == 2){
                                System.out.println("MSG value is"+trnToken);
                            }

                        }
                    }
                }
                if(bProviderSeg){
                    if(currSegment.startsWith("AAA*")){
                        int len = currSegment.length();
                        String trnStr = currSegment.substring(0,len-1);
                        StringTokenizer AAAStrTokenizer = new StringTokenizer(trnStr, "*");
                        int count = 0;
                        while(AAAStrTokenizer.hasMoreTokens()){
                            String trnToken = AAAStrTokenizer.nextToken();
                            count++;
                            if(count == 4){
                                System.out.println("Receiver Loop AAA error reject reason code "+trnToken);
                            }else if(count == 5){
                                System.out.println("Receiver Loop AAA error follow up action code"+trnToken);
                            }
                        }
                    }
                }
                if(bRecvSeg || bRequesterSeg || bSubscriberSeg ){
                    if(currSegment.startsWith("AAA*")){
                        int len = currSegment.length();
                        String trnStr = currSegment.substring(0,len-1);
                        StringTokenizer AAAStrTokenizer = new StringTokenizer(trnStr, "*");
                        int count = 0;
                        while(AAAStrTokenizer.hasMoreTokens()){
                            String trnToken = AAAStrTokenizer.nextToken();
                            count++;
                            if(count == 4){
                                System.out.println("Receiver Loop AAA error reject reason code "+trnToken);
                            }else if(count == 5){
                                System.out.println("Receiver Loop AAA error follow up action code"+trnToken);
                            }
                        }
                    }
                }
            }

            if(bServicelvlSeg){
                if(currSegment.startsWith("TRN*")) {
                    int len = currSegment.length();
                    String trnStr = currSegment.substring(0,len-1);
                    StringTokenizer TRNStrTokenizer = new StringTokenizer(trnStr, "*");
                    int count = 0;
                    while(TRNStrTokenizer.hasMoreTokens()){
                        String trnToken = TRNStrTokenizer.nextToken();
                        count++;
                        if(count != 1){
                            System.out.println("TRN Token in Service loop "+trnToken);
                        }
                    }
                }else if(currSegment.startsWith("AAA*")){
                    int len = currSegment.length();
                    String trnStr = currSegment.substring(0,len-1);
                    StringTokenizer AAAStrTokenizer = new StringTokenizer(trnStr, "*");
                    int count = 0;
                    while(AAAStrTokenizer.hasMoreTokens()){
                        String trnToken = AAAStrTokenizer.nextToken();
                        count++;
                        if(count == 4){
                            System.out.println("Receiver Loop AAA error reject reason code "+trnToken);
                        }else if(count == 5){
                            System.out.println("Receiver Loop AAA error follow up action code"+trnToken);
                        }
                    }
                }else if(currSegment.startsWith("HCR*")){
                    int len = currSegment.length();
                    String trnStr = currSegment.substring(0,len-1);
                    StringTokenizer HCRStrTokenizer = new StringTokenizer(trnStr, "*");
                    int count = 0;
                    while(HCRStrTokenizer.hasMoreTokens()){
                        String trnToken = HCRStrTokenizer.nextToken();
                        count++;
                        if(count != 1){
                            System.out.println("HCR segment elemts are"+trnToken);
                        }

                    }
                }else if(currSegment.startsWith("REF*")){
                    int len = currSegment.length();
                    String trnStr = currSegment.substring(0,len-1);
                    StringTokenizer REFStrTokenizer = new StringTokenizer(trnStr, "*");
                    int count = 0;
                    while(REFStrTokenizer.hasMoreTokens()){
                        String trnToken = REFStrTokenizer.nextToken();
                        count++;
                        if(count == 3){
                            System.out.println("UTN value is"+trnToken);
                        }

                    }
                }else if(currSegment.startsWith("MSG*")){
                    int len = currSegment.length();
                    String trnStr = currSegment.substring(0,len-1);
                    StringTokenizer MSGStrTokenizer = new StringTokenizer(trnStr, "*");
                    int count = 0;
                    while(MSGStrTokenizer.hasMoreTokens()){
                        String trnToken = MSGStrTokenizer.nextToken();
                        count++;
                        if(count == 2){
                            System.out.println("MSG value is"+trnToken);
                        }

                    }
                }else if(currSegment.startsWith("HL*")){
                    System.out.println("Next Service Line");
                }
            }
        }
    }
    public void iterateRecursive(Element LoopNode){
        Element[] childList = XMLUtil.getChildElements(LoopNode);

        for (int j = 0; j < childList.length; j++) {
            Element childNode = childList[j];
            if (childNode.getTagName().startsWith("Loop")) {
                iterateRecursive(childNode);
            }else{
                Element[] attrNodeMap =  XMLUtil.getChildElements(childNode);
              //  NamedNodeMap attrNodeMap = childNode.getAttributes();
                System.out.println("Node name is from rec"+childNode.getTagName());
                String elemTagName = childNode.getTagName().substring(0,childNode.getTagName().indexOf("_"));
                XML2EDI = XML2EDI + elemTagName + "*";
                for (int k = 0; k < attrNodeMap.length; k++){
                    Element childelement = attrNodeMap[k];
                    System.out.println("attr name is from rec"+childelement.getTagName());
                    XML2EDI = XML2EDI + childelement.getTextContent() + "*";
                }
                XML2EDI = XML2EDI + "~\n";
            }
        }

    }
 /*   public String createPdf(String text)
    {
        Document document = new Document();
        String retVal = "helloworld.pdf";

        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(retVal);

            PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            document.add(new Paragraph(text));
            document.close();
            writer.close();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return retVal;
    }

    public String readPdf(String fileName)
    {

        String retVal = "";

        try
        {
            PdfReader pdfReader = new PdfReader(fileName);
            int pages = pdfReader.getNumberOfPages();

            //Iterate the pdf through pages.
            for(int i=1; i<=pages; i++) {
                //Extract the page content using PdfTextExtractor.
                String pageContent =
                        PdfTextExtractor.getTextFromPage(pdfReader, i);
                retVal = retVal + pageContent;
                //Print the page content on console.
                System.out.println("Content on Page "
                        + i + ": " + pageContent);}

            pdfReader.close();
        }  catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return retVal;
    }*/


}