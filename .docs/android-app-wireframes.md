# Hit Promotional Products Android Tablet Application - UI/UX Wireframes & Mockups

## Design Overview

**Target Device:** Samsung Galaxy Tab A9+ (SM-X210)
- Screen: 11.0" - 1920x1200 (206 dpi)
- Orientation: Landscape optimized
- Environment: Industrial production floor
- Users: Production workers (possibly wearing gloves)

## Design Principles

### Industrial Environment Considerations
- **Large Touch Targets:** Minimum 64dp (≈13mm) for gloved operation
- **High Contrast:** Strong color contrast for various lighting conditions
- **Clear Status Indicators:** Unmistakable visual feedback for all states
- **Simple Navigation:** Minimal taps, clear hierarchy
- **Robust Error Handling:** Clear messaging and recovery paths

### Visual Design System

#### Color Palette
- **Primary Blue:** #2196F3 (streaming active, primary actions)
- **Success Green:** #4CAF50 (connected, healthy status)
- **Warning Orange:** #FF9800 (connection issues, warnings)
- **Error Red:** #F44336 (disconnected, errors)
- **Background:** #FAFAFA (light gray, easy on eyes)
- **Surface:** #FFFFFF (cards, panels)
- **Text Primary:** #212121 (high contrast)
- **Text Secondary:** #757575 (labels, metadata)

#### Typography
- **Headers:** Roboto Bold, 24sp minimum
- **Body Text:** Roboto Regular, 18sp minimum
- **Status Text:** Roboto Medium, 16sp minimum
- **Buttons:** Roboto Medium, 20sp minimum

#### Touch Targets
- **Primary Buttons:** 88dp height minimum
- **Secondary Buttons:** 72dp height minimum
- **Toggle Switches:** 64dp touch area
- **List Items:** 80dp height minimum

---

## Screen 1: Login/Authentication

### Layout Structure (1920x1200 landscape)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  [LOGO]                    Hit Promotional Products                         │
│                           Production Monitoring                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                    ┌─────────────────────────────────┐                     │
│                    │                                 │                     │
│                    │          LOGIN CARD             │                     │
│                    │                                 │                     │
│                    │  Username: [_______________]    │                     │
│                    │                                 │                     │
│                    │  Password: [_______________]    │                     │
│                    │                                 │                     │
│                    │  Device ID: SM-X210-001        │                     │
│                    │  (Auto-detected)                │                     │
│                    │                                 │                     │
│                    │     [    SIGN IN    ]          │                     │
│                    │                                 │                     │
│                    │  Status: Ready to Connect       │                     │
│                    │                                 │                     │
│                    └─────────────────────────────────┘                     │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐             │
│  │   Wi-Fi: ●●●○   │  │   Battery: 87%  │  │   Time: 14:32   │             │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Design Annotations

#### Login Form
- **Centered layout** with generous whitespace
- **Card elevation** (8dp) for visual hierarchy
- **Input fields** with clear labels and 88dp height
- **Auto-detected Device ID** reduces user input errors
- **Large Sign In button** (320dp wide, 88dp height) with primary blue color

#### Status Indicators
- **Connection status** clearly displayed below form
- **System status bar** at bottom showing critical info
- **Visual indicators** use color + text for accessibility

#### Technical Considerations
- AWS Cognito authentication flow
- Device registration with unique identifier
- Network connectivity validation before login attempt
- Secure credential storage using Android Keystore

---

## Screen 2: Streaming Control Dashboard (Main Screen)

### Layout Structure

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Streaming Dashboard                                    [Settings] [Logout] │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────┐  ┌─────────────────────────────────┐ │
│  │         CAMERA PREVIEW              │  │       STREAM STATUS             │ │
│  │                                     │  │                                 │ │
│  │  ┌─────────────────────────────┐    │  │  Status: ● STREAMING           │ │
│  │  │                             │    │  │  Protocol: WebRTC              │ │
│  │  │        Live Camera          │    │  │  Quality: 720p @ 30fps         │ │
│  │  │        Feed Preview         │    │  │  Bitrate: 2.5 Mbps             │ │
│  │  │                             │    │  │  Latency: <500ms               │ │
│  │  │     1280 x 720              │    │  │                                 │ │
│  │  └─────────────────────────────┘    │  │  Connected: 00:15:32           │ │
│  │                                     │  │  Viewers: 2 active             │ │
│  │  ● REC  [●] Auto-focus  [○] Flash   │  │                                 │ │
│  └─────────────────────────────────────┘  │  Analytics: 127 detections     │ │
│                                            │  Objects: Person(3), Box(15)   │ │
│  ┌─────────────────────────────────────────┘                                 │ │
│  │                 STREAM CONTROLS                                           │ │
│  ├─────────────────────────────────────────────────────────────────────────┤ │
│  │                                                                           │ │
│  │    [     STOP STREAM     ]     [   START STREAM   ]     [  EMERGENCY  ]  │ │
│  │         (88dp height)            (88dp height)          [    STOP    ]  │ │
│  │                                                           (Red button)   │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐   │
│  │ Network: ●●●● │ │ CPU: 45%      │ │ Memory: 62%   │ │ Temp: 42°C    │   │
│  │ 95 Mbps       │ │ Normal        │ │ 2.1/4.0 GB   │ │ Normal        │   │
│  └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Design Annotations

#### Camera Preview Section
- **Live preview** showing actual camera feed
- **Recording indicator** (red dot) when streaming
- **Camera controls** for focus and flash
- **Resolution display** for technical verification

#### Stream Status Panel
- **Large status indicator** with color coding
- **Protocol information** (WebRTC/RTSP)
- **Quality metrics** updated in real-time
- **Analytics summary** showing detection counts

#### Control Buttons
- **Stop/Start buttons** with different visual weights
- **Emergency Stop** in red for immediate termination
- **High contrast** for visibility in all lighting

#### System Monitoring
- **Four key metrics** in bottom row
- **Color-coded status** (green/orange/red)
- **Actual values** plus status text

#### Technical Considerations
- Real-time status updates via service binding
- Hardware-accelerated camera preview
- Background service status monitoring
- Emergency protocols for production safety

---

## Screen 3: Camera Settings Configuration

### Layout Structure

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  ← Camera Settings                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────┐  ┌─────────────────────────────────┐ │
│  │         VIDEO SETTINGS              │  │        PREVIEW                  │ │
│  │                                     │  │                                 │ │
│  │  Resolution:                        │  │  ┌─────────────────────────┐    │ │
│  │  ○ 1920x1080 (30fps max)           │  │  │                         │    │ │
│  │  ● 1280x720  (60fps max)           │  │  │    Settings Preview     │    │ │
│  │  ○ 640x480   (120fps max)          │  │  │                         │    │ │
│  │                                     │  │  │     1280 x 720         │    │ │
│  │  Frame Rate:                        │  │  │     30 FPS              │    │ │
│  │  ┌─────────────────────────┐        │  │  │     H.264               │    │ │
│  │  │ 30 FPS    ←→   60 FPS   │        │  │  └─────────────────────────┘    │ │
│  │  └─────────────────────────┘        │  │                                 │ │
│  │                                     │  │  Estimated Bandwidth:           │ │
│  │  Bitrate:                           │  │  2.5 Mbps                       │ │
│  │  ┌─────────────────────────┐        │  │                                 │ │
│  │  │ 1 Mbps   ←→   5 Mbps    │        │  │  Storage per hour:              │ │
│  │  └─────────────────────────┘        │  │  1.1 GB                         │ │
│  │                                     │  └─────────────────────────────────┘ │
│  │  Encoding: H.264 (Hardware)        │                                     │ │
│  └─────────────────────────────────────┘                                     │ │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                    CAMERA CONTROLS                                      │ │
│  │                                                                         │ │
│  │  Auto Focus:     [ON ]  [OFF]      Exposure:  ┌───────────────┐       │ │
│  │                                                │ Auto    ←→    │       │ │
│  │  Flash:          [ON ]  [OFF]                  └───────────────┘       │ │
│  │                                                                         │ │
│  │  White Balance:  [AUTO] [DAYLIGHT] [FLUORESCENT]                       │ │
│  │                                                                         │ │
│  │  Zoom:           ┌─────────────────────────────┐                       │ │
│  │                  │ 1x        ←→         4x     │                       │ │
│  │                  └─────────────────────────────┘                       │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌─────────────────────────┐  ┌─────────────────────────┐                   │
│  │    [    RESET TO       │  │    [     APPLY         │                   │
│  │      DEFAULTS    ]     │  │     SETTINGS    ]      │                   │
│  └─────────────────────────┘  └─────────────────────────┘                   │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Design Annotations

#### Video Settings Panel
- **Radio buttons** for resolution with FPS limits shown
- **Slider controls** for frame rate and bitrate with clear ranges
- **Hardware encoding** indicator for performance

#### Live Preview
- **Real-time preview** showing current settings impact
- **Bandwidth estimation** for network planning
- **Storage calculation** for local recording consideration

#### Camera Controls
- **Toggle switches** for binary options
- **Sliders** for continuous adjustments
- **Button groups** for multi-option settings
- **Visual feedback** for all changes

#### Action Buttons
- **Reset button** for quick restoration
- **Apply button** with primary styling
- **Clear hierarchy** between destructive and constructive actions

#### Technical Considerations
- Camera2 API parameter validation
- Real-time preview updates during adjustment
- Hardware capability detection and limiting
- Settings persistence across app restarts

---

## Screen 4: Network Status and Diagnostics

### Layout Structure

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  ← Network Diagnostics                                        [REFRESH]    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                        CONNECTION STATUS                                │ │
│  │                                                                         │ │
│  │  Wi-Fi Network: ProductionFloor_5G              Status: ● Connected     │ │
│  │  Signal Strength: ●●●● (RSSI: -42 dBm)         Quality: Excellent      │ │
│  │  IP Address: 192.168.1.156                      Gateway: 192.168.1.1   │ │
│  │  DNS: 8.8.8.8, 8.8.4.4                         MAC: 5C:FB:3C:A7:B2:1D │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌─────────────────────────────────────┐  ┌─────────────────────────────────┐ │
│  │          BANDWIDTH TEST             │  │        STREAMING STATUS         │ │
│  │                                     │  │                                 │ │
│  │  Upload Speed:    ●●●●○             │  │  Protocol: WebRTC               │ │
│  │  45.6 Mbps       (Testing...)       │  │  Server: us-east-1.kinesis...   │ │
│  │                                     │  │                                 │ │
│  │  Download Speed:  ●●●●●             │  │  Connection: ● Active           │ │
│  │  128.3 Mbps      (Complete)         │  │  Latency: 127ms                 │ │
│  │                                     │  │  Jitter: 3ms                    │ │
│  │  Latency:         24ms              │  │  Packet Loss: 0.1%             │ │
│  │  Jitter:          2ms               │  │                                 │ │
│  │  Packet Loss:     0.0%              │  │  Data Sent: 2.4 GB             │ │
│  │                                     │  │  Duration: 01:23:45             │ │
│  │  [   RUN TEST   ]                   │  └─────────────────────────────────┘ │
│  └─────────────────────────────────────┘                                     │ │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                          CONNECTION LOG                                 │ │
│  │                                                                         │ │
│  │  14:32:15  ● Stream started successfully                               │ │
│  │  14:31:02  ⚠ Network fluctuation detected (2s)                        │ │
│  │  14:30:45  ● Reconnected to AWS KVS                                    │ │
│  │  14:30:43  ● Auto-reconnect initiated                                  │ │
│  │  14:30:41  ⚠ Connection lost - network timeout                        │ │
│  │  14:28:12  ● Stream quality adjusted to 720p                          │ │
│  │  14:25:33  ● WebRTC signaling complete                                 │ │
│  │  14:25:30  ● Authentication successful                                 │ │
│  │                                                                         │ │
│  │  [  EXPORT LOG  ]                            [  CLEAR LOG  ]           │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Design Annotations

#### Connection Status Header
- **Network details** in clear, readable format
- **Signal strength** with both visual and numerical indicators
- **Technical details** for IT troubleshooting

#### Bandwidth Test Panel
- **Real-time testing** with progress indicators
- **Multiple metrics** for comprehensive assessment
- **Manual test trigger** for on-demand diagnostics

#### Streaming Status Panel
- **Protocol information** and server details
- **Real-time metrics** updated continuously
- **Session statistics** for performance tracking

#### Connection Log
- **Chronological events** with timestamps
- **Color-coded severity** (green/orange/red)
- **Export capability** for technical support
- **Clear action** for maintenance

#### Technical Considerations
- Real-time network monitoring APIs
- Bandwidth testing using dedicated endpoints
- Connection event logging and persistence
- Export functionality for troubleshooting

---

## Screen 5: Error Handling and Reconnection

### Layout Structure - Connection Error State

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Connection Error                                              [Diagnostics] │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                          ⚠                                                 │
│                                                                             │
│                    CONNECTION LOST                                          │
│                                                                             │
│                Unable to connect to streaming server                        │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                       ERROR DETAILS                                     │ │
│  │                                                                         │ │
│  │  Error Code: AWS_KVS_TIMEOUT                                            │ │
│  │  Time: 14:35:22                                                         │ │
│  │  Details: WebRTC signaling channel timeout after 30 seconds            │ │
│  │                                                                         │ │
│  │  Last successful connection: 14:30:15 (5 minutes ago)                  │ │
│  │  Network status: Connected to ProductionFloor_5G                       │ │
│  │  Internet connectivity: Available                                       │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                    AUTOMATIC RECOVERY                                   │ │
│  │                                                                         │ │
│  │  ┌─────────────────────────────────────┐                               │ │
│  │  │ Reconnecting...                     │                               │ │
│  │  │ ████████████░░░░░░░░ 65%           │                               │ │
│  │  └─────────────────────────────────────┘                               │ │
│  │                                                                         │ │
│  │  Attempt 3 of 5                        Next retry in: 8 seconds        │ │
│  │  Using exponential backoff             Fallback: RTSP mode             │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌─────────────────────────┐  ┌─────────────────────────┐                   │
│  │    [    RETRY NOW     ] │  │    [   SWITCH TO      ] │                   │
│  │                        │  │      RTSP MODE         │                   │ │
│  └─────────────────────────┘  └─────────────────────────┘                   │
│                                                                             │
│  ┌─────────────────────────┐  ┌─────────────────────────┐                   │
│  │    [   REPORT ISSUE   ] │  │    [     SETTINGS     ] │                   │
│  └─────────────────────────┘  └─────────────────────────┘                   │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Layout Structure - Recovery Success State

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Connection Restored                                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                          ✓                                                 │
│                                                                             │
│                  CONNECTION RESTORED                                        │
│                                                                             │
│              Successfully reconnected to streaming server                   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                    RECOVERY SUMMARY                                     │ │
│  │                                                                         │ │
│  │  Reconnected at: 14:36:01                                              │ │
│  │  Downtime: 39 seconds                                                   │ │
│  │  Recovery method: WebRTC automatic retry                               │ │
│  │  Connection quality: ●●●● Excellent                                    │ │
│  │                                                                         │ │
│  │  Stream status: Resuming in 3 seconds...                               │ │
│  │  Buffered data: No data lost                                           │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│                                                                             │
│  ┌─────────────────────────┐  ┌─────────────────────────┐                   │
│  │    [   CONTINUE       ] │  │    [   VIEW LOG       ] │                   │
│  │      STREAMING        ] │  │                        │                   │
│  └─────────────────────────┘  └─────────────────────────┘                   │
│                                                                             │
│           Automatically returning to dashboard in 5 seconds...             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Design Annotations

#### Error State Design
- **Large warning icon** for immediate recognition
- **Clear error title** in plain language
- **Technical details** collapsible for support
- **Recovery progress** with visual indicators

#### Automatic Recovery Panel
- **Progress bar** showing retry attempts
- **Countdown timer** for next attempt
- **Fallback option** clearly indicated
- **Transparent process** builds user confidence

#### Manual Action Buttons
- **Retry Now** for impatient users
- **Switch Protocol** for alternative path
- **Report Issue** for escalation
- **Settings** for configuration changes

#### Success State Design
- **Success checkmark** for positive reinforcement
- **Recovery summary** with timing details
- **Continuation path** clearly marked
- **Automatic progression** with countdown

#### Technical Considerations
- Exponential backoff algorithm implementation
- Protocol fallback mechanisms (WebRTC → RTSP)
- Connection state persistence during recovery
- User notification and feedback systems

---

## Navigation Flow Diagram

```
Login/Auth ──────→ Streaming Dashboard
    │                    │
    │                    ├── Camera Settings
    │                    │        │
    │                    │        └── Back to Dashboard
    │                    │
    │                    ├── Network Diagnostics
    │                    │        │
    │                    │        └── Back to Dashboard
    │                    │
    │                    └── Error Handling
    │                             │
    │                             ├── Retry → Dashboard
    │                             ├── Switch Protocol → Dashboard
    │                             └── Settings → Camera Settings
    │
    └── Connection Error → Auto Recovery → Dashboard
```

## Key Design Decisions

### Industrial Environment Optimizations

1. **Touch Target Size**: All interactive elements minimum 64dp (≈13mm) for gloved operation
2. **High Contrast Colors**: Strong color differences for visibility in various lighting
3. **Large Text**: Minimum 16sp for readability at arm's length
4. **Simple Navigation**: Maximum 2 taps to reach any function
5. **Clear Status Indicators**: Color + text + icons for accessibility

### Performance Considerations

1. **Hardware Acceleration**: Camera preview and video encoding use hardware APIs
2. **Background Services**: Streaming continues when app is backgrounded
3. **Memory Management**: Efficient preview rendering and buffer management
4. **Thermal Protection**: Auto-adjustment of quality based on device temperature

### Accessibility Features

1. **Screen Reader Support**: All elements properly labeled
2. **High Contrast Mode**: Alternative color scheme for vision impaired
3. **Large Text Support**: Scalable font sizes
4. **Voice Feedback**: Audio confirmation of critical actions

### Error Recovery Strategy

1. **Automatic Retry**: Exponential backoff with circuit breaker
2. **Protocol Fallback**: WebRTC → RTSP → Local recording
3. **User Communication**: Clear status and progress indicators
4. **Graceful Degradation**: Reduced quality before complete failure

## Implementation Notes

### Development Priorities

1. **Phase 1**: Core streaming functionality (Dashboard, Camera Settings)
2. **Phase 2**: Network diagnostics and monitoring
3. **Phase 3**: Advanced error handling and recovery
4. **Phase 4**: Performance optimization and industrial hardening

### Testing Requirements

1. **Network Conditions**: Various bandwidth and latency scenarios
2. **Device Orientation**: Landscape lock with rotation handling
3. **Thermal Testing**: Extended operation under load
4. **Glove Testing**: Actual industrial glove usability testing
5. **Lighting Conditions**: Bright/dim/variable lighting scenarios

### Handoff Documentation

This wireframe package includes:
- Detailed layout specifications with measurements
- Color and typography specifications
- Interaction flow documentation
- Technical implementation notes
- Accessibility requirements
- Performance considerations
- Testing scenarios

The designs prioritize industrial usability while maintaining the technical requirements for real-time video streaming and analytics integration with AWS services.