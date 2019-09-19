package com.mettles.rioc.core;

import java.io.FileOutputStream;
import java.util.UUID;



public class DocumentPrepare {

    public static String getContent(String base64FileContent,String mimetype){
        String recordContent="<ClinicalDocument xmlns=\"urn:hl7-org:v3\"\n" +
                "                  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "                  classCode=\"DOCCLIN\" moodCode=\"EVN\" xsi:schemaLocation=\"urn:hl7-org:v3 CDA.xsd\">\n" +
                "    <typeId extension=\"POCD_HD000040\"\n" +
                "            root=\"2.16.840.1.113883.1.3\"/>\n" +
                "    <id root=\"eab8765b-1424-47cc-9495-ddc934cf5f5d\"/>\n" +
                "    <templateId root=\"2.16.840.1.113883.10.20.3\"\n" +
                "                assigningAuthorityName=\"CDT General Header Constraints\"/>\n" +
                "    <templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.1.1\"\n" +
                "                assigningAuthorityName=\"IHE Medical Document\"/>\n" +
                "    <templateId root=\"1.3.6.1.4.1.19376.1.2.20\"\n" +
                "                assigningAuthorityName=\"IHE Scanned Document\"/>\n" +
                "    <templateId root=\"2.16.840.1.113883.3.88.11.62.1\"\n" +
                "                assigningAuthorityName=\"HITSP Unstructured Document\"/>\n" +
                "    <languageCommunication>\n" +
                "        <templateId root='1.3.6.1.4.1.19376.1.5.3.1.2.1'/>\n" +
                "        <languageCode code='en-US'/>\n" +
                "    </languageCommunication>\n" +
                "    <title>ADR Response Supported Claim Documentation</title>\n" +
                "    <confidentialityCode code=\"V\" codeSystem=\"2.16.840.1.113883.5.25\"\n" +
                "                         codeSystemName=\"Confidentiality\" displayName=\"Very Restricted\"/>\n" +
                "    <effectiveTime value=\"20100319083838-0500\"/>\n" +
                "    <recordTarget>\n" +
                "        <patientRole>\n" +
                "            <id extension=\"12345\"\n" +
                "                root=\"2.16.840.1.113883.3.933\"/>\n" +
                "\n" +
                "        </patientRole>\n" +
                "    </recordTarget>\n" +
                "    <author>\n" +
                "        <templateId\n" +
                "                root=\"1.3.6.1.4.1.19376.1.2.20.1\"/>\n" +
                "\n" +
                "    </author>\n" +
                "\n" +
                "    <templateId\n" +
                "            root=\"1.3.6.1.4.1.19376.1.2.20.2\"/>\n" +
                "\n" +
                "    <dataEnterer>\n" +
                "        <templateId\n" +
                "                root=\"1.3.6.1.4.1.19376.1.2.20.3\"/>\n" +
                "\n" +
                "    </dataEnterer>\n" +
                "    <custodian>\n" +
                "\n" +
                "    </custodian>\n" +
                "    <legalAuthenticator>\n" +
                "\n" +
                "    </legalAuthenticator>\n" +
                "    <documentationOf>\n" +
                "        <serviceEvent>\n" +
                "            <effectiveTime>\n" +
                "                <low value=\"19800127\"/>\n" +
                "                <high value=\"19990522\"/>\n" +
                "            </effectiveTime>\n" +
                "        </serviceEvent>\n" +
                "    </documentationOf>\n" +
                "    <component>\n" +
                "        <nonXMLBody>\n" +
                "            <text mediaType=\""+mimetype+"\"\n" +
                "                  representation=\"B64\">"+base64FileContent+"\n" +
                "            </text>\n" +
                "        </nonXMLBody>\n" +
                "    </component>\n" +
                "</ClinicalDocument>";
        return recordContent;
    }

    public String prepareFile(String base64Str,String mimetype){
        String uniquestr = UUID.randomUUID().toString();

        try {
            FileOutputStream temp = new FileOutputStream(uniquestr + ".xml");
            temp.write(getContent(base64Str,mimetype).getBytes());
            temp.close();
        }catch(Exception e){
            e.printStackTrace();
        }
     return uniquestr + ".xml";
    }
}
