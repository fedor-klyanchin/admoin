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
    private LocalDateTime startActionMapSecond;

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

        Host.restoreFromLocalFile();
        Host.getProperties();
    }

    @Test(groups = { "Host" })
    public void hostNew() throws Exception {
        AssertJUnit.assertTrue(host.getId()== Integer.parseInt(Host.properties.getProperty("id")));
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
        Host.storeProperties(Host.properties, Host.pathPropertiesCurrent);
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
        Host.storeProperties(Host.properties, Host.pathPropertiesCurrent);
        Host.properties.clear();
        Host.getProperties();
        AssertJUnit.assertTrue(Host.properties.getProperty("test_set_property", "false").equals("test_set_property"));
        Host.properties.remove("test_set_property");
        Host.storeProperties(Host.properties, Host.pathPropertiesCurrent);
    }

    @Test(groups = { "Host" })
    public void isOldVersionTrue() throws Exception {
        host.getConfig().put(App.getAppVersionPropertyName(), Host.properties.getProperty(App.getAppVersionPropertyName()));
        AssertJUnit.assertFalse(host.isOldVersion(App.getAppVersionPropertyName()));
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
        Action.map.clear();
        Action.map.put(1, new Action(1, 1, 0));
        Action.map.put(2, new Action(2, 1, 0));
        Action.map.put(3, new Action(3, 1, 0));
        Action.map.put(4, new Action(4, 1, 0));
        Action.map.put(5, new Action(5, 1, 0));
        Action.map.put(6, new Action(6, 1, 60));
        Action.map.put(8, new Action(8, 1, 0));

        List<Link> linkList0 = new ArrayList<>();
        linkList0.add(new Link(0, 1, false));
        linkList0.add(new Link(0, 2, false));
        linkList0.add(new Link(0, 6, false));

        List<Link> linkList1 = new ArrayList<>();
        linkList1.add(new Link(1, 3, false));
        linkList1.add(new Link(1, 5, true));

        List<Link> linkList2 = new ArrayList<>();
        linkList2.add(new Link(2, 4, false));

        List<Link> linkList6 = new ArrayList<>();
        linkList6.add(new Link(6, 8, false));

        Link.map.clear();
        Link.map.put(0, linkList0);
        Link.map.put(1, linkList1);
        Link.map.put(2, linkList2);
        Link.map.put(6, linkList6);

        Type.map.clear();
        Type.map.put(1, new Type(1, "file.Exists", null));

        Exists.map.put(1, new Exists("pom0.xml"));
        Exists.map.put(2, new Exists("README.md"));
        Exists.map.put(3, new Exists("pom.xml"));
        Exists.map.put(4, new Exists("README.md"));
        Exists.map.put(5, new Exists("README.md"));
        Exists.map.put(6, new Exists("README.md"));
        Exists.map.put(8, new Exists("README.md"));

        host.startActionMap();
        startActionMapSecond = LocalDateTime.now();
        host.startActionMap();
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapOneActionTrue() throws Exception {
        AssertJUnit.assertTrue(Action.map.get(2).getResult().equals("true"));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapOneActionFalse() throws Exception {
        AssertJUnit.assertFalse(Action.map.get(1).getResult().equals("true"));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapTwoAction() throws Exception {
        AssertJUnit.assertTrue(
                Action.map.get(1).getResult().equals("false") &&
                        Action.map.get(2).getResult().equals("true"));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapTreeActionTwoLevel() throws Exception {
        AssertJUnit.assertTrue(
                Action.map.get(1).getResult().equals("false") &&
                        !Action.map.get(1).getLastStart().isBefore(localDateTimeTestStart) &&
                        Action.map.get(2).getResult().equals("true") &&
                        !Action.map.get(2).getLastStart().isBefore(localDateTimeTestStart) &&
                        Action.map.get(3).getResult().equals("") &&
                        !Action.map.get(3).getLastStart().isAfter(Action.map.get(1).getLastStart()) &&
                        Action.map.get(4).getResult().equals("true") &&
                        !Action.map.get(4).getLastStart().isBefore(Action.map.get(2).getLastStart()));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionMapTreeActionTwoLevelUseFalseResult() throws Exception {
        AssertJUnit.assertTrue(
                Action.map.get(5).getResult().equals("true") &&
                        !Action.map.get(4).getLastStart().isBefore(Action.map.get(1).getLastStart()));
    }

    @Test(groups = { "HostStartMap" }, dependsOnMethods = { "beforeGroupHostStartMap" })
    public void startActionTestStartIntervalSeconds() throws Exception {
        LocalDateTime startActionFirst = Action.map.get(6).getLastStart();
        LocalDateTime startActionSecond = Action.map.get(8).getLastStart();

        AssertJUnit.assertTrue(
            !startActionMapSecond.isBefore(startActionFirst) &&
            !startActionMapSecond.isBefore(startActionSecond));
    }

    @Test
    public void testRestoreFromLocalFile() throws Exception {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        host.onlineDateTime = localDateTimeNow;
        host.storeToLocalFile(host, Host.properties.getProperty("host_out_path", "hostTest.out"));

        Host hostRestore = Host.restoreFromLocalFile();

        AssertJUnit.assertTrue(hostRestore.onlineDateTime.equals(localDateTimeNow));
    }
}
