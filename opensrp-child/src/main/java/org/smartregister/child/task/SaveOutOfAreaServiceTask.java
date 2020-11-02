package org.smartregister.child.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.location.helper.LocationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class SaveOutOfAreaServiceTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final String formString;
    private WeightRepository weightRepository;
    private VaccineRepository vaccineRepository;
    private ChildRegisterContract.ProgressDialogCallback progressDialogCallback;
    private static final String CARD_ID_PREFIX = "c_";

    public SaveOutOfAreaServiceTask(Context context, String formString, ChildRegisterContract.ProgressDialogCallback progressDialogCallback) {
        this.context = context;
        this.formString = formString;
        this.weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
        this.vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
        this.progressDialogCallback = progressDialogCallback;
    }

    /**
     * Constructs a weight object using the out of service area form
     *
     * @param openSrpContext The context to work with
     * @param outOfAreaForm  Out of area form to extract the weight form
     * @return A weight object if weight recorded in form, or {@code null} if weight not recorded
     * @throws Exception
     */
    private static Weight getWeightObject(org.smartregister.Context openSrpContext, JSONObject outOfAreaForm)
            throws Exception {
        Weight weight = null;
        JSONArray fields = outOfAreaForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
        String serviceDate = null;
        String openSrpId = null;
        String cardId = null;

        int foundFields = 0;
        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.WEIGHT_KG)) {
                foundFields++;
                if (StringUtils.isNotEmpty(curField.getString(JsonFormConstants.VALUE))) {
                    weight = new Weight();
                    weight.setBaseEntityId("");
                    weight.setKg(Float.parseFloat(curField.getString(JsonFormConstants.VALUE)));
                    weight.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                    weight.setLocationId(LocationHelper.getInstance()
                            .getOpenMrsLocationId(LocationHelper.getInstance().getDefaultLocation()));
                    weight.setUpdatedAt(null);
                }
            } else if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.OA_SERVICE_DATE)) {
                foundFields++;
                serviceDate = curField.getString(JsonFormConstants.VALUE);
            } else if (curField.getString(JsonFormConstants.KEY).equalsIgnoreCase(Constants.KEY.ZEIR_ID)
                    || curField.getString(JsonFormConstants.KEY).equalsIgnoreCase(Constants.KEY.OPENSRP_ID)) {
                foundFields++;
                openSrpId = curField.getString(JsonFormConstants.VALUE);
            } else if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.NFC_CARD_IDENTIFIER)) {
                cardId = curField.getString(JsonFormConstants.VALUE);
            }

            if (foundFields == 3) {
                break;
            }
        }

        if (weight != null && serviceDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);
            weight.setDate(dateFormat.parse(serviceDate));
        }

        if (weight != null && openSrpId != null) {
            weight.setProgramClientId(openSrpId);
        }

        if (weight != null && cardId != null) {
            weight.setProgramClientId(CARD_ID_PREFIX + "0" + cardId);
        }

        return weight;
    }

    /**
     * Constructs a list of recorded vaccines from the out of area form provided
     *
     * @param openSrpContext The context to use
     * @param outOfAreaForm  Out of area form to extract recorded vaccines from
     * @return A list of recorded vaccines
     */
    private static ArrayList<Vaccine> getVaccineObjects(Context context, org.smartregister.Context openSrpContext,
                                                        JSONObject outOfAreaForm) throws Exception {
        ArrayList<Vaccine> vaccines = new ArrayList<>();
        JSONArray fields = outOfAreaForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
        String serviceDate = null;
        String openSrpId = null;
        String cardId = null;

        for (int index = 0; index < fields.length(); index++) {
            JSONObject curField = fields.getJSONObject(index);
            if (curField.getString(JsonFormConstants.KEY).equalsIgnoreCase(Constants.KEY.ZEIR_ID) ||
                    curField.getString(JsonFormConstants.KEY).equalsIgnoreCase(Constants.KEY.OPENSRP_ID)) {
                openSrpId = curField.getString(JsonFormConstants.VALUE);
                break;
            }
        }

        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.has(Constants.IS_VACCINE_GROUP) && curField.getBoolean(Constants.IS_VACCINE_GROUP) &&
                    curField.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX)) {
                JSONArray options = curField.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                addSingleVaccine(context, openSrpContext, vaccines, options);
            } else if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.OA_SERVICE_DATE)) {
                serviceDate = curField.getString(JsonFormConstants.VALUE);
            } else if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.NFC_CARD_IDENTIFIER)) {
                cardId = curField.getString(JsonFormConstants.VALUE);
            }
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);
        for (Vaccine curVaccine : vaccines) {
            if (serviceDate != null) {
                curVaccine.setDate(dateFormat.parse(serviceDate));
            }

            if (openSrpId != null) {
                curVaccine.setProgramClientId(openSrpId);
            }

            if (cardId != null) {
                curVaccine.setProgramClientId(CARD_ID_PREFIX + "0" + cardId);
            }
        }

        return vaccines;
    }

    private static void addSingleVaccine(Context context, org.smartregister.Context openSrpContext, ArrayList<Vaccine> vaccines, JSONArray options) throws JSONException {
        for (int j = 0; j < options.length(); j++) {
            JSONObject curOption = options.getJSONObject(j);
            if (curOption.getString(JsonFormConstants.VALUE).equalsIgnoreCase(Boolean.TRUE.toString())) {
                Vaccine curVaccine = new Vaccine();
                curVaccine.setBaseEntityId("");
                curVaccine.setName(curOption.getString(JsonFormConstants.KEY));
                curVaccine.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                curVaccine.setLocationId(LocationHelper.getInstance().getOpenMrsLocationId(LocationHelper.getInstance().getDefaultLocation()));
                curVaccine.setCalculation(VaccinatorUtils.getVaccineCalculation(context, curVaccine.getName()));
                curVaccine.setUpdatedAt(null);
                vaccines.add(curVaccine);
            }
        }
    }

    public static void addVaccine(VaccineRepository vaccineRepository, Vaccine vaccine) {
        try {
            if (vaccineRepository == null || vaccine == null) {
                return;
            }

            // Add the vaccine
            vaccineRepository.add(vaccine);

            String name = vaccine.getName();
            if (StringUtils.isBlank(name) || !name.contains("/")) {
                return;
            }

            Utils.updateFTSForCombinedVaccineAlternatives(vaccineRepository, vaccine);

        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONObject form = new JSONObject(formString);

            // Create a weight object if weight was recorded
            Weight weight = getWeightObject(ChildLibrary.getInstance().context(), form);
            if (weight != null) {
                weightRepository.add(weight);
            }

            // Create a vaccine object for all recorded vaccines
            ArrayList<Vaccine> vaccines = getVaccineObjects(context, ChildLibrary.getInstance().context(), form);
            if (vaccines.size() > 0) {
                for (Vaccine curVaccine : vaccines) {
                    addVaccine(vaccineRepository, curVaccine);
                }
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (progressDialogCallback != null) {
            progressDialogCallback.dissmissProgressDialog();
        }
    }
}
