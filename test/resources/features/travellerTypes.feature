Feature: Selecting One or More Traveller Types

  Scenario: Adding traveller types when creating a profile
    Given A new user is to register with TravelEA with their email "email@email.com" not already taken
    When The user signs up with first name "John", last name "Smith", email "email@email.com", password "password", gender "Male", DOB "1990-09-09", Passports "Australia", Nationalities "Australia", the user selects the traveller type "Groupie"
    Then The traveller type "Groupie" is associated with the users profile


  Scenario: Deleting a traveller type
    Given The user with email "testuser3@uclive.ac.nz", id "3" and traveller types "Groupie" and "Thrillseeker" is signed up
    When The user with id "3" removes the traveller type "Groupie"
    Then The user with id "3" has only one traveller type "Thrillseeker"


  Scenario: Trying to delete all traveller types
    Given The user with email "testuser3@uclive.ac.nz", id "3" and traveller types "Groupie" and "Thrillseeker" is signed up
    When The user with id "3" removes the traveller type "Groupie"
    And The user with id "3" removes the only remaining traveller type "Thrillseeker"
    Then The type "Thrillseeker" is not removed and still is associated with the profile with id "2"





