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

import org.openmrs.logic.op.In;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KPTypeDataDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
public class Moh745CohortLibrary {

    protected static final Log log = LogFactory.getLog(Moh745CohortLibrary.class);


    public CohortDefinition patientVisitType(String visitType, List<Integer> facilities) {
        StringBuilder locations = new StringBuilder();
        if (facilities.size() > 1) {
            facilities.forEach(r -> locations.append( "," + r));
        } else {
            locations.append(facilities.get(0));
        }

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.visit_type = '"+visitType+"' and  i.visit_date between date(:startDate) and date(:endDate) and location_id IN ('"+locations+"') group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientVisitType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Patients Visit Type");


        return cd;
    }

    public CohortDefinition patientScreeningMethod(String[] indicatorVal, List<Integer> facilities) {
        String val = StringUtils.join(indicatorVal,"','");
        StringBuilder locations = new StringBuilder();
        if (facilities.size() > 1) {
            facilities.forEach(r -> locations.append( "," + r));

        } else {
            locations.append(facilities.get(0));
        }

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method IN ('"+val+"') and i.visit_date between date(:startDate) and date(:endDate) and location_id IN ('"+locations+"') group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Screening Method");

        System.out.println("QUERY "+ sqlQuery);
        return cd;
    }

    public CohortDefinition receivedScreeningCl(String[] indicatorVal, String visitType, List<Integer> facilities) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientScreeningMethod", ReportUtils.map(patientScreeningMethod(indicatorVal, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientScreeningMethod AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientPositiveScreening(String[] indicatorVal, List<Integer> facilities) {
        String val = StringUtils.join(indicatorVal,"','");
        StringBuilder locations = new StringBuilder();
        if (facilities.size() > 1) {
            facilities.forEach(r -> locations.append( "," + r));
        } else {
            locations.append(facilities.get(0));
        }

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method IN ('"+val+"') and i.screening_result = 'Positive' and i.visit_date between date(:startDate) and date(:endDate) and location_id IN ('"+locations+"') group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientPositiveScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Positive Screening Method");
        return cd;
    }

    public CohortDefinition receivedPositiveScreeningCl(String[] indicatorVal, String visitType, List<Integer> facilities) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedPositiveScreening", ReportUtils.map(patientPositiveScreening(indicatorVal, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedPositiveScreening AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientScreeningResult(String result, List<Integer> facilities) {
        StringBuilder locations = new StringBuilder();
        if (facilities.size() > 1) {
            facilities.forEach(r -> locations.append( "," + r));
        } else {
            locations.append(facilities.get(0));
        }
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_result = '"+result+"' and i.visit_date between date(:startDate) and date(:endDate) and location_id IN ('"+locations+"') group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientScreeningResult");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Screening Result");
        return cd;
    }

    public CohortDefinition suspiciousScreeningCl(String result, String visitType, List<Integer> facilities) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedResult", ReportUtils.map(patientScreeningResult(result, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedResult AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientTreatment(String[] treatmentMethod, List<Integer> facilities) {

        String val = StringUtils.join(treatmentMethod,"','");
        StringBuilder locations = new StringBuilder();
        if (facilities.size() > 1) {
            facilities.forEach(r -> locations.append( "," + r));
        } else {
            locations.append(facilities.get(0));
        }

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where " +
                " i.treatment_method IN ('"+val+"') and visit_date between date(:startDate) and date(:endDate) and location_id IN ('"+locations+"') group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientTreatmentMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Treatment Method");
        return cd;
    }

    public CohortDefinition treatmentMethodCl(String[] treatmentMethod, String visitType, List<Integer> facilities) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedPositiveScreening", ReportUtils.map(patientTreatment(treatmentMethod, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType, facilities), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedPositiveScreening AND patientVisitType");
        return cd;
    }

    /*HIV Positive Clients Screened*/
    public CohortDefinition HIVPositiveClientsScreenedCl(String visitType, List<Integer> facilities) {
        StringBuilder locations = new StringBuilder();
        if (facilities.size() > 1) {
            facilities.forEach(r -> locations.append( "," + r));
        } else {
            locations.append(facilities.get(0));
        }
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i left join (select h.patient_id from kenyaemr_etl.etl_hiv_enrollment h " +
                "where h.visit_date <= date(:endDate)) h on h.patient_id = i.patient_id left join (select t.patient_id from kenyaemr_etl.etl_hts_test t where " +
                "t.final_test_result = 'Positive' and t.test_type = 2 and t.visit_date <= date(:endDate)) t on t.patient_id = i.patient_id where" +
                " i.visit_type = '"+visitType+"' and (h.patient_id is not null or t.patient_id is not null) and i.visit_date between date(:startDate) and date(:endDate) and location_id IN ('"+locations+"')" +
                "group by i.patient_id;";

        cd.setName("HIV Positive Clients Screened");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Clients Screened");

        return cd;
    }

    /*HIV Positive With Positive Screening Results*/
    public CohortDefinition HIVPositiveClientsScreenedWithPositiveResultsCl(String visitType, List<Integer> facilities) {
        StringBuilder locations = new StringBuilder();
        if (facilities.size() > 1) {
            facilities.forEach(r -> locations.append( "," + r));
        } else {
            locations.append(facilities.get(0));
        }
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i left join (select h.patient_id from kenyaemr_etl.etl_hiv_enrollment h " +
                "where h.visit_date <= date(:endDate)) h on h.patient_id = i.patient_id left join (select t.patient_id from kenyaemr_etl.etl_hts_test t where " +
                "t.final_test_result = 'Positive' and t.test_type = 2 and t.visit_date <= date(:endDate)) t on t.patient_id = i.patient_id where i.visit_type = '"+visitType+"' " +
                " and i.screening_result = 'Positive' and (h.patient_id is not null or t.patient_id is not null) and  i.visit_date between date(:startDate) and " +
                "date(:endDate) and location_id IN ('"+locations+"') group by i.patient_id;";

        cd.setName("HIV Positive With Positive Screening Results");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive With Positive Screening Results");

        return cd;
    }

}
