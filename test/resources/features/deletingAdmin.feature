Feature: Delete Admin
  Scenario: Removing an admin's admin rights
    Given I am logged in
    And I have admin rights
    When I remove an existing admin's rights
    Then The user should no longer be in the admin table