# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Raspberry Pi-based IoT greenhouse management system. A Python Flask web server that runs on a Raspberry Pi to read environmental sensors (DHT11 temperature/humidity, LM393 light/soil moisture), control actuators via GPIO (LED grow light, fan, water pump, servo motor), publish telemetry to a cloud MQTT platform, and forward data to a separate Java application server.

## Commands

```bash
# Start the Flask server (runs on port 8081, bound to 0.0.0.0)
python app.py

# The project has no test suite, no linter, and no build step.
# Clone and run directly on a Raspberry Pi with the required hardware attached.
```

## Architecture

### Entry Point & Core Loop

`app.py` is the single entry point. On startup it:
1. Initializes device state via `init_device()` (resets vtldata.json)
2. Starts a **background thread** running `index()` — an infinite sensor-reading loop
3. Registers three Flask Blueprints and starts the dev server on `0.0.0.0:8081`

The `index()` loop runs in a background thread separate from the Flask server. On each cycle it:
1. Reads LM393 light sensor + LM393 soil sensor + DHT11 temp/humidity
2. If sensor hardware is absent, falls back to random "virtual greenhouse" data
3. Every `Step` cycles (see `Utils.Step = 4`), publishes to MQTT (unless virtual)
4. On every cycle, HTTP-forwards the combined sensor string to the Java application server
5. On the half-step mark, also HTTP-forwards but skips MQTT publish

**Pin mappings** (BCM numbering): 7 = light sensor Do, 1 = soil moisture Do, 12 = DHT11 data, 19 = servo, 16 = water pump, 20 = LED grow light, 21 = exhaust fan.

### Blueprint Modules

| Blueprint | Prefix | File | Purpose |
|-----------|--------|------|---------|
| `sysmgr` | `/sysmgr` | `apps/sysmgr.py` | Management UI pages: servo config, app IP config, MQTT config, WiFi, device status, API docs |
| `user` | `/user` | `apps/user.py` | Single login endpoint that redirects to `/main/iotAdmin` |
| `api` | `/api` | `apps/api.py` | GPIO control endpoints, servo control, sensor data receiver, config persistence. This is the interface the Java server calls. |

### Hardware Abstraction Pattern

All hardware imports (`RPi.GPIO`, `Adafruit_DHT`) use a **try/except import** pattern — the app degrades gracefully to "virtual mode" when not running on a Raspberry Pi. This means the code runs (with simulated data) on any machine for development, but GPIO/physical control only works on the Pi.

### File-Based State Management

There is no database. All persistent state is stored in plain text/JSON files at the project root:
- `db.json` — MQTT connection parameters (TIMER, USERNAME, CLIENT_ID, PASSWORD, TC_HOST)
- `servo.json` — servo open/close angle values
- `static/vtldata.json` — current device states (led/fan/water/greenhouse: "打开"/"关闭")
- `javaip.txt` — external Java server URL
- `localPort.txt` — local Flask port (for self-referencing)
- `initMqtt.txt` — MQTT reconnection retry count
- `ledAuto.txt` — auto/manual light control mode
- `time.vue` — timestamp of last LED state change (used for auto-light delay logic)

### Data Flow

```
[Hardware Sensors]                         [MQTT Cloud Platform]
       │                                           ▲
       ▼                                           │
app.py background loop ──► publishes attributes JSON (qos=1)
       │
       │ HTTP GET /api/sensor?data=...
       ▼
[Java Application Server]                [Flask Web UI on :8081]
                                               ▲
[GPIO Pins] ◄── /api/gpio/<open|close>/<pin> ──┘
```

The sensor data string format passed to the Java server:
`{greenhouse}-{lightStatus}-{soilStatus}-{temperature}℃,{humidity}%-{deviceStates}-{autoMode}{ledAction}-{timestamp}-{sourceIP}`

### Auto-Light Logic

When `ledAuto` is "自动", the system reads the light sensor. If darkness is detected (`sunStatus == '黑夜/阴雨'`), it automatically turns on the LED (pin 20). The light turns off only after `timer * Step` seconds of continuous brightness — this delay prevents flickering from transient light changes.

### Mutual Exclusion for Water/Fan

Pin 16 (water pump) and pin 21 (fan) are mutually exclusive in software — turning one on automatically turns the other off. This is enforced in `vtl_ctl()` in `apps/api.py` to prevent excessive current draw.

### Frontend

Server-rendered Jinja2 templates with the ACE admin Bootstrap 3 theme. jQuery-based, no modern JS framework. The main page (`templates/main.html`) uses an iframe-based layout where management pages load inside `#mainframe`. The login page (`static/login.html`) is a static HTML file served directly.
