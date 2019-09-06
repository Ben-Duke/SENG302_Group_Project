import json
import requests


#response = requests.get('https://randomuser.me/api/?results=3?nat=fr')
#response.encoding = 'utf-8'
users_in = []
users = None
with open('users_from_api.json') as my_file:
        users = json.load(my_file)
print("INSERT INTO 'user' ('userid', 'email', 'password_hash', 'date_of_birth', 'gender','f_name', 'l_name', 'undo_redo_error', 'is_admin', 'creation_date') VALUES ")
for user in users:
        print(f"({1}, '{ user['email']}', '', {user['dob']}, '{user['gender'] }', '{ user['first_name']}', '{ user['last_name']}', 0, 0, 2019-07-26 03:59:17')")

