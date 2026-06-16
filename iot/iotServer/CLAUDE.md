# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Prerequisites

- **Java 8** + Maven
- **MySQL** running on `localhost:3306` with a database named `iotserver`
- **Node.js** (for frontend dev server only)
- **`C:/create/file/`** directory must exist (hardcoded file upload path — Windows only)

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

### Package Layout

```
com.qkd.iotserver
├── controller/    # REST controllers (all extend HttpController)
├── mapper/        # MyBatis-Plus mappers (all extend BaseMapper)
├── pojo/          # Entity classes (@TableName maps to DB tables)
└── util/          # EventBus, Observer, HttpConn, WSUtil, FileUtil, NotNull
```

### Backend (Spring Boot 2.3.2 + MyBatis-Plus 3.4.2, Java 8)

**Generic CRUD Base**: `HttpController<M extends BaseMapper<B>, B>` is the core abstraction. It provides standard REST endpoints (GET `/get`, `/count`, `/{id}` for delete/paginate, POST `/` for insert, POST `/{id}` for update) via MyBatis-Plus. Most entity controllers simply extend it with a `@RequestMapping` and `@CrossOrigin` — they inherit all CRUD for free.

**Important: `GET /{id}` does double duty**. If `id` is a number, it's a delete. If `id` is `"pageN"` (e.g. `page1`, `page2`), it returns paginated results (10 per page, ordered by id desc). This is an unusual overload — be aware when modifying routing.

**Key controllers that customize beyond the base:**

| Controller | Route | What it does differently |
|---|---|---|
| `UserController` | `/user` | Adds `/login` endpoint (manual auth against `tbl_user`), `/option` for dropdown enums, overrides `selectList` to filter `status='租户'` |
| `DeviceController` | `/device` | Overrides `select()`/`insert()` to decorate devices with pin info; generates `idcard` before insert; adds `/device?pid=`, `/pid`, `/dev?uid=` custom queries |
| `ProductController` | `/product` | Adds `/pro?uid=` to list products by user |
| `ApiSensorController` | `/api` | Receives sensor data from IoT devices via `/api/sensor?data=...`, stores in `tbl_datadict` using key matching against product table; `/api/javaip` callback from Python devices |
| `ApiSensorWebSockt` | `/websocket` | WebSocket endpoint that broadcasts sensor updates to connected browser clients |
| `WeatherController` | `/weather` | Scrapes tianqi.2345.com weather data using Jsoup |

**`Record` — universal response object**: `Record` extends `LinkedHashMap<String, Object>` and is the standard API response type. Key methods:
- `opt(key, ...values)` — builds dropdown option lists (array of `{label, value}` objects). The first value is the key name, remaining values become options. Frontend dropdowns depend on this format.
- `add(key, label, values...)` — adds a single labeled entry to an option list.
- `push(key, label, value)` — appends to an existing option list.
- Static factories: `Record.ok()`, `Record.fail(msg)` for standard success/error responses.

**Dynamic dropdown via `menu()`**: `HttpController.menu(String key)` uses Spring's `ApplicationContext` to look up a mapper by naming convention (`key + "Mapper"`), queries all rows, and builds a `Record` of dropdown options keyed by the entity's eponymous field. Controllers like `DeviceController.pid()` combine this with custom queries to populate frontend dropdowns dynamically.

**Observer/EventBus Pattern**: `EventBus` (thread-safe singleton via `synchronized getDefault()`) maintains a list of `Observer` instances. When `ApiSensorController` receives sensor data, it calls `EventBus.getDefault().postUpdate(json)`, which fans out to all registered observers — primarily the WebSocket endpoint (`ApiSensorWebSockt`), which pushes the data to connected browsers in real time. The WebSocket controller registers itself as an observer in its constructor and unregisters on `destroy()`.

**Data layer**: MyBatis-Plus mappers extend `BaseMapper<T>`. Several mappers override `selectList` with `@Select` to query database **views** (`v_device`, `v_product`) instead of raw tables — these views JOIN entity tables with lookup tables (city, user, category) to denormalize foreign keys into display names. Database: MySQL `iotserver`, connection pool: Druid.

**JSON library**: The project uses **Gson** (not Jackson) for JSON serialization in the sensor data path. Jackson is on the classpath via Spring Boot but is only used by the web layer for request/response binding.

**Custom `@NotNull` validation**: `@NotNull` (not javax.validation) with `value()` (custom error message) and `fieldName()` — used by `NotNullUtil` which uses reflection to validate fields before insert/update. Returns Chinese-language error messages like `"请填写设备名称！"`. Called by `HttpController.insert()` and `update()`.

**`HttpConn` utility**: Custom HTTP client wrapping `HttpURLConnection`. Has a **mutable static `mMapHeader`** field — callers set headers on this shared map before making requests. This is not thread-safe; be aware when modifying weather scraping or other HTTP callers.

### Frontend (Vue 2 + Element UI + Vue Router, inside `src/main/resources/iotVue/`)

- **Auto-routing**: `main.js` uses `require.context('./views', ...)` to scan `.vue` files under `views/` and register them as routes automatically. Files map to paths by converting the filename to lowercase: e.g., `Industry.vue` → `/industry`. The root path `/` redirects to `/login`.
- **Login guard**: `router.beforeEach` checks `localStorage` for a `user` key; redirects to `/login` if missing.
- **Global components**: `Table`, `Bg` (background), `Swiper` are registered globally.
- **HTTP**: Axios with `baseURL = http://localhost:8080`, exposed as `Vue.prototype.$http`. A fetch-based fallback `Http` class exists for environments without Axios. `Vue.prototype.$httpUrl` is a convenience wrapper: `$httpUrl(url)` does `$http.get(baseURL+url)` and returns `.data`.
- **`$formatTime(datetime, type)`**: Compensates for GMT+8 timezone offset. `type='-'` subtracts 8 hours (for submitting data to the server), otherwise adds 8 hours (for displaying server data). Returns `{year, mth, day, hour, min, sec}`.
- **`$myAnim(obj, param, nowrap)`**: Promise-based JS animation wrapper (from `move.js`), bound to Vue prototype for use in components.
- **`bus.js`**: A plain Vue instance (`new Vue()`) exported as a minimal event bus for cross-component communication.
- **Views**: Login, Industry, City, Category, Product, Device, User, Weather, Detail, Pro.

### Data Flow (Sensor → Browser)

1. IoT device sends sensor data string to Python middleware on the device
2. Python calls back to Java `/api/sensor?data=...` with parsed telemetry
3. `ApiSensorController.sensor()` parses the data via `Sensor.parse(data)` into a `Sensor` POJO
4. It extracts a shortened device IP and uses `ProductMapper.getLike(devip)` to fuzzy-match against stored product `devip` prefixes — this yields the product name used as the lookup key
5. If `tbl_datadict` has no entry for that key, a new row is inserted; otherwise the existing row is updated
6. `EventBus.getDefault().postUpdate(json)` fires, WebSocket observer pushes to all connected browser clients
7. Frontend receives via WebSocket and updates dashboard charts (ECharts) in real time

The `/api/javaip` endpoint handles the reverse direction: the browser sends the Python device's IP, the Java server calls back to `{devip}/api/javaip?javaip={localHttpIP}` so the Python device knows where to send sensor data.

### Key Configuration

- **application.yml**: Datasource (`localhost:3306/iotserver`), Freemarker suffix `.html`, file upload max 300MB, Jackson date format `yyyy-MM-dd HH:mm:ss` GMT+8
- **Static file serving**: `C:/create/file/` mapped to `/file/**` URL path (hardcoded in `IotServerApplication`)
- **⚠️ Known config bug**: `spring.datasource.filters: stat,wall,log4j` — the `spring.` prefix is incorrect; it should be just `filters`. Druid's stat and wall filters won't load as written.
- **⚠️ Security note**: Database password is hardcoded in `application.yml`.
