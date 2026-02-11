# Postman Test Scenarios

Base URL:
- `http://localhost:8080`

Required headers (for all requests):
- `X-API-Key: Dk7uG2qv9N1zX4pR8sLm3TqY6aV0cJfH5eW1bK2mP9rS8uG7`
- `Content-Type: application/json`

**Napomena:** Korisnik može istovremeno i da kupi i da prodaje neki proizvod

## 1) Korisnik prodavac
**POST** `/api/users`

```json
{
  "firstName": "Petar",
  "lastName": "Petrovic",
  "email": "petar.petrovic@gmail.com",
  "phone": "0611341422",
  "address": "Novi Sad"
}
```

## 2) Korisnik kupac
**POST** `/api/users`

```json
{
  "firstName": "Ana",
  "lastName": "Markovic",
  "email": "ana.markovic@yahoo.com",
  "phone": "0634900499",
  "address": "Beograd"
}
```

## 3) Validan proizvod
**POST** `/api/products`

```json
{
  "name": "Monitor 24",
  "description": "IPS monitor",
  "price": 199.99,
  "available": true,
  "ownerUserId": 1
}
```

## 4) Nevalidan proizvod
**POST** `/api/products`

```json
{
  "name": "Laptop Asus",
  "description": "Moderan laptop sa LED ekranom",
  "price": 1200.00,
  "available": false,
  "ownerUserId": 1
}
```

## 5) Validna porudžbina
**POST** `/api/orders`

```json
{
  "userId": 2,
  "productId": 1,
  "quantity": 2
}
```

## 6) Nevalidna porudžbina
**POST** `/api/orders`

```json
{
  "userId": 2,
  "productId": 2,
  "quantity": 1
}
```

## 7) Pokušaj porudžbine sopstvenog proizvoda
**POST** `/api/orders`

```json
{
  "userId": 1,
  "productId": 1,
  "quantity": 1
}
```

## 8) Agregacioni endpoint - detalji porudžbine
**GET** `/api/orders/{orderId}/details`


## 9) Obaveštenja za prodavca o njegovim proizvodima
**GET** `/api/orders/sellers/{sellerUserId}/notifications`

## 10) Brisanje porudžbine
**DELETE** `/api/orders/{id}`

## 11) Izmena porudžbine
**PUT** `/api/orders/{id}`

```json
{
  "quantity": 3,
  "status": "CONFIRMED",
}
```
Ako se bilo koje drugo polje unese, promena neće biti validna.
