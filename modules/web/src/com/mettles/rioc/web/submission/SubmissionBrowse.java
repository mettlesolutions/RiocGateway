package com.mettles.rioc.web.submission;

import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.Submission;
import com.haulmont.reports.gui.actions.TablePrintFormAction;

import javax.inject.Inject;

@UiController("rioc_Submission.browse")
@UiDescriptor("submission-browse.xml")
@LookupComponent("submissionsTable")
@LoadDataBeforeShow
public class SubmissionBrowse extends StandardLookup<Submission> {

    @Inject
    private Button export;

    @Inject
    private GroupTable<Submission> submissionsTable;

    @Subscribe
    private void onInit(InitEvent event) {
        TablePrintFormAction action = new TablePrintFormAction("report", submissionsTable);
        submissionsTable.addAction(action);
        export.setAction(action);
    }
}