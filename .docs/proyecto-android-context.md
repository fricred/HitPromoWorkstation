# Proyecto Android - Hit Promotional Products
## Intelligent Workstation & Production Visibility Platform (PoC Implementation)

### 📋 Resumen Ejecutivo

Desarrollo de una aplicación Android para tablets Samsung SM-X210 que implementa streaming de video en tiempo real con análisis de detección de objetos usando AWS Fargate + OpenVINO, con dos opciones de arquitectura validadas.

---

## 🎯 Objetivos del PoC

### Metas Principales:
- **10 tablets (SM-X210)** streaming video en vivo a AWS
- **Detección de objetos en tiempo real** (OpenVINO/DL Streamer) en cada stream via Fargate
- **UI web ligera** para: listar dispositivos, ver streams en vivo, ver detecciones, start/stop streams, configurar cámara

### Criterios de Aceptación:
- ✅ Cada dispositivo visible en UI con estado online/offline
- ✅ Vista en vivo funcional (Opción A: WebRTC; Opción B: RTSP→KVS)
- ✅ Analytics muestran conteos de detección y bounding boxes por stream
- ✅ Sistema sostiene 10 streams concurrentes con CPU/memoria estable y latencia predecible

---

## 🏗️ Arquitecturas Técnicas (Dos Opciones)

### 🅰️ Opción A: Android → KVS (WebRTC) → Fargate

#### A1. Android Publisher App:
- **SDK:** KVS WebRTC Android SDK + Cognito
- **Video:** Camera2/CameraX + MediaCodec H.264 (1280×720 @ 15-30fps, GOP ≈ 2s)
- **Service:** Foreground service con camera/dataSync para Android 14+/SDK 34+
- **Resilience:** Auto-reconnect, Wi-Fi lock, exponential backoff, health pings

#### A2. Fargate Viewer + Analytics Workers:
```bash
# Pipeline GStreamer por worker:
appsrc is-live=true format=time do-timestamp=true ! h264parse ! avdec_h264 !
  videoconvert ! video/x-raw,format=NV12 !
  gvadetect model=/models/ssd.xml device=CPU inference-interval=2 !
  gvatrack tracking-type=short-term-imageless !
  queue ! appsink sync=false
```

#### A3. Web UI:
- **Stack:** React (Vite) + Amplify Auth + API Gateway + Lambda + DynamoDB
- **Live Player:** KVS WebRTC JS SDK (sub-second latency)
- **Features:** Device management, stream controls, detection overlays vía MQTT

### 🅱️ Opción B: Android RTSP → Fargate → KVS (HLS/DASH)

#### B1. Android RTSP App:
- **Server:** RTSP server library con H.264 encode
- **Transport:** TCP interleaved para robustez
- **Deployment:** Push a relay público (MediaMTX) para evitar NAT issues

#### B2. Fargate Ingest + Analytics + KVS:
```bash
# Pipeline dual-branch:
rtspsrc location=rtsp://<tablet>/stream protocols=tcp latency=200 !
rtph264depay ! h264parse ! tee name=t

# Branch 1: Analytics
t. ! queue ! avdec_h264 ! videoconvert ! video/x-raw,format=NV12 !
    gvadetect model=/models/ssd.xml device=CPU inference-interval=2 !
    gvatrack ! gvawatermark ! fakesink

# Branch 2: KVS Publishing
t. ! queue ! kvssink stream-name="tablet-01" storage-size=512 aws-region=us-east-1
```

#### B3. Web UI:
- **Live Player:** HLS.js/Shaka Player contra GetHLSStreamingSessionURL
- **Latency:** Multi-segundo pero amplia compatibilidad

### Funcionalidades Core Android:

#### 📹 Sistema de Streaming:
- **Resolución:** 1280×720 @ 15-30fps optimizada para streaming
- **Codec:** H.264 con GOP ≈ 2s para balance calidad/ancho de banda
- **Camera API:** Camera2/CameraX para control avanzado
- **Encoding:** MediaCodec H.264 hardware optimizado

#### 🔧 Service Management:
- **Foreground Service:** Cumple Android 14+/SDK 34+ requirements
- **Service Types:** camera + dataSync declarados correctamente
- **Notifications:** Persistent notification para streaming activo
- **Lifecycle:** Manejo robusto de background/foreground transitions

#### 🌐 Conectividad Robusta:
- **Auto-reconnect:** Lógica de reconexión automática en interrupciones de red
- **Wi-Fi Lock:** Previene pérdida de conexión por power management
- **Exponential Backoff:** Retry inteligente para evitar spam de conexiones
- **Health Pings:** Monitoreo continuo vía IoT MQTT o HTTPS

#### 🔄 Streaming Protocols:

**Opción A - WebRTC:**
- **Latency:** Sub-segundo (ideal para monitoreo en tiempo real)
- **SDK:** KVS WebRTC Android SDK integrado
- **Auth:** AWS Cognito para autenticación segura
- **NAT:** Funciona seamless a través de firewalls/NAT

**Opción B - RTSP:**
- **Server:** Biblioteca RTSP server embebida
- **Transport:** TCP interleaved para máxima confiabilidad
- **Relay:** Push a MediaMTX cloud relay para evitar NAT issues
- **Fallback:** Alternativamente VPN/LAN para acceso directo

---

## 🔄 Flujo de Datos

### Opción A - WebRTC Flow:
```
1. Android App Login → AWS Cognito Authentication
2. Tablet inicia KVS WebRTC stream → Signaling Channel
3. Fargate Viewer Worker se conecta como viewer
4. Streaming H.264 → Worker extrae video frames
5. GStreamer Pipeline → OpenVINO ML Inference
6. Detecciones → DynamoDB + IoT MQTT (live UI)
7. Snapshots opcionales → S3 storage
```

### Opción B - RTSP Flow:
```
1. Android App → Inicia RTSP Server (o push a MediaMTX)
2. Fargate Worker → rtspsrc se conecta vía TCP
3. Dual Pipeline Branch:
   - Branch 1: H.264 decode → ML Analytics → Results storage
   - Branch 2: H.264 passthrough → kvssink → KVS
4. UI consume via HLS/DASH → GetHLSStreamingSessionURL
```

### Analytics Processing:
```
Video Stream → avdec_h264 → videoconvert → NV12
↓
gvadetect (OpenVINO SSD model, inference-interval=2)
↓
gvatrack (short-term-imageless tracking)
↓
Detection Events (JSON) → DynamoDB per-stream tables
↓
Live Overlays → MQTT/WebSocket para UI real-time
```

---

## 🎛️ Interfaz de Usuario

### Android App (Tablet):
**Deliverable:** APK que al login publica a su signaling channel y muestra estado "streaming"

#### Pantallas Core:
1. **Login/Auth** - AWS Cognito authentication + device registration
2. **Streaming Control** - Start/Stop stream, status indicators
3. **Camera Settings** - Resolution, FPS, bitrate adjustment
4. **Network Status** - Connection health, retry controls
5. **Diagnostics** - Stream metrics, error logs

#### Features Clave:
- **Status Display:** Online/Offline, streaming state, network quality
- **Stream Controls:** One-tap start/stop, emergency disconnect
- **Health Monitoring:** CPU temp, battery, network latency
- **Resilience UI:** Auto-reconnect status, retry progress

### Web UI (Dashboard):
**Stack:** React (Vite) + Amplify Auth + API Gateway + Lambda + DynamoDB

#### Páginas Principales:
1. **Device Overview** - Lista de 10 tablets con estados online/offline
2. **Live Streams** - Grid view de streams activos con detecciones
3. **Analytics** - Conteos por stream, bounding boxes, métricas histórica
4. **Device Management** - Configuración remota de cámaras por tablet
5. **System Health** - CPU/memoria Fargate, latencia, error rates

#### Live Viewing:
- **WebRTC Option:** Sub-second latency via KVS WebRTC JS SDK
- **HLS/DASH Option:** Multi-second latency, mayor compatibilidad
- **Detection Overlays:** Real-time bounding boxes vía MQTT/WebSocket
- **Stream Controls:** Remote start/stop, camera parameter tuning

---

## ☁️ Infraestructura AWS

### Servicios Core:
- **Amazon Fargate:** Workers containerizados para video analytics (1 worker por stream)
- **Amazon KVS:** Kinesis Video Streams para ingesta y playback
- **Amazon DynamoDB:** Almacenamiento de detection events per-stream
- **Amazon S3:** Storage de snapshots/clips de video opcionales
- **AWS Cognito:** Autenticación de tablets y web UI
- **IoT MQTT/API Gateway:** Real-time messaging para live overlays

### Fargate Container Architecture:

#### Container Specs:
- **Base Image:** Ubuntu/Amazon Linux con GStreamer + OpenVINO
- **Dependencies:**
  - KVS WebRTC C SDK (para opción A)
  - GStreamer core + plugins (avdec_h264, gvadetect, gvatrack)
  - OpenVINO runtime + SSD detection model
- **Resource Allocation:** CPU/memoria optimizada para 10 streams concurrentes

#### Processing Model:
```
1 Fargate Worker = 1 Video Stream Processing
- WebRTC Viewer connection (Opción A)
- RTSP Client connection (Opción B)
- Real-time ML inference via OpenVINO
- Results output to DynamoDB + MQTT
```

### Scalability Considerations:
- **Current:** Fargate CPU-only (inference-interval=2 para optimización)
- **Future:** Migrate a ECS on EC2 GPU instances para modelos más pesados
- **Monitoring:** CloudWatch para CPU/memoria tracking, X-Ray para tracing

---

## 📈 PoC Implementation

### Fase Única - Real-time Video Analytics PoC:
**Timeline:** 4 semanas de desarrollo intensivo
**Scope:** Validar ambas arquitecturas (WebRTC vs RTSP) simultáneamente

#### Semana 1: Environment Setup & Core Build
- Provisión de infraestructura AWS (Fargate, KVS, DynamoDB)
- Setup de contenedores con GStreamer + OpenVINO
- Android app base con KVS WebRTC SDK integration
- Core authentication flow con Cognito

#### Semana 2: Core Implementation & Internal QA
- Android streaming funcional (ambas opciones A y B)
- Fargate workers procesando video + ML inference
- Web UI básica para device management
- Pipeline de detección calibrado

#### Semana 3: Client Review & Refinement
- Demo completo de 10 streams concurrentes
- Live view funcional (WebRTC sub-second + HLS/DASH)
- Detection overlays en tiempo real
- Performance tuning basado en feedback

#### Semana 4: Final Testing & Handoff
- Stress testing de estabilidad con 10 streams
- Documentación completa + código handoff
- Logs/métricas que demuestren baseline para siguiente fase
- Training session para equipo técnico

---

## 🔧 Configuración de Desarrollo

### Requisitos del Emulador Android:
```
Device: Samsung Galaxy Tab A9+ (SM-X210)
Screen: 11.0" - 1920x1200 (206 dpi)
RAM: 4GB
Android: API Level 35 (Android 15)
Storage: 100GB internal + 512MB expanded
CPU Cores: 4 (para testing de performance)
Graphics: Hardware GLES 2.0
```

### Dependencies Android:
- **Core:** Java/Kotlin native Android
- **Streaming:** KVS WebRTC Android SDK + RTSP library
- **Camera:** Camera2/CameraX APIs
- **Encoding:** MediaCodec H.264 hardware encoder
- **Auth:** AWS SDK for Android (Cognito)
- **Networking:** OkHttp/Retrofit para API calls

---

## ⚠️ Riesgos y Mitigaciones

### Limitaciones Técnicas:
- **Fargate CPU-only:** Usar modelos lightweight, throttle inference-interval, migrar a EC2 GPU si necesario
- **RTSP NAT Issues:** Preferir WebRTC (Opción A), o usar MediaMTX relay para RTSP
- **Android Lifecycle:** Foreground service + adaptive bitrate/fps, monitoreo de temperatura
- **Latency Trade-offs:** WebRTC sub-segundo vs HLS/DASH multi-segundo

### Estrategias de Mitigación:
- **Thermal Management:** Auto-reduce FPS/resolution cuando CPU temp > threshold
- **Network Resilience:** Exponential backoff + circuit breaker patterns
- **Fallback Paths:** RTSP fallback si WebRTC falla, local caching durante disconnects
- **Resource Monitoring:** Real-time CPU/memoria tracking con alertas

---

## 🎯 Deliverables del PoC

### Funcionalidad Demostrada:
- ✅ **10 tablets streaming** video estable a AWS
- ✅ **Real-time object detection** con OpenVINO en Fargate
- ✅ **Live view UI** con ambas opciones (WebRTC + HLS/DASH)
- ✅ **Detection analytics** con conteos y bounding boxes
- ✅ **System stability** con CPU/memoria predecible

### Entregables Técnicos:
- ✅ **Android APK** funcional para SM-X210 tablets
- ✅ **Web Dashboard** para monitoring y control
- ✅ **Fargate containers** dockerizados con analytics pipeline
- ✅ **Infrastructure as Code** para deployment reproducible
- ✅ **Performance baselines** para escalamiento futuro

### Métricas de Éxito:
- **Concurrent Streams:** 10 streams estables simultáneos
- **Detection Latency:** <2 segundos end-to-end
- **Video Latency:** <1s (WebRTC) / <5s (HLS)
- **System Uptime:** >99% durante testing period
- **Resource Utilization:** CPU/memoria dentro de límites Fargate

---

*PoC desarrollado con RevStar (AWS Advanced Tier Partner)*