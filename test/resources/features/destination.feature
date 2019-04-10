Feature: typesOfDestinations

  Scenario: When  I create a destination, it is private by default. Private destinations are only accessible to the user who created them (owner of the destination).
    Given I am logged in with user id "2"
    And user id "2" is the owner of the profile
    When I upload a new photo with