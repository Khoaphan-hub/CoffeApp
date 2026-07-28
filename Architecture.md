# CoffeeApp - Project Architecture

This document describes the actual architecture of the CoffeeApp project, detailing the roles and responsibilities of each component.

## Project File Tree

```text
com.example.coffeeapp/
├── adapter/
│   ├── CartAdapter.java           - Binds cart items to RecyclerView in CartActivity.
│   ├── CoffeeAdapter.java         - Binds coffee list to RecyclerView in HomeFragment.
│   ├── OrderAdapter.java          - Binds order history to RecyclerView in OrdersFragment.
│   ├── PointHistoryAdapter.java   - Binds point transaction history in Rewards screen.
│   └── RedeemAdapter.java         - Binds available rewards to RecyclerView in RedeemActivity.
├── database/
│   └── DatabaseHelper.java        - Manages SQLite database for cart and orders.
├── model/
│   ├── CartItem.java              - Data class for items in the shopping cart.
│   ├── Coffee.java                - Data class for coffee products.
│   ├── Order.java                 - Data class for historical orders.
│   ├── PointTransaction.java      - Data class for reward point transactions.
│   └── User.java                  - Data class for user profile information.
├── ui/
│   ├── cart/
│   │   ├── CartActivity.java      - Screen for managing cart items and checkout.
│   │   └── OrderSuccessActivity.java - Confirmation screen after successful order.
│   ├── dashboard/
│   │   └── DashboardFragment.java - Analytics screen showing spending and calorie charts.
│   ├── details/
│   │   └── DetailsActivity.java   - Product detail and customization screen.
│   ├── home/
│   │   └── HomeFragment.java      - Main landing screen with menu and loyalty card.
│   ├── main/
│   │   └── MainActivity.java      - Host activity with bottom navigation.
│   ├── orders/
│   │   └── OrdersFragment.java    - Screen displaying order history (Ongoing/History).
│   ├── profile/
│   │   └── ProfileFragment.java   - Screen displaying user profile settings.
│   └── rewards/
│       ├── LuckyWheelActivity.java- Gamified reward screen to win points.
│       ├── RedeemActivity.java    - Screen for redeeming points for free coffee.
│       └── RewardsFragment.java   - Main rewards dashboard with points and stamps.
├── utils/
│   ├── CalorieEngine.java         - Logic for calculating calories and price based on options.
│   └── Constants.java             - Application-wide constants and static coffee data.
res/
├── layout/                        - XML UI layout definitions.
├── menu/                          - Navigation menu configurations.
└── values/                        - App themes, colors, and strings.
```

## Package Structure (`com.example.coffeeapp`)

### 1. `adapter/` - Data-to-UI Coordination Layer
Classes here are responsible for binding Java object lists to UI items in `RecyclerView`.
*   **`CoffeeAdapter.java`**: Displays the standard coffee menu on the Home screen.
*   **`RedeemAdapter.java`**: Specifically handles the display of coffee items in the Redeem screen, showing the points cost for each item.
*   **`CartAdapter.java`**: Manages items added to the shopping cart, including customization details.
*   **`OrderAdapter.java`**: Displays order history, distinguishing between "Ongoing" and "History" statuses.
*   **`PointHistoryAdapter.java`**: Displays the history of point earnings and expenditures in the Rewards section.

### 2. `database/` - Persistent Storage Layer
*   **`DatabaseHelper.java`**: Orchestrates local storage using SQLite for both the `cart` and `orders` tables.

### 3. `model/` - Data Entities (Domain Models)
*   **`Coffee.java`**: Basic beverage information (ID, name, price, calories, image).
*   **`CartItem.java`**: Represents a specific selection in the cart with user customizations.
*   **`Order.java`**: Historical order data including timestamp, total, and status.
*   **`PointTransaction.java`**: Represents a single transaction of reward points.
*   **`User.java`**: Placeholder for user data.

### 4. `ui/` - User Interface Layer (Activities & Fragments)
Controls the display logic and user interaction for every screen.
*   **`main/MainActivity.java`**: The primary host activity with Bottom Navigation.
*   **`home/HomeFragment.java`**: Dashboard showing greetings, dynamic Loyalty Card, and coffee menu.
*   **`dashboard/DashboardFragment.java`**: Provides analytics and visualization of spending and calorie intake over time.
*   **`details/DetailsActivity.java`**: Configuration screen for Size, Shot, and Ice levels.
*   **`cart/`**:
    *   **`CartActivity.java`**: Cart management, total calculation, and checkout logic.
    *   **`OrderSuccessActivity.java`**: Confirmation screen after a successful transaction.
*   **`orders/OrdersFragment.java`**: Manages order history with "Ongoing/History" tabs and "Clear History" feature.
*   **`rewards/`**:
    *   **`RewardsFragment.java`**: Shows points/stamps progress and triggers the Lucky Wheel.
    *   **`LuckyWheelActivity.java`**: Gamified experience to win bonus points.
    *   **`RedeemActivity.java`**: Allows users to spend points on free coffee using `RedeemAdapter`.
*   **`profile/ProfileFragment.java`**: Displays basic user profile info.

### 5. `utils/` - Utility and Helper Logic
*   **`Constants.java`**: Centralized configuration and the static coffee catalog.
*   **`CalorieEngine.java`**: Algorithms for adjusting prices and calories based on customizations.

---

## Resources (`res/`)

*   **`drawable/`**: Contains all visual assets including product images (`img_mocha`, `img_capuchino`) and UI icons (`ic_cart`, `ic_home`).
*   **`layout/`**: XML definitions for all Activities, Fragments, and custom list items.
*   **`menu/`**: Defines the structure of the bottom navigation bar (`bottom_nav_menu.xml`).
*   **`values/`**: Global definitions for colors, strings, dimensions, and app themes.

## Specialized Mechanisms
*   **Reward System Logic**: During checkout, the app awards **100 points per $1 spent** and **1 stamp per cup**. These are stored in `SharedPreferences` for cross-session persistence.
*   **Edge-to-Edge Integration**: All Activities use `EdgeToEdge.enable(this)` and `WindowInsets` listeners to ensure UI components are properly padded around system bars and camera cutouts (notches).
