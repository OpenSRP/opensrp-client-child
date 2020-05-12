package org.smartregister.child.util;

import android.widget.TableRow;

import org.joda.time.DateTime;
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
import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.UniqueId;
import org.smartregister.repository.UniqueIdRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChildLibrary.class)
public class UtilsTest {
    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Mock
    android.content.Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
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

    @Test
    public void testPutAll() {
        Map<String, String> map = new HashMap<>();
        map.put("1","one");
        map.put("2","two");

        Map<String, String> extend = new HashMap<>();
        extend.put("3","three");
        extend.put("4", null);

        Utils.putAll(map, extend);
        Assert.assertEquals(3, map.size());
    }

    @Test
    public void testFormatNumber() {
        String s1 = Utils.formatNumber("1234W");
        String s2 = Utils.formatNumber("01234");
        String s3 = Utils.formatNumber(" 1234");

        Assert.assertEquals("1234", s1);
        Assert.assertEquals("1234", s2);
        Assert.assertEquals(" 1234", s3);
    }

    @Test
    public void testBold() {
        String s1 = Utils.bold("test");

        Assert.assertEquals("<b>test</b>", s1);
    }

    @Test
    public void testReverseHyphenatedString() {
        String s1 = Utils.reverseHyphenatedString("04-05-2020");

        Assert.assertEquals("2020-05-04", s1);
    }

    @Test
    public void testGetDate() {
        Date dt1 = Utils.getDate("2020-05-05 11:46:00");
        Date dt2 = Utils.getDate("20200505997782329");

        Assert.assertNotNull(dt1);
        Assert.assertNull(dt2);
    }

    @Test
    public void testFormatAtBirthKey() {
        String s1 = Utils.formatAtBirthKey("birth");
        String s2 = Utils.formatAtBirthKey("Birth");
        String s3 = Utils.formatAtBirthKey("test");

        Assert.assertEquals("at_birth", s1);
        Assert.assertEquals("at_Birth", s2);
        Assert.assertEquals("test", s3);
    }

    @Test
    public void testGetProfileImageResourceIDentifier() {
        int i = Utils.getProfileImageResourceIDentifier();
        Assert.assertEquals(R.mipmap.ic_child, i);
    }

    @Test
    public void testAddAsInts() {
        int i = Utils.addAsInts(true, "10", "", "12");
        Assert.assertEquals(22, i);
    }

    @Test
    public void testGetTodaysDate() {
        String date1 = Utils.getTodaysDate();
        String date2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Assert.assertEquals(date2, date1);
    }

    @Test
    public void testContext() {
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        Context context = Utils.context();
        Assert.assertEquals(childLibrary.context(), context);
    }

    @Test
    public void TestGetWeeksDue() {
        int i = Utils.getWeeksDue(new DateTime());

        Assert.assertEquals(0, 0);
    }

    @Test
    public void testGetDataRow() {
        TableRow tr = Utils.getDataRow(context);
        Assert.assertEquals(0, tr.getPaddingTop());
    }

    @Test
    public void testGetDataRow2() {
        TableRow tr = Utils.getDataRow(context, "label", "value", null);
        Assert.assertEquals(0, tr.getPaddingTop());
    }

    @Test
    public void testGetDataRow3() {
        TableRow tr = Utils.getDataRow(context, "label", "value", "field", null);
        Assert.assertEquals(0, tr.getPaddingTop());
    }
}