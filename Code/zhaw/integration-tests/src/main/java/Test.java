import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import static org.junit.Assert.*;
import java.io.*;

public class Test {

    private static String userServiceURL = "http://localhost:8888/zhaw-p2abc-webservices/user/";
    private static String verificationServiceURL = "http://localhost:8888/zhaw-p2abc-webservices/verification/";
    private static String issuanceServiceURL = "http://localhost:8888/zhaw-p2abc-webservices/issuance/";
    
    private static String magic = "*magic*";

    public static void main(String[] args) {
        System.out.println("hi there");

        /* Test if all three services are running by calling /status/
         * on each service and expecting a 200 response.
         */
        testUserStatus();
        testIssuanceStatus();
        testVerificationStatus();
        
        /* Ok, if we are here all services are at least running */
        
        /* Test authentication */
        testAuthentication(readTextFile("simpleAuth.xml"));
        
        /* Get an attributeInfoCollection and convert it to a credentialSpecification */
        String attributeInfoCollection = testAttributeInfoCollection();
        String credSpec = testGenCredSpec(attributeInfoCollection);

        System.out.println("I'm done!");
    }
    
    public static String readTextFile(String path) {
        try {
            ClassLoader cl = Test.class.getClassLoader();
            File f = new File(cl.getResource(path).getFile());
            BufferedReader br = new BufferedReader(new FileReader(f));
            String lines = "";
            String line = "";
            while((line = br.readLine()) != null)
                lines += line + "\n";
            br.close();
            return lines;
        }
        catch(Exception e) {
            throw new RuntimeException("readTextFile("+path+") failed!");
        }
    }
    
    public static String testAuthentication(String authRequest) {
        Client client = Client.create();

        WebResource webResource = client
                .resource(issuanceServiceURL + "testAuthentication");

        ClientResponse response = webResource.type("application/xml")
                .post(ClientResponse.class, authRequest);

        assertTrue(response.getStatus() == 200);
        
        return response.getEntity(String.class);
    }
    
    public static String testGenCredSpec(String attributeInfoCollection) {
        Client client = Client.create();

        WebResource webResource = client
                .resource(issuanceServiceURL + "genCredSpec/" + magic + "/");

        
        ClientResponse response = webResource.type("application/xml")
                        .post(ClientResponse.class, attributeInfoCollection);
        
        assertTrue(response.getStatus() == 200);
        
        return response.getEntity(String.class);
    }
    
    public static String testAttributeInfoCollection() {
        Client client = Client.create();

        WebResource webResource = client
                .resource(issuanceServiceURL + "attributeInfoCollection/" + magic + "/test");

        ClientResponse response = webResource.get(ClientResponse.class);

        assertTrue(response.getStatus() == 200);
        
        return response.getEntity(String.class);
    }

    public static void testUserStatus() {
        Client client = Client.create();

        WebResource webResource = client
                .resource(userServiceURL + "status");

        ClientResponse response = webResource.get(ClientResponse.class);

        assertTrue(response.getStatus() == 200);
    }
    
    public static void testIssuanceStatus() {
        Client client = Client.create();

        WebResource webResource = client
                .resource(issuanceServiceURL + "status");

        ClientResponse response = webResource.get(ClientResponse.class);

        assertTrue(response.getStatus() == 200);       
    }
    
    public static void testVerificationStatus() {
        Client client = Client.create();

        WebResource webResource = client
                .resource(verificationServiceURL + "status");

        ClientResponse response = webResource.get(ClientResponse.class);

        assertTrue(response.getStatus() == 200); 
    }
    
}
