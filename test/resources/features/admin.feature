Feature: in order to administrate the system
  As a user with administrator rights
  I want to perform actions as an administrator

  Scenario: Attempt to delete default administrator
    Given there is a default administrator with userId=1
    When I try to delete the default administrator with userId=1
    Then an error message is shown advising me that I can't delete the default admin with userId=1
    And the default administrator with userId=1 is not deleted

  Scenario: Default administrator missing on start up
    Given there is no default administrator
    When the system starts up
    Then a default administrator will be created with username "admin1" and password "admin"

  Scenario: Default administrator present on start up
    Given there is a default administrator
    When the system starts up
    Then no new default administrator is created

    Scenario: Default administrator removed from the database

