package org.smartregister.child.cursor;

import android.database.Cursor;

import org.smartregister.child.util.DBConstants;

public class CreateRemoteLocalCursor {
    private String id;
    private String relationalId;
    private String firstName;
    private String lastName;
    private String dob;
    private String zeirId;

    private String phoneNumber;
    private String altName;

    public CreateRemoteLocalCursor(Cursor cursor, boolean isRemote) {
        if (isRemote) {
            id = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ID_LOWER_CASE));
        } else {
            id = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID));
        }
        relationalId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.RELATIONALID));
        firstName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FIRST_NAME));
        lastName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.LAST_NAME));
        dob = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DOB));
        zeirId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ZEIR_ID));
    }

    public String getId() {
        return id;
    }

    public String getRelationalId() {
        return relationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAltName() {
        return altName;
    }

    public String getZeirId() {
        return zeirId;
    }

    public void setZeirId(String zeirId) {
        this.zeirId = zeirId;
    }
}