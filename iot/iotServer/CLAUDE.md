# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build and run the Spring Boot backend (starts on port 8080)
mvn spring-boot:run

# Build only
mvn package -DskipTests

# Run the Vue frontend dev server (from the frontend directory)
cd src/main/resources/iotVue && npm run serve   # starts on port 9000

# Build Vue frontend for production
cd src/main/resources/iotVue && npm run build
```

The frontend dev server proxies API calls to `http://localhost:8080`. No tests exist in this project.

## Architecture Overview

This is an **IoT Smart Agriculture Management System** ("智慧物联系统") — a monolith that embeds a Vue 2 SPA frontend inside a Spring Boot JAR.

### Backend (Spring Boot 2.3.2 + MyBatis-Plus 3.4.2, Java 8)

**Generic CRUD Base**: `HttpController<M extends BaseMapper<B>, B>` is the core abstraction. It provides standard REST endpoints (GET `/get`, `/count`, `/{id}` for delete/paginate, POST `/` for insert, POST `/{id}` for update) via MyBatis-Plus. Most entity controllers simply extend it with a `@RequestMapping` and `@CrossOrigin` — they inherit all CRUD for free.

**Key controllers that customize beyond the base:**

| Controller | Route | What it does differently |
|---|---|---|
| `UserController` | `/user` | Adds `/login` endpoint (manual auth against `tbl_user`), `/option` for dropdown enums, overrides `selectList` to filter `status='租户'` |
| `DeviceController` | `/device` | Overrides `select()`/`insert()` to decorate devices with pin info; adds `/device?pid=`, `/pid`, `/dev?uid=` custom queries |
| `ProductController` | `/product` | Adds `/pro?uid=` to list products by user |
| `ApiSensorController` | `/api` | Receives sensor data from IoT devices via `/api/sensor?data=...`, stores in `tbl_datadict` using key matching against product table; `/api/javaip` callback from Python devices |
| `ApiSensorWebSockt` | `/websocket` | WebSocket endpoint that broadcasts sensor updates to connected browser clients |
| `WeatherController` | `/weather` | Scrapes tianqi.2345.com weather data using Jsoup |

**Observer/EventBus Pattern**: `EventBus` (singleton) maintains a list of `Observer` instances. When `ApiSensorController` receives sensor data, it calls `EventBus.getDefault().postUpdate(json)`, which fans out to all registered observers — primarily the WebSocket endpoint (`ApiSensorWebSockt`), which pushes the data to connected browsers in real time.

**Data layer**: MyBatis-Plus mappers extend `BaseMapper<T>`. Some override `selectList` with `@Select` to query database views (`v_device`, `v_product`) instead of raw tables. Database: MySQL `iotserver`, connection pool: Druid.

**Custom annotations**: `@NotNull` (not javax.validation) with `value()` (custom error message) and `fieldName()` — used by `NotNullUtil` which uses reflection to validate fields before insert/update and returns Chinese-language error messages.

### Frontend (Vue 2 + Element UI + Vue Router, inside `src/main/resources/iotVue/`)

- **Auto-routing**: `main.js` uses `require.context('./views', ...)` to scan `.vue` files under `views/` and register them as routes automatically. Files map to paths by converting the filename to lowercase: e.g., `Industry.vue` → `/industry`.
- **Login guard**: `router.beforeEach` checks `localStorage` for a `user` key; redirects to `/login` if missing.
- **Global components**: `Table`, `Bg` (background), `Swiper` are registered globally.
- **HTTP**: Axios with `baseURL = http://localhost:8080`, exposed as `Vue.prototype.$http`. A fetch-based fallback `Http` class exists for environments without Axios.
- **Views**: Login, Industry, City, Category, Product, Device, User, Weather, Detail, Pro.

### Data Flow (Sensor → Browser)

1. IoT device sends sensor data string to Python middleware on the device
2. Python calls back to Java `/api/sensor?data=...` with parsed telemetry
3. `ApiSensorController` parses the data into a `Sensor` POJO, stores JSON in `tbl_datadict` keyed by product name
4. `EventBus.postUpdate(json)` fires, WebSocket observer pushes to all connected browser clients
5. Frontend receives via WebSocket and updates dashboard charts (ECharts) in real time

### Key Configuration

- **application.yml**: Datasource (`localhost:3306/iotserver`), Freemarker suffix `.html`, file upload max 300MB, Jackson date format `yyyy-MM-dd HH:mm:ss` GMT+8
- **Static file serving**: `C:/create/file/` mapped to `/file/**` URL path (hardcoded in `IotServerApplication`)
