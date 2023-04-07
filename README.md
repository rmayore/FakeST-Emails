# Email Service

### Description
Email Microservice for the Fake-ST project


### Configuration
Local redis instance
```
docker run -p 6379:6379 -d redis:6.0 redis-server --requirepass "mypass"
```

Local mailhog server (SMTP)
```
docker run -p 8025:8025 -p 1025:1025  -d mailhog/mailhog
```

For single node kafka (kafka + zookeeper), use the [docker-compose file](src/test/resources/kafka-docker-compose.yml) in the test directory, then

```
docker-compose up -d

```

