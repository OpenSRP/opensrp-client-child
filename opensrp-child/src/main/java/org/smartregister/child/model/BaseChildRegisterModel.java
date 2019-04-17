package org.smartregister.child.model;

import android.util.Log;

import org.json.JSONObject;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class BaseChildRegisterModel implements ChildRegisterContract.Model {
    private FormUtils formUtils;

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {
        // TODO Save Language
        //Map<String, String> langs = getAvailableLanguagesMap();
        //Utils.saveLanguage(Utils.getKeyByValue(langs, language));
    }

    /*private Map<String, String> getAvailableLanguagesMap() {
        return null;
        //return AncApplication.getJsonSpecHelper().getAvailableLanguagesMap();
    }*/

    @Override
    public String getLocationId(String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public List<ChildEventClient> processRegistration(String jsonString) {
        List<ChildEventClient> childEventClientList = new ArrayList<>();
        ChildEventClient childEventClient = JsonFormUtils.processChildUpdateForm(Utils.context().allSharedPreferences(), jsonString);
        if (childEventClient == null) {
            return null;
        }

        childEventClientList.add(childEventClient);

        ChildEventClient childHeadEventClient = JsonFormUtils.processMotherRegistrationForm(Utils.context().allSharedPreferences(), jsonString, childEventClient.getClient().getRelationalBaseEntityId(),childEventClient);
        if (childHeadEventClient == null) {
            return childEventClientList;
        }

        // Update the child mother
        Client childClient = childEventClient.getClient();
        childClient.addRelationship(Utils.metadata().childRegister.childCareGiverRelationKey, childHeadEventClient.getClient().getBaseEntityId());

        childEventClientList.add(childHeadEventClient);
        return childEventClientList;
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(BaseChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }

    public void setFormUtils(FormUtils formUtils) {
        this.formUtils = formUtils;
    }


    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }
}