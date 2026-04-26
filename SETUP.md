# HeroSync — Local Setup

## Prerequisites
- Java 21
- Node.js 18+
- Docker + Docker Compose (for production)

## Local Development

1. Clone the repository
2. Copy environment template:
   cp .env.example .env
3. Fill in your real API keys in .env:
   - GROQ_API_KEY: Get free key at console.groq.com/keys
   - GEMINI_API_KEY: Get free key at aistudio.google.com
   - OPENAI_API_KEY: Get key at platform.openai.com
4. Run backend:
   cd backend && ./mvnw spring-boot:run
5. Run frontend:
   cd frontend/frontend-ui && npm install && npm run dev

## Production Deployment

1. Copy .env.example to .env on your server
2. Fill in production values in .env
3. Run: docker-compose up -d

## API Keys Required
| Key | Provider | Free Tier | Get It |
|-----|----------|-----------|--------|
| GROQ_API_KEY | Groq | Yes — no card | console.groq.com |
| GEMINI_API_KEY | Google | Yes — generous | aistudio.google.com |
| OPENAI_API_KEY | OpenAI | $5 trial | platform.openai.com |
