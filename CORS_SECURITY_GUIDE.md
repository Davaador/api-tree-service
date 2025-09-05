# 🔒 CORS Security Configuration Guide

## Production CORS Security Best Practices

### ⚠️ **CRITICAL SECURITY RULES**

#### **1. NEVER Use Wildcard Origins in Production**
```yaml
# ❌ DANGEROUS - Never do this in production!
CORS_ALLOWED_ORIGINS=*

# ✅ SECURE - Always specify exact domains
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

#### **2. Use HTTPS Only in Production**
```yaml
# ❌ DANGEROUS - HTTP allows man-in-the-middle attacks
CORS_ALLOWED_ORIGINS=http://yourdomain.com

# ✅ SECURE - Always use HTTPS in production
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

#### **3. Limit Allowed Headers**
```yaml
# ❌ DANGEROUS - Allows any header
CORS_ALLOWED_HEADERS=*

# ✅ SECURE - Specify only needed headers
CORS_ALLOWED_HEADERS=Authorization,Content-Type,X-Requested-With,Accept,Origin
```

### **Production Environment Configuration**

#### **Environment Variables:**
```bash
# Production CORS Configuration
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,https://www.your-frontend-domain.com
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers
CORS_ALLOW_CREDENTIALS=true
CORS_MAX_AGE=3600
```

#### **Security Considerations:**

1. **Origin Validation:**
   - Only allow your frontend domains
   - Use exact domain names (no wildcards)
   - Include both www and non-www versions

2. **Method Restrictions:**
   - Only allow necessary HTTP methods
   - Remove unused methods (PATCH, HEAD, etc.)

3. **Header Restrictions:**
   - Specify exact headers needed
   - Avoid wildcard (*) for headers
   - Include security headers

4. **Credentials Handling:**
   - Only set `allowCredentials=true` if needed
   - When true, origins must be specific (no wildcards)

### **Development vs Production**

| Setting | Development | Production |
|---------|-------------|------------|
| **Origins** | `http://localhost:3000` | `https://yourdomain.com` |
| **Headers** | `*` (for convenience) | Specific headers only |
| **Methods** | All methods | Only needed methods |
| **Credentials** | `true` | `true` (if needed) |
| **Max-Age** | `3600` | `3600` or higher |

### **Security Headers to Consider**

```yaml
# Additional security headers for production
security:
  headers:
    frame-options: DENY
    content-type-options: nosniff
    xss-protection: "1; mode=block"
    strict-transport-security: "max-age=31536000; includeSubDomains"
```

### **Testing CORS Configuration**

#### **1. Test with curl:**
```bash
# Test preflight request
curl -X OPTIONS \
  -H "Origin: https://yourdomain.com" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization" \
  https://your-api-domain.com/api/endpoint

# Check response headers:
# Access-Control-Allow-Origin: https://yourdomain.com
# Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
# Access-Control-Allow-Headers: Authorization,Content-Type
# Access-Control-Allow-Credentials: true
```

#### **2. Test with browser:**
```javascript
// Test from your frontend
fetch('https://your-api-domain.com/api/endpoint', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer your-token'
  },
  credentials: 'include',
  body: JSON.stringify({data: 'test'})
})
.then(response => console.log('CORS working!'))
.catch(error => console.error('CORS error:', error));
```

### **Common CORS Security Issues**

1. **Wildcard Origins with Credentials:**
   ```yaml
   # ❌ This will fail in browsers
   CORS_ALLOWED_ORIGINS=*
   CORS_ALLOW_CREDENTIALS=true
   ```

2. **Missing HTTPS:**
   ```yaml
   # ❌ Vulnerable to man-in-the-middle
   CORS_ALLOWED_ORIGINS=http://yourdomain.com
   ```

3. **Overly Permissive Headers:**
   ```yaml
   # ❌ Allows any header
   CORS_ALLOWED_HEADERS=*
   ```

### **Monitoring and Logging**

Enable CORS logging in production:
```yaml
logging:
  level:
    com.api.family.apitreeservice.configuration.SecurityConfig: DEBUG
```

This will log CORS configuration and any CORS-related issues.

### **Deployment Checklist**

- [ ] Set specific origins (no wildcards)
- [ ] Use HTTPS only
- [ ] Limit allowed methods
- [ ] Specify exact headers
- [ ] Test CORS configuration
- [ ] Monitor CORS logs
- [ ] Set appropriate max-age
- [ ] Review security headers
