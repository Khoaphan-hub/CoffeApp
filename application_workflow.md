# Application Workflow (FSM Architecture) - The Code Cup

This document models the core user journeys of "The Code Cup" application. The architecture is designed as a state-driven system where user interactions trigger transitions between logical states (screens), ensuring robust state management and seamless data persistence.

---

## 1. Browsing & Customization Flow
*The core loop where users select, configure, and add products to their cart. This flow heavily utilizes dynamic state management for real-time calculations.*

*   **[State: Home]** 
    *   **Action:** The user launches the app. The `MainActivity` loads the `HomeFragment`. The UI renders the available coffee menu.
    *   **Transition:** User taps on a specific coffee item (e.g., "Latte").
*   **[State: Details]**
    *   **Action:** `DetailsActivity` opens, receiving the specific coffee ID. 
    *   **Internal Logic (Custom Feature):** As the user interacts with customization controls (Size, Ice, Shots), the `CalorieEngine` and pricing logic execute. The UI dynamically updates the **Total Price** and **Estimated Calories** in real-time without screen reloads.
    *   **Transition:** User taps the "Add to Cart" button.
    *   **Internal Logic (Database):** A new `CartItem` object is created and `INSERTED` into the local `Cart` database table to ensure persistence. The app safely closes `DetailsActivity` and returns to the Home state.

## 2. Checkout & Order Lifecycle Flow
*This flow manages the transition of data from temporary holding (Cart) to permanent ledger (Orders), handling the mock financial transaction.*

*   **[State: Cart]**
    *   **Action:** User navigates to `CartActivity`. The system queries the `Cart` database table to render the list of queued items and computes the final aggregate price. Users can manage items (e.g., swipe-to-delete).
    *   **Transition:** User taps "Checkout".
*   **[State: Order Success]**
    *   **Action:** A brief confirmation UI is displayed.
    *   **Crucial Internal Logic (Database & SharedPreferences):** 
        1. All current items in the `Cart` table are aggregated into a single `Order` entity.
        2. This `Order` is `INSERTED` into the `Orders` database table with an initial status of **`Ongoing`**.
        3. The `Cart` table is completely cleared (`DELETE`).
        4. User's Loyalty Stamps and Reward Points are incremented and saved via `SharedPreferences`.
    *   **Transition:** User taps "Track My Order".
*   **[State: My Orders]**
    *   **Action:** The `OrdersFragment` loads. The "Ongoing" tab queries and displays the newly placed order.
    *   **Transition:** The user (or mock system) triggers a "Mark as Complete" action. The order's status in the database is updated to **`History`**, and it dynamically moves to the History tab.

## 3. Gamification & Rewards Flow
*This flow manages the loyalty program, intercepting standard behavior to inject the custom gamified experience.*

*   **[State: Rewards]**
    *   **Action:** When the user accesses the `RewardsFragment`, the system reads the current stamp count from `SharedPreferences`.
    *   **Transition (Trigger Event):** When a checkout event (from Flow 2) pushes the loyalty stamp count to exactly **`8/8`**, standard navigation is intercepted.
*   **[State: Lucky Wheel (Custom Feature)]**
    *   **Action:** The `LuckyWheelActivity` or a prominent modal launches. 
    *   **Internal Logic:** The user spins the wheel. A randomized algorithm determines the bonus points (e.g., +50, +100 Points). The bonus is added to the user's total balance in `SharedPreferences`, and the stamp count is forcefully reset to `0/8`.
    *   **Transition:** User navigates to the Redeem section.
*   **[State: Redeem]**
    *   **Action:** In `RedeemActivity`, the user spends their accumulated points on a free product. The system deducts the appropriate points and injects the free item directly into the `Cart` database table with a `$0.00` price tag.
