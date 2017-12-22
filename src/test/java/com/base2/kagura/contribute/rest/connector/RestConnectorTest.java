package com.base2.kagura.contribute.rest.connector;

import com.jayway.jsonpath.JsonPath;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class RestConnectorTest {
    @Test
    public void WebTest() throws IOException {
        byte[] serverResponse = "{\"totalCount\": 7, \"data\": [{\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-6ed97da69a18\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"User\", \"name\": \"User\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-22T00:23:09.331Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-7f2ecc806bf7\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Admin\", \"name\": \"Admin\"}, \"timeZone\": \"Antarctica/Macquarie\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T01:31:27.486Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-a6c8f5ed1eff\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"User\", \"name\": \"User\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T05:21:32.54Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-a55c3dd50544\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Admin\", \"name\": \"Admin\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:59:25.085Z\"}, {\"blocked\": false, \"verified\": false, \"id\": \"00000000-0000-0000-0000-adcb7b4815d4\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Admin\", \"name\": \"Admin\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:59:25.111Z\"}, {\"blocked\": false, \"verified\": false, \"id\": \"00000000-0000-0000-0000-d86ce629f971\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"Owner\", \"name\": \"Owner\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:52:36.856Z\"}, {\"blocked\": false, \"verified\": true, \"id\": \"00000000-0000-0000-0000-6a480a3fafc4\", \"username\": \"user@domain\", \"fullName\": \"Name Name\", \"role\": {\"id\": \"User\", \"name\": \"User\"}, \"timeZone\": \"Australia/Melbourne\", \"locale\": \"en_US\", \"userAddress\": {\"country\": \"\", \"state\": \"\", \"city\": \"\", \"line\": \"\", \"zipCode\": \"\"}, \"createdAt\": \"2017-12-18T00:59:25.116Z\"}], \"paging\": {\"first\": \"https://api.opsgenie.com/v2/users?limit=100&offset=0&order=ASC&sort=username\", \"last\": \"https://api.opsgenie.com/v2/users?limit=100&offset=0&order=ASC&sort=username\"}, \"took\": 0.006, \"requestId\": \"00000000-0000-0000-0000-3527e9f4342f\"}".getBytes();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
        httpServer.createContext("/api/endpoint", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, serverResponse.length);
                exchange.getResponseBody().write(serverResponse);
                exchange.close();
            }
        });
        httpServer.start();

        try {

        } finally {
            httpServer.stop(0);
        }
    }
}
