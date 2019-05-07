Feature: A treasure hunt is defined by a previously created public destination, a riddle, a start and end date

  Scenario: Viewing a treasure hunt
    When I view a treasure hunt of title "Surprise"
    Then I should be shown a riddle of "The garden city" with a start date of "2019-04-17" and end date of "2019-12-25"
    And The treasure hunt should have a destination with name "Christchurch"
