# API Tree Service - Render Deployment Guide

## 🚀 Render дээр Deploy хийх

### 1. GitHub Repository-г бэлтгэх

```bash
# Git repository-д бүх файлуудыг commit хийх
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### 2. Render дээр Web Service үүсгэх

1. **Render.com** дээр нэвтрэх
2. **"New +"** → **"Web Service"** сонгох
3. **GitHub repository** холбох
4. **Service settings** тохируулах:

#### Service Configuration:

- **Name**: `api-tree-service`
- **Environment**: `Java`
- **Build Command**: `mvn clean package -DskipTests`
- **Start Command**: `java -Xmx512m -Xms256m -jar target/*.jar`
- **Health Check Path**: `/actuator/health`

#### Environment Variables:

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=10000
JWT_SECRET=<generate-secure-secret>
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=86400000
JWT_ISSUER=api-tree-service
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=Authorization,Content-Type,X-Requested-With,Accept,Origin
CORS_ALLOW_CREDENTIALS=true
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS=60
API_FILE_UPLOAD_DIR=/tmp/uploads
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name
CLOUDINARY_API_KEY=your-cloudinary-api-key
CLOUDINARY_API_SECRET=your-cloudinary-api-secret
```

### 3. PostgreSQL Database үүсгэх

1. **Render Dashboard** дээр **"New +"** → **"PostgreSQL"** сонгох
2. **Database settings**:
   - **Name**: `postgres`
   - **Plan**: `Starter` (free tier)
3. **Database connection string**-г environment variable болгон нэмэх:
   - **Key**: `SPRING_DATASOURCE_URL`
   - **Value**: Database connection string

### 4. Database Migration

Database-г бэлтгэхын тулд:

```sql
-- Database schema үүсгэх
CREATE SCHEMA IF NOT EXISTS family;

-- Tables үүсгэх (Hibernate auto-create)
-- эсвэл manual migration scripts ашиглах
```

### 5. Deploy хийх

1. **"Create Web Service"** дарж deploy эхлүүлэх
2. **Build logs** шалгах
3. **Service logs** шалгах
4. **Health check** шалгах: `https://your-service.onrender.com/actuator/health`

### 6. Production Checklist

- [ ] JWT secret key secure болгосон
- [ ] Database connection string зөв тохируулсан
- [ ] CORS origins production domain-д тохируулсан
- [ ] Mail configuration зөв тохируулсан
- [ ] Cloudinary configuration зөв тохируулсан
- [ ] File upload directory зөв тохируулсан
- [ ] Health check endpoint ажиллаж байна
- [ ] Logs зөв харагдаж байна

### 7. Monitoring

- **Health Check**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

### 8. Troubleshooting

#### Common Issues:

1. **Build fails**: Maven dependencies шалгах
2. **Database connection fails**: Connection string шалгах
3. **JWT errors**: Secret key урт шалгах (256 bits+)
4. **CORS errors**: Allowed origins шалгах
5. **File upload fails**: Upload directory permissions шалгах

#### Logs шалгах:

```bash
# Render dashboard дээр "Logs" tab дарж шалгах
# эсвэл curl ашиглах:
curl https://your-service.onrender.com/actuator/health
```

### 9. Environment Variables Reference

| Variable                 | Description      | Example                       |
| ------------------------ | ---------------- | ----------------------------- |
| `SPRING_PROFILES_ACTIVE` | Spring profile   | `prod`                        |
| `SERVER_PORT`            | Server port      | `10000`                       |
| `JWT_SECRET`             | JWT secret key   | `your-secure-secret-256-bits` |
| `SPRING_DATASOURCE_URL`  | Database URL     | `jdbc:postgresql://...`       |
| `CORS_ALLOWED_ORIGINS`   | CORS origins     | `https://yourdomain.com`      |
| `MAIL_USERNAME`          | Email username   | `your-email@gmail.com`        |
| `CLOUDINARY_CLOUD_NAME`  | Cloudinary cloud | `your-cloud-name`             |

### 10. Security Notes

- JWT secret key-г Render-ийн environment variables дээр secure хадгалах
- Database password-г secure хадгалах
- CORS origins-г зөв тохируулах
- Rate limiting идэвхжүүлэх
- HTTPS ашиглах (Render автоматаар өгдөг)

## 🎯 Success!

Deploy амжилттай болсны дараа:

- API endpoint: `https://your-service.onrender.com/api`
- Health check: `https://your-service.onrender.com/actuator/health`
- Swagger UI: `https://your-service.onrender.com/swagger-ui.html`
