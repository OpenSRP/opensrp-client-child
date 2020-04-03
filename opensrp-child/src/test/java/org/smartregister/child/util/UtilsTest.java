package org.smartregister.child.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.ChildLibrary;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.UniqueId;
import org.smartregister.repository.UniqueIdRepository;

import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChildLibrary.class)
public class UtilsTest {
    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getNextOpenMrsId() {
        UniqueId uniqueId = new UniqueId();
        uniqueId.setId("1");
        uniqueId.setCreatedAt(new Date());
        uniqueId.setOpenmrsId("34334-9");
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.when(childLibrary.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        PowerMockito.when(uniqueIdRepository.getNextUniqueId()).thenReturn(uniqueId);
        Assert.assertEquals(uniqueId.getOpenmrsId(), Utils.getNextOpenMrsId());

    }

    @Test
    public void getChildBirthDate() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FormEntityConstants.Person.birthdate.toString(), "2019-09-04T03:00:00.000+03:00");
        String expected = "2019-09-04";
        Assert.assertEquals(expected, Utils.getChildBirthDate(jsonObject));
    }

    @Test
    public void testFormatIdentifiersFormatsValueCorrectly(){
        String testString = "3929389829839829839835";
        String formattedIdentifier = Utils.formatIdentifiers(testString);
        Assert.assertEquals("3929-3898-2983-9829-8398-35",formattedIdentifier);
    }
}