@gov
Feature: Disable Pod Exec for Kibana Feature Test

@application.yaml

  Scenario: Disable Pod Exec for Kibana
#        Given I have access to cluster
#        Given I have kubectl access to the cluster
  Given I have limited access role to the cluster
  And I have a pod running in the cluster
  And I exec into the pod for a service
  Then I should get an error
