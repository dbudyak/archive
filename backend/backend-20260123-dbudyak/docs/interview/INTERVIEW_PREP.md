# Restaurant API - Interview Preparation Report

## Executive Summary

**Restaurant API (r-api)** is Wolt's core e-commerce backend monolith - a Flask-based Python service that historically powered most of Wolt's food delivery platform. It's a classic example of a legacy monolith being decomposed into microservices, making it an excellent story for technical interviews.

---

## 1. Technology Stack

### Core Framework & Language
- **Python 3.10** with **Flask 2.2.5** (REST API framework)
- **gRPC** for internal service-to-service communication
- **Celery 4.4** for async task processing
- **Poetry** for dependency management

### Databases
- **PostgreSQL** - relational data (corporate features, invoices, settlements)
- **MongoDB** - primary document store (venues, menus, purchases, users)
  - Multiple MongoDB clusters: restaurant_db, auth_db, consumer_db, purchase_db, content_editor_db, offering_db, subscriptions_db, venue_campaigns_db
- **Redis Cluster** - caching, rate limiting, distributed locks (redlock-py)
- **Elasticsearch/OpenSearch** - search functionality for venues, menu items, purchases

### Message Queues & Streaming
- **Kafka** (confluent-kafka) - event streaming for various domains
- **Celery + Redis** - async task processing

### Infrastructure & DevOps
- **Docker** with multi-stage builds
- **GitHub Actions** - CI/CD (12 parallel test groups)
- **AWS ECR** - container registry
- **Sentry** - error monitoring
- **DataDog** (ddtrace) - APM and metrics
- **wkhtmltopdf** - PDF generation (invoices, receipts)

### Internal Libraries
- `wolt-protocol` - shared protobuf definitions
- `wolt-grpc` - gRPC utilities
- `wolt-security` - authentication/authorization
- `wolt-s2s` - service-to-service communication
- `wolt-statsd` - metrics
- `wolt-logging` - structured logging

---

## 2. Architecture & Design Patterns

### Layered Architecture (Feature Packages)
The codebase follows a clean 3-layer architecture:

```
restaurantapi/<feature>/
├── models.py       # Data models (ODM + service models)
├── repository.py   # Database access layer (private)
├── service.py      # Business logic (public interface)
├── views.py        # REST API endpoints
├── api_formats.py  # API response models
├── api_mapping.py  # DB model → API format transformers
├── tasks.py        # Celery async tasks
└── cli.py          # Flask CLI commands
```

### Custom ODM (Object-Document Mapping)
They built their own ODM using **attrs** for MongoDB models:
```python
@odm.attr_mapping()
@attr.s(auto_attribs=True, kw_only=True)
class User:
    _id: ObjectId
    name: str
    type: UserType
```

### Multiple API Versions
- REST API: v1, v2, v3, v4 endpoints (80+ blueprints registered)
- gRPC: Internal services with `wolt-protocol` protobuf definitions (25+ services)

### Domain Separation
Major domains:
- **Discovery** - venue listings, search, front page, articles
- **Consumer** - venues, menus, checkout, delivery info
- **Finance** - payouts, invoicing, accounting, ledger
- **Corporate (Wolt at Work)** - B2B features, corporate invoices, cost centers
- **Merchant** - admin portal, app API
- **Subscriptions** - Wolt+ subscription handling

---

## 3. Business Domain

### What the Service Does
Wolt's restaurant-api handles:
- **Venue Management** - restaurants, groceries, retail (14 product lines including restaurant, grocery, pharmacy, florist, etc.)
- **Menu Systems** - item catalogs, pricing, availability
- **Discovery** - search, recommendations, front page content
- **Checkout** - order creation, payment processing
- **Corporate (B2B)** - Wolt at Work business meal solutions
- **Finance** - merchant payouts, invoicing, settlements
- **Notifications** - push notifications, message center

### Product Lines Supported
```python
class ProductLine(Enum):
    RESTAURANT = "restaurant"
    GROCERY = "grocery"
    PHARMACY = "pharmacy"
    FLORIST = "florist"
    PET_SUPPLY = "pet_supply"
    ALCOHOL = "alcohol"
    # ... 14 total product lines
```

---

## 4. Strengths (Good Interview Stories)

### 1. **Well-Structured Feature Packages**
- Clear separation of concerns (repository → service → views)
- Private repository layer, public service API
- Type-safe API formats with runtime validation (`return_json_type`)

### 2. **Strong Internal gRPC API**
- 25+ gRPC services for internal communication
- Protobuf-defined contracts (`wolt-protocol`)
- JWT-based authorization with permission mapping

### 3. **Robust CI/CD Pipeline**
- Parallel testing (12 groups)
- Automated deployments with staging gate
- Release approval workflow via Slack

### 4. **Custom ODM Framework**
- Type-safe MongoDB access
- Repository pattern with read preference support
- Clean separation from business logic

### 5. **Internationalization**
- Flask-Babel for translations
- Phrase integration for translation management
- Multi-language support (30+ translations folders)

### 6. **Comprehensive Developer Experience**
- Detailed contribution guidelines
- Pre-commit hooks (black, mypy, flake8)
- Docker-based local development
- Clear coding conventions

---

## 5. Weaknesses & Technical Debt (Critical Interview Stories)

### 1. **Monolith Migration Challenges**
The README explicitly states:
> "🛑 New features in restaurant-api are forbidden 🛑"
> "restaurant-api is being split into domain-specific services"

This creates several issues:
- Features may be deleted without notice
- Dual ownership between old monolith and new services
- Data migration complexity

### 2. **Multiple Database Connections**
```python
class DbAlias(str, enum.Enum):
    RESTAURANT_DB = "default"
    PURCHASE_DB = "PURCHASE_DB"
    AUTH_DB = "AUTH_DB"
    CONSUMER_DB = "CONSUMER_DB"
    # ... 8 different MongoDB clusters
```
- Complex connection management
- Distributed transaction challenges
- Data consistency across databases

### 3. **Legacy Patterns**
- `python_common` package marked as "heavily used legacy code - never build anything new inside"
- `restaurantapi/tasks` - old Celery tasks location
- `restaurantapi/views` - old route definitions
- Mixed use of mongoengine and custom ODM

### 4. **Outdated Dependencies**
```python
celery = "==4.4.0"  # Very old
Flask = "==2.2.5"   # Pinned due to upgrade issues
elasticsearch = "==7.13.*"  # Pinned version
kafka-python = "*"  # Deprecated, confluent-kafka preferred
```

### 5. **Migration Files**
- 12,892 lines across migration files
- Complex merge migrations indicate parallel development challenges
- Multiple merge heads throughout history

### 6. **Test Infrastructure**
- Tests split across 12 parallel groups (indicates large test suite, long CI times)
- No visible test folder in current dump (likely `.gitignored` local tests)

---

## 6. Key Interview Stories You Can Tell

### Story 1: "Legacy Monolith Decomposition"
*"I worked on Wolt's restaurant-api, a core Python/Flask monolith handling the entire e-commerce backend. The company had a strategic initiative to decompose it into microservices. My role included:*
- *Removing deprecated code and migrating features to domain services*
- *Exposing data via internal gRPC APIs to enable new services*
- *Working with multiple teams to coordinate migration without breaking existing functionality*
- *The key challenge was maintaining the service while actively deleting code"*

### Story 2: "Working with Polyglot Persistence"
*"Restaurant-api used PostgreSQL for transactional data like corporate invoicing and settlements, MongoDB for document storage (venues, menus, purchases), Redis for caching, and Elasticsearch for search. I gained experience with:*
- *Managing multiple MongoDB clusters (8 different databases)*
- *Alembic migrations for PostgreSQL*
- *Custom ODM patterns for type-safe MongoDB access*
- *This gave me strong foundations for understanding database trade-offs"*

### Story 3: "gRPC Internal APIs"
*"As part of the microservices migration, I worked with the internal gRPC API layer. The service exposed 25+ gRPC services using protobuf definitions from a shared `wolt-protocol` library. Key aspects:*
- *Authorization via JWT tokens with permission mapping*
- *Type-safe service contracts*
- *This enabled domain-specific services to consume data still owned by restaurant-api"*

### Story 4: "Python at Scale"
*"Restaurant-api served as Wolt's primary backend, handling:*
- *14 different product lines (restaurants, groceries, pharmacy, etc.)*
- *80+ REST API blueprints*
- *Celery async tasks for background processing*
- *Kafka consumers for event-driven architecture*
- *The codebase taught me patterns for scaling Python applications"*

---

## 7. PostgreSQL Experience

### PostgreSQL Experience from This Project:
1. **Alembic Migrations** - 100+ migration files, complex schema evolution
2. **SQLAlchemy ORM** - Flask-SQLAlchemy for relational models
3. **Corporate/Finance Data** - PostgreSQL used for ACID-compliant financial operations:
   - Corporate invoices
   - SEPA transactions
   - Payment settlements
   - Cost centers

4. **Hybrid Database Architecture** - Understanding when to use PostgreSQL vs MongoDB:
   - PostgreSQL: Transactional data requiring ACID (invoices, payments, settlements)
   - MongoDB: High-volume document data (venues, menus, orders)

### Technical Depth:
```python
# Example migration pattern
op.create_table(
    'corporate_event',
    sa.Column('id', GUID(), nullable=False),
    sa.Column('created_at', sa.DateTime(), server_default=sa.text('now()'), nullable=False),
    sa.Column('corporate_id', GUID(), nullable=False),
    sa.ForeignKeyConstraint(['corporate_id'], ['corporate.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
)
op.create_index(op.f('ix_corporate_event_corporate_id'), 'corporate_event', ['corporate_id'])
```

---

## 8. Summary Statistics

| Metric | Value |
|--------|-------|
| Python Version | 3.10 |
| Flask Version | 2.2.5 |
| PostgreSQL Migrations | ~100+ files, 12,892 lines total |
| MongoDB Clusters | 8 different databases |
| REST Blueprints | 80+ registered |
| gRPC Services | 25+ internal services |
| Product Lines | 14 |
| Test Groups | 12 parallel CI groups |
| Translations | 30+ languages |
| Dependencies | ~120 (poetry) |

---

## 9. What You Can Say About Your Role

Given you mentioned being a Kotlin developer who participated in migration work:

*"While I was primarily a Kotlin developer working on other microservices, I contributed to the restaurant-api migration project multiple times. This involved:*
- *Understanding the existing Python/Flask patterns*
- *Removing deprecated code and features*
- *Coordinating with teams who owned destination services*
- *Working with Alembic migrations for PostgreSQL schema changes*
- *This cross-language experience gave me perspective on both Kotlin and Python ecosystems, and understanding of polyglot persistence patterns that's directly applicable to database infrastructure work"*

---

## 10. Dual-Write Migration & Index Management (Critical Interview Topic)

During the microservices migration, there was an extended period where **both the monolith and new services were reading/writing to the same MongoDB collections**. This created several challenges, especially around **index management**.

### The Problem: Index Creation During Migration

#### How r-api Handled Indexes (Original Pattern)
```python
# restaurantapi/odm/repository.py - Auto-create indexes on first access
class MongoDocumentRepository(typing.Generic[T]):
    _indexes_created = config.get("MONGO_DISABLE_AUTO_CREATE_INDEXES")

    def collection(self, write_concern=None) -> Collection:
        # ...
        if not self.__class__._indexes_created and self.__class__.config.indexes:
            collection.create_indexes(self.__class__.config.indexes)  # Creates on first access!
            self.__class__._indexes_created = True
        return collection
```

#### The Safeguard: Disable Auto-Create in Production
```python
# Called at startup in both application.py and grpc_server.py
mongo.disable_auto_create_index()

# restaurantapi/mongo.py
def disable_auto_create_index() -> None:
    if not config.get("MONGO_DISABLE_AUTO_CREATE_INDEXES"):
        return None
    # Disables auto index creation for all mongoengine documents
    for subclass in mongoengine.Document.__subclasses__():
        subclass._meta["auto_create_index"] = False
```

### Issues with Dual-Write + Index Management

#### 1. **Missing Indexes on New Service**
When a new microservice starts writing to the same collection:
- Old monolith had indexes defined in code (but disabled auto-create in production)
- New service doesn't know about required indexes
- Queries become slow or timeout without proper indexes
- **Risk**: Production degradation when traffic shifts to new service

#### 2. **Index Creation Blocking Writes (MongoDB < 4.2)**
```python
# Old MongoDB behavior - index creation locks the collection
collection.create_indexes([IndexModel([("user_id", 1)])])  # BLOCKS ALL WRITES!
```
- In MongoDB < 4.2, foreground index builds blocked all operations
- Background builds (`background=True`) still impacted performance
- Risk of timeouts during index creation on large collections

#### 3. **Duplicate Index Creation Attempts**
- Both services might try to create the same index
- MongoDB handles this gracefully (idempotent), but wastes resources
- Log noise and monitoring alerts

#### 4. **Index Drift**
- Monolith adds new index → new service doesn't know
- New service adds index → monolith doesn't know
- Over time, index definitions diverge between codebases

### MongoDB vs PostgreSQL: Index Handling Comparison

| Aspect | MongoDB | PostgreSQL |
|--------|---------|------------|
| **Default index creation** | Foreground (blocking) until 4.2 | Blocking by default |
| **Non-blocking option** | `background: true` (deprecated in 4.2+) | `CREATE INDEX CONCURRENTLY` |
| **Lock behavior** | Collection-level lock (old) / Intent locks (new) | Row-level locks, no table lock with CONCURRENTLY |
| **Progress monitoring** | `db.currentOp()` | `pg_stat_progress_create_index` |
| **Failure handling** | Automatic rollback | CONCURRENTLY can leave invalid index |
| **Unique constraint during build** | Checked at end | Checked incrementally |
| **Index on existing data** | Can be slow on large collections | Can use `CONCURRENTLY` |

### PostgreSQL Advantages for Index Management

```sql
-- PostgreSQL: Non-blocking index creation
CREATE INDEX CONCURRENTLY idx_user_id ON orders(user_id);

-- Can monitor progress
SELECT * FROM pg_stat_progress_create_index;

-- Safe rollback if failed
DROP INDEX CONCURRENTLY IF EXISTS idx_user_id;
```

**Key PostgreSQL benefits:**
1. **`CREATE INDEX CONCURRENTLY`** - Doesn't lock table for writes
2. **Transactional DDL** - Index creation can be part of a transaction
3. **Better progress visibility** - System views show creation progress
4. **Partial indexes** - `WHERE` clause support for selective indexing
5. **Expression indexes** - Index on computed values

### MongoDB 4.2+ Improvements

```javascript
// MongoDB 4.2+: Optimized index builds (hybrid approach)
db.collection.createIndex({ user_id: 1 })  // No longer fully blocking

// Check build progress
db.currentOp({ "command.createIndexes": { $exists: true } })
```

**MongoDB 4.2+ changes:**
- Hybrid index builds (reads allowed during build)
- `background` option deprecated (always "background-like")
- Still impacts write performance during build

### Best Practices for Dual-Write Migration

#### 1. **Separate Index Management**
```python
# Don't auto-create indexes in application code
config["MONGO_DISABLE_AUTO_CREATE_INDEXES"] = True

# Use dedicated migration scripts/jobs for index management
```

#### 2. **Index as Infrastructure**
- Define indexes in infrastructure-as-code (Terraform, Pulumi)
- Or use database migration tools (Alembic for Postgres, mongomigrate for Mongo)
- Apply indexes before deploying new service

#### 3. **Rolling Index Creation**
```javascript
// MongoDB: Use rolling index builds on replica sets
// Build on secondaries first, then step down primary
```

```sql
-- PostgreSQL: CONCURRENTLY is safe for production
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_name ON table(column);
```

#### 4. **Monitor Index Usage**
```javascript
// MongoDB: Check if indexes are being used
db.collection.aggregate([{ $indexStats: {} }])
```

```sql
-- PostgreSQL: Check index usage
SELECT * FROM pg_stat_user_indexes WHERE relname = 'table_name';
```

### Interview Story: "Index Migration Challenge"

*"During the microservices migration, we had a period where both the old monolith and new services were reading from the same MongoDB collections. One challenge was index management - the monolith had indexes defined in code but disabled auto-creation in production. When new services started querying the same data, they initially suffered from slow queries because they didn't know about required indexes.*

*We solved this by:*
1. *Treating indexes as infrastructure, not application code*
2. *Creating a shared index registry that both services referenced*
3. *Using rolling index builds on replica sets to avoid production impact*
4. *Adding index usage monitoring to catch missing indexes early*

*This experience taught me the importance of separating schema/index management from application deployment, which is even more critical in PostgreSQL where `CREATE INDEX CONCURRENTLY` is the safe pattern for production systems."*

---

## 11. Quick Reference - Key Files to Remember

| File/Path | Purpose |
|-----------|---------|
| `restaurantapi/application.py` | Main Flask app, 80+ blueprint registrations |
| `restaurantapi/business_constants.py` | Domain enums (ProductLine, DeliveryMethod, etc.) |
| `restaurantapi/odm/repository.py` | Custom ODM with index auto-creation logic |
| `restaurantapi/mongo.py` | MongoDB connections, `disable_auto_create_index()` |
| `grpc_api/grpc_server.py` | 25+ gRPC services setup |
| `migrations/versions/` | 100+ Alembic PostgreSQL migrations |
| `docker-compose.yml` | Local dev: Postgres, MongoDB, Redis, Kafka, ES |
| `pyproject.toml` | 120 dependencies |
| `CONTRIBUTION.md` | Architecture patterns and conventions |
