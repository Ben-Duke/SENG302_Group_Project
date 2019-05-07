Feature: displayProfilePhoto

  Scenario: The registered user who is the owner of the profile can choose a photo to be the display pic.
  This may be by uploading a new photo or selecting an existing photo from the profile.
    Given I am logged in with user id "2"
    And user id "2" is the owner of the profile
    When I upload a new photo with

  Scenario: A placeholder of a certain size and aspect ratio (e.g. square) exists on each profile page that does
  not already have a profile photo. If a profile photo exists, it is shown instead.
    Given I have just created a new account
    When I visit the home page
    Then The placeholder profile picture is displayed in place of a profile picture
    And The aspect ratio of the image is square

  Scenario: A placeholder of a certain size and aspect ratio (e.g. square) exists on each profile page that does
  not already have a profile photo. If a profile photo exists, it is shown instead.
    Given I have not uploaded any pictures to my profile
    When I visit the home page
    Then The placeholder profile picture is displayed in place of a profile picture
    And The aspect ratio of the image is square

  Scenario: A placeholder of a certain size and aspect ratio (e.g. square) exists on each profile page that does
  not already have a profile photo. If a profile photo exists, it is shown instead.
    Given I have uploaded pictures to my profile
    And I have not set a profile picture
    When I visit the home page
    Then The placeholder profile picture is displayed in place of a profile picture
    And The aspect ratio of the image is square

  Scenario: A placeholder of a certain size and aspect ratio (e.g. square) exists on each profile page that does
  not already have a profile photo. If a profile photo exists, it is shown instead.
    Given I have set a profile picture
    When I visit the home page
    Then The image I set as my profile picture is displayed in the place of a profile picture
    And The aspect ratio of the image is square

  Scenario: The registered user who is the owner of the profile can choose a photo to be the display pic.
  This may be by uploading a new photo or selecting an existing photo from the profile.
    Given I have photos already uploaded to my account
    When I set one of these photos to be my profile picture
    Then The image I set as my profile picture is displayed in the place of a profile picture
    And The aspect ratio of the image is square

  Scenario: The registered user who is the owner of the profile can choose a photo to be the display pic.
  This may be by uploading a new photo or selecting an existing photo from the profile.
    Given I have photos already uploaded to my account
    When I set one of these photos to be my profile picture
    Then The image I set as my profile picture is displayed in the place of a profile picture
    And The aspect ratio of the image is square

  Scenario: The registered user who is the owner of the profile can choose a photo to be the display pic.
  This may be by uploading a new photo or selecting an existing photo from the profile.
    When I upload a new image to be set as my profile picture
    Then This new image is displayed in the place of a profile picture
    And The aspect ratio of the image is square

  Scenario: The registered user is given the choice of cropping the photo to the given resolution
  or the system automatically crops the photo so that it fits into the required aspect ratio.
  The photo should never be stretched.
    When I am uploading a new image to be my profile picture
    Then The image is cropped into a square aspect ratio
    And The image is not stretched to fit this aspect ratio

  Scenario: A small thumbnail of the photo is automatically created.
    Given I have existing images in my profile
    When I select one of these to be my profile picture
    Then The profile picture version of the image is also stored
    And This version of the image has the same aspect ratio as the orifinal image

  Scenario: A small thumbnail of the photo is automatically created.
    When I am uploading a new image to be my profile picture
    Then The cropped version of this image is stored








