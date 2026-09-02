# portfolio-pulse

A full-stack application for tracking your investment portfolio.

## Local Development Setup

### Environment Variables

The backend uses `spring-dotenv` to automatically load environment variables from `backend/.env`. 

To set up your local environment:
1. Create a `backend/.env` file.
2. Add your secrets using `.env.example` as a reference:
   ```env
   JWT_SECRET=your_super_secret_jwt_key_that_is_at_least_256_bits_long
   ALPHA_VANTAGE_API_KEY=your_alpha_vantage_api_key_here
   ```
3. Run the backend normally via `.\mvnw.cmd spring-boot:run`. The secrets will be loaded automatically!

The frontend uses Vite, which also automatically loads its variables from `frontend/.env`.
