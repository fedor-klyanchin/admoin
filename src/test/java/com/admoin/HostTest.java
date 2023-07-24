package com.admoin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.testng.AssertJUnit;
import org.testng.annotations.*;

import com.admoin.action.Action;
import com.admoin.action.Link;
import com.admoin.action.type.Type;
import com.admoin.action.type.file.Exists;

public class HostTest {// configVersion =
                       // Host.properties.getProperty(App.getConfigVersionPropertyName());
    private Host host;
    private LocalDateTime localDateTimeTestStart = LocalDateTime.now();

    @BeforeClass
    public void setUp() throws Exception {
        // code that will be invoked when this test is instantiated
        Log.create();
        Host.getProperties();
        host = new Host();
        host.setName("TEST");

        try {
            host.storeToLocalFile(host, Host.properties.getProperty("host_out_path", "host.out"));
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        host.restoreFromLocalFile();
    }

    @Test(groups = { "Host" })
    public void hostNew() throws Exception {
        AssertJUnit.assertTrue(host.configVersion == Host.properties.getProperty(App.getConfigVersionPropertyName()));
    }

    @Test(groups = { "Host" }, dependsOnMethods = { "hostNew" })
    public void hostRestoreFromLocalFile() {
        System.out.println("host.id: " + host.id);
        AssertJUnit.assertTrue(host.getName().equals("TEST"));
    }

    @Test(groups = { "Host" }, dependsOnMethods = { "hostRestoreFromLocalFile" })
    public void hostRestoreHostId() {
        host.id = Integer.parseInt(Host.properties.getProperty("id"));
        System.out.println("host.id: " + host.id);
        AssertJUnit.assertTrue(host.getName().equals("TEST"));
    }

    @Test(groups = { "Host" }, dependsOnMethods = { "hostRestoreHostId" })
    public void hostIsGetNewIdTrue() throws Exception {
        Host.setProperty("id", "0");
        AssertJUnit.assertTrue(host.isIdEqualsNull());
        Host.getProperties();
    }

    @Test(groups = { "Host" }, dependsOnMethods = { "hostIsGetNewIdTrue" })
    public void hostIsGetNewIdFalse() throws Exception {
        host.id = 1;
        AssertJUnit.assertTrue(!host.isIdEqualsNull() || host.getName().equals("TEST"));
        Host.getProperties();
    }

    @Test(groups = { "Properties" })
    public void PropertiesGetNotEmpty() throws Exception {
        AssertJUnit.assertTrue(!Host.properties.isEmpty());
    }

    @Test(groups = { "Properties" }, dependsOnMethods = { "PropertiesGetNotEmpty" })
    public void PropertiesGetLogName() {
        AssertJUnit.assertTrue(Host.properties.getProperty("log_name").equals("Log"));
    }

    @Test(groups = { "Properties" }, dependsOnMethods = { "PropertiesGetNotEmpty" })
    public void PropertiesSetProperty() throws Exception {
        Host.properties.remove("test_set_property");
        Host.storeProperties();
        Host.setProperty("test_set_property", "test_set_property");
        AssertJUnit.assertTrue(Host.properties.getProperty("test_set_property").equals("test_set_property"));
    }

    @Test(groups = { "Properties" }, dependsOnMethods = { "PropertiesSetProperty" })
    public void PropertiesSetPropertyTestGetLocalSaveProperty() throws Exception {
        Host.properties.clear();
        Host.getProperties();
        AssertJUnit.assertTrue(!Host.properties.getProperty("test_set_property", "false").equals("test_set_property"));
    }

    @Test(groups = { "Properties" }, dependsOnMethods = { "PropertiesSetPropertyTestGetLocalSaveProperty" })
    public void storeProperties() throws Exception {
        Host.setProperty("test_set_property", "test_set_property");
        Host.storeProperties();
        Host.properties.clear();
        Host.getProperties();
        AssertJUnit.assertTrue(Host.properties.getProperty("test_set_property", "false").equals("test_set_property"));
        Host.properties.remove("test_set_property");
        Host.storeProperties();
    }

    @Test(groups = { "Host" })
    public void isOldVersionTrue() throws Exception {
        Host.config.put(App.getAppVersionPropertyName(), Host.properties.getProperty(App.getAppVersionPropertyName()));
        AssertJUnit.assertFalse(Host.isOldVersion(App.getAppVersionPropertyName()));
    }

    @Test(groups = { "Host" })
    public void getPropertyIsNotEmpty() throws Exception {
        AssertJUnit.assertTrue(Host.properties.getProperty("id", null).length() != 0);
    }

    @Test(groups = { "Host" })
    public void getPropertyIsEmpty() throws Exception {
        AssertJUnit.assertTrue(Host.properties.getProperty("isEmpty", null).length() == 0);
    }

    @Test(groups = { "Host" })
    public void getPropertyisNull() throws Exception {
        AssertJUnit.assertTrue(Host.properties.getProperty("id1", null) == null);
    }

    @Test
    @BeforeGroups("HostStartMap")
    public void beforeGroupHostStartMap() throws Exception {
        Host.actionMap.clear();
        Host.actionMap.put(1, new Action(1, 1, 0));
        Host.actionMap.put(2, new Action(2, 1, 0));
        Host.actionMap.put(3, new Action(3, 1, 0));
        Host.actionMap.put(4, new Action(4, 1, 0));
        Host.actionMap.put(5, new Action(5, 1, 0));

        List<Link> linkList0 = new ArrayList<>();
        linkList0.add(new Link(0, 1, false));
        linkList0.add(new Link(0, 2, false));

        List<Link> linkList1 = new ArrayList<>();
        linkList1.add(new Link(1, 3, false));
        linkList1.add(new Link(1, 5, true));

        List<Link> linkList2 = new ArrayList<>();
        linkList2.add(new Link(2, 4, false));

        Host.actionLinkMap.clear();
        Host.actionLinkMap.put(0, linkList0);
        Host.actionLinkMap.put(1, linkList1);
        Host.actionLinkMap.put(2, linkList2);

        Host.actionTypeMap.clear();
        Host.actionTypeMap.put(1, new Type(1, "file.Exists", null));

        Host.actionTypeFileExists.put(1, new Exists("pom0.xml"));
        Host.actionTypeFileExists.put(2, new Exists("README.md"));
        Host.actionTypeFileExists.put(3, new Exists("pom.xml"));
        Host.actionTypeFileExists.put(4, new Exists("README.md"));
        Host.actionTypeFileExists.put(5, new Exists("README.md"));

        host.startActionMap();
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapOneActionTrue() throws Exception {
        AssertJUnit.assertTrue(Host.actionMap.get(2).getResult().equals("true"));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapOneActionFalse() throws Exception {
        AssertJUnit.assertFalse(Host.actionMap.get(1).getResult().equals("true"));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapTwoAction() throws Exception {
        AssertJUnit.assertTrue(
                Host.actionMap.get(1).getResult().equals("false") &&
                        Host.actionMap.get(2).getResult().equals("true"));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapTreeActionTwoLevel() throws Exception {
        AssertJUnit.assertTrue(
                Host.actionMap.get(1).getResult().equals("false") &&
                        !Host.actionMap.get(1).getLastStart().isBefore(localDateTimeTestStart) &&
                        Host.actionMap.get(2).getResult().equals("true") &&
                        !Host.actionMap.get(2).getLastStart().isBefore(localDateTimeTestStart) &&
                        Host.actionMap.get(3).getResult().equals("") &&
                        !Host.actionMap.get(3).getLastStart().isAfter(Host.actionMap.get(1).getLastStart()) &&
                        Host.actionMap.get(4).getResult().equals("true") &&
                        !Host.actionMap.get(4).getLastStart().isBefore(Host.actionMap.get(2).getLastStart()));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapTreeActionTwoLevelUseFalseResult() throws Exception {
        AssertJUnit.assertTrue(
                Host.actionMap.get(5).getResult().equals("true") &&
                        !Host.actionMap.get(4).getLastStart().isBefore(Host.actionMap.get(1).getLastStart()));
    }
}
