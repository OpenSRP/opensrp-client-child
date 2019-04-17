package org.smartregister.child.contract;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public interface ChildRegisterContract {

    interface View extends BaseRegisterContract.View {
        ChildRegisterContract.Presenter presenter();
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void saveLanguage(String language);

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveForm(String jsonString, boolean isEditMode);

        void closeChildRecord(String jsonString);

    }

    interface Model {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        List<ChildEventClient> processRegistration(String jsonString);

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws Exception;

        String getInitials();

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void getNextUniqueId(Triple<String, String, String> triple, ChildRegisterContract.InteractorCallBack callBack);

        void saveRegistration(final List<ChildEventClient> childEventClientList, final String jsonString, final boolean isEditMode, final ChildRegisterContract.InteractorCallBack callBack);

        void removeChildFromRegister(String closeFormJsonString, String providerId);

    }

    interface InteractorCallBack {

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(boolean isEdit);

    }
}