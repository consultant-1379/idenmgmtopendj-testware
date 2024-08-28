package com.ericsson.nms.security.taf.test.operators;

import static org.testng.Assert.*;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.DataRecordImpl;
import com.ericsson.oss.testware.remoteexecution.operators.RemoteEnmBuilderImplement;
import com.google.common.collect.Maps;

public class OpenDjAndDbOperatorTest extends TafTestBase {

    @Inject
    protected TestContext context;

    private DataRecord getInfo1() {
        final String FirstDBavailable = OpenDjAndDbOperator.getHostLdapOpendj1();
        final Map<String, Object> map = Maps.newHashMap();
        map.put("db", FirstDBavailable);
        map.put("opendj", "opendj_1");
        map.put("podname", "opendj-0");
        final DataRecordImpl datatRecord = new DataRecordImpl(map);
        return datatRecord;
    }

    private DataRecord getInfo2() {
        final String SecondDBavailable = OpenDjAndDbOperator.getHostLdapOpendj2();
        final Map<String, Object> map = Maps.newHashMap();
        map.put("db", SecondDBavailable);
        map.put("opendj", "opendj_2");
        map.put("podname", "opendj-1");
        final DataRecordImpl datatRecord = new DataRecordImpl(map);
        return datatRecord;
    }

    @Test
    public void sendMessage() {
        final String cmd = "/ericsson/opendj/opendj/bin/dsrepl status  -h opendj-1 -p 4444 -X -D \"cn=directory manager\" -w Idapadmin  --showReplicas --showChangelogs --showGroups| grep -A5 \"^dc\" | grep GOOD |"
                + "wc -l";
        final OpenDjAndDbOperator openDJOperator = new OpenDjAndDbOperator();
        final DataRecord dataSource = getInfo2();
        final boolean connection = openDJOperator.setParameter(dataSource);
        if (connection) {
            final RemoteEnmBuilderImplement remoteEnmBuilderImplement = openDJOperator.getRemoteEnmBuilderImplement();
            final String result = remoteEnmBuilderImplement.execCommand(cmd);
            System.out.println(result);
        }
    }

}
