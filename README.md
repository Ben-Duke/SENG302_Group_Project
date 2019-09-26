# iVentr - Team 800
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

Create an environmental variable file .env in the same folder as the team-800 
executable. 

```bash
touch ./bin/.env
```
See DOT_ENV_TEMPLATE in this projects root directory for a .env template (includes explanation of the .env file).
Write to the .env file the following contents, with actual values where directed.
```
GOOGLE_MAPS_API_KEY=[insert value here]
EVENTFINDA_API_KEY_USERNAME=[insert value here]
EVENTFINDA_API_KEY_PASSWORD=[insert value here]
ADMIN_USER_PASSWORD_DEFAULT=[insert value here]
TEST_USER_PASSWORD_DEFAULT=[insert value here]
```

Now run the server 
```bash
./bin/team-800
```
And open <http://localhost:9000/>

## Login details
##### We recommend you use the test user to test our system

* Default admin:

        Email: admin@admin.com

        Password: [set by ADMIN_USER_PASSWORD_DEFAULT .env key]

* Test user:

        Email: testuser1@uclive.ac.nz

        Password: [set by TEST_USER_PASSWORD_DEFAULT .env key]
        
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
* Note: Home/Logo buttons navigate to the profile page but only if you are logged in.
* You can undo/redo many actions by pressing the undo/redo buttons on the top right
  of the navigation bar. 
* Alternatively to undo use the ctrl + z keyboard shortcut and ctrl + shift + z for redo.

## Changing the database you are connected to for manual testing
### Connect to mysql
1. Open application.conf
2. Comment in the block (around line 12)
```//  default.url="jdbc:mysql://mysql2.csse.canterbury.ac.nz/seng302-2019-team800-prod"```
  ```default.url="jdbc:mysql://mysql2.csse.canterbury.ac.nz/seng302-2019-team800-test"```
  ```default.username=seng302-team800```
  ```default.password="ChampHails8911"```


3. comment in the line ending with 'test' to use the dev database or the line ending with 'prod' to use the production database

4. Check evolutions are disabled (=false) on the line 
```play.evolutions {```
```db.default.enabled = false```
near line 31

### Connect to h2
1. Open Application.conf
2. Comment out the block 
```//  default.url="jdbc:mysql://mysql2.csse.canterbury.ac.nz/seng302-2019-team800-prod"```
  ```default.url="jdbc:mysql://mysql2.csse.canterbury.ac.nz/seng302-2019-team800-test"```
  ```default.username=seng302-team800```
  ```default.password="ChampHails8911"```

3. Uncomment the block (around line 8)
```//  default.driver=org.h2.Driver```
```//  default.url="jdbc:h2:mem:play"```

4. Ensure evolutions are enabled (=true) on the line
```play.evolutions {```
```db.default.enabled = true```
near line 31

### Reset/resample db
##### mysql
1. Enable evolutions (=true) near line 31
```play.evolutions {```
```db.default.enabled = true```

2. Change the password of testuser1 (this will be overriden by TestdDatabaseManager so their actual passowrd will not be affected)
Refresh the app

##### h2 
As long as you have enabled evolutions as per the instructions related to using h2 above the db will be reset and resampled
when you start the application


### Further information
See wiki page https://eng-git.canterbury.ac.nz/seng302-2019/team-800/wikis/design-decisions/Story-8c-Connect-to-MySql-database

## How to run tests
Clone the repository as above
```bash
git clone https://eng-git.canterbury.ac.nz/seng302-2019/team-800
```

Navigate to the root of the cloned repository and execute the test command
```bash
sbt test
```
All tests will run and a summary will be displayed. This will take a few minutes.

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