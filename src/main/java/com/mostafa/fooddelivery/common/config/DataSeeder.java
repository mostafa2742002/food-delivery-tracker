package com.mostafa.fooddelivery.common.config;

import com.mostafa.fooddelivery.driver.entity.Driver;
import com.mostafa.fooddelivery.driver.entity.VehicleType;
import com.mostafa.fooddelivery.driver.repository.DriverRepository;
import com.mostafa.fooddelivery.restaurant.entity.MenuItem;
import com.mostafa.fooddelivery.restaurant.entity.Restaurant;
import com.mostafa.fooddelivery.restaurant.repository.MenuItemRepository;
import com.mostafa.fooddelivery.restaurant.repository.RestaurantRepository;
import com.mostafa.fooddelivery.user.entity.Role;
import com.mostafa.fooddelivery.user.entity.User;
import com.mostafa.fooddelivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;  // ADD THIS

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already has data. Skipping seeding.");
            return;
        }

        log.info("🌱 Starting database seeding...");

        List<User> users = createUsers();
        createRestaurants(users);
        createDrivers(users);

        log.info("✅ Database seeding completed!");
    }

    private List<User> createUsers() {
        log.info("Creating users...");

        // USE passwordEncoder.encode() for all passwords!
        User customer1 = User.builder()
                .name("Ahmed Hassan")
                .email("ahmed@test.com")
                .password(passwordEncoder.encode("password123"))  // HASHED!
                .phone("+201234567890")
                .role(Role.CUSTOMER)
                .build();

        User customer2 = User.builder()
                .name("Sara Mohamed")
                .email("sara@test.com")
                .password(passwordEncoder.encode("password123"))  // HASHED!
                .phone("+201234567891")
                .role(Role.CUSTOMER)
                .build();

        User restaurantOwner1 = User.builder()
                .name("Mohamed Ali")
                .email("mohamed@pizzahouse.com")
                .password(passwordEncoder.encode("password123"))  // HASHED!
                .phone("+201111111111")
                .role(Role.RESTAURANT_OWNER)
                .build();

        User restaurantOwner2 = User.builder()
                .name("Fatma Ibrahim")
                .email("fatma@burgerking.com")
                .password(passwordEncoder.encode("password123"))  // HASHED!
                .phone("+201111111112")
                .role(Role.RESTAURANT_OWNER)
                .build();

        User driverUser1 = User.builder()
                .name("Omar Khaled")
                .email("omar@driver.com")
                .password(passwordEncoder.encode("password123"))  // HASHED!
                .phone("+201222222221")
                .role(Role.DRIVER)
                .build();

        User driverUser2 = User.builder()
                .name("Youssef Mahmoud")
                .email("youssef@driver.com")
                .password(passwordEncoder.encode("password123"))  // HASHED!
                .phone("+201222222222")
                .role(Role.DRIVER)
                .build();

        User admin = User.builder()
                .name("Admin User")
                .email("admin@fooddelivery.com")
                .password(passwordEncoder.encode("admin123"))  // HASHED!
                .phone("+201000000000")
                .role(Role.ADMIN)
                .build();

        List<User> users = Arrays.asList(
                customer1, customer2,
                restaurantOwner1, restaurantOwner2,
                driverUser1, driverUser2,
                admin
        );

        userRepository.saveAll(users);
        log.info("Created {} users", users.size());

        return users;
    }

    // Keep createRestaurants() and createDrivers() the same as before
    private void createRestaurants(List<User> users) {
        log.info("Creating restaurants and menu items...");

        User pizzaOwner = users.stream()
                .filter(u -> u.getEmail().equals("mohamed@pizzahouse.com"))
                .findFirst().orElseThrow();

        User burgerOwner = users.stream()
                .filter(u -> u.getEmail().equals("fatma@burgerking.com"))
                .findFirst().orElseThrow();

        Restaurant pizzaHouse = Restaurant.builder()
                .name("Pizza House")
                .address("123 Main Street, Cairo")
                .phone("+201111111111")
                .description("Best pizza in town! Fresh ingredients and fast delivery.")
                .cuisineType("Italian")
                .rating(4.5)
                .owner(pizzaOwner)
                .build();

        restaurantRepository.save(pizzaHouse);

        List<MenuItem> pizzaMenuItems = Arrays.asList(
                MenuItem.builder()
                        .name("Margherita Pizza")
                        .description("Classic tomato sauce, mozzarella, and fresh basil")
                        .price(new BigDecimal("89.99"))
                        .category("Pizza")
                        .restaurant(pizzaHouse)
                        .build(),
                MenuItem.builder()
                        .name("Pepperoni Pizza")
                        .description("Loaded with pepperoni and extra cheese")
                        .price(new BigDecimal("109.99"))
                        .category("Pizza")
                        .restaurant(pizzaHouse)
                        .build(),
                MenuItem.builder()
                        .name("Chicken BBQ Pizza")
                        .description("Grilled chicken with BBQ sauce and onions")
                        .price(new BigDecimal("119.99"))
                        .category("Pizza")
                        .restaurant(pizzaHouse)
                        .build(),
                MenuItem.builder()
                        .name("Garlic Bread")
                        .description("Crispy bread with garlic butter")
                        .price(new BigDecimal("29.99"))
                        .category("Appetizer")
                        .restaurant(pizzaHouse)
                        .build(),
                MenuItem.builder()
                        .name("Coca Cola")
                        .description("330ml can")
                        .price(new BigDecimal("15.00"))
                        .category("Drinks")
                        .restaurant(pizzaHouse)
                        .build()
        );

        menuItemRepository.saveAll(pizzaMenuItems);

        Restaurant burgerKing = Restaurant.builder()
                .name("Burger Kingdom")
                .address("456 Food Court, Giza")
                .phone("+201111111112")
                .description("Juicy burgers made with 100% fresh beef")
                .cuisineType("American")
                .rating(4.3)
                .owner(burgerOwner)
                .build();

        restaurantRepository.save(burgerKing);

        List<MenuItem> burgerMenuItems = Arrays.asList(
                MenuItem.builder()
                        .name("Classic Burger")
                        .description("Beef patty, lettuce, tomato, and special sauce")
                        .price(new BigDecimal("79.99"))
                        .category("Burgers")
                        .restaurant(burgerKing)
                        .build(),
                MenuItem.builder()
                        .name("Double Cheese Burger")
                        .description("Two beef patties with double cheese")
                        .price(new BigDecimal("109.99"))
                        .category("Burgers")
                        .restaurant(burgerKing)
                        .build(),
                MenuItem.builder()
                        .name("Chicken Burger")
                        .description("Crispy chicken fillet with mayo")
                        .price(new BigDecimal("89.99"))
                        .category("Burgers")
                        .restaurant(burgerKing)
                        .build(),
                MenuItem.builder()
                        .name("French Fries")
                        .description("Crispy golden fries")
                        .price(new BigDecimal("25.00"))
                        .category("Sides")
                        .restaurant(burgerKing)
                        .build(),
                MenuItem.builder()
                        .name("Chocolate Milkshake")
                        .description("Creamy chocolate shake")
                        .price(new BigDecimal("35.00"))
                        .category("Drinks")
                        .restaurant(burgerKing)
                        .build()
        );

        menuItemRepository.saveAll(burgerMenuItems);

        log.info("Created 2 restaurants with menu items");
    }

    private void createDrivers(List<User> users) {
        log.info("Creating drivers...");

        User driverUser1 = users.stream()
                .filter(u -> u.getEmail().equals("omar@driver.com"))
                .findFirst().orElseThrow();

        User driverUser2 = users.stream()
                .filter(u -> u.getEmail().equals("youssef@driver.com"))
                .findFirst().orElseThrow();

        Driver driver1 = Driver.builder()
                .user(driverUser1)
                .vehicleType(VehicleType.MOTORCYCLE)
                .licensePlate("ABC-1234")
                .isAvailable(true)
                .rating(4.7)
                .totalDeliveries(156)
                .latitude(30.0444)
                .longitude(31.2357)
                .build();

        Driver driver2 = Driver.builder()
                .user(driverUser2)
                .vehicleType(VehicleType.CAR)
                .licensePlate("XYZ-5678")
                .isAvailable(true)
                .rating(4.5)
                .totalDeliveries(89)
                .latitude(30.0131)
                .longitude(31.2089)
                .build();

        driverRepository.saveAll(Arrays.asList(driver1, driver2));

        log.info("Created 2 drivers");
    }
}