Feature: Only open treasure hunts should be shown

  Scenario: Only open treasure hunts should be shown to the user
    Given There are three treasure hunts in the database
    And Two of the existing treasure hunts are open and one is closed
    Then The user should only be shown two treasure hunts
  Scenario: Editing an treasure hunt to make it a closed treasure hunt
    Given There are three treasure hunts in the database
    And Two of the existing treasure hunts are open and one is closed
    When I edit those open treasure hunts to make it closed
    Then The user should not be shown any treasure hunts

