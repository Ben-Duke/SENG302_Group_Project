import json
import string

#response = requests.get('https://randomuser.me/api/?results=3?nat=fr')
#response.encoding = 'utf-8'


def is_user_safe(user):
    alphabetical_chars = set(list(string.ascii_letters))
    digits = set(list(string.digits))
    other_chars = {'.', '@', '-', ' '}

    safe_chars = set()
    safe_chars.update(alphabetical_chars)
    safe_chars.update(digits)
    safe_chars.update(other_chars)

    user_chars = set(list(f"{user['email']}{user['dob']}{user['gender']}{user['first_name']}{user['last_name']}"))

    for char in user_chars:
        if char not in safe_chars:
            return False

    return True


def generate_sql():
    with open('users_from_api.json') as my_file:
        users = json.load(my_file)

    with open('userData.sql', 'w') as file:
        file.write("INSERT INTO `user` (`email`, `password_hash`, `date_of_birth`, `gender`,`f_name`, `l_name`, `undo_redo_error`, `is_admin`, `creation_date`) VALUES \n")
        for i in range(0, len(users)):
            user = users[i]
            filter_user_data(user)

            if is_user_safe(user):
                file.write(
                    f"('{user['email']}', '', '{user['dob']}', '{user['gender']}', '{user['first_name']}', '{user['last_name']}', 0, 0, '2019-01-01 00:00:00')")
                if i != len(users) - 1:
                    file.write(",\n")
                else:
                    file.write(";\n")  # end of file


def filter_user_data(user):
    fields_to_filter = ['first_name', 'last_name']
    for field in fields_to_filter:
        user[field] = replace_apostrophes(user[field])


def replace_apostrophes(input):
    return input.replace("'", "\\'")  # escape apostrophes


def main():
    generate_sql()


if __name__ == "__main__":
    main()
