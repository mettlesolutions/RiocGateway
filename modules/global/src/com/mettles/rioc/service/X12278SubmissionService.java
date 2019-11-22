package com.mettles.rioc.service;

import com.mettles.rioc.X12278SubmissionServiceStatus;

public interface X12278SubmissionService {
    String NAME = "rioc_X12278SubmissionService";

    public X12278SubmissionServiceStatus ReadEDIString(String ediString);
}