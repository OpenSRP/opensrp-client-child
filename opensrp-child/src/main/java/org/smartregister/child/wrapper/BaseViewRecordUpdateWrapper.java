package org.smartregister.child.wrapper;

import android.view.View;

import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by ndegwamartin on 04/03/2019.
 */
public class BaseViewRecordUpdateWrapper {

    private View convertView;
    private String inactive;
    private SmartRegisterClient client;

    private String lostToFollowUp;

    public View getConvertView() {
        return convertView;
    }

    public void setConvertView(View convertView) {
        this.convertView = convertView;
    }

    public String getLostToFollowUp() {
        return lostToFollowUp;
    }

    public void setLostToFollowUp(String lostToFollowUp) {
        this.lostToFollowUp = lostToFollowUp;
    }

    public String getInactive() {
        return inactive;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }

    public SmartRegisterClient getClient() {
        return client;
    }

    public void setClient(SmartRegisterClient client) {
        this.client = client;
    }
}
