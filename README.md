# TravelEA - Team 800
Basic Play project using sbt build and basic GitLab CI.

## Basic Project Structure
* app/ Application source
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
./bin/team-800 -Dplay.http.secret.key="bmcsykbm3[IAkc3XKZnWCAEX[K[RmtJMDmJCe9Nhy;?UDpNu`p9`fuW@^h5tcwlQ" -Dplay.evolutions.db.autoApply=true
```
And open <http://localhost:9000/>

## Login details
* Default admin:

        Email: admin@admin.com

        Password: admin


## How to use
* Create/Login using the buttons on the main page.
* To sign up for the first time you must complete all required fields
* Once logged in successfully, you will be directed to your profile page.
* You can perform all profile actions from the profile page.
* The admin button is only visible if you are an admin.
* To get back to the main page Logout from the navigation bar.
* Note: Home/TravelEA buttons navigate to the profile page but only if you are logged in.
