# API Documentation

## Base URL

**Development:** `http://localhost:8080/api`  
**Production:** `https://api.yourshel ter.com/api`

## Authentication

**Current:** None (open access)  
**Production:** JWT Bearer tokens in `Authorization` header

---

## Public Endpoints

### GET /pets

Get list of pets with optional filters.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | Filter by status: AVAILABLE, PENDING, ADOPTED |
| speciesId | number | No | Filter by species ID |

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "Buddy",
    "species": "Dog",
    "age": 3,
    "imageUrl": "https://s3.../buddy.jpg",
    "description": "Friendly golden retriever...",
    "status": "AVAILABLE",
    "pendingApplicationCount": 2,
    "createdAt": "2024-01-15T10:30:00Z"
  }
]
```

**Example:**

```bash
curl "http://localhost:8080/api/pets?status=AVAILABLE&speciesId=1"
```

---

### GET /pets/{id}

Get single pet details.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | number | Pet ID |

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "Buddy",
  "species": "Dog",
  "age": 3,
  "imageUrl": "https://s3.../buddy.jpg",
  "description": "Friendly golden retriever loves to play fetch...",
  "status": "AVAILABLE",
  "pendingApplicationCount": 2,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

**Errors:**

- `404 Not Found` - Pet doesn't exist

---

### GET /species

Get all species (reference data).

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "Dog"
  },
  {
    "id": 2,
    "name": "Cat"
  },
  {
    "id": 3,
    "name": "Rabbit"
  }
]
```

---

### POST /applications

Submit adoption application.

**Request Body:**

```json
{
  "petId": 1,
  "applicantName": "John Doe",
  "email": "john@example.com",
  "phone": "555-0101",
  "reason": "I have always loved golden retrievers and have a large fenced yard. I work from home so can give Buddy lots of attention..."
}
```

**Validation Rules:**
| Field | Required | Constraints |
|-------|----------|-------------|
| petId | Yes | Must exist, not ADOPTED |
| applicantName | Yes | 1-200 characters |
| email | Yes | Valid email format |
| phone | No | Max 20 characters |
| reason | Yes | 50-5000 characters |

**Response:** `201 Created`

```json
{
  "id": 42,
  "petId": 1,
  "petName": "Buddy",
  "applicantName": "John Doe",
  "status": "PENDING",
  "submittedAt": "2024-01-20T14:30:00Z",
  "message": "Application submitted successfully. We will review your application and contact you soon."
}
```

**Errors:**

- `400 Bad Request` - Validation failed
- `404 Not Found` - Pet doesn't exist
- `409 Conflict` - Duplicate application (same email + pet)
- `422 Unprocessable Entity` - Pet already adopted

**Example:**

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "petId": 1,
    "applicantName": "John Doe",
    "email": "john@example.com",
    "reason": "I have a large yard and love dogs..."
  }'
```

---

## Admin Endpoints

### GET /admin/applications

List all applications with filters.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | PENDING, APPROVED, REJECTED, ADOPTION_CANCELLED |
| petId | number | No | Filter by specific pet |

**Response:** `200 OK`

```json
[
  {
    "id": 42,
    "petId": 1,
    "petName": "Buddy",
    "petSpecies": "Dog",
    "petImageUrl": "https://s3.../buddy.jpg",
    "petStatus": "PENDING",
    "applicantId": 10,
    "applicantName": "John Doe",
    "applicantEmail": "john@example.com",
    "applicantPhone": "555-0101",
    "reason": "I have always loved golden retrievers...",
    "status": "PENDING",
    "submittedAt": "2024-01-20T14:30:00Z",
    "reviewedAt": null,
    "reviewedBy": null
  }
]
```

---

### POST /admin/applications/{id}/approve

Approve an application.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | number | Application ID |

**Request Body:**

```json
{
  "reviewedBy": "Admin Sarah"
}
```

**Response:** `200 OK`

```json
{
  "applicationId": 42,
  "status": "APPROVED",
  "adoptionHistoryId": 15,
  "adoptionStatus": "PENDING_PICKUP",
  "message": "Application approved. Adoption history created. Waiting for family to pick up pet."
}
```

**Errors:**

- `404 Not Found` - Application doesn't exist
- `422 Unprocessable Entity` - Business rule violation:
    - Application not in PENDING status
    - Pet already adopted
    - **Another application already approved for this pet**
    - Active adoption exists for this pet

**Example Error Response:**

```json
{
  "timestamp": "2024-01-20T15:00:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Cannot approve: This pet already has an approved application. Please reject or cancel the existing approved application first.",
  "path": "/api/admin/applications/43/approve"
}
```

---

### POST /admin/applications/{id}/reject

Reject an application.

**Request Body:**

```json
{
  "reviewedBy": "Admin Sarah"
}
```

**Response:** `200 OK`

```json
{
  "applicationId": 42,
  "status": "REJECTED",
  "petStatus": "AVAILABLE",
  "message": "Application rejected. Pet status updated to AVAILABLE (no pending applications remaining)."
}
```

---

### GET /admin/adoptions

List all adoptions with filters.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | PENDING_PICKUP, ACTIVE, RETURNED, CANCELLED |
| petId | number | No | Filter by specific pet |

**Response:** `200 OK`

```json
[
  {
    "id": 15,
    "petId": 1,
    "petName": "Buddy",
    "applicationId": 42,
    "applicantId": 10,
    "applicantName": "John Doe",
    "applicantEmail": "john@example.com",
    "status": "PENDING_PICKUP",
    "adoptedAt": "2024-01-20T15:00:00Z",
    "returnedAt": null,
    "returnReason": null,
    "notes": null
  }
]
```

---

### POST /admin/adoptions/{id}/confirm

Confirm pet pickup (family arrived).

**Request Body:**

```json
{
  "notes": "Family was very excited. Provided care instructions and food samples."
}
```

**Response:** `200 OK`

```json
{
  "adoptionHistoryId": 15,
  "adoptionStatus": "ACTIVE",
  "petId": 1,
  "petStatus": "ADOPTED",
  "rejectedApplicationCount": 3,
  "message": "Adoption confirmed. Pet marked as ADOPTED. 3 other application(s) rejected."
}
```

---

### POST /admin/adoptions/{id}/cancel

Cancel adoption (family didn't show up).

**Request Body:**

```json
{
  "reason": "Family did not respond to calls. No-show after 7 days."
}
```

**Response:** `200 OK`

```json
{
  "adoptionHistoryId": 15,
  "adoptionStatus": "CANCELLED",
  "applicationId": 42,
  "applicationStatus": "ADOPTION_CANCELLED",
  "petId": 1,
  "petStatus": "AVAILABLE",
  "message": "Adoption cancelled. Application marked as ADOPTION_CANCELLED. Pet status: AVAILABLE."
}
```

---

### POST /admin/adoptions/{id}/return

Mark pet as returned to shelter.

**Request Body:**

```json
{
  "returnReason": "Owner developed severe allergies. Pet is healthy and well-behaved.",
  "notes": "Pet is ready for immediate re-adoption. Very friendly."
}
```

**Response:** `200 OK`

```json
{
  "adoptionHistoryId": 15,
  "adoptionStatus": "RETURNED",
  "petId": 1,
  "petStatus": "AVAILABLE",
  "returnedAt": "2024-02-15T10:00:00Z",
  "message": "Pet marked as returned. Pet status updated to AVAILABLE for re-adoption."
}
```

---

### GET /admin/pets

List all pets (including soft-deleted).

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| includeDeleted | boolean | No | false | Include soft-deleted pets |

**Response:** Same as `GET /pets`

---

### POST /admin/pets

Create new pet.

**Request Body:**

```json
{
  "name": "Max",
  "speciesId": 1,
  "age": 2,
  "imageUrl": "https://s3.../max.jpg",
  "description": "Energetic young dog looking for active family."
}
```

**Validation:**
| Field | Required | Constraints |
|-------|----------|-------------|
| name | Yes | 1-100 characters |
| speciesId | Yes | Must exist in species table |
| age | Yes | 0-50 |
| imageUrl | No | Max 500 characters |
| description | No | Max 10000 characters |

**Response:** `201 Created`

```json
{
  "id": 25,
  "name": "Max",
  "species": "Dog",
  "age": 2,
  "imageUrl": "https://s3.../max.jpg",
  "description": "Energetic young dog...",
  "status": "AVAILABLE",
  "pendingApplicationCount": 0,
  "createdAt": "2024-01-21T09:00:00Z"
}
```

---

### PUT /admin/pets/{id}

Update pet details.

**Request Body:** (all fields optional)

```json
{
  "name": "Maximus",
  "age": 3,
  "description": "Updated description..."
}
```

**Response:** `200 OK` (updated pet object)

---

### DELETE /admin/pets/{id}

Soft delete pet.

**Response:** `204 No Content`

**Errors:**

- `422 Unprocessable Entity` - Cannot delete pet with active applications or adoptions

---

### POST /admin/images

Upload pet image to S3.

**Request:** `multipart/form-data`

```
POST /admin/images
Content-Type: multipart/form-data

image: [binary file data]
```

**Constraints:**

- File types: JPEG, PNG, WebP
- Max size: 5MB

**Response:** `201 Created`

```json
{
  "imageUrl": "https://s3.amazonaws.com/shelter-images/pets/uuid-filename.jpg",
  "message": "Image uploaded successfully"
}
```

**Errors:**

- `400 Bad Request` - Invalid file type or size exceeded

**Example (curl):**

```bash
curl -X POST http://localhost:8080/api/admin/images \
  -F "image=@/path/to/pet-photo.jpg"
```

---

## Error Responses

All errors follow this format:

```json
{
  "timestamp": "2024-01-20T15:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/applications",
  "details": {
    "reason": "must be at least 50 characters",
    "email": "must be a valid email address"
  }
}
```

### HTTP Status Codes

| Code | Meaning               | When Used               |
|------|-----------------------|-------------------------|
| 200  | OK                    | Successful GET/POST/PUT |
| 201  | Created               | Resource created        |
| 204  | No Content            | Successful DELETE       |
| 400  | Bad Request           | Validation error        |
| 404  | Not Found             | Resource doesn't exist  |
| 409  | Conflict              | Duplicate resource      |
| 422  | Unprocessable Entity  | Business rule violation |
| 500  | Internal Server Error | Server error            |

---

## Rate Limiting (Production)

**Not implemented in demo**

Production would use:

- 100 requests/minute per IP for public endpoints
- 1000 requests/minute for admin endpoints
- 429 Too Many Requests when exceeded

---

## Pagination (Future Enhancement)

**Not implemented in demo**

Production would use cursor-based pagination:

```
GET /api/pets?limit=20&cursor=eyJpZCI6MTAwfQ==

Response:
{
  "data": [...],
  "pagination": {
    "nextCursor": "eyJpZCI6MTIwfQ==",
    "hasMore": true
  }
}
```

---

## CORS Configuration

**Development:**

```
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

**Production:**

```
Access-Control-Allow-Origin: https://yourshel ter.com
```

---

## Testing the API

### Using curl

**Get all dogs:**

```bash
curl "http://localhost:8080/api/pets?speciesId=1"
```

**Submit application:**

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d @application.json
```

### Using Postman

Import this collection: `docs/postman-collection.json` (if provided)

### Using HTTPie

```bash
http GET http://localhost:8080/api/pets status==AVAILABLE
http POST http://localhost:8080/api/applications < application.json
```