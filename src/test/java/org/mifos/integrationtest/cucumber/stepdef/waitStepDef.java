package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.Then;
import static java.util.concurrent.TimeUnit.MINUTES; // Import MINUTES
import static java.util.concurrent.TimeUnit.SECONDS; // Ensure you import SECONDS from the correct TimeUnit
import static org.awaitility.Awaitility.await;

public class waitStepDef { // Replace with your actual step definition class name

    private int waited_secs = 0; // You can still keep this if you need to track time

    @Then("I can wait for {int} minutes") // Make it more flexible
    public void waitForNMinutes(int duration) {
        System.out.println("Waiting for " + duration + " minutes");
        waited_secs = 0;
        await().atMost(duration, MINUTES).pollInterval(500, SECONDS).until(() -> {
            // This lambda expression needs to return a boolean.
            // To simply wait, we can return true immediately after some minimal operation.
            try {
                Thread.sleep(30000); // Sleep to avoid busy-waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true; // Signal that the condition (waiting) is "met" after the poll interval
        });
    }

    @Then("I can wait for {int} seconds") // Add a seconds version for flexibility
    public void waitForNSeconds(int duration) {
        System.out.println("Waiting for " + duration + " seconds");
        waited_secs = 0;
        await().atMost(duration, SECONDS).pollInterval(1, SECONDS).until(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true;
        });
    }
}
