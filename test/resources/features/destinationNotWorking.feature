#Feature: typesOfDestinations

#  Background:
#    Given There is a prepopulated database
#    And I am logged in with user id "2"
#    And I create a destination with name "Lighthouse of Alexandria" of type "Attraction" at district "Alexandria" at country "Egypt at latitude "31.170739" and longitude "29.844310"
#
#
#  Scenario: When  I create a destination, it is private by default. Private destinations are only accessible to the user who created them (owner of the destination)
#    When I access my private destinations
#    Then "Lighthouse of Alexandria" should be within my list of private destinations
#    And "Lighthouse of Alexandria" should not be within my list of public destinations
#
#
#  Scenario: I (the owner of the destination) can CRUD my own private destinations
#    When I update my destination with name "Temple of Artemis", type "Attraction", district "Artemis", country "Turkey", latitude "37.949753" and longitude "27.363899"
#    Then the destination will be updated to the respective attributes.
#
#  Scenario: I (the owner of the destination) can CRUD my own private destinations
#    When I delete my destination with name "Lighthouse of Alexandria"
#    Then the destination will be deleted.
#
#  Scenario: I can mark any of my own destinations as public as long as the same public destination does not already exist. Public destinations are accessible to all registered users.
#    When I mark "Lighthouse of Alexandria" as public
#    And the same public destination does not already exist
#    Then "Lighthouse of Alexandria" should not be within my list of private destinations
#    And "Lighthouse of Alexandria" should be within my list of public destinations
#
#  Scenario: I can mark any of my own destinations as public as long as the same public destination does not already exist. Public destinations are accessible to all registered users.
#    When I mark "Lighthouse of Alexandria" as public
#    And the same public destination already exists
#    Then "Lighthouse of Alexandria" should not be updated to a public destination
#
#  Scenario:  I can only create a destination if it does not already exist in destinations that are accessible to me (i.e. public destinations or my private destinations).
#    When I create another destination with name "Lighthouse of Alexandria" of type "Attraction" at district "Alexandria" at country "Egypt at latitude "31.170739" and longitude "29.844310"
#    Then the destination should not be created and there should only be one "Lighthouse of Alexandria" in the database.
#
#
#  Scenario:  I can only create a destination if it does not already exist in destinations that are accessible to me (i.e. public destinations or my private destinations).
#    Given there exists a public destination with name "Temple of Artemis", type "Attraction", district "Artemis", country "Turkey", latitude "37.949753" and longitude "27.363899"
#    When I create a destination with name "Temple of Artemis", type "Attraction", district "Artemis", country "Turkey", latitude "37.949753" and longitude "27.363899"
#    Then the destination should not be created and there should only be one "Temple of Artemis" in the database.
#
#  Scenario: I (the owner of the destination) can CRUD my own public destinations until another user uses this public destination. As soon as it is used by another user, ownership of the destination is transferred to the admins from that point forward. I (the original owner) can no longer modify or delete that destination.
#    Given I mark "Lighthouse of Alexandria" as public
#    And nobody uses the destination
#    When I update the destination "Lighthouse of Alexandria"
#    Then the destination should be updated
#
#  Scenario: I (the owner of the destination) can CRUD my own public destinations until another user uses this public destination. As soon as it is used by another user, ownership of the destination is transferred to the admins from that point forward. I (the original owner) can no longer modify or delete that destination.
#    Given I mark "Lighthouse of Alexandria" as public
#    And a user with user id "3" uses the destination
#    When I update the destination "Lighthouse of Alexandria"
#    Then the destination should be not be updated
#
#    Scenario: If a public destination is created and I have the same destination in my private list of destinations, it will automatically be merged with the public one. Any private information (e.g. photos, notes - future stories) on that destination will continue to remain private and only be accessible to me.
#      When a user with user id "3" creates a destination with name "Lighthouse of Alexandria" of type "Attraction" at district "Alexandria" at country "Egypt at latitude "31.170739" and longitude "29.844310"
#      Then I will no longer have "Lighthouse of Alexandria" in my private list of destinations because it will have been merged
#      And "Lighthouse of Alexandria" will be in a public list of destinations.