import json

# INSERT INTO `destination` (`destid`, `user`, `is_public`, `dest_name`, `dest_type`, `district`, `country`,
#                            `is_country_valid`, `latitude`, `longitude`, `dest_is_public`, `primary_photo_media_id`) VALUES
# (1, 2, 0, 'Christchurch', 'Town', 'Canterbury', 'New Zealand', 1, -43.5321, 172.6362, 1, NULL)

def is_safe(airport):
    #airport['name']}', 'Town', '{airport['city']}', '{airport['country']}
    
    all_chars_concat = airport['name'] + airport['city'] + airport['country']
    all_chars_concat = all_chars_concat.replace(' ', '')
    return all_chars_concat.isalpha()

airports = None

with open("airports.json") as file:
    airports = json.load(file)

if airports is not None:
    count = 0
    offset = 100
    
    destination_out_file = open("airports.sql", "w")
    album_out_file = open("albums.sql", "w")
    
    insertHeader = "INSERT INTO `destination` (`destid`, `user`, `is_public`, "
    insertHeader += "`dest_name`, `dest_type`, `district`, `country`, `is_country_valid`, "
    insertHeader += "`latitude`, `longitude`, `dest_is_public`, `primary_photo_media_id`) VALUES\n"
    destination_out_file.write(insertHeader)
    
    albumHeader = "INSERT INTO `album` (`album_id`, `user`, `destination`, `primary_photo_media_id`, `is_default`, `title`) VALUES\n"
    album_out_file.write(albumHeader)
    
    
    for i in range(0, len(airports) - 2):
        airport = airports[i]
        
        if is_safe(airport):
            latitude = float(airport['lat'])
            longitude = float(airport['lon'])
            
            
            
            destination_out_file.write(f"({count + offset}, 1, 1, '{airport['name']}', 'Town', '{airport['city']}', '{airport['country']}', 1, {latitude}, {longitude}, 1, NULL),\n")
            album_out_file.write(f"({count + offset}, NULL, {count + offset}, NULL, 1, '{airport['name']}'),\n")
            count += 1
        
    last_airport = airports[-1]
    if is_safe(last_airport):
        latitude = float(last_airport['lat'])
        longitude = float(last_airport['lon'])
        
        destination_out_file.write(f"({count + offset}, 1, 1, '{last_airport['name']}', 'Town', '{last_airport['city']}', '{last_airport['country']}', 1, {latitude}, {longitude}, 1, NULL);")
        album_out_file.write(f"({count + offset}, NULL, {count + offset}, NULL, 1, '{airport['name']}');")
        count += 1
    else:
        destination_out_file.write("BAD")
        
        
    destination_out_file.close()
    album_out_file.close()
            
            
    
            
        
    
            
        







