import sys
import os

#GOOGLE_MAPS_API_KEY=
#EVENTFINDA_API_KEY_USERNAME=
#EVENTFINDA_API_KEY_PASSWORD=
#ADMIN_USER_PASSWORD_DEFAULT=
#TEST_USER_PASSWORD_DEFAULT=

if (len(sys.argv) != 5):
    sys.exit(1)

google_api_key = sys.argv[0]
eventfinda_username = sys.argv[1]
eventfinda_password = sys.argv[2]
admin_password = sys.argv[3]
test_user_password = sys.argv[4]

if (os.path.exists(".env")):
    os.remove(".env")
    
if (not os.path.exists(".env")):
    with open(".env", "w") as envFile:
        envFile.write("GOOGLE_MAPS_API_KEY={}\n".format(google_api_key))
        envFile.write("EVENTFINDA_API_KEY_USERNAME={}\n".format(eventfinda_username))
        envFile.write("EVENTFINDA_API_KEY_PASSWORD={}\n".format(eventfinda_password))
        envFile.write("ADMIN_USER_PASSWORD_DEFAULT={}\n".format(admin_password))
        envFile.write("TEST_USER_PASSWORD_DEFAULT={}\n".format(test_user_password))
        

sys.exit(0)        