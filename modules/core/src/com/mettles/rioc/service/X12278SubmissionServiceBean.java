package com.mettles.rioc.service;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.mettles.rioc.X12278SubmissionServiceStatus;
import com.mettles.rioc.entity.PAError;
import com.mettles.rioc.entity.PANotificationSlot;
import com.mettles.rioc.entity.TransType;
import com.mettles.rioc.entity.X12278Submission;
import com.mettles.rioc.x12.AAAErrorInfo;
import com.mettles.rioc.x12.HealthCareReviewInfo;
import com.mettles.rioc.x12.X12278EDIReadWriter;
import com.mettles.rioc.x12.X12278EDIResponse;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service(X12278SubmissionService.NAME)
public class X12278SubmissionServiceBean implements X12278SubmissionService {

    @Inject
    DataManager datamanager;

    @Override
    public X12278SubmissionServiceStatus ReadEDIString(String ediString) {

        X12278SubmissionServiceStatus x12278SubServSts = new X12278SubmissionServiceStatus();
        X12278EDIReadWriter x12278 = new X12278EDIReadWriter();
        X12278EDIResponse x12278respn = new X12278EDIResponse();
        try {

            x12278respn = x12278.parse278ResponseEDI(ediString);
            System.out.println(" X12278EDIResponse from Service Call to EsMD: ");
            System.out.println("-------------------------------------------------");
            System.out.println(" X12278EDIResponse from EsMD: CLient Transaction ID: " + x12278respn.getClienttransid());
            System.out.println(" X12278EDIResponse from EsMD: COnsolidated HCR: " + x12278respn.getConsolidated_hcr());
            System.out.println(" X12278EDIResponse from EsMD: Esmd Transaction ID: " + x12278respn.getEsmdtransid());
            System.out.println(" X12278EDIResponse from EsMD: Message: " + x12278respn.getMessage());
            System.out.println(" X12278EDIResponse from EsMD: Reciever ID: " + x12278respn.getRcvrId());
            System.out.println(" X12278EDIResponse from EsMD: Sender ID: " + x12278respn.getSenderId());
            System.out.println(" X12278EDIResponse from EsMD: Service Health Care: " + x12278respn.getService_healthcare());

            String esmdTrasID = x12278respn.getEsmdtransid();

            //New code
            x12278SubServSts.setRcverID(x12278respn.getRcvrId());
            x12278SubServSts.setSenderID(x12278respn.getSenderId());
            //end

            System.out.println("ESMD Transaction ID for Given Response PayLoad: "+esmdTrasID);

            /*List<X12278Submission> x12278SubList = datamanager.load(X12278Submission.class).query("select p from rioc$rioc_X12278Submission p where p.esmdTransactionId = :esmdTransid")
                    .parameter("esmdTransid", esmdTrasID).view("x12278Submission-view").list();*/

            List<X12278Submission> x12278SubList = datamanager.load(X12278Submission.class)
                    .query("select p from rioc_X12278Submission p where p.esmdTransactionId=:esmdTransid")
                    .parameter("esmdTransid", esmdTrasID.trim())
                    .view("x12278Submission-view")
                    .list();


            if (x12278SubList == null) {
                System.out.println("HIHConfiguration parameters are not set");
                x12278SubServSts.setX12278SubID(0);
            }

            else if(x12278SubList.size()== 0)
            {
                x12278SubServSts.setX12278SubID(0);
            }
            else if (x12278SubList.size() > 0) {
                Iterator<X12278Submission> x12278SubIterator = x12278SubList.iterator();
                if (x12278SubIterator.hasNext()) {
                    X12278Submission x12278SubParam = x12278SubIterator.next();

                    CommitContext commitContext = new CommitContext();
                    System.out.println("X12278Submission Data for provided EDIString -> ESMD Transaction ID:");
                    System.out.println("----------------------------------------------------------------------");
                    System.out.println("X12278Submission Attachment Control Number: " + x12278SubParam.getAttachmentControlNumber());
                    System.out.println("X12278Submission Unique ID: " + x12278SubParam.getX12UnqID());
                    System.out.println("X12278Submission Supporting Document esmdTransactionId: " + x12278SubParam.getSuppDocesmdTransactionId());
                    System.out.println("X12278Submission Document Unique ID: " + x12278SubParam.getDocUnqID());
                    System.out.println("X12278Submission ESMD Transaction ID: " + x12278SubParam.getEsmdTransactionId());
                    System.out.println("X12278Submission Status: " + x12278SubParam.getStatus());
                    System.out.println("X12278Submission ID: " + x12278SubParam.getId());

                    try{

                        x12278SubServSts.setX12278SubID(x12278SubParam.getId());

                        System.out.println("Response Errors: "+x12278respn.getErrors());

                        if (x12278respn.getErrors().size()>0) {

                            System.out.println("Error Size: "+x12278respn.getErrors().size());

                            x12278SubParam.setPaMessage("AAA Error");
                            commitContext.addInstanceToCommit(x12278SubParam);
                            //x12278SubServSts.setPaMessage(x12278SubParam.getPaMessage());
                            x12278SubServSts.setPaMessage("AAA Error");

                            ArrayList<AAAErrorInfo> aaaErrList = new ArrayList<>();
                            aaaErrList = x12278respn.getErrors();
                            Iterator<AAAErrorInfo> aaaErrinfoIt = aaaErrList.iterator();

                            while (aaaErrinfoIt.hasNext()) {
                                AAAErrorInfo aaaErrInfoItIn = aaaErrinfoIt.next();

                                System.out.println("AAA Error Code: " + aaaErrInfoItIn.getError_code());
                                System.out.println("AAA Error String: " + aaaErrInfoItIn.getError_string());
                                System.out.println("AAA Error Follow UP Action: " + aaaErrInfoItIn.getFollowup_action());
                                System.out.println("AAA Error Segment Info: " + aaaErrInfoItIn.getSegment_info());

                                //CommitContext commitContext = new CommitContext();
                                PAError paErrTemp = datamanager.create(PAError.class);
                                paErrTemp.setErrorCode(aaaErrInfoItIn.getError_code());
                                paErrTemp.setCodeContext(aaaErrInfoItIn.getError_string());
                                paErrTemp.setDescription(aaaErrInfoItIn.getFollowup_action());
                                paErrTemp.setSeverity(aaaErrInfoItIn.getSegment_info());
                                paErrTemp.setX12278submissionID(x12278SubParam);
                                paErrTemp.setTransType("X12 Transaction");
                                commitContext.addInstanceToCommit(paErrTemp);
                                //datamanager.commit(commitContext);

                            }
                        }
                        else {
                            System.out.println("SuccessFull Submission from Shared Systems:");
                            System.out.println("---------------------------------------------");
                            System.out.println("PA Message: "+x12278respn.getMessage());

                            //Code changed
                            //if(x12278respn.getMessage()!=null) {
                                x12278SubParam.setPaMessage(x12278respn.getMessage());
                                x12278SubServSts.setPaMessage(x12278respn.getMessage());
                            //}//End of Code changes
                            try{
                                if (x12278respn.getConsolidated_hcr().getUtn_value() != null){

                                    x12278SubServSts.setPaMessage(x12278respn.getConsolidated_hcr().getAction_code());
                                    x12278SubParam.setPaMessage(x12278respn.getConsolidated_hcr().getAction_code());
                                    System.out.println("PA Message: "+x12278respn.getConsolidated_hcr().getAction_code());
                                    System.out.println("PA UTN: "+x12278respn.getConsolidated_hcr().getUtn_value());
                                    x12278SubParam.setPaUTN(x12278respn.getConsolidated_hcr().getUtn_value());
                                    x12278SubServSts.setPaUTN(x12278respn.getConsolidated_hcr().getUtn_value());
                                }
                                else
                                {
                                    x12278SubParam.setPaUTN("ESMDNUMBER");
                                    x12278SubServSts.setPaUTN("ESMDNUMBER");
                                }
                            }
                            catch(NullPointerException e)
                            {
                                e.printStackTrace();
                            }

                            commitContext.addInstanceToCommit(x12278SubParam);

                            ArrayList<HealthCareReviewInfo> hlthCareRvwList = x12278respn.getService_healthcare();
                            Iterator<HealthCareReviewInfo> hltCareRvwInfIt = hlthCareRvwList.iterator();

                            while (hltCareRvwInfIt.hasNext()) {

                                try{

                                    HealthCareReviewInfo hltCareRvwInfItIn = hltCareRvwInfIt.next();
                                    if (hltCareRvwInfItIn.getService_code() != null)
                                    {
                                        System.out.println("X12278Submission Service Success Scenerio");
                                        System.out.println("Health Care Review Info");
                                        System.out.println("------------------------------------------");
                                        System.out.println("Health Care Review Info Service Code: Value: " + hltCareRvwInfItIn.getService_code());
                                        System.out.println("Health Care Review Info Action Code: Field: " + hltCareRvwInfItIn.getAction_code());
                                        //System.out.println("Health Care Review Info Date: "+hltCareRvwInfItIn.getStart_Date()); Store in PA Notification SLot Entity -> field "notes"
                                        //System.out.println("Health Care Review Info Date: "+hltCareRvwInfItIn.getEnd_Date()); Store in PA Notification SLot Entity -> field "notes"
                                        //CommitContext commitContext = new CommitContext();


                                        PANotificationSlot paNotiSltTemp = datamanager.create(PANotificationSlot.class);

                                        paNotiSltTemp.setX12278submissionID(x12278SubParam);
                                        paNotiSltTemp.setTransType("X12 Transaction");

                                        /* New Code Manual Insert for Testing
                                        paNotiSltTemp.setValue("Health Care Info Service");
                                        paNotiSltTemp.setField("Health Care Info Reason");
                                         */

                                        paNotiSltTemp.setValue(hltCareRvwInfItIn.getService_code());
                                        if (hltCareRvwInfItIn.getReason_code() != null)
                                        {
                                            paNotiSltTemp.setField(hltCareRvwInfItIn.getAction_code()+"::"+hltCareRvwInfItIn.getReason_code());
                                        }
                                        else {
                                            paNotiSltTemp.setField(hltCareRvwInfItIn.getAction_code());
                                        }

                                        //paNotiSltTemp.setNotes(hltCareRvwInfItIn.getStart_Date or .getEnd_Date);

                                        //END of New Code

                                        commitContext.addInstanceToCommit(paNotiSltTemp);
                                    }

                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        }

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    datamanager.commit(commitContext);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    return x12278SubServSts;
    }


}