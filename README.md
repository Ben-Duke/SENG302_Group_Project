# TravelEA - Team 800
Basic Play project using sbt build and basic GitLab CI.



## Basic Project Structure
* app/ Application source
* public/ Javascript, images and stylesheets
* conf/ configuration files required to ensure the project builds properly

## How to run
Open terminal
```bash
git clone https://eng-git.canterbury.ac.nz/seng302-2019/team-800
```
Change directory to the root directory of the project and execute:
```bash
sbt dist
```
Unzip snapshot files from target/universal folder of your application to a chosen directory

Navigate to the unzipped directory root 

(For Unix users: Then make the file executable)
```bash
chmod +x ./bin/team-800
```

And run the server 
```bash
./bin/team-800
```
And open <http://localhost:9000/>

## Login details
##### We recommend you use the test user to test our system

* Default admin:

        Email: admin@admin.com

        Password: FancyRock08

* Test user:

        Email: testuser1@uclive.ac.nz

        Password: TinyHuman57
        
## How to use
A detailed guide exists here: <https://eng-git.canterbury.ac.nz/seng302-2019/team-800/wikis/user-guide>
* Create/Login using the buttons on the main page.
* To sign up for the first time you must complete all required fields
* Once logged in successfully, you will be directed to your profile page.
* You can perform all profile actions from the profile page.
* The admin button is only visible if you are an admin.
* An admin can act as another user to reset their password or do other user tasks through the admin panel.
* An admin can accept or reject modification requests for public destinations.
* To get back to the main page Logout from the navigation bar.
* Note: Home/TravelEA buttons navigate to the profile page but only if you are logged in.
* You can undo/redo many actions by pressing the undo/redo buttons on the top right
  of the navigation bar. 
* Alternatively to undo use the ctrl + z keyboard shortcut and ctrl + shift + z for redo.

## How to run tests
Clone the repository as above
```bash
git clone https://eng-git.canterbury.ac.nz/seng302-2019/team-800
```

Navigate to the root of the cloned repository and execute the test command
```bash
sbt test
```
All tests will run and a summary will be displayed. This will take a few minutes

## Contributors
* Benjamin Duke
* Gavin Ong
* Luke Parkinson
* Michael Shannon
* Noel Bisson
* Logan Shaw
* Jason Little
* Jack Orchard
* Priyesh Shah


## Attribution

* Google Maps API Code (partially modified)
        
        https://developers.google.com/maps/documentation/javascript/localization
        https://developers.google.com/maps/documentation/javascript/adding-a-google-map
        https://developers.google.com/maps/documentation/javascript/adding-a-legend
        https://developers.google.com/maps/documentation/javascript/events
        https://developers.google.com/maps/documentation/javascript/markers
        
## Dependencies

https://eng-git.canterbury.ac.nz/seng302-2019/team-800/wikis/dependencies

## License
Creative Commons Zero v1.0 Universal