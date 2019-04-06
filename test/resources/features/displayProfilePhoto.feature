Feature: displayProfilePhoto

  Scenario: The registered user who is the owner of the profile can choose a photo to be the display pic.
  This may be by uploading a new photo or selecting an existing photo from the profile.
    Given I am logged in with user id "2"
    And user id "2" is the owner of the profile
    When I upload a new photo with