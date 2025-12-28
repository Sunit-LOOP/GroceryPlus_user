# PHP Backend Setup for Stripe Payments

This guide helps you set up PHP as the backend for Stripe payments in GroceryPlus app.

## 🚀 Quick Setup

### 1. Install PHP Backend

#### Option A: XAMPP (Recommended for Windows)
```bash
# Download and install XAMPP from https://www.apachefriends.org/
# Copy backend-php folder to XAMPP/htdocs/
# Start Apache server from XAMPP control panel
# Backend will be available at: http://localhost/backend-php/
```

#### Option B: Built-in PHP Server
```bash
# Navigate to backend-php directory
cd "D:\6TH PROJECT\GroceryPLus\backend-php"

# Start PHP server
php -S localhost:80

# Or with custom port
php -S localhost:8000
```

#### Option C: Docker
```bash
# Create Dockerfile in backend-php/
FROM php:8.0-apache
COPY . /var/www/html/
RUN docker-php-ext-install curl

# Build and run
docker build -t groceryplus-backend .
docker run -p 80:80 groceryplus-backend
```

### 2. Configure Backend

#### Edit `stripe-backend-simple.php`:
```php
// Replace with your Stripe test keys
$STRIPE_SECRET_KEY = 'sk_test_...';
$STRIPE_PUBLISHABLE_KEY = 'pk_test_...';
```

#### Get Stripe Keys:
1. Go to [Stripe Dashboard](https://dashboard.stripe.com/)
2. Developers → API Keys → Test keys
3. Copy Secret Key and Publishable Key

### 3. Test Backend

#### Test with Browser:
```bash
# Open in browser
http://localhost/backend-php/stripe-backend-simple.php?test=true

# Expected response:
{
  "status": "backend_working",
  "timestamp": "2024-01-01 12:00:00",
  "stripe_configured": true
}
```

#### Test Payment Intent Creation:
```bash
# Using curl
curl -X POST http://localhost/backend-php/stripe-backend-simple.php \
  -H "Content-Type: application/json" \
  -d '{"amount": 5000, "currency": "inr"}'

# Expected response:
{
  "clientSecret": "pi_...",
  "paymentIntentId": "pi_..."
}
```

### 4. Configure Android App

#### Update `PHPBackendConfig.java`:
```java
// For development (localhost testing)
public static final String BACKEND_URL_DEV = "http://10.0.2.2/backend-php/";

// For production (your server)
public static final String BACKEND_URL_PROD = "https://your-domain.com/backend-php/";

// Toggle development/production
public static final boolean USE_PRODUCTION = false;
```

#### Network Permissions (AndroidManifest.xml):
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🔧 Configuration Options

### Development Setup:
- **Android Emulator**: Use `10.0.2.2` to access localhost
- **Physical Device**: Use your computer's IP address
  ```bash
  # Find your IP (Windows)
  ipconfig
  
  # Update BACKEND_URL_DEV to:
  public static final String BACKEND_URL_DEV = "http://192.168.1.100/backend-php/";
  ```

### Production Setup:
- **Domain**: Point your domain to the backend folder
- **HTTPS**: Enable SSL for production (required by Stripe)
- **CORS**: Configure headers for cross-origin requests

## 📋 Available Endpoints

### Create Payment Intent
```
POST /stripe-backend-simple.php
Content-Type: application/json

{
  "amount": 5000,
  "currency": "inr"
}

Response:
{
  "clientSecret": "pi_xxx_secret_xxx",
  "paymentIntentId": "pi_xxx"
}
```

### Get Payment Status
```
GET /stripe-backend-simple.php?payment_intent_id=pi_xxx

Response:
{
  "status": "succeeded",
  "amount": 5000,
  "currency": "inr",
  "payment_method": "card_xxx"
}
```

### Webhook Handler
```
POST /stripe-backend-simple.php

Handles:
- payment_intent.succeeded
- payment_intent.payment_failed
```

### Test Connection
```
GET /stripe-backend-simple.php?test=true

Response:
{
  "status": "backend_working",
  "timestamp": "2024-01-01 12:00:00",
  "stripe_configured": true
}
```

## 🔒 Security Considerations

### For Development:
- Test keys are safe to use
- No webhook verification needed
- Basic error logging enabled

### For Production:
- **Use Live Keys**: Replace test keys with production keys
- **HTTPS Required**: Stripe requires HTTPS for live payments
- **Webhook Security**: Verify webhook signatures
- **Input Validation**: Sanitize all user inputs
- **Rate Limiting**: Prevent abuse of API endpoints

### Recommended Security Headers:
```php
// Add to top of stripe-backend-simple.php
header('Strict-Transport-Security: max-age=31536000; includeSubDomains');
header('X-Content-Type-Options: nosniff');
header('X-Frame-Options: DENY');
header('X-XSS-Protection: 1; mode=block');
```

## 🐛 Debugging

### Enable Debug Logging:
```php
// Add to stripe-backend-simple.php
error_log('Stripe Payment Debug: ' . print_r($data, true));
```

### Check Payments Log:
```bash
# View payment logs
tail -f payments.log

# Search for specific payment
grep "pi_xxx" payments.log
```

### Common Issues:

#### 1. Network Error:
- **Check**: PHP server is running
- **Fix**: Start Apache/XAMPP
- **URL**: Verify backend URL in Android app

#### 2. CORS Error:
- **Check**: CORS headers are set
- **Fix**: Add `Access-Control-Allow-Origin: *`

#### 3. Stripe Key Error:
- **Check**: Stripe keys are correct
- **Fix**: Verify in Stripe Dashboard

#### 4. SSL Error (Production):
- **Check**: HTTPS certificate
- **Fix**: Install SSL certificate

## 🚀 Deployment

### To Production Server:
1. **Upload** backend-php folder to your server
2. **Set permissions**:
   ```bash
   chmod 755 stripe-backend-simple.php
   chmod 666 payments.log
   ```
3. **Configure HTTPS** with SSL certificate
4. **Update Android app** with production URL
5. **Test** with Stripe test cards first

### Hosting Options:
- **Shared Hosting**: cPanel, Plesk
- **Cloud Hosting**: AWS, Google Cloud, Azure
- **VPS**: DigitalOcean, Linode, Vultr
- **PaaS**: Heroku, Render

## 📱 Testing

### Android App Testing:
1. **Install** the app with PHP backend configured
2. **Add items** to cart
3. **Proceed to checkout**
4. **Select "Pay with Stripe"**
5. **Complete payment** with test card: `4242 4242 4242 4242`

### Backend Testing:
```bash
# Test all endpoints
curl -X GET "http://localhost/backend-php/stripe-backend-simple.php?test=true"

# Create test payment
curl -X POST http://localhost/backend-php/stripe-backend-simple.php \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000, "currency": "inr"}'
```

## ✅ Success Checklist

- [ ] PHP server is running and accessible
- [ ] Stripe keys are configured
- [ ] Backend test endpoint returns success
- [ ] Android app can connect to backend
- [ ] Payment Intent creation works
- [ ] Stripe PaymentSheet appears
- [ ] Test payments complete successfully
- [ ] Payment logs are being created
- [ ] Error handling works correctly

---

**🎉 Your PHP backend is now ready for Stripe payments!**