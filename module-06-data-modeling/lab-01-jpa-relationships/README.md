# Lab 01 — JPA Relationships: Users, Projects, Tasks

## What I built
- Extended the cached Task API with User and Project entities
- User (1) → Project (many) → Task (many) using @OneToMany / @ManyToOne
- Foreign keys: projects.user_id, tasks.project_id
- DataLoader: 10 users × 10 projects × 500 tasks = 50,000 tasks
- REST endpoints: /api/v1/users, /api/v1/projects, /api/v1/projects/{id}/tasks

## Key concepts
- Foreign key lives on the "many" side (owning side)
- `mappedBy` marks the inverse side (no column in DB, just Java navigation)
- @JsonIgnore prevents infinite recursion and bloated responses
- @ManyToOne defaults to EAGER fetch — loads related entity even when not needed
- Navigate relationships via URL structure, not nested JSON

## SQL observed
- GET /projects/1 → 1 query (project JOIN user)
- GET /projects/1/tasks → 3 queries (count + tasks + eager project load)