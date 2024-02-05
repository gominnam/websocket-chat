#!/bin/bash

# Dockerfile에서 정의한 환경변수 사용
EMAIL="${EMAIL}"

# 인증서가 이미 존재하는지 확인
if ! [ -e "/etc/letsencrypt/live/boosterko.kr/fullchain.pem" ]; then
  # 인증서가 없으면 발급 받음
  sudo certbot certonly --webroot -w /var/www/certbot --email "$EMAIL" --agree-tos --no-eff-email -d boosterko.kr -d www.boosterko.kr --deploy-hook "nginx -s reload"
else
  # 인증서가 있으면 갱신 시도
  sudo certbot renew --deploy-hook "nginx -s reload"
fi

# 갱신 실패시 60일 후 다시 시도
trap exit TERM; while :; do sleep 30d & wait ${!}; done;
