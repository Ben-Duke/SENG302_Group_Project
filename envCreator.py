"""
python3 script which takes command line arguments and creates a .env
file from them. It will overwrite an existing .env file in same directory.
"""
import sys
import os


NUMBER_ENV_VARIABLES = 5;


if (len(sys.argv) != NUMBER_ENV_VARIABLES + 1): # + 1 because first argument is script name.
    print("Invalid sys.argv length: ", len(sys.argv))
    sys.exit(1)
    
print("Successfully read ({}) env variables".format(len(sys.argv) - 1))

# sys.argv[0] is the name of the script running this program (which we dont use).
google_api_key = sys.argv[1]
eventfinda_username = sys.argv[2]
eventfinda_password = sys.argv[3]
admin_password = sys.argv[4]
test_user_password = sys.argv[5]

# Delete any existing .env file in current directory.
if (os.path.exists(".env")):
    os.remove(".env")
    
# If there is still a .env file then an error occured.
if (not os.path.exists(".env")):
    with open(".env", "w") as envFile:
        envFile.write("GOOGLE_MAPS_API_KEY={}\n".format(google_api_key))
        envFile.write("EVENTFINDA_API_KEY_USERNAME={}\n".format(eventfinda_username))
        envFile.write("EVENTFINDA_API_KEY_PASSWORD={}\n".format(eventfinda_password))
        envFile.write("ADMIN_USER_PASSWORD_DEFAULT={}\n".format(admin_password))
        envFile.write("TEST_USER_PASSWORD_DEFAULT={}\n".format(test_user_password))
        
        print("Successfully creater .env")
    sys.exit(0)    
else:
    print("Unknown error creating .env file.")
    sys.exit(1)
        

    