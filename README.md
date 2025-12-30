# GroceryPlus - Complete Grocery Management System

![GroceryPlus Logo](images/splash_logo.png)

A comprehensive Android e-commerce application for grocery shopping with real-time order tracking, delivery management, and admin dashboard.

## 🚀 Quick Start

### Features at a Glance
- 🛒 **Customer App**: Browse, cart, wishlist, order tracking
- 👨‍💼 **Admin Dashboard**: Products, orders, delivery, analytics
- 📱 **Real-time**: Live order tracking, notifications
- 💳 **Payments**: Stripe + Cash on Delivery
- 📊 **Reports**: CSV export, inventory alerts
- 🎨 **Modern UI**: Material Design, smooth animations

## 📱 Screenshots

### Customer Experience
- 🏠 **Home Screen**: Personalized product recommendations
- 🛍️ **Product Catalog**: Search, categories, filters
- 🛒 **Shopping Cart**: Smart cart with real-time updates
- 📦 **Order Tracking**: Live map-based delivery tracking
- 💬 **Customer Support**: Direct chat with admin

### Admin Experience
- 📊 **Dashboard**: Business metrics and analytics
- 📦 **Order Management**: Process and assign orders
- 👥 **Delivery Management**: Team availability and assignments
- 📈 **Reports**: Export business data
- ⚙️ **Settings**: Store configuration and maintenance

## 🏗️ Architecture

### Technology Stack
- **Platform**: Android (Java)
- **Database**: SQLite with Repository Pattern
- **Backend**: Firebase Cloud Messaging (FCM)
- **Maps**: OSMDroid for tracking
- **Payment**: Stripe + Cash on Delivery
- **UI**: Material Design Components

### Key Components
```
├── Customer App
│   ├── Authentication & Profiles
│   ├── Product Catalog & Search
│   ├── Shopping Cart & Wishlist
│   ├── Order Management & Tracking
│   ├── Payment Processing
│   └── Notifications & Chat
│
├── Admin Dashboard
│   ├── Product & Category Management
│   ├── Order Processing & Assignment
│   ├── Delivery Personnel Management
│   ├── Analytics & Reports
│   ├── Inventory Alerts
│   ├── Settings & Configuration
│   └── Customer Management
```

## 🎯 Core Features

### Customer Features
- ✅ **Secure Authentication**: Login/signup with role-based access
- ✅ **Product Discovery**: Search, categories, favorites, reviews
- ✅ **Smart Cart**: Add/remove items, quantity management
- ✅ **Wishlist**: Save items for later purchase
- ✅ **Order Management**: Place orders, track delivery, reorder
- ✅ **Payment Options**: Stripe integration and Cash on Delivery
- ✅ **Live Tracking**: Real-time map-based delivery tracking
- ✅ **Notifications**: Order status and delivery updates
- ✅ **Chat Support**: Direct messaging with admin
- ✅ **Profile Management**: Personal info and addresses

### Admin Features
- ✅ **Analytics Dashboard**: Revenue, orders, customers metrics
- ✅ **Product Management**: Add/edit products, stock control
- ✅ **Category Management**: Organize product categories
- ✅ **Order Processing**: View orders, assign delivery, status updates
- ✅ **Delivery Management**: Staff management, availability tracking
- ✅ **Customer Management**: View customer details and history
- ✅ **Inventory Alerts**: Low stock and out-of-stock notifications
- ✅ **Reports**: CSV export for business analytics
- ✅ **Promotions**: Create and manage discount codes
- ✅ **Reviews Management**: Moderate customer feedback
- ✅ **Settings**: Store configuration, payment methods, maintenance mode

## 🗄️ Database Schema

### Core Tables
- **users**: Customer and admin accounts
- **products**: Product catalog with stock tracking
- **categories**: Product categorization
- **orders**: Customer orders with status tracking
- **order_items**: Line items for each order
- **cart_items**: Shopping cart contents
- **delivery_personnel**: Delivery staff management
- **favorites**: Customer favorite items
- **wishlists**: Save for later functionality
- **reviews**: Product ratings and feedback
- **promotions**: Discount codes and campaigns
- **payments**: Transaction records
- **addresses**: Delivery addresses
- **messages**: Chat communication
- **notifications**: In-app notifications
- **admin_settings**: Application configuration

## 🔧 Technical Implementation

### Repository Pattern
```java
public class ProductRepository {
    private DatabaseHelper dbHelper;
    
    public List<Product> getAllProducts() { ... }
    public Product getProductById(int id) { ... }
    public boolean addProduct(Product product) { ... }
    public boolean updateProduct(Product product) { ... }
    public boolean deleteProduct(int id) { ... }
}
```

### Real-time Features
- **FCM Integration**: Push notifications for order updates
- **Order Tracking**: Live map updates using OSMDroid
- **Chat System**: Real-time messaging between users and admin

## 📱 Installation

### Prerequisites
- Android 7.0 (API level 24) or higher
- 50MB storage space
- Internet connection for full functionality

### Setup Instructions
1. Clone the repository
2. Open in Android Studio
3. Build and run the app
4. Use admin credentials for admin access:
   - Email: `admin@gmail.com`
   - Password: `admin123`
5. Use customer credentials for shopping:
   - Email: `ram@gmail.com`
   - Password: `ram123`

## 🎨 UI/UX Design

### Material Design Components
- **Navigation**: Bottom navigation for users, drawer for admin
- **Cards**: Material cards for product display
- **Forms**: Material text fields with validation
- **Dialogs**: Alert dialogs for confirmations
- **Progress**: Loading indicators and progress bars

### User Experience
- **Intuitive Navigation**: Clear flow from browse to checkout
- **Visual Feedback**: Loading states, success/error messages
- **Responsive Design**: Adapts to different screen sizes
- **Accessibility**: Content descriptions, semantic markup

## 🧪 Testing

### Manual Testing
- [ ] User registration and login
- [ ] Product browsing and search
- [ ] Add to cart and checkout flow
- [ ] Payment processing (Stripe & COD)
- [ ] Order tracking functionality
- [ ] Admin dashboard access
- [ ] Product management
- [ ] Order assignment to delivery
- [ ] Inventory alerts
- [ ] Report generation
- [ ] Notification system

## 📊 Performance

### Database Optimization
- **Indexes**: Primary keys on frequently queried columns
- **Queries**: Optimized SQL with proper joins
- **Pagination**: Large datasets loaded in chunks
- **Caching**: Repository pattern with local caching

### Memory Management
- **RecyclerView**: Efficient list rendering
- **Image Loading**: Lazy loading with caching
- **Background Tasks**: Async operations for network/database
- **Lifecycle**: Proper activity/fragment lifecycle management

## 🔒 Security

### Authentication
- **Password Hashing**: Salt-based password storage
- **Session Management**: Secure user sessions
- **Role-Based Access**: Admin vs customer permissions

### Data Protection
- **Input Validation**: SQL injection prevention
- **Local Storage**: Encrypted sensitive data where needed
- **API Security**: Secure payment processing

## 📚 Documentation

- [**Project Documentation**](PROJECT_DOCUMENTATION.md) - Complete technical overview
- [**Demo Guide**](DEMO_GUIDE.md) - Step-by-step demo script
- [**Database Usage**](DATABASE_USAGE.md) - Database schema and queries
- [**Payment Setup**](STRIPE_PAYMENT_SETUP.md) - Stripe integration guide
- [**Development Log**](logbook.md) - Development timeline and decisions

## 🎓 Learning Outcomes

### Technical Skills
1. **Android Development**: Activities, fragments, services
2. **Database Design**: SQLite with complex relationships
3. **API Integration**: Stripe payments, FCM notifications
4. **Architecture Patterns**: Repository, MVC, clean code
5. **UI/UX Design**: Material Design principles
6. **Real-time Features**: Live tracking, notifications
7. **E-commerce Logic**: Cart, checkout, order management

### Business Understanding
- **Supply Chain**: Inventory to delivery workflow
- **Customer Experience**: Browse to purchase journey
- **Admin Operations**: Backend management systems
- **Payment Processing**: Multiple payment methods
- **Logistics**: Delivery tracking and assignment

## 🚀 Future Enhancements

### Planned Features
- **Multi-language Support**: Regional language options
- **Dark Mode**: Theme customization
- **Loyalty Program**: Points and rewards system
- **Voice Search**: Hands-free product searching
- **Barcode Scanner**: Quick product identification
- **Social Sharing**: Product sharing capabilities
- **Subscription Service**: Recurring grocery delivery
- **AI Recommendations**: Personalized product suggestions

## 🏆 Project Highlights

### Unique Features
- **Dual Interface**: Complete customer and admin experience
- **Real-time Tracking**: Live order delivery visualization
- **Smart Inventory**: Automated low-stock alerts
- **Comprehensive Reports**: CSV export for business analytics
- **Flexible Payments**: Stripe + Cash on Delivery options
- **Wishlist System**: Save for later functionality
- **Chat Support**: Direct customer-admin communication

### Innovation Points
- **Auto-assignment**: Intelligent delivery boy assignment
- **Status Automation**: Availability management based on order status
- **Unified Dashboard**: Single admin interface for all operations
- **Mobile-First**: Optimized for on-the-go grocery shopping

## 📞 Support

### Documentation
- **Architecture**: See `PROJECT_DOCUMENTATION.md`
- **Database**: See `DATABASE_USAGE.md`
- **Payment**: See `STRIPE_PAYMENT_SETUP.md`
- **Development**: See `logbook.md`

### Contact
- **Project**: GroceryPlus
- **Platform**: Android (Java)
- **Database**: SQLite
- **Backend**: Firebase (FCM)
- **Payment**: Stripe + COD

---

*GroceryPlus demonstrates a complete understanding of mobile application development, database design, user experience, and business logic implementation suitable for academic evaluation and real-world application.*
