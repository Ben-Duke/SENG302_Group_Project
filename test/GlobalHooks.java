public class GlobalHooks {
    /**
     * The fake database
     */
//    public static Database database;
//
//    @Override
//    protected Application provideApplication() {
//        return new GuiceApplicationBuilder().build();
//    }

//    @Before
//    public void before() {
//        database = Databases.inMemory();
//        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
//                1,
//                "create table test (id bigint not null, name varchar(255));",
//                "drop table test;"
//        )));
//        User user = new User("testUser");
//        user.save();
//        User user2 = new User("testUser2");
//        user2.save();
//        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
//        testDatabaseManager.populateDatabase();
//        Assert.assertEquals(3, User.find.all().size());
//    }
//
//    @After
//    public void after() {
//        Evolutions.cleanupEvolutions(database);
//        database.shutdown();
//    }
}
