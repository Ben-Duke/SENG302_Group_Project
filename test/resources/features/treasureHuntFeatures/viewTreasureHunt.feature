Feature: View a list of available Treasure Hunts

  Scenario: Viewing a list of treasure hunts where there are available (open) treasure hunts.
    When I view the list of available treasure hunts
    Then I should be shown a list of two treasure hunts
    And The titles of the treasure hunt should be "Surprise" and "Surprise2"

  Scenario: Viewing a list of treasure hunts where there are no available treasure hunts
    Given There are no available treasure hunts in the database
    When I view the list of available treasure hunts
    Then I should be shown an empty list of treasure hunts

