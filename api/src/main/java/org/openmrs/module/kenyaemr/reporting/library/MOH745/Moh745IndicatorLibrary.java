/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.MOH745;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openmrs.module.kenyacore.report.ReportUtils;

import java.util.List;

@Component
public class Moh745IndicatorLibrary {

    @Autowired
    private Moh745CohortLibrary Moh745CohortLibrary;

    /*Received Screening*/
    public CohortIndicator receivedScreening(String[] indicatorVal, String visitType, List<Integer> facility) {

        return cohortIndicator("Received Screening", ReportUtils.map(Moh745CohortLibrary.receivedScreeningCl(indicatorVal , visitType, facility), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received Positive Screening Result*/
    public CohortIndicator receivedPositiveScreening(String[] indicatorVal, String visitType, List<Integer> facilities) {

        return cohortIndicator("Received Positive Screening", ReportUtils.map(Moh745CohortLibrary.receivedPositiveScreeningCl(indicatorVal, visitType, facilities), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Suspicious Screening Result*/
    public CohortIndicator receivedSuspiciousScreening(String result, String visitType, List<Integer> facilities) {

        return cohortIndicator("Received Suspicious Screening", ReportUtils.map(Moh745CohortLibrary.suspiciousScreeningCl( result, visitType, facilities), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Treatment Method */
    public CohortIndicator treatedMethod(String[] treatmentMethod, String visitType, List<Integer> facilities) {

        return cohortIndicator("Treatment Method", ReportUtils.map(Moh745CohortLibrary.treatmentMethodCl(treatmentMethod, visitType, facilities), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*HIV Positive Clients Screened*/
    public CohortIndicator HIVPositiveClientsScreened(String visitType, List<Integer> facilities) {

        return cohortIndicator("HIV Positive Clients Screened",map(Moh745CohortLibrary.HIVPositiveClientsScreenedCl(visitType, facilities), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*HIV Positive With Positive Screening Results*/
    public CohortIndicator HIVPositiveClientsScreenedWithPositiveResults(String visitType, List<Integer> facilities) {

        return cohortIndicator("HIV Positive With Positive Screening Results",map(Moh745CohortLibrary.HIVPositiveClientsScreenedWithPositiveResultsCl(visitType, facilities), "startDate=${startDate},endDate=${endDate}")
        );
    }

}


