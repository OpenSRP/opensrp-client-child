package org.smartregister.child.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-06-11.
 */
public class Field {

    private String key;

    private String type;

    @SerializedName("sub_type")
    private String subType;

    @SerializedName("render_type")
    private String renderType;

    private List<String> keys;

    private List<String> values;

    private String hint;

    @SerializedName("entity_id")
    private String entityId;

    @SerializedName("openmrs_entity")
    private String openmrsEntity;

    @SerializedName("openmrs_entity_id")
    private String openmrsEntityId;

    @SerializedName("options")
    private List<Map<String, String>> options;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getOpenmrsEntity() {
        return openmrsEntity;
    }

    public String getOpenmrsEntityId() {
        return openmrsEntityId;
    }

    public List<String> getKeys() {
        return keys;
    }

    public List<String> getValues() {
        return values;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getRenderType() {
        return renderType;
    }

    public List<Map<String, String>> getOptions() {
        return options;
    }
}
