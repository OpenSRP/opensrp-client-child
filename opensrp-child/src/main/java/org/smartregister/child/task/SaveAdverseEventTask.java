package org.smartregister.child.task;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.repository.EventClientRepository;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class SaveAdverseEventTask extends AsyncTask<Void, Void, Void> {
    private final String jsonString;
    private final String locationId;
    private final String baseEntityId;
    private final String providerId;
    private final EventClientRepository db;

    private SaveAdverseEventTask(String jsonString, String locationId, String baseEntityId,
                                 String providerId, EventClientRepository eventClientRepository) {
        this.jsonString = jsonString;
        this.locationId = locationId;
        this.baseEntityId = baseEntityId;
        this.providerId = providerId;
        this.db = eventClientRepository;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            JSONObject jsonForm = new JSONObject(jsonString);

            JSONArray fields = JsonFormUtils.fields(jsonForm);
            if (fields == null) {
                return null;
            }

            String encounterDateField = JsonFormUtils.getFieldValue(fields, "Date_Reaction");

            String encounterType = JsonFormUtils.getString(jsonForm, JsonFormUtils.ENCOUNTER_TYPE);
            JSONObject metadata = JsonFormUtils.getJSONObject(jsonForm, JsonFormUtils.METADATA);

            Date encounterDate = new Date();
            if (StringUtils.isNotBlank(encounterDateField)) {
                Date dateTime = JsonFormUtils.formatDate(encounterDateField, false);
                if (dateTime != null) {
                    encounterDate = dateTime;
                }
            }

            Event event = (Event) new Event()
                    .withBaseEntityId(baseEntityId) //should be different for main and subform
                    .withEventDate(encounterDate)
                    .withEventType(encounterType)
                    .withLocationId(locationId)
                    .withProviderId(providerId)
                    .withEntityType(Constants.CHILD_TYPE)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());


            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = JsonFormUtils.getJSONObject(fields, i);
                String value = JsonFormUtils.getString(jsonObject, JsonFormUtils.VALUE);
                if (StringUtils.isNotBlank(value)) {
                    JsonFormUtils.addObservation(event, jsonObject);
                }
            }

            if (metadata != null) {
                Iterator<?> keys = metadata.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject jsonObject = JsonFormUtils.getJSONObject(metadata, key);
                    String value = JsonFormUtils.getString(jsonObject, JsonFormUtils.VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        String entityVal = JsonFormUtils.getString(jsonObject, JsonFormUtils.OPENMRS_ENTITY);
                        if (entityVal != null) {
                            if (entityVal.equals(JsonFormUtils.CONCEPT)) {
                                JsonFormUtils.addToJSONObject(jsonObject, Constants.KEY.KEY, key);
                                JsonFormUtils.addObservation(event, jsonObject);
                            } else if ("encounter".equals(entityVal)) {
                                String entityIdVal = JsonFormUtils.getString(jsonObject, JsonFormUtils.OPENMRS_ENTITY_ID);
                                if (entityIdVal.equals(
                                        FormEntityConstants.Encounter.encounter_date.name())) {
                                    Date eDate = JsonFormUtils.formatDate(value, false);
                                    if (eDate != null) {
                                        event.setEventDate(eDate);
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (event != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
                db.addEvent(event.getBaseEntityId(), eventJson);

            }

        } catch (Exception e) {
            Log.e(SaveAdverseEventTask.class.getCanonicalName(), Log.getStackTraceString(e));
        }

        return null;
    }

}