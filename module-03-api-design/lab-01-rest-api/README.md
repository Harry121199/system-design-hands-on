# Lab 01 — REST API

## Problem
A frontend needs to create, read, update, and delete tasks. REST maps domain
objects to URLs and uses HTTP verbs as actions — the most common API style.

## What I learned
- URL = noun (resource), HTTP verb = action (POST create, GET read, PATCH update, DELETE remove)
- API versioning via URL path: /api/v1/tasks
- POST returns 201 Created with a Location header pointing to the new resource
- DELETE returns 204 No Content — success but no body
- GET on a missing resource returns 404 Not Found
- PATCH updates only the fields sent — unlike PUT which replaces the entire resource
- Query parameters handle filtering: ?status=TODO&priority=HIGH
- ConcurrentHashMap is thread-safe for multi-threaded request handling
- Optional forces explicit handling of "not found" cases
- ResponseEntity gives full control over status codes and headers

## Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/tasks | Create a task |
| GET | /api/v1/tasks | List all (filter with ?status=&priority=) |
| GET | /api/v1/tasks/{id} | Get one task |
|