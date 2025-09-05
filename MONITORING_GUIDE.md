# 📊 Monitoring & Logging Guide

## Overview

This guide covers the comprehensive monitoring and logging setup for the API Tree Service application.

## 🔍 Monitoring Features

### **1. Health Checks**
- **Database Health**: PostgreSQL connection validation
- **Application Health**: Custom application status
- **Endpoint**: `/actuator/health`

```bash
curl http://localhost:8080/actuator/health
```

### **2. Metrics Collection**
- **Prometheus Metrics**: Exported at `/actuator/prometheus`
- **Custom Metrics**: Business-specific counters and timers
- **JVM Metrics**: Memory, GC, threads
- **HTTP Metrics**: Request duration, status codes

#### **Available Metrics:**
- `customer.created` - Number of customers created
- `customer.updated` - Number of customers updated
- `customer.deleted` - Number of customers deleted
- `auth.success` - Successful authentications
- `auth.failure` - Failed authentications
- `rate.limit.exceeded` - Rate limit violations
- `customer.service.duration` - Customer service execution time
- `auth.service.duration` - Authentication service execution time

### **3. Distributed Tracing**
- **Zipkin Integration**: Request tracing across services
- **Brave Tracer**: Automatic instrumentation
- **Trace IDs**: Included in all log messages

## 📝 Logging Configuration

### **1. Structured Logging**
- **JSON Format**: Logstash-compatible JSON logs
- **Trace Correlation**: Trace and span IDs in logs
- **Service Context**: Service name, environment, version

### **2. Log Levels**
- **Application**: `INFO` (configurable)
- **Security**: `WARN` (configurable)
- **Hibernate**: `WARN` (configurable)
- **Web**: `INFO` (configurable)

### **3. Log Files**
- **Application Log**: `/var/log/api-tree-service/application.log`
- **Error Log**: `/var/log/api-tree-service/error.log`
- **Security Log**: `/var/log/api-tree-service/security.log`

### **4. Log Rotation**
- **Max Size**: 100MB per file
- **Max History**: 30 days
- **Total Size Cap**: 1GB

## 🚀 Production Setup

### **1. Prometheus Configuration**

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'api-tree-service'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
```

### **2. Grafana Dashboard**

Import the following dashboard configuration:

```json
{
  "dashboard": {
    "title": "API Tree Service",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count[5m])",
            "legendFormat": "{{uri}}"
          }
        ]
      },
      {
        "title": "Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count{status=~\"5..\"}[5m])",
            "legendFormat": "5xx errors"
          }
        ]
      }
    ]
  }
}
```

### **3. ELK Stack Integration**

#### **Logstash Configuration:**
```ruby
input {
  file {
    path => "/var/log/api-tree-service/*.log"
    codec => "json"
  }
}

filter {
  if [service] == "api-tree-service" {
    mutate {
      add_field => { "service_name" => "api-tree-service" }
    }
  }
}

output {
  elasticsearch {
    hosts => ["localhost:9200"]
    index => "api-tree-service-%{+YYYY.MM.dd}"
  }
}
```

## 🔧 Configuration

### **Environment Variables:**

```bash
# Logging
LOG_LEVEL=INFO
APP_LOG_LEVEL=INFO
SECURITY_LOG_LEVEL=WARN
LOG_FILE_PATH=/var/log/api-tree-service/application.log

# Monitoring
ENVIRONMENT=production
APP_VERSION=1.0.0
```

### **Application Properties:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
        step: 30s
```

## 📊 Key Metrics to Monitor

### **1. Application Metrics**
- **Request Rate**: Requests per second
- **Response Time**: P50, P95, P99 latencies
- **Error Rate**: 4xx and 5xx error rates
- **Active Users**: Concurrent authenticated users

### **2. Business Metrics**
- **Customer Operations**: Create, update, delete rates
- **Authentication**: Success/failure rates
- **Rate Limiting**: Violation rates

### **3. Infrastructure Metrics**
- **Database**: Connection pool usage
- **Memory**: Heap usage, GC frequency
- **CPU**: Usage percentage
- **Disk**: Log file sizes

## 🚨 Alerting Rules

### **Prometheus Alert Rules:**

```yaml
groups:
  - name: api-tree-service
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.1
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          
      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
          
      - alert: DatabaseDown
        expr: up{job="api-tree-service"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Database connection failed"
```

## 🔍 Troubleshooting

### **Common Issues:**

1. **High Memory Usage**
   - Check heap dumps: `/actuator/heapdump`
   - Monitor GC metrics
   - Review cache configurations

2. **Slow Queries**
   - Enable SQL logging: `HIBERNATE_SQL_LOG=DEBUG`
   - Check database indexes
   - Monitor query execution times

3. **Authentication Issues**
   - Check security logs
   - Monitor auth failure rates
   - Review JWT token expiration

### **Useful Endpoints:**

- **Health**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`
- **Loggers**: `/actuator/loggers`
- **Environment**: `/actuator/env`
- **Thread Dump**: `/actuator/threaddump`
- **Heap Dump**: `/actuator/heapdump`

## 📈 Performance Optimization

### **1. Logging Optimization**
- Use async appenders for high-throughput scenarios
- Configure appropriate log levels
- Implement log sampling for debug logs

### **2. Metrics Optimization**
- Use histogram buckets for latency metrics
- Implement custom metrics for business KPIs
- Configure appropriate scrape intervals

### **3. Monitoring Optimization**
- Set up proper alerting thresholds
- Implement dashboard automation
- Regular monitoring review and optimization
