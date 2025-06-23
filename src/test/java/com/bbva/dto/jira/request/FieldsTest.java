package com.bbva.dto.jira.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FieldsTest {

    private Fields fields;

    @BeforeEach
    void setUp() {
        fields = new Fields();
    }

    @Test
    void testStringFields() {
        String testValue = "test";

        fields.setCustomfield_13200(testValue);
        assertEquals(testValue, fields.getCustomfield_13200());

        fields.setCustomfield_12100(testValue);
        assertEquals(testValue, fields.getCustomfield_12100());

        fields.setCustomfield_11800(testValue);
        assertEquals(testValue, fields.getCustomfield_11800());

        fields.setCustomfield_10260(testValue);
        assertEquals(testValue, fields.getCustomfield_10260());

        fields.setCustomfield_10009(testValue);
        assertEquals(testValue, fields.getCustomfield_10009());

        fields.setCustomfield_10004(testValue);
        assertEquals(testValue, fields.getCustomfield_10004());

        fields.setCustomfield_12530(testValue);
        assertEquals(testValue, fields.getCustomfield_12530());

        fields.setCustomfield_19700(testValue);
        assertEquals(testValue, fields.getCustomfield_19700());

        fields.setCustomfield_13702(testValue);
        assertEquals(testValue, fields.getCustomfield_13702());

        fields.setCustomfield_13701(testValue);
        assertEquals(testValue, fields.getCustomfield_13701());

        fields.setCustomfield_18000(testValue);
        assertEquals(testValue, fields.getCustomfield_18000());

        fields.setCustomfield_10006(testValue);
        assertEquals(testValue, fields.getCustomfield_10006());

        fields.setCustomfield_12323(testValue);
        assertEquals(testValue, fields.getCustomfield_12323());

        fields.setSummary(testValue);
        assertEquals(testValue, fields.getSummary());

        fields.setDescription(testValue);
        assertEquals(testValue, fields.getDescription());
    }

    @Test
    void testNumericFields() {
        Double doubleValue = 123.45;
        Integer integerValue = 42;

        fields.setCustomfield_11000(doubleValue);
        assertEquals(doubleValue, fields.getCustomfield_11000());

        fields.setCustomfield_10002(doubleValue);
        assertEquals(doubleValue, fields.getCustomfield_10002());

        fields.setWorkratio(integerValue);
        assertEquals(integerValue, fields.getWorkratio());
    }

    @Test
    void testDateTimeFields() {
        LocalDateTime now = LocalDateTime.now();

        fields.setLastViewed(now);
        assertEquals(now, fields.getLastViewed());

        fields.setResolutiondate(now);
        assertEquals(now, fields.getResolutiondate());

        fields.setUpdated(now);
        assertEquals(now, fields.getUpdated());

        fields.setCreated(now);
        assertEquals(now, fields.getCreated());
    }

    @Test
    void testComplexObjectFields() {
        Resolution resolution = new Resolution();
        Assignee assignee = new Assignee();
        Reporter reporter = new Reporter();
        Progress progress = new Progress();
        Votes votes = new Votes();
        Issuetype issuetype = new Issuetype();
        Project project = new Project();
        Watches watches = new Watches();
        Priority priority = new Priority();
        Status status = new Status();
        Creator creator = new Creator();
        Aggregateprogress aggregateprogress = new Aggregateprogress();

        fields.setResolution(resolution);
        assertEquals(resolution, fields.getResolution());

        fields.setAssignee(assignee);
        assertEquals(assignee, fields.getAssignee());

        fields.setReporter(reporter);
        assertEquals(reporter, fields.getReporter());

        fields.setProgress(progress);
        assertEquals(progress, fields.getProgress());

        fields.setVotes(votes);
        assertEquals(votes, fields.getVotes());

        fields.setIssuetype(issuetype);
        assertEquals(issuetype, fields.getIssuetype());

        fields.setProject(project);
        assertEquals(project, fields.getProject());

        fields.setWatches(watches);
        assertEquals(watches, fields.getWatches());

        fields.setPriority(priority);
        assertEquals(priority, fields.getPriority());

        fields.setStatus(status);
        assertEquals(status, fields.getStatus());

        fields.setCreator(creator);
        assertEquals(creator, fields.getCreator());

        fields.setAggregateprogress(aggregateprogress);
        assertEquals(aggregateprogress, fields.getAggregateprogress());
    }

    @Test
    void testListFields() {
        List<String> stringList = Arrays.asList("a", "b");
        List<Object> objectList = Arrays.asList(new Object(), "obj2");
        List<Customfield> cfList = Arrays.asList(new Customfield(), new Customfield());

        Issuelink issuelink = new Issuelink();
        Subtask subtask = new Subtask();

        fields.setLabels(stringList);
        assertEquals(stringList, fields.getLabels());

        fields.setComponents(objectList);
        assertEquals(objectList, fields.getComponents());

        fields.setIssuelinks(Arrays.asList(issuelink));
        assertEquals(issuelink, fields.getIssuelinks().get(0));

        fields.setSubtasks(Arrays.asList(subtask));
        assertEquals(subtask, fields.getSubtasks().get(0));

        fields.setCustomfield_13300(stringList);
        assertEquals(stringList, fields.getCustomfield_13300());

        fields.setCustomfield_10264(stringList);
        assertEquals(stringList, fields.getCustomfield_10264());

        fields.setCustomfield_14623(objectList);
        assertEquals(objectList, fields.getCustomfield_14623());

        fields.setCustomfield_11101(objectList);
        assertEquals(objectList, fields.getCustomfield_11101());

        fields.setFixVersions(objectList);
        assertEquals(objectList, fields.getFixVersions());

        fields.setVersions(objectList);
        assertEquals(objectList, fields.getVersions());

        fields.setCustomfield_16303(cfList);
        assertEquals(cfList, fields.getCustomfield_16303());

        fields.setCustomfield_16302(cfList);
        assertEquals(cfList, fields.getCustomfield_16302());

        fields.setCustomfield_16301(cfList);
        assertEquals(cfList, fields.getCustomfield_16301());

        fields.setCustomfield_16300(cfList);
        assertEquals(cfList, fields.getCustomfield_16300());

        fields.setCustomfield_16304(cfList);
        assertEquals(cfList, fields.getCustomfield_16304());

        fields.setCustomfield_10601(cfList);
        assertEquals(cfList, fields.getCustomfield_10601());

        fields.setCustomfield_10600(cfList);
        assertEquals(cfList, fields.getCustomfield_10600());

        fields.setAttachment(objectList);
        assertEquals(objectList, fields.getAttachment());
    }

    @Test
    void testCustomfieldTypeFields() {
        Customfield cf = new Customfield();

        fields.setCustomfield_19200(cf);
        assertEquals(cf, fields.getCustomfield_19200());

        fields.setCustomfield_12129(cf);
        assertEquals(cf, fields.getCustomfield_12129());

        fields.setCustomfield_20301(cf);
        assertEquals(cf, fields.getCustomfield_20301());

        fields.setCustomfield_20302(cf);
        assertEquals(cf, fields.getCustomfield_20302());

        fields.setCustomfield_20300(cf);
        assertEquals(cf, fields.getCustomfield_20300());

        fields.setCustomfield_18900(cf);
        assertEquals(cf, fields.getCustomfield_18900());

        fields.setCustomfield_14301(cf);
        assertEquals(cf, fields.getCustomfield_14301());

        fields.setCustomfield_14302(cf);
        assertEquals(cf, fields.getCustomfield_14302());

        fields.setCustomfield_17003(cf);
        assertEquals(cf, fields.getCustomfield_17003());

        fields.setCustomfield_17002(cf);
        assertEquals(cf, fields.getCustomfield_17002());

        fields.setCustomfield_12115(cf);
        assertEquals(cf, fields.getCustomfield_12115());

        fields.setCustomfield_10270(cf);
        assertEquals(cf, fields.getCustomfield_10270());

        fields.setCustomfield_10100(cf);
        assertEquals(cf, fields.getCustomfield_10100());

        fields.setCustomfield_11311(cf);
        assertEquals(cf, fields.getCustomfield_11311());

        fields.setCustomfield_11313(cf);
        assertEquals(cf, fields.getCustomfield_11313());

        fields.setCustomfield_11304(cf);
        assertEquals(cf, fields.getCustomfield_11304());

        fields.setCustomfield_20107(cf);
        assertEquals(cf, fields.getCustomfield_20107());

        fields.setCustomfield_20108(cf);
        assertEquals(cf, fields.getCustomfield_20108());

        fields.setCustomfield_20105(cf);
        assertEquals(cf, fields.getCustomfield_20105());

        fields.setCustomfield_20106(cf);
        assertEquals(cf, fields.getCustomfield_20106());

        fields.setCustomfield_20103(cf);
        assertEquals(cf, fields.getCustomfield_20103());

        fields.setCustomfield_20104(cf);
        assertEquals(cf, fields.getCustomfield_20104());

        fields.setCustomfield_12600(cf);
        assertEquals(cf, fields.getCustomfield_12600());

        fields.setCustomfield_11511(cf);
        assertEquals(cf, fields.getCustomfield_11511());

        fields.setCustomfield_18700(cf);
        assertEquals(cf, fields.getCustomfield_18700());

        fields.setCustomfield_18701(cf);
        assertEquals(cf, fields.getCustomfield_18701());
    }

    @Test
    void testCustomfieldTypeFields2() {
        Customfield cf = new Customfield();

        fields.setCustomfield_11502(cf);
        assertEquals(cf, fields.getCustomfield_11502());

        fields.setCustomfield_11501(cf);
        assertEquals(cf, fields.getCustomfield_11501());

        fields.setCustomfield_13800(cf);
        assertEquals(cf, fields.getCustomfield_13800());

        fields.setCustomfield_11503(cf);
        assertEquals(cf, fields.getCustomfield_11503());

        fields.setCustomfield_11506(cf);
        assertEquals(cf, fields.getCustomfield_11506());

        fields.setCustomfield_11507(cf);
        assertEquals(cf, fields.getCustomfield_11507());

        fields.setCustomfield_11509(cf);
        assertEquals(cf, fields.getCustomfield_11509());

        fields.setCustomfield_12154(cf);
        assertEquals(cf, fields.getCustomfield_12154());

        fields.setCustomfield_17601(cf);
        assertEquals(cf, fields.getCustomfield_17601());

        fields.setCustomfield_12153(cf);
        assertEquals(cf, fields.getCustomfield_12153());

        fields.setCustomfield_17600(cf);
        assertEquals(cf, fields.getCustomfield_17600());

        fields.setCustomfield_17604(cf);
        assertEquals(cf, fields.getCustomfield_17604());

        fields.setCustomfield_17603(cf);
        assertEquals(cf, fields.getCustomfield_17603());

        fields.setCustomfield_19900(cf);
        assertEquals(cf, fields.getCustomfield_19900());

        fields.setCustomfield_17602(cf);
        assertEquals(cf, fields.getCustomfield_17602());

        fields.setCustomfield_20200(cf);
        assertEquals(cf, fields.getCustomfield_20200());

        fields.setCustomfield_20201(cf);
        assertEquals(cf, fields.getCustomfield_20201());

        fields.setCustomfield_12143(cf);
        assertEquals(cf, fields.getCustomfield_12143());

        fields.setCustomfield_13900(cf);
        assertEquals(cf, fields.getCustomfield_13900());

        fields.setCustomfield_11604(cf);
        assertEquals(cf, fields.getCustomfield_11604());

        fields.setCustomfield_10265(cf);
        assertEquals(cf, fields.getCustomfield_10265());

        fields.setCustomfield_19001(cf);
        assertEquals(cf, fields.getCustomfield_19001());
    }

    @Test
    void testObjectTypeCustomFields1() {
        Object objectValue = new Object();

        fields.setAggregatetimeoriginalestimate(objectValue);
        assertEquals(objectValue, fields.getAggregatetimeoriginalestimate());

        fields.setCustomfield_17001(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17001());

        fields.setCustomfield_17000(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17000());

        fields.setCustomfield_17004(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17004());

        fields.setCustomfield_17802(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17802());

        fields.setCustomfield_17801(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17801());

        fields.setCustomfield_17800(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17800());

        fields.setCustomfield_12900(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12900());

        fields.setCustomfield_10271(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10271());

        fields.setCustomfield_10272(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10272());

        fields.setCustomfield_13302(objectValue);
        assertEquals(objectValue, fields.getCustomfield_13302());

        fields.setCustomfield_13301(objectValue);
        assertEquals(objectValue, fields.getCustomfield_13301());

        fields.setCustomfield_17900(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17900());

        fields.setCustomfield_12206(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12206());

        fields.setCustomfield_12205(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12205());

        fields.setCustomfield_12207(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12207());

        fields.setCustomfield_12209(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12209());

        fields.setCustomfield_20500(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20500());

        fields.setCustomfield_18300(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18300());

        fields.setCustomfield_18301(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18301());

        fields.setCustomfield_10261(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10261());

        fields.setCustomfield_12321(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12321());

        fields.setCustomfield_10263(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10263());

        fields.setCustomfield_12320(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12320());
    }

    @Test
    void testObjectTypeCustomFields2() {
        Object objectValue = new Object();

        fields.setCustomfield_18302(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18302());

        fields.setCustomfield_12322(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12322());

        fields.setCustomfield_18303(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18303());

        fields.setCustomfield_10267(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10267());

        fields.setCustomfield_18304(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18304());

        fields.setCustomfield_11105(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11105());

        fields.setCustomfield_11900(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11900());

        fields.setCustomfield_11902(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11902());

        fields.setCustomfield_12319(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12319());

        fields.setCustomfield_11901(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11901());

        fields.setCustomfield_11904(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11904());

        fields.setCustomfield_11903(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11903());

        fields.setCustomfield_11905(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11905());

        fields.setTimeoriginalestimate(objectValue);
        assertEquals(objectValue, fields.getTimeoriginalestimate());

        fields.setCustomfield_11100(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11100());

        fields.setCustomfield_11102(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11102());

        fields.setCustomfield_11103(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11103());

        fields.setCustomfield_11104(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11104());

        fields.setCustomfield_10008(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10008());

        fields.setCustomfield_20604(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20604());

        fields.setCustomfield_20602(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20602());

        fields.setCustomfield_20603(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20603());

        fields.setCustomfield_20600(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20600());

        fields.setCustomfield_20601(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20601());
    }

    @Test
    void testObjectTypeCustomFields3() {
        Object objectValue = new Object();

        fields.setCustomfield_16100(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16100());

        fields.setCustomfield_11331(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11331());

        fields.setCustomfield_10000(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10000());

        fields.setCustomfield_11330(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11330());

        fields.setCustomfield_10001(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10001());

        fields.setCustomfield_11333(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11333());

        fields.setCustomfield_11332(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11332());

        fields.setCustomfield_16900(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16900());

        fields.setCustomfield_10003(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10003());

        fields.setCustomfield_10245(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10245());

        fields.setCustomfield_11326(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11326());

        fields.setCustomfield_11325(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11325());

        fields.setCustomfield_11328(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11328());

        fields.setEnvironment(objectValue);
        assertEquals(objectValue, fields.getEnvironment());

        fields.setCustomfield_11327(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11327());

        fields.setCustomfield_11329(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11329());

        fields.setDuedate(objectValue);
        assertEquals(objectValue, fields.getDuedate());

        fields.setCustomfield_17300(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17300());

        fields.setCustomfield_11320(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11320());

        fields.setCustomfield_15800(objectValue);
        assertEquals(objectValue, fields.getCustomfield_15800());

        fields.setCustomfield_11200(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11200());

        fields.setCustomfield_11321(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11321());

        fields.setCustomfield_11201(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11201());

        fields.setCustomfield_11322(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11322());
    }

    @Test
    void testObjectTypeCustomFields4() {
        Object objectValue = new Object();

        fields.setCustomfield_11323(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11323());

        fields.setCustomfield_11202(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11202());

        fields.setCustomfield_11324(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11324());

        fields.setCustomfield_11314(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11314());

        fields.setCustomfield_11315(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11315());

        fields.setCustomfield_11316(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11316());

        fields.setCustomfield_11317(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11317());

        fields.setCustomfield_11318(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11318());

        fields.setCustomfield_11319(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11319());

        fields.setCustomfield_20701(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20701());

        fields.setCustomfield_20702(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20702());

        fields.setCustomfield_20700(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20700());

        fields.setCustomfield_16200(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16200());

        fields.setCustomfield_11310(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11310());

        fields.setCustomfield_12400(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12400());

        fields.setCustomfield_11312(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11312());

        fields.setCustomfield_11303(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11303());

        fields.setCustomfield_11305(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11305());

        fields.setCustomfield_12516(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12516());

        fields.setCustomfield_11306(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11306());

        fields.setCustomfield_11307(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11307());

        fields.setTimeestimate(objectValue);
        assertEquals(objectValue, fields.getTimeestimate());

        fields.setCustomfield_11308(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11308());

        fields.setCustomfield_11309(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11309());
    }

    @Test
    void testObjectTypeCustomFields5() {
        Object objectValue = new Object();

        fields.setCustomfield_16310(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16310());

        fields.setCustomfield_16313(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16313());

        fields.setCustomfield_16312(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16312());

        fields.setCustomfield_17400(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17400());

        fields.setCustomfield_11300(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11300());

        fields.setCustomfield_11301(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11301());

        fields.setCustomfield_11302(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11302());

        fields.setCustomfield_16309(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16309());

        fields.setCustomfield_16308(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16308());

        fields.setAggregatetimeestimate(objectValue);
        assertEquals(objectValue, fields.getAggregatetimeestimate());

        fields.setCustomfield_20121(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20121());

        fields.setCustomfield_20122(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20122());

        fields.setCustomfield_14000(objectValue);
        assertEquals(objectValue, fields.getCustomfield_14000());

        fields.setCustomfield_20120(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20120());

        fields.setCustomfield_16307(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16307());

        fields.setCustomfield_16306(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16306());

        fields.setCustomfield_18600(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18600());

        fields.setCustomfield_16305(objectValue);
        assertEquals(objectValue, fields.getCustomfield_16305());

        fields.setCustomfield_18601(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18601());

        fields.setCustomfield_11403(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11403());

        fields.setCustomfield_11402(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11402());

        fields.setCustomfield_13704(objectValue);
        assertEquals(objectValue, fields.getCustomfield_13704());

        fields.setCustomfield_13703(objectValue);
        assertEquals(objectValue, fields.getCustomfield_13703());

        fields.setCustomfield_13706(objectValue);
        assertEquals(objectValue, fields.getCustomfield_13706());
    }

    @Test
    void testObjectTypeCustomFields6() {
        Object objectValue = new Object();

        fields.setCustomfield_13705(objectValue);
        assertEquals(objectValue, fields.getCustomfield_13705());

        fields.setCustomfield_20118(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20118());

        fields.setCustomfield_20119(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20119());

        fields.setCustomfield_20116(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20116());

        fields.setCustomfield_20117(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20117());

        fields.setCustomfield_20114(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20114());

        fields.setCustomfield_20115(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20115());

        fields.setCustomfield_20112(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20112());

        fields.setCustomfield_20111(objectValue);
        assertEquals(objectValue, fields.getCustomfield_20111());

        fields.setTimespent(objectValue);
        assertEquals(objectValue, fields.getTimespent());

        fields.setCustomfield_15200(objectValue);
        assertEquals(objectValue, fields.getCustomfield_15200());

        fields.setCustomfield_17500(objectValue);
        assertEquals(objectValue, fields.getCustomfield_17500());

        fields.setAggregatetimespent(objectValue);
        assertEquals(objectValue, fields.getAggregatetimespent());

        fields.setCustomfield_13700(objectValue);
        assertEquals(objectValue, fields.getCustomfield_13700());

        fields.setCustomfield_19800(objectValue);
        assertEquals(objectValue, fields.getCustomfield_19800());

        fields.setCustomfield_11512(objectValue);
        assertEquals(objectValue, fields.getCustomfield_11512());

        fields.setCustomfield_12601(objectValue);
        assertEquals(objectValue, fields.getCustomfield_12601());

        fields.setCustomfield_10304(objectValue);
        assertEquals(objectValue, fields.getCustomfield_10304());

        fields.setCustomfield_18001(objectValue);
        assertEquals(objectValue, fields.getCustomfield_18001());
    }
}