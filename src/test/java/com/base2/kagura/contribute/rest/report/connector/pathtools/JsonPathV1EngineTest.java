package com.base2.kagura.contribute.rest.report.connector.pathtools;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class JsonPathV1EngineTest {
    String serverResponse = "{\"totalCount\": 7, \"data\": [{\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-6ed97da69a18\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"User\", \"name\": \"User\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-22T00:23:09.331Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-7f2ecc806bf7\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Admin\", \"name\": \"Admin\"}, \"timeZone\": \"Antarctica/Macquarie\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T01:31:27.486Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-a6c8f5ed1eff\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"User\", \"name\": \"User\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T05:21:32.54Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-a55c3dd50544\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Admin\", \"name\": \"Admin\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:59:25.085Z\"}, {\"blocked\": false, \"verified\": false, \"id\": \"00000000-0000-0000-0000-adcb7b4815d4\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Admin\", \"name\": \"Admin\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:59:25.111Z\"}, {\"blocked\": false, \"verified\": false, \"id\": \"00000000-0000-0000-0000-d86ce629f971\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Owner\", \"name\": \"Owner\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:52:36.856Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-6a480a3fafc4\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"User\", \"name\": \"User\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:59:25.116Z\"}], \"paging\": {\"first\": \"https://api.opsgenie.com/v2/users?limit=100&offset=0&order=ASC&sort=username\", \"last\": \"https://api.opsgenie.com/v2/users?limit=100&offset=0&order=ASC&sort=username\"}, \"took\": 0.006, \"requestId\": \"00000000-0000-0000-0000-3527e9f4342f\"}";
    @Test
    public void JsonPathTest() throws IOException {
        Object result = JsonPath.read(serverResponse, "$..totalCouant");
        System.out.println(result);
        System.out.println(result.getClass().toString());
    }
    @Test
    public void MatchSuccessSingleValue() {
        Engine engine = new JsonPathV1Engine();
        Assert.assertTrue(engine.Match(serverResponse, "$.totalCount"));
    }
    @Test
    public void MatchSuccessMultiValue() {
        Engine engine = new JsonPathV1Engine();
        Assert.assertTrue(engine.Match(serverResponse, "$..totalCount"));
    }
    @Test
    public void MatchFailSingleValue() {
        Engine engine = new JsonPathV1Engine();
        Assert.assertFalse(engine.Match(serverResponse, "$.totaasdflCount"));
    }
    @Test
    public void MatchFailMultiValue() {
        Engine engine = new JsonPathV1Engine();
        Assert.assertFalse(engine.Match(serverResponse, "$..totalasdfCount"));
    }
}
