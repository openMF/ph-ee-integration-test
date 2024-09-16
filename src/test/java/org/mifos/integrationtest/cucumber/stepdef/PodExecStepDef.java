package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;


public class PodExecStepDef {

  public String podname;
    @Given("I have kubectl access to the cluster")
    public void iHaveKubectl() throws InterruptedException {
        try {
            System.out.println("Checking if kubectl is installed...");
//            processBuilder.command("sh", "-c", " src/test/java/resources/step1.sh");
//            processBuilder.environment().putAll(environment);
            Process step1 =  Runtime.getRuntime().exec("src/test/java/resources/step1.sh");
            StringBuilder outputStep1 = getValForProcess(step1);
            int exitVal = step1.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(outputStep1);
            } else {
                    System.out.println("Failure!");
                     BufferedReader   reader = new BufferedReader(
                                new InputStreamReader(step1.getErrorStream()));
                     outputStep1 = new StringBuilder();
                     String line;while ((line = reader.readLine()) != null) {
                         outputStep1.append(line + "\n");}
                            System.out.println(outputStep1);}
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failure!");
        }

    }



    @And("I have limited access role to the cluster")
    public void iHaveLimitedAccessRoleToTheCluster() throws InterruptedException {
        try {
//            processBuilder.command("sh", "-c", " src/test/java/resources/step2.sh");
//            processBuilder.environment().putAll(environment);
            Process process = Runtime.getRuntime().exec("src/test/java/resources/step2.sh");
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
            }
            else{
            StringBuilder output1 = new StringBuilder();
            BufferedReader reader1 = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
                while ((line = reader1.readLine()) != null) {
                    output1.append(line + "\n");
                }
                System.out.println("Failure!"+ output1);}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @And("I have a pod running in the cluster")
    public void iHaveAPodRunningInTheCluster() {
        try {
            Process process = Runtime.getRuntime().exec("src/test/java/resources/step3.sh");
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output.indexOf("ph-ee-connector-ams-mifos"));
                podname = output.substring(output.indexOf("ph-ee-connector-ams-mifos"),
                        output.indexOf("ph-ee-connector-ams-mifos") + 42);

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failure!");
        }

    }

    @Given("I exec into the pod for a service")
    public void iExecIntoThePodForAService() {
        try {
            Process process = Runtime.getRuntime().exec("src/test/java/resources/step4.sh");
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output.indexOf("ph-ee-connector-ams-mifos"));
                System.out.println(output.substring(output.indexOf("ph-ee-connector-ams-mifos"),
                        output.indexOf("ph-ee-connector-ams-mifos") + 42));

            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failure!");
        }

    }

    @Then("I should get an error")
    public void iShouldGetAnError() {
    }
    private StringBuilder getValForProcess(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output;
    }

}

