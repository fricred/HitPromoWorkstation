# Hit Promotional Products Android App - Detailed Screen Mockups

## Visual Design System Specifications

### Color Palette (Hex Values)
```
Primary Blue:     #2196F3  (rgb(33, 150, 243))
Success Green:    #4CAF50  (rgb(76, 175, 80))
Warning Orange:   #FF9800  (rgb(255, 152, 0))
Error Red:        #F44336  (rgb(244, 67, 54))
Background:       #FAFAFA  (rgb(250, 250, 250))
Surface:          #FFFFFF  (rgb(255, 255, 255))
Text Primary:     #212121  (rgb(33, 33, 33))
Text Secondary:   #757575  (rgb(117, 117, 117))
Divider:          #E0E0E0  (rgb(224, 224, 224))
```

### Typography Scale
```
H1 (Screen Titles):     Roboto Bold, 32sp, #212121
H2 (Section Headers):   Roboto Bold, 24sp, #212121
H3 (Card Titles):       Roboto Medium, 20sp, #212121
Body Large:             Roboto Regular, 18sp, #212121
Body Regular:           Roboto Regular, 16sp, #212121
Body Small:             Roboto Regular, 14sp, #757575
Button Text:            Roboto Medium, 20sp, #FFFFFF
Status Text:            Roboto Medium, 16sp, (varies by status)
```

### Spacing System (8dp Grid)
```
Micro:     4dp   (0.5 units)
Small:     8dp   (1 unit)
Medium:    16dp  (2 units)
Large:     24dp  (3 units)
XLarge:    32dp  (4 units)
XXLarge:   48dp  (6 units)
```

---

## Screen 1: Login/Authentication - Detailed Mockup

### Screen Dimensions: 1920 x 1200 (Landscape)

```
╔═══════════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                           ║
║   ┌─────────────────────────────────────────────────────────────────────────────────────┐ ║
║   │  [📱 Icon]    Hit Promotional Products                           v1.2.3    🔋87%   │ ║
║   │               Production Monitoring System                                   📶●●●● │ ║
║   └─────────────────────────────────────────────────────────────────────────────────────┘ ║
║                                                                                           ║
║                                      48dp margin                                         ║
║                                                                                           ║
║                        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓         ║
║                        ┃                                                           ┃         ║
║                        ┃                    SECURE LOGIN                          ┃         ║
║                        ┃                                                           ┃         ║
║                        ┃   Username or Email                                      ┃         ║
║                        ┃   ┌─────────────────────────────────────────────────────┐ ┃         ║
║                        ┃   │  production.user@hitpromo.com                      │ ┃         ║
║                        ┃   └─────────────────────────────────────────────────────┘ ┃         ║
║                        ┃                                                           ┃         ║
║                        ┃   Password                                               ┃         ║
║                        ┃   ┌─────────────────────────────────────────────────────┐ ┃         ║
║                        ┃   │  ••••••••••••••••••••••••••••••••••••••••••••••••   │ ┃         ║
║                        ┃   └─────────────────────────────────────────────────────┘ ┃         ║
║                        ┃                                                           ┃         ║
║                        ┃   Device Information                                     ┃         ║
║                        ┃   Device ID: SM-X210-PROD-001                           ┃         ║
║                        ┃   Location: Production Floor A                          ┃         ║
║                        ┃   ✓ Auto-detected and verified                          ┃         ║
║                        ┃                                                           ┃         ║
║                        ┃   ┌─────────────────────────────────────────────────────┐ ┃         ║
║                        ┃   │                   SIGN IN                          │ ┃         ║
║                        ┃   │                  #2196F3                           │ ┃         ║
║                        ┃   └─────────────────────────────────────────────────────┘ ┃         ║
║                        ┃                                                           ┃         ║
║                        ┃   ┌─ Status ──────────────────────────────────────────┐  ┃         ║
║                        ┃   │ 🟢 Ready to Connect                              │  ┃         ║
║                        ┃   │ Network: Excellent signal                        │  ┃         ║
║                        ┃   │ AWS: Cognito service available                   │  ┃         ║
║                        ┃   └──────────────────────────────────────────────────┘  ┃         ║
║                        ┃                                                           ┃         ║
║                        ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛         ║
║                                                                                           ║
║                                                                                           ║
║   ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     ║
║   │ Network Status  │  │ System Health   │  │ Time & Date     │  │ Battery & Temp  │     ║
║   │ WiFi: ●●●●      │  │ CPU: Normal     │  │ 14:32:45        │  │ 87% • 39°C      │     ║
║   │ 95 Mbps         │  │ Memory: 62%     │  │ March 28, 2025  │  │ Charging        │     ║
║   └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘     ║
║                                                                                           ║
╚═══════════════════════════════════════════════════════════════════════════════════════════╝

Dimensions:
- Card width: 640dp
- Card height: 480dp
- Input fields: 88dp height
- Button: 88dp height
- Status indicators: 72dp height each
```

### Login Screen Interaction States

#### Input Field Focus State
```
┌─────────────────────────────────────────────────────────────┐
│  production.user@hitpromo.com                            │
│  ▓                                                       │ ← Cursor
└─────────────────────────────────────────────────────────────┘
Border: #2196F3 (2dp stroke)
Background: #FFFFFF
```

#### Button Press State
```
┌─────────────────────────────────────────────────────────────┐
│                   SIGNING IN...                           │
│              [●●●●●○○○] Processing                        │
└─────────────────────────────────────────────────────────────┘
Background: #1976D2 (darker blue)
```

#### Error State
```
┌─ Status ──────────────────────────────────────────────────┐
│ 🔴 Authentication Failed                                  │
│ Invalid credentials. Please try again.                   │
│ Attempts remaining: 2/3                                  │
└───────────────────────────────────────────────────────────┘
```

---

## Screen 2: Streaming Dashboard - Detailed Mockup

### Screen Layout with Measurements

```
╔═══════════════════════════════════════════════════════════════════════════════════════════╗
║  ┌─ Header ─────────────────────────────────────────────────────────────────────────────┐  ║
║  │ [←] Streaming Dashboard    SM-X210-PROD-001       [⚙️Settings] [🚪Logout] [🔄Refresh] │  ║
║  └──────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Camera Section ─────────────────────┐  ┌─ Status Panel ──────────────────────────────┐  ║
║  │                                      │  │                                             │  ║
║  │  ┌─ Live Preview ─────────────────┐   │  │  Stream Status                             │  ║
║  │  │                               │   │  │  ┌─────────────────────────────────────────┐  │  ║
║  │  │     📹 LIVE CAMERA FEED      │   │  │  │ ● STREAMING                            │  │  ║
║  │  │                               │   │  │  │ #4CAF50 background                     │  │  ║
║  │  │        1280 x 720             │   │  │  └─────────────────────────────────────────┘  │  ║
║  │  │        H.264 Encoded          │   │  │                                             │  ║
║  │  │                               │   │  │  Protocol: WebRTC (Primary)                │  ║
║  │  │  ┌─ Overlay Info ──────────┐   │   │  │  Quality: 720p @ 30fps                     │  ║
║  │  │  │ ⏺ REC  14:35:22        │   │   │  │  Bitrate: 2.5 Mbps (Adaptive)             │  ║
║  │  │  │ 🎯 Focus: AUTO          │   │   │  │  Latency: <500ms                           │  ║
║  │  │  │ 💡 Flash: OFF           │   │   │  │                                             │  ║
║  │  │  └─────────────────────────┘   │   │  │  Session Information                       │  ║
║  │  └───────────────────────────────┘   │  │  Connected: 00:15:32                       │  ║
║  │                                      │  │  Data Sent: 847 MB                         │  ║
║  │  Camera Controls                     │  │  Viewers: 2 active                         │  ║
║  │  [🎯 Focus] [💡 Flash] [⚙️ Settings] │  │                                             │  ║
║  └──────────────────────────────────────┘  │  Analytics Summary                         │  ║
║                                            │  Total Detections: 127                     │  ║
║  ┌─ Stream Controls (Full Width) ────────────┤  Objects Found:                            │  ║
║  │                                         │  • Person: 3 current                       │  ║
║  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ • Packages: 15 detected            │  ║
║  │  │    STOP     │ │   START     │ │ EMERGENCY │  │  • Equipment: 8 visible              │  ║
║  │  │   STREAM    │ │   STREAM    │ │   STOP    │  │                                     │  ║
║  │  │  #F44336    │ │  #4CAF50    │ │  #FF5722  │  │                                     │  ║
║  │  └─────────────┘ └─────────────┘ └─────────────┘  └─────────────────────────────────────┘  ║
║  └──────────────────────────────────────────────┘                                        ║
║                                                                                           ║
║  ┌─ System Monitoring ──────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  ┌─ Network ─────┐ ┌─ Performance ──┐ ┌─ Storage ─────┐ ┌─ Temperature ──┐          │  ║
║  │  │ 📶 Wi-Fi      │ │ 💻 CPU Usage   │ │ 💾 Memory     │ │ 🌡️ Device      │          │  ║
║  │  │ ●●●● Excellent│ │ 45% Normal     │ │ 2.1/4.0 GB   │ │ 42°C Normal    │          │  ║
║  │  │ 95 Mbps       │ │ 4 cores active │ │ 62% Used     │ │ Optimal Range  │          │  ║
║  │  │ Stable        │ │ Efficient      │ │ Available     │ │ Safe Operation │          │  ║
║  │  └───────────────┘ └────────────────┘ └───────────────┘ └────────────────┘          │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
╚═══════════════════════════════════════════════════════════════════════════════════════════╝

Section Dimensions:
- Camera preview: 720dp x 480dp
- Status panel: 600dp x 480dp
- Control buttons: 200dp x 88dp each
- Monitoring cards: 300dp x 120dp each
```

### Dashboard Interactive Elements

#### Streaming Status Indicator Animation
```
● STREAMING (Active)     ⭕ CONNECTING (Pulsing)     ○ OFFLINE (Static)
#4CAF50                  #FF9800                     #9E9E9E
```

#### Control Button States
```
STOP STREAM (Active)     START STREAM (Ready)       EMERGENCY STOP
┌─────────────────┐      ┌─────────────────┐        ┌─────────────────┐
│      STOP       │      │     START       │        │   EMERGENCY     │
│     STREAM      │      │    STREAM       │        │      STOP       │
│    #F44336      │      │    #4CAF50      │        │    #FF5722      │
└─────────────────┘      └─────────────────┘        └─────────────────┘
Pressed: #C62828        Pressed: #388E3C            Pressed: #D84315
```

#### System Health Color Coding
```
Excellent: #4CAF50      Good: #8BC34A              Warning: #FF9800      Critical: #F44336
●●●●●                   ●●●●○                       ●●●○○                  ●●○○○
```

---

## Screen 3: Camera Settings - Detailed Mockup

```
╔═══════════════════════════════════════════════════════════════════════════════════════════╗
║  ┌─ Header ─────────────────────────────────────────────────────────────────────────────┐  ║
║  │ [←] Camera Settings                                    [💾 Save] [🔄 Reset] [❌ Cancel] │  ║
║  └──────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Video Configuration ─────────────────────┐  ┌─ Live Preview ─────────────────────────┐  ║
║  │                                           │  │                                        │  ║
║  │  Resolution Settings                      │  │  ┌─ Preview Window ─────────────────┐   │  ║
║  │  ┌─────────────────────────────────────┐   │  │  │                               │   │  ║
║  │  │ ○ 1920x1080 (30fps max) • Premium  │   │  │  │     Settings Preview         │   │  ║
║  │  │ ● 1280x720  (60fps max) • Standard │   │  │  │                               │   │  ║
║  │  │ ○ 640x480   (120fps max) • Basic   │   │  │  │     Current: 1280 x 720      │   │  ║
║  │  └─────────────────────────────────────┘   │  │  │     30 FPS • H.264           │   │  ║
║  │                                           │  │  │                               │   │  ║
║  │  Frame Rate Control                       │  │  │     Bandwidth: 2.5 Mbps      │   │  ║
║  │  ┌─────────────────────────────────────┐   │  │  └───────────────────────────────┘   │  ║
║  │  │ 15 ●────●────●────●────● 60         │   │  │                                        │  ║
║  │  │     Current: 30 FPS                 │   │  │  Quality Estimation                    │  ║
║  │  └─────────────────────────────────────┘   │  │  ┌─────────────────────────────────┐   │  ║
║  │                                           │  │  │ Bandwidth: 2.5 Mbps           │   │  ║
║  │  Bitrate Control                          │  │  │ Storage/hour: 1.1 GB           │   │  ║
║  │  ┌─────────────────────────────────────┐   │  │  │ Network impact: Moderate       │   │  ║
║  │  │ 1 ●────●────●────●────● 5           │   │  │  │ Quality rating: ●●●●○          │   │  ║
║  │  │     Current: 2.5 Mbps               │   │  │  └─────────────────────────────────┘   │  ║
║  │  └─────────────────────────────────────┘   │  └────────────────────────────────────────┘  ║
║  │                                           │                                            ║
║  │  Encoding Options                         │                                            ║
║  │  Format: H.264 (Hardware ✓)              │                                            ║
║  │  Profile: Baseline (Recommended)         │                                            ║
║  └───────────────────────────────────────────┘                                            ║
║                                                                                           ║
║  ┌─ Camera Controls ────────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  Focus Settings          Exposure Control           White Balance                    │  ║
║  │  ┌─────────────────┐     ┌─────────────────────┐     ┌─────────────────────────────┐ │  ║
║  │  │ [●] Auto Focus  │     │ Auto ●──────○ Manual│     │ [●] Auto  [ ] Daylight     │ │  ║
║  │  │ [ ] Manual      │     │      Current: Auto   │     │ [ ] Cloudy [ ] Fluorescent │ │  ║
║  │  └─────────────────┘     └─────────────────────┘     │ [ ] Incandescent [ ] Shade  │ │  ║
║  │                                                      └─────────────────────────────┘ │  ║
║  │  Flash Control           Zoom Control                Stabilization                   │  ║
║  │  ┌─────────────────┐     ┌─────────────────────┐     ┌─────────────────────────────┐ │  ║
║  │  │ [ ] Flash Auto  │     │ 1x ●────○────○ 4x   │     │ [●] Digital Stabilization  │ │  ║
║  │  │ [ ] Flash On    │     │    Current: 1.2x    │     │ [ ] Optical (Not Available) │ │  ║
║  │  │ [●] Flash Off   │     └─────────────────────┘     └─────────────────────────────┘ │  ║
║  │  └─────────────────┘                                                                │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Advanced Settings ──────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  Performance Options                    Network Optimization                         │  ║
║  │  [●] Hardware Acceleration              [●] Adaptive Bitrate                         │  ║
║  │  [●] Auto Quality Adjustment            [●] Network Loss Compensation               │  ║
║  │  [ ] Battery Optimization Mode          [ ] Low Latency Mode                        │  ║
║  │                                                                                       │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Action Buttons ─────────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │  ║
║  │  │   SAVE & APPLY  │  │ RESET TO DEFAULT│  │   TEST SETTINGS │  │     CANCEL      │  │  ║
║  │  │    #4CAF50      │  │    #FF9800      │  │    #2196F3      │  │    #757575      │  │  ║
║  │  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘  │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
╚═══════════════════════════════════════════════════════════════════════════════════════════╝
```

### Camera Settings Interaction Details

#### Slider Components
```
Frame Rate Slider:
15 ●────●────●────●────● 60
   │    │    │    │    │
  15   22   30   45   60 FPS

Active handle: #2196F3 (12dp radius)
Track: #E0E0E0 (4dp height)
Progress: #2196F3 (4dp height)
```

#### Radio Button States
```
Selected: ● 1280x720          Unselected: ○ 1920x1080
         #2196F3                        #757575
```

#### Toggle Switch States
```
ON:  [●●●●●●●●●○○]          OFF: [○○●●●●●●●●●]
     #4CAF50                     #BDBDBD
```

---

## Screen 4: Network Diagnostics - Detailed Mockup

```
╔═══════════════════════════════════════════════════════════════════════════════════════════╗
║  ┌─ Header ─────────────────────────────────────────────────────────────────────────────┐  ║
║  │ [←] Network Diagnostics                  Last Update: 14:35:22    [🔄 Refresh] [📊 Export] │  ║
║  └──────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Connection Overview ────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  Wi-Fi Network: ProductionFloor_5G              Status: ● Connected                  │  ║
║  │  Signal Strength: ●●●● (RSSI: -42 dBm)         Quality: Excellent                   │  ║
║  │  IP Address: 192.168.1.156                      Gateway: 192.168.1.1               │  ║
║  │  DNS: 8.8.8.8, 8.8.4.4                         MAC: 5C:FB:3C:A7:B2:1D             │  ║
║  │  Channel: 149 (5GHz)                            Security: WPA3-Enterprise           │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Bandwidth Testing ──────────────────┐  ┌─ Streaming Performance ───────────────────┐  ║
║  │                                      │  │                                           │  ║
║  │  Current Test Results                │  │  Protocol: WebRTC                        │  ║
║  │  ┌────────────────────────────────┐   │  │  Server: us-east-1.kinesisvideo...      │  ║
║  │  │ Upload Speed                   │   │  │                                           │  ║
║  │  │ ●●●●●●●●●○ 45.6 Mbps          │   │  │  Connection Status                       │  ║
║  │  │ Status: Testing... 67%         │   │  │  ┌─────────────────────────────────────┐ │  ║
║  │  └────────────────────────────────┘   │  │  │ ● Active - Streaming               │ │  ║
║  │                                      │  │  │ Uptime: 01:23:45                   │ │  ║
║  │  ┌────────────────────────────────┐   │  │  └─────────────────────────────────────┘ │  ║
║  │  │ Download Speed                 │   │  │                                           │  ║
║  │  │ ●●●●●●●●●● 128.3 Mbps         │   │  │  Performance Metrics                     │  ║
║  │  │ Status: Complete ✓             │   │  │  Latency: 127ms (Good)                  │  ║
║  │  └────────────────────────────────┘   │  │  Jitter: 3ms (Excellent)               │  ║
║  │                                      │  │  Packet Loss: 0.1% (Excellent)         │  ║
║  │  Quality Metrics                     │  │  Throughput: 2.4 Mbps actual           │  ║
║  │  • Latency: 24ms (Excellent)        │  │                                           │  ║
║  │  • Jitter: 2ms (Excellent)          │  │  Data Transfer                           │  ║
║  │  • Packet Loss: 0.0% (Perfect)      │  │  Sent: 2.4 GB                           │  ║
║  │                                      │  │  Received: 156 MB                       │  ║
║  │  ┌─────────────────────────────────┐  │  │  Efficiency: 94%                        │  ║
║  │  │       [RUN NEW TEST]            │  │  └───────────────────────────────────────────┘  ║
║  │  │        #2196F3                  │  │                                            ║
║  │  └─────────────────────────────────┘  │                                            ║
║  └──────────────────────────────────────┘                                            ║
║                                                                                           ║
║  ┌─ Connection History & Logs ──────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  Recent Activity (Last 2 hours)                                                      │  ║
║  │  ┌─────────────────────────────────────────────────────────────────────────────────┐ │  ║
║  │  │ 14:35:15  ● Stream started successfully                                        │ │  ║
║  │  │ 14:34:02  ⚠ Network fluctuation detected (duration: 2s)                      │ │  ║
║  │  │ 14:33:45  ● Auto-reconnected to AWS Kinesis Video Streams                     │ │  ║
║  │  │ 14:33:43  ⚡ Auto-reconnect sequence initiated                                 │ │  ║
║  │  │ 14:33:41  ⚠ Connection lost - network timeout (30s)                          │ │  ║
║  │  │ 14:31:12  ● Stream quality auto-adjusted to 720p                             │ │  ║
║  │  │ 14:28:33  ● WebRTC signaling handshake completed                              │ │  ║
║  │  │ 14:28:30  ● AWS Cognito authentication successful                             │ │  ║
║  │  │ 14:28:15  ⚡ Application started                                               │ │  ║
║  │  └─────────────────────────────────────────────────────────────────────────────────┘ │  ║
║  │                                                                                       │  ║
║  │  Log Actions                                                                          │  ║
║  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │  ║
║  │  │  [📄 EXPORT]    │  │  [🗑️ CLEAR]     │  │  [🔍 FILTER]   │  │  [📧 SEND]      │ │  ║
║  │  │  Full Log       │  │  History        │  │  Events        │  │  to Support     │ │  ║
║  │  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘ │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
╚═══════════════════════════════════════════════════════════════════════════════════════════╝
```

### Network Status Indicators

#### Signal Strength Visualization
```
●●●● Excellent (-30 to -50 dBm)     ●●●○ Good (-50 to -60 dBm)
●●○○ Fair (-60 to -70 dBm)          ●○○○ Poor (-70 to -80 dBm)
```

#### Bandwidth Test Progress
```
Testing State:
┌─────────────────────────────┐
│ ●●●●●●●○○○ 67%             │
│ Upload: Testing...          │
└─────────────────────────────┘

Complete State:
┌─────────────────────────────┐
│ ●●●●●●●●●● 100% ✓          │
│ Upload: 45.6 Mbps          │
└─────────────────────────────┘
```

---

## Screen 5: Error Handling - Detailed Mockup

### Connection Error State

```
╔═══════════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                           ║
║                                    ⚠️                                                    ║
║                                                                                           ║
║                              CONNECTION LOST                                             ║
║                                                                                           ║
║                        Unable to reach streaming server                                  ║
║                                                                                           ║
║                                                                                           ║
║  ┌─ Error Information ─────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  Error Details                                                                        │  ║
║  │  ┌─────────────────────────────────────────────────────────────────────────────────┐ │  ║
║  │  │ Error Code: AWS_KVS_SIGNALING_TIMEOUT                                          │ │  ║
║  │  │ Timestamp: 14:35:22                                                             │ │  ║
║  │  │ Description: WebRTC signaling channel timeout after 30 seconds                │ │  ║
║  │  │                                                                                 │ │  ║
║  │  │ Last Successful Connection: 14:30:15 (5 minutes ago)                          │ │  ║
║  │  │ Network Status: Connected to ProductionFloor_5G                               │ │  ║
║  │  │ Internet Connectivity: Available (ping: 23ms)                                 │ │  ║
║  │  │ Server Status: AWS KVS service operational                                     │ │  ║
║  │  └─────────────────────────────────────────────────────────────────────────────────┘ │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Auto-Recovery Status ───────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  Automatic Reconnection in Progress                                                   │  ║
║  │  ┌─────────────────────────────────────────────────────────────────────────────────┐ │  ║
║  │  │ Reconnecting...                                                                 │ │  ║
║  │  │ ████████████████████████████████████████████████████████████░░░░░░░░░ 65%     │ │  ║
║  │  └─────────────────────────────────────────────────────────────────────────────────┘ │  ║
║  │                                                                                       │  ║
║  │  ┌─ Retry Information ───────────────────────────────────────────────────────────┐   │  ║
║  │  │ Attempt: 3 of 5                        Next retry in: 8 seconds              │   │  ║
║  │  │ Strategy: Exponential backoff          Fallback: RTSP mode available         │   │  ║
║  │  │ Interval: 2s → 4s → 8s → 16s → 32s    Protocol: WebRTC → RTSP                │   │  ║
║  │  └─────────────────────────────────────────────────────────────────────────────────┘   │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Manual Actions ─────────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │  ║
║  │  │   RETRY NOW     │  │  SWITCH TO      │  │  REPORT ISSUE   │  │   SETTINGS      │  │  ║
║  │  │                 │  │   RTSP MODE     │  │                 │  │                 │  │  ║
║  │  │    #2196F3      │  │    #FF9800      │  │    #9C27B0      │  │    #757575      │  │  ║
║  │  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘  │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║                          🔄 Auto-return to dashboard when connected                      ║
║                                                                                           ║
╚═══════════════════════════════════════════════════════════════════════════════════════════╝
```

### Connection Restored State

```
╔═══════════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                           ║
║                                     ✅                                                   ║
║                                                                                           ║
║                            CONNECTION RESTORED                                           ║
║                                                                                           ║
║                     Successfully reconnected to streaming server                         ║
║                                                                                           ║
║                                                                                           ║
║  ┌─ Recovery Summary ──────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  Reconnection Successful                                                              │  ║
║  │  ┌─────────────────────────────────────────────────────────────────────────────────┐ │  ║
║  │  │ Reconnected at: 14:36:01                                                        │ │  ║
║  │  │ Total downtime: 39 seconds                                                      │ │  ║
║  │  │ Recovery method: WebRTC automatic retry (attempt 3)                            │ │  ║
║  │  │ Connection quality: ●●●● Excellent                                             │ │  ║
║  │  │                                                                                 │ │  ║
║  │  │ Stream status: Resuming in 3 seconds...                                        │ │  ║
║  │  │ Data integrity: No frames lost during reconnection                             │ │  ║
║  │  │ Performance: All systems nominal                                               │ │  ║
║  │  └─────────────────────────────────────────────────────────────────────────────────┘ │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║  ┌─ Next Steps ─────────────────────────────────────────────────────────────────────────┐  ║
║  │                                                                                       │  ║
║  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │  ║
║  │  │   CONTINUE      │  │   VIEW LOG      │  │  SYSTEM CHECK   │  │   REPORT        │  │  ║
║  │  │   STREAMING     │  │                 │  │                 │  │   SUCCESS       │  │  ║
║  │  │    #4CAF50      │  │    #2196F3      │  │    #FF9800      │  │    #9C27B0      │  │  ║
║  │  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘  │  ║
║  └───────────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                           ║
║                     🔄 Automatically returning to dashboard in 5 seconds...             ║
║                                      [Skip Wait]                                         ║
║                                                                                           ║
╚═══════════════════════════════════════════════════════════════════════════════════════════╝
```

### Error State Variations

#### Network Timeout Error
```
⚠️ NETWORK TIMEOUT
Unable to establish connection
Check your Wi-Fi connection and try again
```

#### Authentication Error
```
🔐 AUTHENTICATION FAILED
Invalid credentials or expired session
Please log in again
```

#### Server Error
```
🔧 SERVER UNAVAILABLE
AWS Kinesis Video Streams is temporarily unavailable
Trying alternative connection methods...
```

#### Device Error
```
📱 DEVICE ERROR
Camera or hardware malfunction detected
Please restart the application
```

---

## User Flow Navigation

### Complete App Navigation Structure

```
Login Screen
     │
     ├─ Success ──→ Dashboard (Main)
     │                   │
     │                   ├─ Camera Settings
     │                   │      │
     │                   │      └─ Back to Dashboard
     │                   │
     │                   ├─ Network Diagnostics
     │                   │      │
     │                   │      └─ Back to Dashboard
     │                   │
     │                   ├─ Settings Menu
     │                   │      │
     │                   │      └─ Various sub-settings
     │                   │
     │                   └─ Error States
     │                          │
     │                          ├─ Auto Recovery → Dashboard
     │                          ├─ Manual Recovery → Dashboard
     │                          └─ Protocol Switch → Dashboard
     │
     └─ Error ──→ Error Screen → Retry → Login
```

## Key Measurements and Specifications

### Touch Target Specifications
- Minimum touch target: 64dp (≈13mm at 160dpi)
- Recommended touch target: 88dp (≈18mm at 160dpi)
- Button spacing: 16dp minimum between targets
- Edge margins: 24dp from screen edges

### Text Size Guidelines
- Screen titles: 32sp (≈8.5mm at reading distance)
- Section headers: 24sp (≈6.5mm at reading distance)
- Body text: 18sp (≈4.8mm at reading distance)
- Status text: 16sp (≈4.3mm at reading distance)

### Color Contrast Ratios
- Text on background: 4.5:1 minimum (WCAG AA)
- Large text on background: 3:1 minimum
- UI controls: 3:1 minimum for boundaries
- Status indicators: 4.5:1 for text content

This comprehensive mockup package provides the development team with detailed specifications for implementing the Hit Promotional Products Android tablet application, ensuring optimal usability in industrial production environments while maintaining robust streaming functionality.