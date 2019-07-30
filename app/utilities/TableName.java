package utilities;

/** Enum mapping database table names to the column name of their primary key(id)
 *  Used for clearing the database in TestDatabaseManager
 *
 * Order of names must be the same as in 3.sql #!Downs to prevent foreign key violations
 */
public enum TableName {
    // 3.sql tables
    visit("visitid"),
    user_photo_destination("user_photo_photo_id", false),
    user_photo("photo_id"),
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
    destination("destid"),
    admin("id"),
    user("userid"),

    // 2.sql tables
    passport("passid"),
    nationality("natid");

    private String columnName;
    private boolean autoIncremented;

    TableName(String columnName) {
        this.columnName = columnName;
        this.autoIncremented = false;
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
