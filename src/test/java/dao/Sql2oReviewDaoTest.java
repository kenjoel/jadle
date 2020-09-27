package dao;

import model.Restaurant;
import model.Review;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

import static org.junit.Assert.*;

public class Sql2oReviewDaoTest {
    private static  Sql2o sql2o;
    private static Connection conn; //these variables are now static.
    private static Sql2oRestaurantDao restaurantDao; //these variables are now static.
    private static Sql2oFoodTypeDao foodtypeDao; //these variables are now static.
    private static Sql2oReviewDao reviewDao; //these variables are now static.


    @BeforeClass //changed to @BeforeClass (run once before running any tests in this file)
    public static void setUp() throws Exception { //changed to static
        String connectionString = "jdbc:postgresql://localhost:5432/jadlet_test"; //connect to postgres test database
        Sql2o sql2o = new Sql2o(connectionString, "moringa", "://postgres"); //changed user and pass to null for mac users...Linux & windows need strings
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodTypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open(); //open connection once before this test file is run
        reviewDao.clearAll();
    }

    @After //run after every test
    public void tearDown() throws Exception {  //I have changed
        System.out.println("clearing database");
        restaurantDao.clearAll(); //clear all restaurants after every test
        foodtypeDao.clearAll(); //clear all restaurants after every test
        reviewDao.clearAll(); //clear all restaurants after every test
    }

    @AfterClass //changed to @AfterClass (run once after all tests in this file completed)
    public static void shutDown() throws Exception{ //changed to static
        conn.close(); // close connection once after this entire test file is finished
        System.out.println("connection closed");
    }

    @Test
    public void addingReviewSetsId() throws Exception {
        Review testReview = setupReview();
        System.out.println(testReview.getId());
        assertEquals("Kim", testReview.getWrittenBy());
    }

    @Test
    public void timeStampIsReturnedCorrectly() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Review testReview = new Review("Captain Kirk", "Captain Kirk",3, testRestaurant.getId());
        reviewDao.add(testReview);

        long creationTime = testReview.getCreatedat();
        long savedTime = reviewDao.getAll().get(0).getCreatedat();
        String formattedCreationTime = testReview.getFormattedCreatedAt();
        String formattedSavedTime = reviewDao.getAll().get(0).getFormattedCreatedAt();
        assertEquals(formattedCreationTime,formattedSavedTime);
        assertEquals(creationTime, savedTime);
    }



    @Test
    public void getAll() throws Exception {
        Review review1 = setupReview();
        Review review2 = setupReview();
        assertEquals(2, reviewDao.getAll().size());
    }

    @Test
    public void getAllReviewsByRestaurant() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant(); //add in some extra data to see if it interferes
        Review review1 = setupReviewForRestaurant(testRestaurant);
        Review review2 = setupReviewForRestaurant(testRestaurant);
        Review reviewForOtherRestaurant = setupReviewForRestaurant(otherRestaurant);
        assertEquals(2, reviewDao.getAllReviewsByRestaurant(testRestaurant.getId()).size());
    }

    @Test
    public void deleteById() throws Exception {
        Review testReview = setupReview();
        Review otherReview = setupReview();
        assertEquals(2, reviewDao.getAll().size());
        reviewDao.deleteById(testReview.getId());
        assertEquals(1, reviewDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Review testReview = setupReview();
        Review otherReview = setupReview();
        reviewDao.clearAll();
        assertEquals(0, reviewDao.getAll().size());
    }

    @Test
    public void reviewsAreReturnedInCorrectOrder() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Review testReview = new Review("Captain Kirk", "foodcoma!", 4, testRestaurant.getId());
        reviewDao.add(testReview);
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        Review testSecondReview = new Review("Mr. Spock", "passable", 1,  testRestaurant.getId());
        reviewDao.add(testSecondReview);

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        Review testThirdReview = new Review("Scotty", "bloody good grub!", 3, testRestaurant.getId());
        reviewDao.add(testThirdReview);

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        Review testFourthReview = new Review("I prefer home cooking", "Mr. Sulu", 2, testRestaurant.getId());
        reviewDao.add(testFourthReview);

        assertEquals(4, reviewDao.getAllReviewsByRestaurant(testRestaurant.getId()).size()); //it is important we verify that the list is the same size.
        assertEquals("I prefer home cooking", reviewDao.getAllReviewsByRestaurantSortedNewestToOldest(testRestaurant.getId()).get(0).getContent());
    }



    //helpers

    public Review setupReview() {
        Review review = new Review("great", "Kim", 4, 555);
        reviewDao.add(review);
        return review;
    }

    public Review setupReviewForRestaurant(Restaurant restaurant) {
        Review review = new Review("great", "Kim", 4, restaurant.getId());
        reviewDao.add(review);
        return review;
    }

    public Restaurant setupRestaurant() {
        Restaurant restaurant = new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

}