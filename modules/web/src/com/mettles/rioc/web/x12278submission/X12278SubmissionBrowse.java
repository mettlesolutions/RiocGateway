package com.mettles.rioc.web.x12278submission;

import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.reports.gui.actions.TablePrintFormAction;
import com.mettles.rioc.entity.X12278Submission;

import javax.inject.Inject;

@UiController("rioc_X12278Submission.browse")
@UiDescriptor("x12278-submission-browse.xml")
@LookupComponent("x12278SubmissionsTable")
@LoadDataBeforeShow
public class X12278SubmissionBrowse extends StandardLookup<X12278Submission> {

    @Inject
    private GroupTable<X12278Submission> x12278SubmissionsTable;

    @Subscribe
    private void OnInit(InitEvent event) {
        TablePrintFormAction action = new TablePrintFormAction("report", x12278SubmissionsTable);
        x12278SubmissionsTable.addAction(action);
    }
}