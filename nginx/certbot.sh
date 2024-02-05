#!/bin/bash

# Dockerfile에서 정의한 환경변수 사용
EMAIL="${EMAIL}"

# 인증서가 이미 존재하는지 확인
certbot certonly --webroot -w /var/www/certbot --email "$EMAIL" --agree-tos --no-eff-email -d boosterko.kr -d www.boosterko.kr


