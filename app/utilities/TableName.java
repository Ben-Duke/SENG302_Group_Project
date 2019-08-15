package utilities;

/** Enum mapping database table names to the column name of their primary key(id)
 *  Used for clearing evolutions data the database in TestDatabaseManager
 *
 * Order of names must be the same as in 3.sql #!Downs to prevent foreign key violations
 */
public enum TableName {
    // drop all connecting tables first - don't need to specify primary key column name
    trip_tag("", false),
    destination_tag("", false),
    media_tag("", false),

    tag("tag_id"),

    visit("visitid"),
    destination_media("destination_destid", false),
    album_media("media_media_id", false),
    user_treasure_hunt("user_userid", false),
    user_traveller_type("user_userid", false),
    user_passport("user_userid", false),
    user_nationality("user_userid", false),
    trip("tripid"),
    treasure_hunt("thuntid"),
    destination_modification_request_traveller_type(
            "destination_modification_request_id", false),
    destination_modification_request("id"),
    destination_traveller_type("destination_destid", false),
    traveller_type("ttypeid"),
    media("media_id"),
    album("album_id"),

    destination("destid"),
    admin("user_id"),   // has an underscore but user primary key does not
    user("userid"),

    passport("passid"),
    nationality("natid");

    private String columnName;
    private boolean autoIncremented;

    TableName(String columnName) {
        this.columnName = columnName;
        this.autoIncremented = true;
    }

    TableName(String columnName, boolean autoIncremented) {
        this.columnName = columnName;
        this.autoIncremented = autoIncremented;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isAutoIncremented() {
        return autoIncremented;
    }
}
