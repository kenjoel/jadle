package dao;

import model.Foodtype;
import model.Restaurant;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

import static org.junit.Assert.*;

public class Sql2oRestaurantDaoTest {
    private static Connection conn; //these variables are now static.
    private static Sql2oRestaurantDao restaurantDao; //these variables are now static.
    private static Sql2oFoodTypeDao foodtypeDao; //these variables are now static.
    private static Sql2oReviewDao reviewDao; //these variables are now static.


    @BeforeClass //changed to @BeforeClass (run once before running any tests in this file)
    public static void setUp() throws Exception { //changed to static
        String connectionString = "jdbc:postgresql://ec2-3-95-87-221.compute-1.amazonaws.com:5432/d7ld0jefl65db7" + "?sslmode=require" ; //connect to postgres test database
        Sql2o sql2o = new Sql2o(connectionString, "sltjvgqfwdvedr", "b11b9460a36c13d06bf8b62676e12b3b6385346df6817b5a446f1870a8e77c58"); //changed user and pass to null for mac users...Linux & windows need strings
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodTypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open(); //open connection once before this test file is run
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
    public void  testSaveWorks(){
        Restaurant restaurant = new Restaurant("kims","072672", "72627662", "089278828");
        restaurantDao.add(restaurant);
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void RestaurantReturnsFoodtypesCorrectly() throws Exception {
        Foodtype testFoodtype  = setupNewFoodtype();
        foodtypeDao.add(testFoodtype);

        Foodtype otherFoodtype  = setupNewFoodtype();
        foodtypeDao.add(otherFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodType(testRestaurant,otherFoodtype);

        Foodtype[] foodtypes = {testFoodtype, otherFoodtype}; //oh hi what is this? Observe how we use its assertion below.
        System.out.println(Arrays.toString(foodtypes));
        assertEquals(Arrays.asList(foodtypes), restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()));
    }

    //helper functions
    public Restaurant setupRestaurant() {
        Restaurant restaurant = new Restaurant("Mama Kiwinya", "878272672", "0100", "0782874673", "https://mamakiwinya.github.oi", "kenhyoiitr@gmail.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Restaurant setupAltRestaurant(){
        Restaurant restaurant1 = new Restaurant("WINDERGUARDLTD","034500","02100","06094512");
        restaurantDao.add(restaurant1);
        return restaurant1;

    }

    public Foodtype setupNewFoodtype(){
        Foodtype foodtype = new Foodtype("Samchi-gui");
        return foodtype;
    }
}

