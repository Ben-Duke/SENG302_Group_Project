Feature: I can CRUD any of my treasure hunts

  Scenario: Creating a valid treasure hunt with unique title
    Given There are no treasure hunts with the title "NewTreasureHunt"
    When I create a valid treasure hunt with the title "NewTreasureHunt", destination "The Wok", riddle "Cheap rice for a good price", start date "2019-05-04" and end date "2019-12-05"
    Then There should be a treasure hunt in the database with title "NewTreasureHunt", destination "The Wok", riddle "Cheap rice for a good price", start date "2019-05-04" and end date "2019-12-05"

  Scenario: Creating an invalid treasure hunt with duplicate title
    Given There is a treasure hunt with the title "NewTreasureHunt"
    When I create an invalid treasure hunt with the title "NewTreasureHunt", destination "The Wok", riddle "Cheap rice for a good price", start date "2019-05-04" and end date "2019-12-05"
    Then There should be no treasure hunts in the database with title "NewTreasureHunt"

  Scenario: Editing an existing treasure hunt
    When I edit a treasure hunt with the title "Surprise" by changing the destination to "Niagra Falls", riddle to "The Waterfall", start date "2019-05-04" and end date "2019-12-05"
    Then The treasure hunt with the title "Surprise" in the database should have the destination as "Niagra Falls", riddle to "The Waterfall", start date "2019-05-04" and end date "2019-12-05"

  Scenario: Editing an existing treasure hunt to a duplicate title
    When I edit a treasure hunt with the title "Surprise" by changing the title to "Surprise2"
    Then There should be only one treasure hunt with the title "Surprise2" in the database
    And There should be a treasure hunt with the title "Surprise"
