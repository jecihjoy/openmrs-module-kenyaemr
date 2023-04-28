/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Program;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by schege on 27/04/23.
 */
public class PendingCD4ResultCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    /**
     * @see PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return "Pending CD4 result";
    }

    protected static final Log log = LogFactory.getLog(PendingCD4ResultCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Concept CD4COUNT = Dictionary.getConcept(Dictionary.CD4_COUNT);
        Concept CD4PERCENTAGE = Dictionary.getConcept(Dictionary.CD4_PERCENT);
        Concept CD4QUALITATIVE = Dictionary.getConcept(Dictionary.CD4_COUNT_QUALITATIVE);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean pendingCD4Result = false;

            OrderService orderService = Context.getOrderService();
            //In HIV program
            if (inHivProgram.contains(ptId)) {
                //Check whether client has active Serum CrAg order
                OrderType patientLabOrders = orderService.getOrderTypeByUuid(TEST_ORDER_TYPE_UUID);
                if (patientLabOrders != null) {

                    //Get active lab orders
                    List<Order> activeTestOrders = orderService.getActiveOrders(Context.getPatientService().getPatient(ptId), patientLabOrders, null, null);
                    if (activeTestOrders.size() > 0) {
                        for (Order o : activeTestOrders) {
                            Concept orderConcept = o.getConcept();
                            if (orderConcept != null) {
                                if (orderConcept.equals(CD4COUNT) || orderConcept.equals(CD4PERCENTAGE) || orderConcept.equals(CD4QUALITATIVE)) {
                                    pendingCD4Result = true;
                                }
                            }

                        }
                    }
                }
            }

            ret.put(ptId, new BooleanResult(pendingCD4Result, this));
        }
        return ret;
    }
}
