# Mocking - Part 1 - Rackmonitor - Code-Along

This activity will be walked through by your instructor.

**Your participation is critical to your understanding**, so please respond when asked by the instructor and ask any questions you may have immediately.

Please do not be afraid to ask questions.  Chances are very good that others have the same and/or similar questions and are bashful about asking.  They will appreciate your asking the question.

This activity will add code to the *RackMonitorDependencyTest* in the *Mocking-Rack-Monitor* Guided Project.

We will explore using [Mockito](https://www.tutorialspoint.com/mockito/index.htm) 's ability to perform advanced testing of Java code. 

Successful completion will be when all tests provided in the project run successfully.

### Part 1 - Complete the test method `monitorRacks_withOneUnhealthyServer_replacesServer()` in the *RackMonitorDependencyTest* to successfully verify whether certain methods were called and/or called a specific number of times.

It is not unusual to be unsure if ones logic is successfully calling the method one wants called and not calling it more or less often that expected.  Mockito provides us with a relatively simple way to verify whether or not a method is called and how many time.

You will have successfully completed this activity when the `monitorRacks_withOneUnhealthyServer_replacesServer()` passes.

### Part 2 - Complete the test method `monitorRacks_withOneShakyServer_inspectsServer()` in the *RackMonitorDependencyTest* to successfully verify whether certain methods were called and/or called a specific number of times.

Mockito provides us with a way to test whether exceptions are thrown by a method or not.  This activity will utilize this feature of Mockito.

You will have successfully completed this activity when the `monitorRacks_withOneShakyServer_inspectsServer()` passes.


## Requirements

- IntelliJ with Java 11
- If you're running the code from your terminal, make sure you have Java 11 installed on your machine (not just in IntelliJ)

## Set Up

- Clone this repo and open it's project folder in IntelliJ.
