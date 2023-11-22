package com.amazon.ata.mocking.rackmonitor;

import com.amazon.ata.mocking.rackmonitor.clients.warranty.Warranty;
import com.amazon.ata.mocking.rackmonitor.clients.warranty.WarrantyClient;
import com.amazon.ata.mocking.rackmonitor.clients.warranty.WarrantyNotFoundException;
import com.amazon.ata.mocking.rackmonitor.clients.wingnut.WingnutClient;
import com.amazon.ata.mocking.rackmonitor.exceptions.RackMonitorException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;  // @initMocks is deprecated (hence the strikeout)
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

// Use a mocking framework called Mockito to do our Mocking
// Mockito is a popular mocking framework - it's easy and free
// A framework is a set of classes designed to make common programming processes easier

public class RackMonitorIncidentTest {
    // Instantiate objects used in the tests
    RackMonitor rackMonitor;        // the class we are testing

    // Before Mocking instantiate objects available for every test
    // WingnutClient wingnutClient;    // an instance of a WingnutClient object
    // WarrantyClient warrantyClient;  // an instance of a WarrantyClient object
    // Rack aRack;                     // an instance of a Rack object

    // WITH Mocking we will Mock the external classes used in these tests
    //      replace an actual object with Mock object managed by Mockito
    // We are mocking these external classes so we are not dependant on their code
    //                either being complete or correct
    @Mock
    WarrantyClient warrantyClient;

    @Mock
    WingnutClient wingnutClient;

    @Mock
    Rack aRack;

    // Since we are Mocking the Rack class which uses the servers
    // we no longer need multiple servers to test
    // we can define one Server and set it's attributes to what we need for the tests
    //Server unhealthyServer = new Server("TEST0001");  // An instancte for an unhealthy server (health < .8)
    //Server shakyServer = new Server("TEST0067");      // An instance for a shaky server (health between .81 and .9)

    Server aServer = new Server("TEST001");  // Server object will use in each test we need a Server

    Map<Server, Integer> aRackServerUnits;   // hold result from tests - server map

    Map<Server, Double> serverHealth;       // hold the health for a server
                                            //     returned from Rack.getHealth() Mockito call


    @BeforeEach    // do this before each test is run
    void setUp() {
        // Since we are using Mock for these classes - no need to instantiate objects for then
        // warrantyClient = new WarrantyClient();
        // wingnutClient = new WingnutClient();
        // aRack = new Rack("RACK01", aRackServerUnits); // instantiate a rack ro be available for each test

        // initMocks() - deprecated - Java committee has decided it will go away some day - replaced
        initMocks(this);  // Tell Mockito to setup the mock objects in this class/test quite defined
                          // Mockito will use these Mock objects to simulate their behavior

        aRackServerUnits = new HashMap<>();   // Map of servers in our test rack
        aRackServerUnits.put(aServer, 1);     // Set up an unhealthy server in server map with id=1

        serverHealth = new HashMap<>();

        // We are using Mock'd aRack, wingnutClient and warrantyClient - Mockito will simulate them in this statement
        // Using Mock'd objects is no different than real Objects
        rackMonitor = new RackMonitor(new HashSet<>(Arrays.asList(aRack)),  // instantiate class with methods to test
            wingnutClient, warrantyClient, 0.9D, 0.8D);                     //  object used to test methods
    }

    @Test
    public void getIncidents_withOneUnhealthyServer_createsOneReplaceIncident() throws Exception {
        // GIVEN - set up the test data for the test

        // Since we are using Mock'd objects - we need to tell Mockito what the method calls shoudl return
        //       The real classes/objects have methods that accept parameters and return values
        //       Mock'd objects do not have methods
        //          so we need to tell Mockito what to return when method gets called with certain parameters
        //       We tell Mockito given certain parameters to a method what we expect to be returned
        //       We need to this BEFORE the method that uses Mock'd objects is called

        // The rack is set up with a single unhealthy server (health less than .8)
        serverHealth.put(aServer, .5 );  // Set up an unhealthy server in the Test Server Health Map

        // What you tell Mockito is....
        // when you see this method call ,return this value
        when(aRack.getHealth()).thenReturn(serverHealth);   // Tell Mockito what to return when Rack.getHealth() is called
        when(aRack.getUnitForServer(aServer)).thenReturn(1); // Tell Mockito to return the id of our server(1) when getUnitForServer is called
        when(warrantyClient.getWarrantyForServer(aServer)).thenReturn(Warranty.nullWarranty());

        // We've reported the unhealthy server to Wingnut
        rackMonitor.monitorRacks();   // run the monitorRacks() in the class we are testing

        // WHEN - run the method to be tested using the test data
        Set<HealthIncident> actualIncidents = rackMonitor.getIncidents();

        // THEN - verify the result is what was expected
        HealthIncident expected =
            new HealthIncident(aServer, aRack, 1, RequestAction.REPLACE);
        assertTrue(actualIncidents.contains(expected),
            "Monitoring an unhealthy server should record a REPLACE incident!");
    }

    @Test
    public void getIncidents_withOneShakyServer_createsOneInspectIncident() throws Exception {
        // GIVEN
        // The rack is set up with a single shaker server (health between .81 and .89)
        serverHealth.put(aServer, .85 );  // Set up an shaky server in the Test Server Health Map

        // What you tell Mockito is....
        // when you see this method call.return this value
        when(aRack.getHealth()).thenReturn(serverHealth);   // Tell Mockito what to return when Rack.getHealth() is called
        when(aRack.getUnitForServer(aServer)).thenReturn(1); // Tell Mockito to return the id of our server(1) when getUnitForServer is called
        when(warrantyClient.getWarrantyForServer(aServer)).thenReturn(Warranty.nullWarranty());

// Since this is done in our @BeforeEach Setup() method, we don't really need it here
//        rackMonitor = new RackMonitor(new HashSet<>(Arrays.asList(aRack)),
//            wingnutClient, warrantyClient, 0.9D, 0.8D);

        // We've reported the shaky server to Wingnut
        rackMonitor.monitorRacks();

        // WHEN
        Set<HealthIncident> actualIncidents = rackMonitor.getIncidents();

        // THEN
        HealthIncident expected =
            new HealthIncident(aServer, aRack, 1, RequestAction.INSPECT);
        assertTrue(actualIncidents.contains(expected),
            "Monitoring a shaky server should record an INSPECT incident!");
    }

    @Test
    public void getIncidents_withOneHealthyServer_createsNoIncidents() throws Exception {
        // GIVEN
        // monitorRacks() will find only healthy servers
        // The rack is set up with a single healthy server (health .9 or more)
        serverHealth.put(aServer, .91 );  // Set up an unhealthy server in the Test Server Health Map

        // What you tell Mockito is....
        // when you see this method call ,return this value
        when(aRack.getHealth()).thenReturn(serverHealth);   // Tell Mockito what to return when Rack.getHealth() is called
        when(aRack.getUnitForServer(aServer)).thenReturn(1); // Tell Mockito to return the id of our server(1) when getUnitForServer is called
        when(warrantyClient.getWarrantyForServer(aServer)).thenReturn(Warranty.nullWarranty());

        // We've reported the unhealthy server to Wingnut
        rackMonitor.monitorRacks();   // run the monitorRacks() in the class we are testing

        // WHEN
        Set<HealthIncident> actualIncidents = rackMonitor.getIncidents();

        // THEN
        assertEquals(0, actualIncidents.size(),
            "Monitoring a healthy server should record no incidents!");
    }

    @Test
    public void monitorRacks_withOneUnhealthyServer_replacesServer() throws Exception {
        // GIVEN
        // The rack is set up with a single unhealthy server
        // The rack is set up with a single unhealthy server (health less than .8)
        serverHealth.put(aServer, .5 );  // Set up an unhealthy server in the Test Server Health Map

        // What you tell Mockito is....
        // when you see this method call ,return this value
        when(aRack.getHealth()).thenReturn(serverHealth);   // Tell Mockito what to return when Rack.getHealth() is called
        when(aRack.getUnitForServer(aServer)).thenReturn(1); // Tell Mockito to return the id of our server(1) when getUnitForServer is called
        when(warrantyClient.getWarrantyForServer(aServer)).thenReturn(Warranty.nullWarranty());

        // WHEN
        rackMonitor.monitorRacks();

        // THEN
        // There were no exceptions
        // No way to tell we called the warrantyClient for the server's Warranty
        // UNLESS we use Mockito to test whether the getWarrantyForServer() is called using Mockito verify()
        verify(warrantyClient).getWarrantyForServer(aServer); // verify the getWarrtantyForServer() was called at least once

        // No way to tell we called Wingnut to replace the server - was the requestReplacement() method ever called?
        // UNLESS we use Mockito to test whether the requestReplacement() is called using Mockito verify()
        verify(wingnutClient).requestReplacement(aRack,1,Warranty.nullWarranty());
    }

    @Test
    public void monitorRacks_withUnwarrantiedServer_throwsServerException() throws Exception {
        // GIVEN
        // The rack is set up with a single unhealthy server (health .9 or more)
        serverHealth.put(aServer, .5 );  // Set up an unhealthy server in the Test Server Health Map

        // What you tell Mockito is....
        // when you see this method call ,return this value
        when(aRack.getHealth()).thenReturn(serverHealth);   // Tell Mockito what to return when Rack.getHealth() is called
        when(aRack.getUnitForServer(aServer)).thenReturn(1); // Tell Mockito to return the id of our server(1) when getUnitForServer is called
        // Tell Mockito to throw an exception when it sees the method call with the specified parameters
        //      Note: .class following the WarrantyNotFoundException to tell Mockito its a user defined Exception (class)
        when(warrantyClient.getWarrantyForServer(aServer)).thenThrow(WarrantyNotFoundException.class);

        // WHEN and THEN
        assertThrows(RackMonitorException.class,
            () -> rackMonitor.monitorRacks(),
            "Monitoring a server with no warranty should throw exception!");
    }
}
