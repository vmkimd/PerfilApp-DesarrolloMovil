# PerfilApp – Registro, login y perfil con foto

Proyecto del curso **Desarrollo Móvil** – Universidad Da Vinci de Guatemala.

Aplicación Android que permite registrar un usuario, iniciar sesión y ver una pantalla de perfil.
Dentro del perfil el usuario puede **tomar una fotografía** o **elegir una imagen de la galería** como foto de perfil.
La foto **no se guarda en el teléfono ni en una carpeta del servidor**: se envía al backend y se almacena como `BLOB` dentro de la base de datos relacional.

## Estructura del repositorio

```
PerfilUsuario/
├── app-android/   Aplicación nativa Android (Kotlin, XML, MVVM)
└── backend/       API REST (Node.js, Express, SQLite, JWT)
```

## Tecnologías

| Parte | Tecnología |
|---|---|
| App | Kotlin, XML Layouts, ViewBinding, MVVM (ViewModel + LiveData), Corrutinas, Retrofit, OkHttp |
| Backend | Node.js 22+, Express, `node:sqlite`, JWT (jsonwebtoken), bcryptjs, Multer (memoryStorage) |
| Base de datos | SQLite (relacional), tabla `usuarios` con la foto en una columna `BLOB` |

## Endpoints

| Método | Ruta | Descripción | Token |
|---|---|---|---|
| POST | `/api/auth/registro` | Registra un usuario (`nombre`, `correo`, `password`) | No |
| POST | `/api/auth/login` | Inicia sesión y devuelve el token JWT | No |
| GET | `/api/perfil` | Devuelve los datos del usuario | Sí |
| PUT | `/api/perfil/foto` | Sube la foto de perfil (multipart, campo `foto`) | Sí |
| GET | `/api/perfil/foto` | Devuelve la foto de perfil guardada en la BD | Sí |

## Cómo ejecutar

### Backend
```bash
cd backend
npm install
npm start
```
El servidor queda en el puerto `3000`. En Windows también se puede usar `iniciar-backend.bat`.

### App
1. Abrir la carpeta `app-android` en Android Studio.
2. Conectar el teléfono por cable USB con la **depuración USB** activada.
3. Ejecutar `conectar-telefono.bat` (hace `adb reverse tcp:3000 tcp:3000`, así el teléfono llega al backend por el cable usando `http://127.0.0.1:3000/`).
4. Presionar **Run** en Android Studio.

> Para usar Wi-Fi en lugar del cable, cambiar `BASE_URL` en `RetrofitClient.kt` por la IP de la computadora.
