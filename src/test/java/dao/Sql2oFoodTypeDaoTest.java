package dao;

import model.Foodtype;
import model.Restaurant;
import model.Review;
import org.graalvm.compiler.lir.amd64.AMD64Arithmetic;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import static org.junit.Assert.*;

public class Sql2oFoodTypeDaoTest{
    private static Connection conn; //these variables are now static.
    private static Sql2oRestaurantDao restaurantDao; //these variables are now static.
    private static Sql2oFoodTypeDao foodTypeDao; //these variables are now static.
    private static Sql2oReviewDao reviewDao; //these variables are now static.


    @BeforeClass //changed to @BeforeClass (run once before running any tests in this file)
    public static void setUp() throws Exception { //changed to static
        String connectionString = "jdbc:postgresql://localhost:5432/jadlet_test"; //connect to postgres test database
        Sql2o sql2o = new Sql2o(connectionString, "moringa", "://postgres"); //changed user and pass to null for mac users...Linux & windows need strings
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodTypeDao = new Sql2oFoodTypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open(); //open connection once before this test file is run
    }

    @After //run after every test
    public void tearDown() throws Exception {  //I have changed
        System.out.println("clearing database");
        restaurantDao.clearAll(); //clear all restaurants after every test
        foodTypeDao.clearAll(); //clear all restaurants after every test
        reviewDao.clearAll(); //clear all restaurants after every test
    }

    @AfterClass //changed to @AfterClass (run once after all tests in this file completed)
    public static void shutDown() throws Exception{ //changed to static
        conn.close(); // close connection once after this entire test file is finished
        System.out.println("connection closed");
    }


    @Test
    public void addingFoodSetsId() throws Exception {
        Foodtype testFoodtype = setupNewFoodtype();
        int originalFoodtypeId = testFoodtype.getId();
        foodTypeDao.add(testFoodtype);
        assertNotEquals(originalFoodtypeId,testFoodtype.getId());
    }

    @Test
    public void addedFoodtypesAreReturnedFromGetAll() throws Exception {
        Foodtype testfoodtype = setupNewFoodtype();
        foodTypeDao.add(testfoodtype);
        assertEquals(1, foodTypeDao.getAll().size());
    }

    @Test
    public void noFoodtypesReturnsEmptyList() throws Exception {
        assertEquals(0, foodTypeDao.getAll().size());
    }

    @Test
    public void deleteByIdDeletesCorrectFoodtype() throws Exception {
        Foodtype foodtype = setupNewFoodtype();
        foodTypeDao.add(foodtype);
        foodTypeDao.deleteById(foodtype.getId());
        assertEquals(0, foodTypeDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Foodtype testFoodtype = setupNewFoodtype();
        Foodtype otherFoodtype = setupNewFoodtype();
        foodTypeDao.clearAll();
        assertEquals(0, foodTypeDao.getAll().size());
    }

    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {

        Restaurant testRestaurant = setupRestaurant();
        Restaurant altRestaurant = setupAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype = setupNewFoodtype();

        foodTypeDao.add(testFoodtype);

        foodTypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodTypeDao.addFoodtypeToRestaurant(testFoodtype, altRestaurant);

        assertEquals(2, foodTypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodTypeDao.add(testFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodType(altRestaurant, testFoodtype);

        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()).size());
    }

    // helpers

    public Foodtype setupNewFoodtype(){
        return new Foodtype("Sushi");
    }

    public Restaurant setupRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Safaricom", "97232", "254-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Restaurant setupAltRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Safaricom", "97232", "254-402-9874");
        restaurantDao.add(restaurant);
        return restaurant;
    }
}