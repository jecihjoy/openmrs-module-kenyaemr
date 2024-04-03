/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.Moh705ReportUtils.DiagnosisLists;
import org.openmrs.module.kenyaemr.reporting.Moh705ReportUtils.ReportAddonUtils;
import org.openmrs.module.kenyaemr.reporting.Moh705ReportUtils.ReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH705.MOH705IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Report builder for MOH705A
 * Under 5 years
 * Diagnosis
 */
@Component
@Builds({ "kenyaemr.common.report.moh705A" })
public class MOH705AReportBuilder extends AbstractReportBuilder {
	static final String AGE_BELOW_FIVE = "<5";
	static final int DIARRHOEA_WITHOUT_DEHYDRATION = 168737;
	static final int DIARRHOEA_WITH_SOME_DEHYDRATION = 166569;
	static final int DIARRHOEA_WITH_SEVERE_DEHYDRATION = 168736 ;
	static final int CHOLERA = 145622;	
	static final int CONFIRMED_MALARIA = 160148;
	static final int TESTED_MALARIA = 168740;
	static final int SUSPECTED_MALARIA = 166623;
	static final int PRESUMED_TUBERCULOSIS = 168739;

	private MOH705IndicatorLibrary moh705indicatorLibrary;

	private final CommonDimensionLibrary commonDimensionLibrary;

	@Autowired
	public MOH705AReportBuilder(MOH705IndicatorLibrary moh705indicatorLibrary, CommonDimensionLibrary commonDimensionLibrary) {
		this.moh705indicatorLibrary = moh705indicatorLibrary;
		this.commonDimensionLibrary = commonDimensionLibrary;
	}

	@Override
	protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
		return Arrays.asList(new Parameter("startDate", "Start Date", Date.class), new Parameter("endDate", "End Date",
		        Date.class), new Parameter("dateBasedReporting", "", String.class));
	}

	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor,
	        ReportDefinition reportDefinition) {
		return Arrays.asList(ReportUtils.map(moh705ADataset(), "startDate=${startDate},endDate=${endDate}"));
	}

	protected DataSetDefinition moh705ADataset() {
		String indParams = "startDate=${startDate},endDate=${endDate}";

		CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
		cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cohortDsd.setName("MOH705A");
		cohortDsd.setDescription("MOH 705A");
		cohortDsd.addDimension("day", ReportUtils.map(commonDimensionLibrary.encountersOfMonthPerDay(), indParams));

		// populate datasets
		ReportingUtils.addRow(cohortDsd,"DWND","Diarrhoea with no dehydration",ReportUtils.map(moh705indicatorLibrary.diagnosis(Arrays.asList(DIARRHOEA_WITHOUT_DEHYDRATION),AGE_BELOW_FIVE),indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DWSOD","Diarrhoea with some dehydration",ReportUtils.map(moh705indicatorLibrary.diagnosis(Arrays.asList(DIARRHOEA_WITH_SOME_DEHYDRATION),AGE_BELOW_FIVE),indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DWSED","Diarrhoea with severe dehydration",ReportUtils.map(moh705indicatorLibrary.diagnosis(Arrays.asList(DIARRHOEA_WITH_SEVERE_DEHYDRATION),AGE_BELOW_FIVE),indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"CLC","Cholera",ReportUtils.map(moh705indicatorLibrary.diagnosis(Arrays.asList(CHOLERA),AGE_BELOW_FIVE),indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"TC","Tuberculosis",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getTuberculosisDiagnosisList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"TCP","Presumed Tuberculosis",ReportUtils.map(moh705indicatorLibrary.diagnosis(Arrays.asList(PRESUMED_TUBERCULOSIS), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DYC","Dysentery",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getDysenteryList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"GAS","Gastroenteritis",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getGastroenteritisList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"SP","Severe pneumonia",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getSeverePneumoniaList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "LTI", "Lower Respiratory Tract Infections", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getSLowerTractInfectionList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"NS","Neonatal Sepsis",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getNeutalSepsisList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"AM","AMOEBIASIS",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getAmoebiasis(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DS","Down’s syndrome",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getDownSyndromeList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"RF","Rheumatic Fever",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getReumonicFeverList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"CKU","Chikungunya fever",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getChikungunyaFeverList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DENF","Dengue fever",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getDengueFeverList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"CL", "Cutaneous Leishmaniasis",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getCutaneousLeishmaniasisList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"ANT","Suspected Anthrax",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getAnthraxList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"CLC","Cholera",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getCholeraList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"MCC","Meningococcal Meningitis",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getMeningococcalMeningitisList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"OMC", "Other Menignitis", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getOtherMenigitisList(),	AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"NNC", "NeonatalTetanus", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getNeonatalTetanusList(),	AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"PMC","Poliomyelitis", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getPoliomyelitisList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"CPC","Chicken Pox",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getChickenPoxList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"MSC","Measles",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getMeaslesList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"HPC","Hepatitis", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getHepatitisList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"MPC","Mumps", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getMumpsList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"SMC", "Suspected Malaria", ReportUtils.map(moh705indicatorLibrary.diagnosis(Arrays.asList(SUSPECTED_MALARIA), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"TMC", "Tested Malaria", ReportUtils.map(moh705indicatorLibrary.diagnosis(Arrays.asList(TESTED_MALARIA), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"CMC", "Confirmed Malaria", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getMalariaList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"UTC","Urinary Tract Infection", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getUrinaryTractInfectionList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"TYC","Typhoid Fever", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getTyphoidList(),	AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"BLC","Bilharzia", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getBilharziaList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "IWC","Interstinal worms", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getInterstinalwormsList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"MNC","Malnutrition", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getMalnutritionList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"ANC","Anaemia",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getAnaemiaList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"EC","Eye Infections", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getEyeInfectionsList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"EIC","Ear Infections Conditions", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getEarInfectionsConditionsList(),AGE_BELOW_FIVE), indParams),	ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"URC","Upper Respiratory Tract Infections", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getUpperRespiratoryTractInfectionsList(), AGE_BELOW_FIVE), indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"ASC","Asthma", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getAsthmaList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"TSC","Tonsilities",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getTonsilitiesList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"PNC","Pneumonia", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getPneumoniaList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "MDC", "Mental Disorders", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getMentalDisordersList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "DDC", "DentalDisorders", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getDentalDisordersList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "JIC", "Jiggers Infestation", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getJiggersInfestationList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "DOC", "Disease Of The Skin", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getDiseaseOfTheSkinList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"PC",	"Poisoning",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getPoisoningList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "RTC", "Road TrafficI Injuries", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getRoadTrafficInjuriesList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"OIC","Other Injuries", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getOtherInjuriesList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"SAC","Sexual Assault", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getSexualAssaultList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"BC","Burns",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getBurnsList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"SBC","Snake Bites",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getSnakeBitesList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DBC","Dog BITES",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getDogBitesList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"OBC","Other Bites",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getOtherBitesList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DTC","Diabetes", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getDiabetesList(),	AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
    	ReportingUtils.addRow(cohortDsd,"EPC","Epilepsy", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getEpilepsyList(),	AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"OCDC","Other Convulsive Disorders", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getOtherConvulsiveDisordersList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"BRC","Brucellosis",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getBrucellosisList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"RKC","Rickets", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getRicketsList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"VRC","Violence related  injuries", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getViolenceRelatedInjuriesList(), AGE_BELOW_FIVE), indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"CRPC","Cerebral Palsy",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getCerebralPalsyList(),	AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"ATC","Autism",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getAutismList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"TRC","Tryponomiasis", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getTryponomiasisList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"KLC","Kalazar leishmaniasis",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getKalazarLeishmaniasisList(), AGE_BELOW_FIVE), indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"YFC","Yellow Fever", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getYellowFeverList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"VHC","Viral Haemorrhagic Fever", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getViralHaemorrhagicFeverList(), AGE_BELOW_FIVE), indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"OVC","Overweight", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getOvrerweightList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"SCC","Suspected Childhood Cancers",	ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getChildHoodCancerist(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"HYPO","Hypoxaemia (Spo2<90%)", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getHypoxaemiaList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"RVF","Rift valley fever", ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getRiftValleyFeverList(), AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"DRTC","Deaths due to Road Traffic Injuries",ReportUtils.map(moh705indicatorLibrary.diagnosis(DiagnosisLists.getRoadTrafficInjuriesList(),AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "NFAC", "No of first attendances", ReportUtils.map(moh705indicatorLibrary.newAttendances(AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd, "RAC", "Re-attendances", ReportUtils.map(moh705indicatorLibrary.reAttendances(AGE_BELOW_FIVE), indParams),	ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"RFHC","Referrals from other health facility", ReportUtils.map(moh705indicatorLibrary.referralsFromOtherFacilities(AGE_BELOW_FIVE), indParams),ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"RFCC","Referrals to other health facility", ReportUtils.map(moh705indicatorLibrary.referralsToOtherFacilities(AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
		ReportingUtils.addRow(cohortDsd,"AODC","All other diseases for children",ReportUtils.map(moh705indicatorLibrary.allOtherDiseasesAboveFive(AGE_BELOW_FIVE), indParams), ReportAddonUtils.getAdultChildrenColumns());
			//TODO: Referrals from community unit
			//TODO:Referrals to community unit
		return cohortDsd;

	}
}
