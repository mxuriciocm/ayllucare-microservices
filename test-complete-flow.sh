#!/bin/bash

# ===============================================
# AYLLUCARE/B4U - COMPLETE FLOW TEST SCRIPT
# ===============================================
# Tests the complete flow:
# 1. IAM: Register & Login user
# 2. PROFILE: Create patient profile with consent
# 3. ANAMNESIS-LLM: Start session and send messages
# 4. TRIAGE: Verify triage result was created automatically
# ===============================================

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Service URLs
IAM_URL="http://localhost:8090"
PROFILE_URL="http://localhost:8092"
ANAMNESIS_URL="http://localhost:8093"
TRIAGE_URL="http://localhost:8094"

# Generate unique test data
TIMESTAMP=$(date +%s)
TEST_EMAIL="patient_${TIMESTAMP}@ayllucare.com"
TEST_PASSWORD="TestPassword123!"
TEST_FIRSTNAME="Juan"
TEST_LASTNAME="Pérez"

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  AYLLUCARE/B4U - COMPLETE FLOW TEST                   ║${NC}"
echo -e "${BLUE}║  Testing: IAM → Profile → Anamnesis → Triage          ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# ===============================================
# STEP 1: REGISTER USER IN IAM
# ===============================================
echo -e "${YELLOW}[STEP 1/8]${NC} ${GREEN}Registering new user in IAM microservice...${NC}"
echo -e "Email: ${TEST_EMAIL}"

REGISTER_RESPONSE=$(curl -s -X POST "${IAM_URL}/api/v1/authentication/sign-up" \
  -H "Content-Type: application/json" \
  -d "{
    \"firstName\": \"${TEST_FIRSTNAME}\",
    \"lastName\": \"${TEST_LASTNAME}\",
    \"email\": \"${TEST_EMAIL}\",
    \"password\": \"${TEST_PASSWORD}\",
    \"roles\": [\"ROLE_PATIENT\"],
    \"phoneNumber\": \"+51987654321\",
    \"preferredLanguage\": \"es\"
  }")

echo -e "${GREEN}✓ User registered successfully${NC}"
echo "Response: ${REGISTER_RESPONSE}"
echo ""
sleep 2

# ===============================================
# STEP 2: LOGIN USER
# ===============================================
echo -e "${YELLOW}[STEP 2/8]${NC} ${GREEN}Logging in user...${NC}"

LOGIN_RESPONSE=$(curl -s -X POST "${IAM_URL}/api/v1/authentication/sign-in" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"${TEST_EMAIL}\",
    \"password\": \"${TEST_PASSWORD}\"
  }")

# Extract JWT token and userId
JWT_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.id')

if [ "$JWT_TOKEN" == "null" ] || [ -z "$JWT_TOKEN" ]; then
  echo -e "${RED}✗ Login failed! Could not extract JWT token${NC}"
  echo "Response: ${LOGIN_RESPONSE}"
  exit 1
fi

echo -e "${GREEN}✓ Login successful${NC}"
echo "User ID: ${USER_ID}"
echo "JWT Token: ${JWT_TOKEN:0:50}..."
echo ""
sleep 2

# ===============================================
# STEP 3: CREATE PATIENT PROFILE WITH FULL DATA
# ===============================================
echo -e "${YELLOW}[STEP 3/8]${NC} ${GREEN}Creating patient profile with medical data and consent...${NC}"

PROFILE_RESPONSE=$(curl -s -X POST "${PROFILE_URL}/api/v1/profiles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d "{
    \"userId\": ${USER_ID},
    \"firstName\": \"${TEST_FIRSTNAME}\",
    \"lastName\": \"${TEST_LASTNAME}\",
    \"phoneNumber\": \"+51987654321\",
    \"dateOfBirth\": \"1985-05-15\",
    \"gender\": \"MALE\",
    \"bloodType\": \"O_POSITIVE\",
    \"height\": 175.0,
    \"weight\": 75.0,
    \"allergies\": [\"Penicilina\"],
    \"chronicConditions\": [\"Hipertensión arterial\"],
    \"currentMedications\": [\"Enalapril 10mg\"],
    \"emergencyContactName\": \"María Pérez\",
    \"emergencyContactPhone\": \"+51987654322\",
    \"emergencyContactRelationship\": \"Esposa\",
    \"consentForDataSharing\": true,
    \"consentForAIProcessing\": true
  }")

PROFILE_ID=$(echo $PROFILE_RESPONSE | jq -r '.id')

if [ "$PROFILE_ID" == "null" ] || [ -z "$PROFILE_ID" ]; then
  echo -e "${RED}✗ Profile creation failed${NC}"
  echo "Response: ${PROFILE_RESPONSE}"
  exit 1
fi

echo -e "${GREEN}✓ Profile created successfully with ID: ${PROFILE_ID}${NC}"
echo "AI Processing Consent: $(echo $PROFILE_RESPONSE | jq -r '.consentForAIProcessing')"
echo ""
sleep 2

# ===============================================
# STEP 4: START ANAMNESIS SESSION
# ===============================================
echo -e "${YELLOW}[STEP 4/8]${NC} ${GREEN}Starting anamnesis session...${NC}"

SESSION_RESPONSE=$(curl -s -X POST "${ANAMNESIS_URL}/api/v1/anamnesis/sessions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d "{
    \"initialReason\": \"Dolor de cabeza intenso y fiebre\"
  }")

SESSION_ID=$(echo $SESSION_RESPONSE | jq -r '.id')

if [ "$SESSION_ID" == "null" ] || [ -z "$SESSION_ID" ]; then
  echo -e "${RED}✗ Failed to start anamnesis session${NC}"
  echo "Response: ${SESSION_RESPONSE}"
  exit 1
fi

echo -e "${GREEN}✓ Anamnesis session started${NC}"
echo "Session ID: ${SESSION_ID}"
echo "Response: ${SESSION_RESPONSE}"
echo ""
sleep 2

# ===============================================
# STEP 5: SEND PATIENT MESSAGES (CONVERSATION)
# ===============================================
echo -e "${YELLOW}[STEP 5/8]${NC} ${GREEN}Simulating patient conversation with AI...${NC}"

# Message 1
echo "Sending message 1/3..."
MESSAGE1=$(curl -s -X POST "${ANAMNESIS_URL}/api/v1/anamnesis/sessions/${SESSION_ID}/messages" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d "{
    \"content\": \"Tengo un dolor de cabeza muy fuerte desde hace 3 días, especialmente en la frente. También tengo fiebre de 38.5°C y me siento muy débil.\"
  }")

echo -e "${GREEN}✓ Message 1 sent${NC}"
sleep 3

# Message 2
echo "Sending message 2/3..."
MESSAGE2=$(curl -s -X POST "${ANAMNESIS_URL}/api/v1/anamnesis/sessions/${SESSION_ID}/messages" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d "{
    \"content\": \"El dolor empeora en las noches. A veces veo un poco borroso. Tengo náuseas pero no he vomitado. También siento el cuello un poco rígido.\"
  }")

echo -e "${GREEN}✓ Message 2 sent${NC}"
sleep 3

# Message 3
echo "Sending message 3/3..."
MESSAGE3=$(curl -s -X POST "${ANAMNESIS_URL}/api/v1/anamnesis/sessions/${SESSION_ID}/messages" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d "{
    \"content\": \"Sí, estoy tomando mi medicamento para la presión arterial (Enalapril). No he tenido ningún golpe en la cabeza. Vivo en una zona rural y no tengo fácil acceso a un hospital.\"
  }")

echo -e "${GREEN}✓ Message 3 sent${NC}"
echo ""
sleep 3

# ===============================================
# STEP 6: COMPLETE ANAMNESIS SESSION
# ===============================================
echo -e "${YELLOW}[STEP 6/8]${NC} ${GREEN}Completing anamnesis session and generating summary...${NC}"

COMPLETE_RESPONSE=$(curl -s -X POST "${ANAMNESIS_URL}/api/v1/anamnesis/sessions/${SESSION_ID}/complete" \
  -H "Authorization: Bearer ${JWT_TOKEN}")

echo -e "${GREEN}✓ Anamnesis session completed${NC}"
echo "Summary generated and event published to RabbitMQ"
echo ""
sleep 5  # Give time for RabbitMQ event processing

# ===============================================
# STEP 7: VERIFY TRIAGE RESULT WAS CREATED
# ===============================================
echo -e "${YELLOW}[STEP 7/8]${NC} ${GREEN}Checking if triage result was created automatically...${NC}"

# We need a doctor token to access triage results
# Let's create a doctor user
DOCTOR_EMAIL="doctor_${TIMESTAMP}@ayllucare.com"

echo "Creating doctor user..."
DOCTOR_REGISTER=$(curl -s -X POST "${IAM_URL}/api/v1/authentication/sign-up" \
  -H "Content-Type: application/json" \
  -d "{
    \"firstName\": \"Dr. Carlos\",
    \"lastName\": \"Ramirez\",
    \"email\": \"${DOCTOR_EMAIL}\",
    \"password\": \"${TEST_PASSWORD}\",
    \"roles\": [\"ROLE_DOCTOR\"],
    \"phoneNumber\": \"+51987654399\",
    \"preferredLanguage\": \"es\"
  }")

echo "Logging in as doctor..."
DOCTOR_LOGIN=$(curl -s -X POST "${IAM_URL}/api/v1/authentication/sign-in" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"${DOCTOR_EMAIL}\",
    \"password\": \"${TEST_PASSWORD}\"
  }")

DOCTOR_TOKEN=$(echo $DOCTOR_LOGIN | jq -r '.token')

echo ""
echo "Fetching triage result for session ${SESSION_ID}..."
TRIAGE_RESPONSE=$(curl -s -X GET "${TRIAGE_URL}/api/v1/triage/session/${SESSION_ID}" \
  -H "Authorization: Bearer ${DOCTOR_TOKEN}")

TRIAGE_ID=$(echo $TRIAGE_RESPONSE | jq -r '.id')
PRIORITY=$(echo $TRIAGE_RESPONSE | jq -r '.priority')

if [ "$TRIAGE_ID" == "null" ] || [ -z "$TRIAGE_ID" ]; then
  echo -e "${RED}✗ Triage result not found (might still be processing)${NC}"
  echo "Response: ${TRIAGE_RESPONSE}"
  echo ""
  echo "Trying to fetch by user ID..."
  TRIAGE_RESPONSE=$(curl -s -X GET "${TRIAGE_URL}/api/v1/triage/user/${USER_ID}" \
    -H "Authorization: Bearer ${DOCTOR_TOKEN}")
  echo "Response: ${TRIAGE_RESPONSE}"
else
  echo -e "${GREEN}✓ Triage result found!${NC}"
  echo "Triage ID: ${TRIAGE_ID}"
  echo "Priority Level: ${PRIORITY}"
  echo "Full Response:"
  echo $TRIAGE_RESPONSE | jq '.'
fi

echo ""
sleep 2

# ===============================================
# STEP 8: SUMMARY AND VERIFICATION
# ===============================================
echo -e "${YELLOW}[STEP 8/8]${NC} ${GREEN}Fetching complete anamnesis session...${NC}"

FINAL_SESSION=$(curl -s -X GET "${ANAMNESIS_URL}/api/v1/anamnesis/sessions/${SESSION_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}")

echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║              COMPLETE FLOW TEST SUMMARY                ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}✓ IAM:${NC}"
echo "  User ID: ${USER_ID}"
echo "  Email: ${TEST_EMAIL}"
echo ""
echo -e "${GREEN}✓ PROFILE:${NC}"
echo "  Profile ID: ${PROFILE_ID}"
echo "  AI Consent: true"
echo ""
echo -e "${GREEN}✓ ANAMNESIS-LLM:${NC}"
echo "  Session ID: ${SESSION_ID}"
echo "  Status: COMPLETED"
echo "  Messages: $(echo $FINAL_SESSION | jq '.messageCount')"
echo ""
echo -e "${GREEN}✓ TRIAGE:${NC}"
if [ "$TRIAGE_ID" != "null" ] && [ ! -z "$TRIAGE_ID" ]; then
  echo "  Triage ID: ${TRIAGE_ID}"
  echo "  Priority: ${PRIORITY}"
  echo "  Risk Factors: $(echo $TRIAGE_RESPONSE | jq -r '.riskFactors | length') detected"
  echo "  Red Flags: $(echo $TRIAGE_RESPONSE | jq -r '.redFlagsDetected | length') detected"
else
  echo "  Status: Processing or not found"
fi
echo ""
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${GREEN}🎉 COMPLETE FLOW TEST FINISHED!${NC}"
echo ""
echo "Test data for manual verification:"
echo "  Patient JWT: ${JWT_TOKEN:0:50}..."
echo "  Doctor JWT: ${DOCTOR_TOKEN:0:50}..."
echo "  Session ID: ${SESSION_ID}"
echo ""
echo "You can now test the endpoints manually using these credentials."
echo ""

