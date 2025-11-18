.PHONY: build-auth build-compose up rebuild logs down

build-auth:
	docker build -f auth-service/Dockerfile -t hexaend/auth-service:latest .

up:
	docker-compose up -d

