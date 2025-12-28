# Stripe Payment Integration Setup Guide

This guide will help you set up real Stripe payments for your GroceryPlus app.

## Current Status ✅
- ✅ Stripe SDK properly integrated
- ✅ PaymentSheet UI implemented
- ✅ Backend PaymentIntent creation ready
- ✅ Test environment configured
- ❌ **DISABLED**: Currently using simulated payments

## To Enable Real Stripe Payments:

### 1. Backend Setup

#### For Development (Testing):
```bash
# Navigate to backend directory
cd backend

# Start the backend server
./gradlew run
```

#### For Production:
- Deploy the backend to a cloud service (Heroku, AWS, Google Cloud, etc.)
- Update `PaymentConfig.java` with your production backend URL:
```java
public static final String BACKEND_URL_PROD = "https://your-backend-domain.com/";
public static final boolean USE_PRODUCTION = true;
```

### 2. Stripe Configuration

#### Get Your Stripe Keys:
1. Go to [Stripe Dashboard](https://dashboard.stripe.com/)
2. Create an account or sign in
3. Get your API keys from Developers → API Keys

#### For Testing (Recommended First):
1. Use **Test Keys** (already configured)
2. Test with Stripe test cards:
   - Card Number: `4242 4242 4242 4242`
   - Expiry: Any future date
   - CVC: Any 3 digits
   - ZIP: Any 5 digits

#### For Production:
1. Replace test keys with production keys in:
   - `PaymentConfig.java` (Frontend)
   - `Server.java` (Backend)

### 3. Android Configuration

#### Update `PaymentConfig.java`:
```java
public static final boolean USE_PRODUCTION = false; // Set to true for production
public static final String BACKEND_URL_PROD = "https://your-backend-domain.com/";
```

#### Required Permissions (AndroidManifest.xml):
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 4. Testing Process

#### Step 1: Backend Test
```bash
curl -X POST http://localhost:4567/create-payment-intent \
  -H "Content-Type: application/json" \
  -d '{"amount": 5000, "currency": "inr"}'
```

#### Step 2: App Test
1. Build and run the app
2. Add items to cart
3. Proceed to checkout
4. Select "Pay with Stripe"
5. Complete payment using test card details

### 5. Production Deployment Checklist

#### Security:
- [ ] Use HTTPS for backend
- [ ] Enable Stripe Radar fraud detection
- [ ] Set up webhooks for payment events
- [ ] Implement proper error handling
- [ ] Add logging for debugging

#### Webhooks Setup:
1. In Stripe Dashboard → Webhooks → Add Endpoint
2. Add your webhook URL: `https://your-backend-domain.com/webhook`
3. Select events: `payment_intent.succeeded`, `payment_intent.payment_failed`
4. Implement webhook handler in backend

#### Backend Production Updates:
```java
// In Server.java
private static final String STRIPE_SECRET_KEY = "sk_live_YOUR_PRODUCTION_KEY";

// Add webhook endpoint
post("/webhook", (req, res) -> {
    String payload = req.body();
    String sigHeader = req.headers("Stripe-Signature");
    
    // Verify webhook signature and process events
    // ...
});
```

### 6. Payment Flow

#### Current Flow (Fixed):
1. User selects "Pay with Stripe"
2. App calls backend to create PaymentIntent ✅
3. Backend calls Stripe API to create PaymentIntent ✅
4. Backend returns client secret to app ✅
5. App presents PaymentSheet with client secret ✅
6. User completes payment via Stripe UI ✅
7. PaymentSheet returns result (success/failure) ✅
8. On success → Create order and record payment ✅

### 7. Error Handling

#### Common Issues:
- **Network Timeout**: Check internet connection
- **Invalid Amount**: Minimum ₹0.50 required
- **Backend Offline**: Ensure backend server is running
- **Invalid Keys**: Verify Stripe API keys are correct

#### Debug Mode:
Enable debug logging in `PaymentActivity.java`:
```java
// In onCreate()
PaymentConfiguration.init(this, PaymentConfig.STRIPE_PUBLISHABLE_KEY);
```

### 8. Test Cards

#### Successful Payments:
- `4242 4242 4242 4242` (Visa)
- `4000 0000 0000 0077` (Requires authentication)
- `5555 5555 5555 4444` (Mastercard)

#### Declined Payments:
- `4000 0000 0000 0002` (Card declined)
- `4000 0000 0000 9995` (Insufficient funds)

## Next Steps

1. **Immediate**: Test with current test setup
2. **Development**: Deploy backend to staging
3. **Production**: Get live Stripe keys and deploy
4. **Monitoring**: Set up Stripe Dashboard alerts

## Support

- Stripe Documentation: https://stripe.com/docs
- Android SDK: https://stripe.com/docs/mobile/android
- Test your integration: https://stripe.com/docs/testing

---

**⚠️ Important**: Always test thoroughly with test cards before using production keys!