# 📘 AYLLUCARE - Documentación Completa de API

**Versión:** 1.0  
**Base URL (Producción):** `http://18.116.14.204:8080`  
**Base URL (Local):** `http://localhost:8080`

---

## 📑 Índice

1. [Autenticación](#1-autenticación-iam-service)
2. [Gestión de Usuarios](#2-gestión-de-usuarios)
3. [Perfiles de Pacientes](#3-perfiles-de-pacientes-profiles-service)
4. [Sesiones de Anamnesis (IA)](#4-sesiones-de-anamnesis-anamnesis-service)
5. [Triage Médico](#5-triage-médico-triage-service)
6. [Gestión de Casos](#6-gestión-de-casos-casedesk-service)
7. [Códigos de Estado](#códigos-de-estado-http)
8. [Flujo Completo](#flujo-completo-de-uso)

---

## 🔐 Autenticación

Todos los endpoints (excepto registro y login) requieren un **JWT Token** en el header:

```
Authorization: Bearer <tu-token-jwt>
```

---

## 1. Autenticación (IAM Service)

### 1.1 Registrar Usuario

**Endpoint:** `POST /api/v1/authentication/sign-up`  
**Descripción:** Crea una nueva cuenta de usuario en el sistema.  
**Autenticación:** No requerida  
**Puerto directo:** `8090` (si no usas Gateway)

#### Request Body:
```json
{
  "email": "paciente@example.com",
  "password": "SecurePass123!",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "PATIENT",
  "phoneNumber": "+51987654321",
  "preferredLanguage": "es"
}
```

#### Campos:
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `email` | string | ✅ | Email único del usuario |
| `password` | string | ✅ | Contraseña (mín. 8 caracteres) |
| `firstName` | string | ✅ | Nombre del usuario |
| `lastName` | string | ✅ | Apellido del usuario |
| `role` | string | ✅ | `PATIENT`, `DOCTOR`, `ADMIN` |
| `phoneNumber` | string | ❌ | Teléfono con código país |
| `preferredLanguage` | string | ❌ | `es`, `en`, `qu` (por defecto: `es`) |

#### Response (201 Created):
```json
{
  "id": 1,
  "email": "paciente@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roles": ["ROLE_PATIENT"],
  "status": "ACTIVE",
  "phoneNumber": "+51987654321",
  "preferredLanguage": "es"
}
```

#### Errores comunes:
- `400 Bad Request`: Email ya existe o datos inválidos
- `400 Bad Request`: Contraseña no cumple requisitos

---

### 1.2 Iniciar Sesión

**Endpoint:** `POST /api/v1/authentication/sign-in`  
**Descripción:** Autentica un usuario y devuelve un JWT token.  
**Autenticación:** No requerida

#### Request Body:
```json
{
  "email": "paciente@example.com",
  "password": "SecurePass123!"
}
```

#### Response (200 OK):
```json
{
  "id": 1,
  "email": "paciente@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roles": ["ROLE_PATIENT"],
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYWNpZW50ZUBle..."
}
```

#### Errores comunes:
- `404 Not Found`: Usuario no existe o credenciales incorrectas

---

## 2. Gestión de Usuarios

### 2.1 Obtener Todos los Usuarios

**Endpoint:** `GET /api/v1/users`  
**Descripción:** Lista todos los usuarios del sistema.  
**Autenticación:** ✅ Requerida (cualquier rol)

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "email": "paciente@example.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "roles": ["ROLE_PATIENT"],
    "status": "ACTIVE",
    "phoneNumber": "+51987654321",
    "preferredLanguage": "es"
  },
  {
    "id": 2,
    "email": "doctor@example.com",
    "firstName": "María",
    "lastName": "García",
    "roles": ["ROLE_DOCTOR"],
    "status": "ACTIVE",
    "phoneNumber": "+51987654322",
    "preferredLanguage": "es"
  }
]
```

---

### 2.2 Obtener Usuario por ID

**Endpoint:** `GET /api/v1/users/{userId}`  
**Descripción:** Obtiene información de un usuario específico.  
**Autenticación:** ✅ Requerida

#### Parámetros:
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `userId` | Long | ID del usuario a consultar |

#### Ejemplo: `GET /api/v1/users/1`

#### Response (200 OK):
```json
{
  "id": 1,
  "email": "paciente@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roles": ["ROLE_PATIENT"],
  "status": "ACTIVE",
  "phoneNumber": "+51987654321",
  "preferredLanguage": "es"
}
```

#### Errores:
- `404 Not Found`: Usuario no existe

---

### 2.3 Eliminar Usuario

**Endpoint:** `DELETE /api/v1/users/{userId}`  
**Descripción:** Elimina un usuario del sistema.  
**Autenticación:** ✅ Requerida (ADMIN o el mismo usuario)

#### Response (204 No Content):
Sin contenido en respuesta exitosa.

#### Errores:
- `404 Not Found`: Usuario no existe
- `400 Bad Request`: Error al eliminar usuario

---

## 3. Perfiles de Pacientes (Profiles Service)

### 3.1 Crear Perfil de Paciente

**Endpoint:** `POST /api/v1/profiles`  
**Descripción:** Crea un perfil médico completo para un paciente. El `userId` se extrae automáticamente del JWT token.  
**Autenticación:** ✅ Requerida (PATIENT)  
**Puerto directo:** `8092`

#### Request Body:
```json
{
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "address": "Av. Principal 123, Lima",
  "emergencyContact": {
    "name": "María Pérez",
    "relationship": "Esposa",
    "phone": "+51987654322"
  },
  "medicalHistory": {
    "chronicConditions": ["Diabetes tipo 2", "Hipertensión"],
    "previousSurgeries": ["Apendicectomía (2010)"],
    "familyHistory": ["Padre: diabetes", "Madre: hipertensión"],
    "currentMedications": [
      {
        "name": "Metformina",
        "dosage": "850mg",
        "frequency": "2 veces al día"
      },
      {
        "name": "Enalapril",
        "dosage": "10mg",
        "frequency": "1 vez al día"
      }
    ],
    "allergies": ["Penicilina", "Mariscos"],
    "immunizations": [
      {
        "vaccine": "COVID-19",
        "date": "2023-01-15",
        "booster": true
      }
    ]
  },
  "lifestyle": {
    "smokingStatus": "NEVER",
    "alcoholConsumption": "OCCASIONAL",
    "exerciseFrequency": "2-3 veces por semana",
    "diet": "Balanceada, baja en azúcares"
  },
  "insuranceInfo": {
    "provider": "EsSalud",
    "policyNumber": "ES-12345678",
    "coverageType": "COMPLETO",
    "validUntil": "2025-12-31"
  },
  "aiProcessingConsent": true,
  "dataShareConsent": true
}
```

#### Campos Principales:
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `dateOfBirth` | date | ✅ | Fecha de nacimiento (YYYY-MM-DD) |
| `gender` | string | ✅ | `MALE`, `FEMALE`, `OTHER`, `PREFER_NOT_TO_SAY` |
| `address` | string | ❌ | Dirección completa |
| `emergencyContact` | object | ✅ | Contacto de emergencia |
| `medicalHistory` | object | ❌ | Historia médica completa |
| `lifestyle` | object | ❌ | Información de estilo de vida |
| `insuranceInfo` | object | ❌ | Información de seguro |
| `aiProcessingConsent` | boolean | ✅ | Consentimiento para procesamiento con IA |
| `dataShareConsent` | boolean | ✅ | Consentimiento para compartir datos |

#### Response (201 Created):
```json
{
  "id": 1,
  "userId": 5,
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "address": "Av. Principal 123, Lima",
  "emergencyContact": {
    "name": "María Pérez",
    "relationship": "Esposa",
    "phone": "+51987654322"
  },
  "medicalHistory": {
    "chronicConditions": ["Diabetes tipo 2", "Hipertensión"],
    "currentMedications": [...],
    "allergies": ["Penicilina", "Mariscos"]
  },
  "aiProcessingConsent": true,
  "dataShareConsent": true,
  "createdAt": "2025-12-03T10:00:00Z",
  "updatedAt": "2025-12-03T10:00:00Z"
}
```

#### Errores:
- `401 Unauthorized`: Token inválido o no proporcionado
- `409 Conflict`: Ya existe un perfil para este usuario
- `400 Bad Request`: Datos inválidos

---

### 3.2 Obtener Perfil por ID

**Endpoint:** `GET /api/v1/profiles/{profileId}`  
**Descripción:** Obtiene un perfil de paciente por su ID.  
**Autenticación:** ✅ Requerida

#### Response (200 OK):
Mismo formato que la respuesta de creación.

---

### 3.3 Obtener Perfil por User ID

**Endpoint:** `GET /api/v1/profiles/user/{userId}`  
**Descripción:** Obtiene el perfil asociado a un usuario específico.  
**Autenticación:** ✅ Requerida

#### Ejemplo: `GET /api/v1/profiles/user/5`

---

### 3.4 Obtener Todos los Perfiles

**Endpoint:** `GET /api/v1/profiles`  
**Descripción:** Lista todos los perfiles de pacientes.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "userId": 5,
    "dateOfBirth": "1990-05-15",
    "gender": "MALE",
    ...
  }
]
```

---

### 3.5 Actualizar Perfil

**Endpoint:** `PATCH /api/v1/profiles/{profileId}`  
**Descripción:** Actualiza información del perfil. Todos los campos son opcionales.  
**Autenticación:** ✅ Requerida (PATIENT propietario)

#### Request Body (ejemplo parcial):
```json
{
  "address": "Nueva dirección",
  "medicalHistory": {
    "currentMedications": [
      {
        "name": "Aspirina",
        "dosage": "100mg",
        "frequency": "1 vez al día"
      }
    ]
  }
}
```

---

### 3.6 Firmar Consentimiento

**Endpoint:** `POST /api/v1/profiles/{profileId}/consent`  
**Descripción:** El paciente firma el consentimiento para procesamiento de datos con IA.  
**Autenticación:** ✅ Requerida (PATIENT)

#### Response (200 OK):
```json
{
  "id": 1,
  "aiProcessingConsent": true,
  "consentSignedAt": "2025-12-03T10:30:00Z",
  ...
}
```

---

## 4. Sesiones de Anamnesis (Anamnesis Service)

### 4.1 Iniciar Sesión de Anamnesis

**Endpoint:** `POST /api/v1/anamnesis/sessions`  
**Descripción:** Inicia una nueva sesión de anamnesis con IA. El sistema usa un dataset médico para hacer preguntas contextuales.  
**Autenticación:** ✅ Requerida (PATIENT)  
**Puerto directo:** `8093`

#### Request Body:
```json
{
  "initialReason": "Dolor de cabeza intenso y fiebre"
}
```

#### Response (201 Created):
```json
{
  "id": 1,
  "userId": 5,
  "status": "CREATED",
  "initialReason": "Dolor de cabeza intenso y fiebre",
  "messageCount": 1,
  "summary": null,
  "createdAt": "2025-12-03T10:00:00Z",
  "updatedAt": "2025-12-03T10:00:00Z"
}
```

#### Estados de sesión:
- `CREATED`: Sesión creada, esperando primer mensaje
- `IN_PROGRESS`: Conversación activa
- `COMPLETED`: Sesión finalizada con resumen generado

---

### 4.2 Enviar Mensaje a la Sesión

**Endpoint:** `POST /api/v1/anamnesis/sessions/{sessionId}/messages`  
**Descripción:** Envía un mensaje del paciente y recibe respuesta de la IA basada en el dataset médico.  
**Autenticación:** ✅ Requerida (PATIENT)

#### Request Body:
```json
{
  "content": "Tengo un dolor de cabeza muy fuerte desde hace 3 días, especialmente en un lado. También tengo fiebre de 38.5°C y me siento muy débil. La luz me molesta mucho."
}
```

#### Response (200 OK):
```json
{
  "session": {
    "id": 1,
    "userId": 5,
    "status": "IN_PROGRESS",
    "initialReason": "Dolor de cabeza intenso y fiebre",
    "messageCount": 3,
    "createdAt": "2025-12-03T10:00:00Z",
    "updatedAt": "2025-12-03T10:05:00Z"
  },
  "messages": [
    {
      "senderType": "SYSTEM",
      "content": "Sesión de anamnesis iniciada. Por favor, cuénteme el motivo de su consulta.",
      "timestamp": "2025-12-03T10:00:00Z"
    },
    {
      "senderType": "PATIENT",
      "content": "Tengo un dolor de cabeza muy fuerte desde hace 3 días...",
      "timestamp": "2025-12-03T10:05:00Z"
    },
    {
      "senderType": "ASSISTANT",
      "content": "Entiendo que tiene un dolor de cabeza intenso con fiebre. Basándome en sus síntomas:\n\n⚠️ SEÑALES DE ALARMA DETECTADAS:\n- Cefalea intensa + fiebre + fotofobia puede indicar meningitis\n- Rigidez de cuello es un síntoma crítico\n\nPREGUNTAS IMPORTANTES:\n1. ¿Tiene rigidez en el cuello?\n2. ¿Ha notado cambios en la visión?\n3. ¿Ha tenido náuseas o vómitos?",
      "timestamp": "2025-12-03T10:05:03Z"
    }
  ]
}
```

#### Cómo funciona la IA:
1. **Dataset médico:** La IA consulta un JSON con 31 condiciones médicas comunes
2. **Detección de síntomas:** Identifica síntomas clave (cefalea, fiebre, fotofobia)
3. **Red Flags:** Detecta señales de alarma críticas
4. **Preguntas contextuales:** Hace preguntas según el perfil del paciente
5. **Consideraciones culturales:** Adapta respuestas según ubicación (zona rural, acceso limitado)

---

### 4.3 Completar Sesión

**Endpoint:** `POST /api/v1/anamnesis/sessions/{sessionId}/complete`  
**Descripción:** Finaliza la sesión y genera un resumen estructurado con IA.  
**Autenticación:** ✅ Requerida (PATIENT, DOCTOR)

#### Response (200 OK):
```json
{
  "id": 1,
  "userId": 5,
  "status": "COMPLETED",
  "initialReason": "Dolor de cabeza intenso y fiebre",
  "messageCount": 7,
  "summary": {
    "chiefComplaint": "Cefalea intensa unilateral de 3 días de evolución, asociada a fiebre de 38.5°C",
    "historyOfPresentIllness": "Paciente refiere dolor de cabeza pulsátil, fotofobia, náuseas. Toma Enalapril para hipertensión. Vive en zona rural con acceso limitado a hospital.",
    "pastMedicalHistory": "Hipertensión arterial en tratamiento",
    "medications": ["Enalapril 10mg 1 vez al día"],
    "allergies": ["Penicilina"],
    "redFlags": [
      "Cefalea intensa + fiebre + fotofobia (posible meningitis)",
      "Rigidez de cuello",
      "Alteración visual",
      "Zona rural - acceso limitado a atención de emergencia"
    ],
    "additionalNotes": "REQUIERE EVALUACIÓN MÉDICA URGENTE. Sospecha de meningitis. Paciente en zona rural, coordinar traslado inmediato."
  },
  "createdAt": "2025-12-03T10:00:00Z",
  "updatedAt": "2025-12-03T10:15:00Z"
}
```

#### Evento automático:
Al completar la sesión, se publica un evento a **RabbitMQ** que:
1. Trigger automático a **Triage Service** para evaluar urgencia
2. Si es urgente, crea caso automáticamente en **CaseDesk**

---

### 4.4 Obtener Sesiones del Usuario

**Endpoint:** `GET /api/v1/anamnesis/sessions`  
**Descripción:** Lista todas las sesiones del usuario autenticado.  
**Autenticación:** ✅ Requerida (PATIENT)

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "userId": 5,
    "status": "COMPLETED",
    "initialReason": "Dolor de cabeza intenso y fiebre",
    "messageCount": 7,
    "createdAt": "2025-12-03T10:00:00Z"
  },
  {
    "id": 2,
    "userId": 5,
    "status": "IN_PROGRESS",
    "initialReason": "Tos persistente",
    "messageCount": 3,
    "createdAt": "2025-12-03T11:00:00Z"
  }
]
```

---

### 4.5 Obtener Detalle de Sesión

**Endpoint:** `GET /api/v1/anamnesis/sessions/{sessionId}`  
**Descripción:** Obtiene una sesión específica con todos los mensajes.  
**Autenticación:** ✅ Requerida (PATIENT propietario, DOCTOR, ADMIN)

#### Response (200 OK):
Mismo formato que la respuesta de "Enviar Mensaje".

---

### 4.6 Obtener Resumen de Sesión

**Endpoint:** `GET /api/v1/anamnesis/sessions/{sessionId}/summary`  
**Descripción:** Obtiene solo el resumen estructurado de una sesión completada.  
**Autenticación:** ✅ Requerida (PATIENT propietario, DOCTOR, ADMIN)

#### Response (200 OK):
```json
{
  "chiefComplaint": "Cefalea intensa unilateral de 3 días de evolución...",
  "historyOfPresentIllness": "...",
  "pastMedicalHistory": "...",
  "medications": ["..."],
  "allergies": ["..."],
  "redFlags": ["..."],
  "additionalNotes": "..."
}
```

---

## 5. Triage Médico (Triage Service)

### 5.1 Obtener Triage por User ID

**Endpoint:** `GET /api/v1/triage/user/{userId}`  
**Descripción:** Obtiene todos los resultados de triage de un paciente.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)  
**Puerto directo:** `8094`

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "userId": 5,
    "anamnesisSessionId": 1,
    "urgencyLevel": "URGENT",
    "priority": 1,
    "estimatedWaitTime": "0 minutos",
    "clinicalSummary": "Paciente con cefalea intensa, fiebre y fotofobia. Sospecha de meningitis.",
    "redFlags": [
      "Cefalea intensa + fiebre + fotofobia",
      "Rigidez de cuello",
      "Zona rural - acceso limitado"
    ],
    "recommendedSpecialty": "NEUROLOGIA",
    "recommendations": [
      "Evaluación médica inmediata",
      "Considerar traslado a centro de mayor complejidad",
      "Punción lumbar si se confirma sospecha de meningitis"
    ],
    "vitalSignsRequired": ["Temperatura", "Presión arterial", "Frecuencia cardíaca"],
    "createdAt": "2025-12-03T10:15:30Z"
  }
]
```

#### Niveles de urgencia:
- `IMMEDIATE`: Atención inmediata (minutos)
- `URGENT`: Atención urgente (< 1 hora)
- `SEMI_URGENT`: Atención en < 2 horas
- `NON_URGENT`: Atención en < 4 horas
- `ROUTINE`: Atención rutinaria

---

### 5.2 Obtener Triage por ID

**Endpoint:** `GET /api/v1/triage/{id}`  
**Descripción:** Obtiene un resultado de triage específico.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)

---

### 5.3 Obtener Triage por Session ID

**Endpoint:** `GET /api/v1/triage/session/{sessionId}`  
**Descripción:** Obtiene el triage generado desde una sesión de anamnesis específica.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)

---

### 5.4 Obtener Todos los Triages

**Endpoint:** `GET /api/v1/triage`  
**Descripción:** Lista todos los triages del sistema.  
**Autenticación:** ✅ Requerida (ADMIN)

---

## 6. Gestión de Casos (CaseDesk Service)

### 6.1 Obtener Mis Casos

**Endpoint:** `GET /api/v1/cases/my`  
**Descripción:** Obtiene los casos del usuario autenticado (paciente ve sus casos, doctor ve casos asignados).  
**Autenticación:** ✅ Requerida (PATIENT, DOCTOR)  
**Puerto directo:** `8095`

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "patientId": 5,
    "triageId": 1,
    "anamnesisSessionId": 1,
    "assignedDoctorId": null,
    "status": "OPEN",
    "priority": "URGENT",
    "title": "Sospecha de meningitis - Cefalea intensa + fiebre",
    "description": "Paciente con cefalea intensa unilateral de 3 días, fiebre 38.5°C, fotofobia y rigidez de cuello.",
    "requiredSpecialty": "NEUROLOGIA",
    "estimatedTimeToAttention": "0 minutos",
    "createdAt": "2025-12-03T10:15:35Z",
    "updatedAt": "2025-12-03T10:15:35Z"
  }
]
```

#### Estados de caso:
- `OPEN`: Caso abierto, sin asignar
- `ASSIGNED`: Asignado a un doctor
- `IN_PROGRESS`: Doctor atendiendo el caso
- `RESOLVED`: Caso resuelto
- `CLOSED`: Caso cerrado

---

### 6.2 Obtener Casos (con filtros)

**Endpoint:** `GET /api/v1/cases`  
**Descripción:** Obtiene casos con filtros opcionales.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)

#### Query Parameters:
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `status` | string | Filtrar por estado (`OPEN`, `ASSIGNED`, etc.) |
| `assignedToMe` | boolean | `true` para ver solo casos asignados a mí |

#### Ejemplos:
```
GET /api/v1/cases?status=OPEN
GET /api/v1/cases?assignedToMe=true
GET /api/v1/cases?status=ASSIGNED&assignedToMe=true
```

---

### 6.3 Obtener Caso por ID

**Endpoint:** `GET /api/v1/cases/{caseId}`  
**Descripción:** Obtiene detalle completo de un caso.  
**Autenticación:** ✅ Requerida (PATIENT propietario, DOCTOR, ADMIN)

#### Response (200 OK):
```json
{
  "id": 1,
  "patientId": 5,
  "triageId": 1,
  "anamnesisSessionId": 1,
  "assignedDoctorId": 2,
  "status": "IN_PROGRESS",
  "priority": "URGENT",
  "title": "Sospecha de meningitis - Cefalea intensa + fiebre",
  "description": "...",
  "requiredSpecialty": "NEUROLOGIA",
  "notes": [
    {
      "id": 1,
      "addedBy": 2,
      "content": "Paciente evaluado. Se solicitó TAC cerebral urgente.",
      "createdAt": "2025-12-03T11:00:00Z"
    }
  ],
  "timeline": [
    {
      "event": "CASE_CREATED",
      "performedBy": "SYSTEM",
      "timestamp": "2025-12-03T10:15:35Z"
    },
    {
      "event": "CASE_ASSIGNED",
      "performedBy": 2,
      "timestamp": "2025-12-03T10:30:00Z"
    }
  ],
  "createdAt": "2025-12-03T10:15:35Z",
  "updatedAt": "2025-12-03T11:00:00Z"
}
```

---

### 6.4 Asignar Caso

**Endpoint:** `PATCH /api/v1/cases/{caseId}/assign`  
**Descripción:** Asigna un caso a un doctor.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)

#### Request Body:
```json
{
  "doctorId": 2
}
```

**Nota:** Si no se proporciona `doctorId`, se asigna al doctor autenticado.

#### Response (200 OK):
```json
{
  "id": 1,
  "assignedDoctorId": 2,
  "status": "ASSIGNED",
  ...
}
```

---

### 6.5 Actualizar Estado del Caso

**Endpoint:** `PATCH /api/v1/cases/{caseId}/status`  
**Descripción:** Actualiza el estado de un caso.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)

#### Request Body:
```json
{
  "status": "IN_PROGRESS",
  "notes": "Paciente en evaluación. Se solicitaron exámenes."
}
```

---

### 6.6 Agregar Nota al Caso

**Endpoint:** `POST /api/v1/cases/{caseId}/notes`  
**Descripción:** Agrega una nota médica al caso.  
**Autenticación:** ✅ Requerida (DOCTOR, ADMIN)

#### Request Body:
```json
{
  "content": "Resultados de TAC: Sin signos de hemorragia. Se descarta meningitis bacteriana. Diagnóstico: Migraña severa. Tratamiento: Sumatriptán 50mg."
}
```

#### Response (200 OK):
Devuelve el caso completo con la nueva nota agregada.

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Solicitud exitosa |
| `201 Created` | Recurso creado exitosamente |
| `204 No Content` | Operación exitosa sin contenido |
| `400 Bad Request` | Datos inválidos en la solicitud |
| `401 Unauthorized` | Token no proporcionado o inválido |
| `403 Forbidden` | No tiene permisos para esta operación |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Conflicto (ej: email ya existe) |
| `500 Internal Server Error` | Error del servidor |

---

## Flujo Completo de Uso

### 📝 Escenario: Paciente con dolor de cabeza intenso

#### 1. Registro y Login
```bash
# 1.1 Registrar paciente
curl -X POST http://18.116.14.204:8080/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@example.com",
    "password": "SecurePass123!",
    "firstName": "Juan",
    "lastName": "Pérez",
    "role": "PATIENT",
    "phoneNumber": "+51987654321"
  }'

# 1.2 Login
curl -X POST http://18.116.14.204:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@example.com",
    "password": "SecurePass123!"
  }'

# Guardar el token recibido
TOKEN="eyJhbGciOiJIUzUxMiJ9..."
```

#### 2. Crear Perfil Médico
```bash
curl -X POST http://18.116.14.204:8080/api/v1/profiles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dateOfBirth": "1990-05-15",
    "gender": "MALE",
    "address": "Zona rural de Cajamarca",
    "emergencyContact": {
      "name": "María Pérez",
      "relationship": "Esposa",
      "phone": "+51987654322"
    },
    "medicalHistory": {
      "currentMedications": [
        {
          "name": "Enalapril",
          "dosage": "10mg",
          "frequency": "1 vez al día"
        }
      ],
      "allergies": ["Penicilina"]
    },
    "aiProcessingConsent": true,
    "dataShareConsent": true
  }'
```

#### 3. Iniciar Sesión de Anamnesis
```bash
curl -X POST http://18.116.14.204:8080/api/v1/anamnesis/sessions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "initialReason": "Dolor de cabeza intenso y fiebre"
  }'

# Guardar el sessionId
SESSION_ID=1
```

#### 4. Conversación con IA (Dataset médico)
```bash
# Mensaje 1
curl -X POST http://18.116.14.204:8080/api/v1/anamnesis/sessions/$SESSION_ID/messages \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Tengo un dolor de cabeza muy fuerte desde hace 3 días, especialmente en un lado. También tengo fiebre de 38.5°C y me siento muy débil. La luz me molesta mucho."
  }'

# La IA responde detectando síntomas de migraña/cefalea del dataset
# y pregunta sobre red flags (rigidez de cuello, alteración visual)

# Mensaje 2
curl -X POST http://18.116.14.204:8080/api/v1/anamnesis/sessions/$SESSION_ID/messages \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "El dolor es pulsátil y muy intenso. A veces veo un poco borroso. Tengo náuseas pero no he vomitado. También siento el cuello un poco rígido."
  }'

# La IA detecta RED FLAGS: rigidez cuello + cefalea + fiebre = posible meningitis

# Mensaje 3
curl -X POST http://18.116.14.204:8080/api/v1/anamnesis/sessions/$SESSION_ID/messages \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Sí, estoy tomando mi medicamento para la presión. No he tenido ningún golpe en la cabeza. Vivo en una zona rural y no tengo fácil acceso a un hospital."
  }'

# La IA considera el contexto: zona rural + síntomas graves
```

#### 5. Completar Sesión (genera resumen con IA)
```bash
curl -X POST http://18.116.14.204:8080/api/v1/anamnesis/sessions/$SESSION_ID/complete \
  -H "Authorization: Bearer $TOKEN"

# AUTOMÁTICAMENTE se disparan:
# - Evento a RabbitMQ: "AnamnesisSessionCompletedEvent"
# - Triage Service procesa el evento
# - Se crea evaluación de triage (URGENT)
# - Si es URGENT, CaseDesk crea caso automáticamente
```

#### 6. Doctor revisa casos urgentes
```bash
# Login como doctor
curl -X POST http://18.116.14.204:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@example.com",
    "password": "DoctorPass123!"
  }'

DOCTOR_TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# Ver casos abiertos urgentes
curl -X GET "http://18.116.14.204:8080/api/v1/cases?status=OPEN" \
  -H "Authorization: Bearer $DOCTOR_TOKEN"

# Asignar caso a sí mismo
curl -X PATCH http://18.116.14.204:8080/api/v1/cases/1/assign \
  -H "Authorization: Bearer $DOCTOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}'

# Ver detalle completo con historial de anamnesis
curl -X GET http://18.116.14.204:8080/api/v1/cases/1 \
  -H "Authorization: Bearer $DOCTOR_TOKEN"

# Agregar nota médica
curl -X POST http://18.116.14.204:8080/api/v1/cases/1/notes \
  -H "Authorization: Bearer $DOCTOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Paciente evaluado. Descartada meningitis. Diagnóstico: Migraña severa. Tratamiento: Sumatriptán 50mg + reposo."
  }'

# Resolver caso
curl -X PATCH http://18.116.14.204:8080/api/v1/cases/1/status \
  -H "Authorization: Bearer $DOCTOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "RESOLVED",
    "notes": "Paciente estable. Seguimiento en 48 horas."
  }'
```

---

## 🔍 Dataset Médico de la IA

La IA de anamnesis utiliza un JSON con **31 condiciones médicas** que incluye:

### Categorías:
1. **Cardiovascular**: Infarto, angina, hipertensión
2. **Respiratorio**: Asma, neumonía, EPOC
3. **Neurológico**: Migraña, ACV, meningitis
4. **Gastrointestinal**: Apendicitis, gastritis, hepatitis
5. **Musculoesquelético**: Fracturas, esguinces
6. **Dermatológico**: Celulitis, herpes zóster
7. **Endocrino**: Diabetes, hipotiroidismo
8. **Infeccioso**: COVID-19, dengue, malaria

### Para cada condición incluye:
- ✅ Síntomas principales
- ✅ Síntomas secundarios
- ✅ Red flags (señales de alarma)
- ✅ Factores de riesgo
- ✅ Preguntas específicas a realizar
- ✅ Exámenes recomendados
- ✅ Diagnósticos diferenciales
- ✅ Consideraciones culturales/geográficas

---

## 📊 Arquitectura de Microservicios

```
┌─────────────────┐
│   API Gateway   │ :8080
│  (Spring Cloud) │
└────────┬────────┘
         │
    ┌────┴─────────────────────────────────┐
    │                                      │
┌───▼────┐  ┌────────┐  ┌──────────┐  ┌──────────┐
│  IAM   │  │Profiles│  │ Anamnesis│  │  Triage  │
│  :8090 │  │  :8092 │  │   :8093  │  │  :8094   │
└───┬────┘  └───┬────┘  └─────┬────┘  └─────┬────┘
    │           │             │             │
    └───────────┴─────────────┴─────────────┴──────┐
                                                    │
                              ┌──────────────┐  ┌──▼────────┐
                              │   CaseDesk   │  │  RabbitMQ │
                              │    :8095     │  │   :5672   │
                              └──────────────┘  └───────────┘
                                     │
                              ┌──────▼────────┐
                              │   MySQL       │
                              │   :3306       │
                              └───────────────┘
```

---

## 🚀 URLs de Producción

| Servicio | URL |
|----------|-----|
| **Gateway (API principal)** | http://18.116.14.204:8080 |
| **Eureka Dashboard** | http://18.116.14.204:8761 |
| **RabbitMQ Management** | http://18.116.14.204:15672 |
| IAM Service | http://18.116.14.204:8090 |
| Profiles Service | http://18.116.14.204:8092 |
| Anamnesis Service | http://18.116.14.204:8093 |
| Triage Service | http://18.116.14.204:8094 |
| CaseDesk Service | http://18.116.14.204:8095 |

**Recomendación:** Usa siempre el **Gateway (8080)** en producción para balanceo de carga y seguridad.

---

## 📞 Soporte y Contacto

Para preguntas o problemas con la API, contacta al equipo de desarrollo de AylluCare.

**Versión:** 1.0  
**Última actualización:** Diciembre 3, 2025

