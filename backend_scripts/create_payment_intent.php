<?php
// create_payment_intent.php

// 1. Setup
header('Content-Type: application/json');

// ---------------------------------------------------------
// REPACE WITH YOUR STRIPE SECRET KEY
// ---------------------------------------------------------
$stripeSecretKey = 'sk_test_51SdBfzD0CBYYErpfg5pIH7DiWP8rBpMVdxMpZm0YexTCbMtA1WQismaPAKlowryeCXFYZIaBkmPV8MAh4ZGWnZ5G00DLnFaJtS';
// ---------------------------------------------------------

// 2. Get POST data
$input = file_get_contents('php://input');
$data = json_decode($input, true);

if (!isset($data['amount']) || !isset($data['currency'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Missing amount or currency']);
    exit;
}

$amount = $data['amount']; // Amount in smallest currency unit (e.g., cents/paisa)
$currency = $data['currency'];

// 3. Call Stripe API to create PaymentIntent
$url = 'https://api.stripe.com/v1/payment_intents';
$fields = [
    'amount' => $amount,
    'currency' => $currency,
    'automatic_payment_methods[enabled]' => 'true',
];

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($fields));
curl_setopt($ch, CURLOPT_USERPWD, $stripeSecretKey . ':'); // Basic Auth with Secret Key
curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$error = curl_error($ch);
curl_close($ch);

// 4. Handle Response
if ($error) {
    http_response_code(500);
    echo json_encode(['error' => 'Curl error: ' . $error]);
} else {
    // Pass Stripe response directly back to app (contains client_secret)
    http_response_code($httpCode);
    echo $response;
}
?>
