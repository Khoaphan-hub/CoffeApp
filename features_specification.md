# The Code Cup - Comprehensive Application Features Specification

This document outlines all the required and custom features for "The Code Cup" mobile application, organized by their respective screens.

---

## 1. Home Screen
The primary landing screen of the application where users begin their journey.
* **UI Implementation & Header Component:** A welcoming and clean layout displaying a branded header, personalized user greeting, and an intuitive layout for navigation.
* **Loyalty Card View:** A miniature dashboard showing the user's current loyalty stamp progress (e.g., 4/8 stamps) at a glance.
* **Coffee List View:** A scrollable list (ListView or RecyclerView) populated with available coffee products (e.g., Americano, Cappuccino, Mocha, Flat White). Includes product names, images, and base prices.
* **Navigation Intent:** An on-click listener on any coffee item that seamlessly navigates the user to the "Details" screen of that specific product.
* **Bottom Navigation Bar:** A persistent navigation controller allowing quick switching between core screens: Home, Rewards, My Orders.

## 2. Details Screen
The product configuration screen where users customize their selected beverage.
* **Product Customization Interface:** Interactive UI controls allowing users to modify their drink (e.g., Number of shots (Single/Double), Cup Size, Ice Level).
* **Dynamic Price Calculation:** A real-time updating total price label. As the user toggles sizes or adds extra shots, the final price adjusts immediately without page reloads.
* **[CUSTOM FEATURE] Dynamic Nutrition & Calories Tracker:** 
    * **Description:** As users customize their drink options (size, sugar, milk type, extra shots), the app dynamically calculates and displays the estimated total calories in real-time. This provides health-conscious users with immediate nutritional feedback before adding the item to their cart.
* **Add to Cart Functionality:** A primary button that persists the fully customized beverage into the application's global cart state and triggers navigation to the "My Cart" screen.
* **Cart Preview:** A quick-access icon (usually in the header) that reveals a summary of current cart items without forcing the user to leave the Details screen.
* **Back Navigation:** Standardized back button and gesture support to return to the Home Screen safely without losing un-added configurations.

## 3. My Cart Screen
The pre-checkout summary screen managing intended purchases.
* **Cart Item Rendering:** A detailed list (ListView/RecyclerView) of all queued items, explicitly displaying the customizations chosen (e.g., "Cappuccino - Double Shot - Less Ice") and their individual prices.
* **Gesture-Based Item Removal:** Intuitive swipe-to-delete functionality (swipe left) allowing users to easily remove unwanted items from the cart.
* **Total Price Display:** Computation and display of the aggregate sum of all items currently in the cart.
* **Checkout Navigation:** A decisive "Checkout" button that finalizes the cart, processes the mock payment, and navigates the user to the "Order Success" screen.

## 4. Order Success Screen
A transitional confirmation screen post-checkout.
* **Order Success UI:** A visually pleasing layout confirming the transaction was successful (e.g., checkmark animation, confirmation message).
* **Track Order Navigation:** A direct call-to-action button ("Track My Order") that pushes the user to the "My Orders" screen to view their ongoing drink preparation status.

## 5. My Orders Screen
The tracking and history management screen.
* **Order History Display:** A tabbed or sectioned list separating "Ongoing" (currently being prepared) and "History" (completed/past) orders. Includes dates, items, and total prices.
* **Order Status Transition:** An interactive event handler (e.g., a "Mark as Picked Up" button or swipe gesture) that moves an order's state from the "Ongoing" list to the "History" list.

## 6. Rewards Screen
The gamified loyalty and point management center.
* **Loyalty Stamp Logic:** A visual representation of a stamp card. For every completed order, the stamp count increments by one, up to a maximum cap of eight.
* **[CUSTOM FEATURE] Gamified Rewards (Lucky Wheel/Scratch Card):** 
    * **Description:** Upon collecting exactly 8 loyalty stamps, instead of simply resetting to zero, a celebratory animation triggers. The user is presented with a "Lucky Spin" wheel or a virtual scratch card. They interact with it to win a randomized amount of bonus reward points (e.g., 50, 100, or 200 pts) before the stamp card resets to zero. This drastically increases user retention and engagement.
* **Loyalty Card Reset:** The logic that automatically resets the stamp count back to zero after the milestone is reached and the gamified reward is claimed.
* **Points Calculation & Display:** Background logic that awards standard reward points based on the total monetary value of each past order, alongside a prominent display of total accumulated points.
* **History Rewards:** A transaction ledger showing points earned (+12 Pts) and points spent.

## 7. Redeem Rewards Screen
The marketplace for spending accumulated loyalty points.
* **Points Redemption:** A list of eligible free products (e.g., Cafe Latte for 1340 pts). On click, the app deducts the correct amount from the user's total points and adds the free item to their active cart or issues a voucher.

## 8. Profile Screen
User identity and preferences management.
* **UI Implementation:** A layout displaying the user's avatar, name, phone number, email, and address.
* **Profile Editing Functionality:** An edit toggle that transforms text fields into editable inputs, allowing the user to update and save their personal information to local storage.

## 9. General Application Architecture (Global Requirements)
Under-the-hood technical specifications required for stability.
* **State & Lifecycle Management:** Robust handling of Android/App lifecycles (onPause, onResume, onStop) to ensure that cart data, selected customizations, and navigation history are not lost during screen rotations or backgrounding.
* **Data Persistence & Initialization:** Utilizing local storage solutions (SharedPreferences for simple key-value pairs like user profile, or Room/SQLite for complex relational data like Order History and Cart contents) to persist data across application restarts.

---
*Prepared for final project submission. Good luck on development!*
