import json

#response = requests.get('https://randomuser.me/api/?results=3?nat=fr')
#response.encoding = 'utf-8'

def generate_sql():
    with open('users_from_api.json') as my_file:
        users = json.load(my_file)

    with open('userData.sql', 'w') as file:
        file.write("INSERT INTO `user` (`email`, `password_hash`, `date_of_birth`, `gender`,`f_name`, `l_name`, `undo_redo_error`, `is_admin`, `creation_date`) VALUES \n")
        for i in range(0, len(users)):
            user = users[i]
            filter_user_data(user)

            user[''] = user['email'].replace("'", "\'")    # replace commas with
            file.write(f"('{user['email']}', '', '{user['dob']}', '{user['gender']}', '{user['first_name']}', '{user['last_name']}', 0, 0, NULL)")
            if i != len(users) - 1:
                file.write(",\n")
            else:
                file.write(";\n")   # end of file


def filter_user_data(user):
    fields_to_filter = ['first_name', 'last_name']
    for field in fields_to_filter:
        user[field] = replace_apostrophes(user[field])
        user[field] = remove_semicolons(user[field])


def replace_apostrophes(input):
    return input.replace("'", "\\'")  # escape apostrophes


def remove_semicolons(input):
    return input.replace(";", "")


def main():
    generate_sql()


if __name__ == "__main__":
    main()
