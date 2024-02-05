FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive \
    DOMAIN_NAME=${DOMAIN_NAME} \
    EMAIL=${EMAIL}

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y \
    openssl \
    software-properties-common \
    nginx \
    certbot \
    python3-certbot-nginx \
    cron \
    bash \
    && apt-get upgrade -y \
    && rm -rf /var/lib/apt/lists/*

# nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# certbot 설정 파일 복사
COPY certbot.sh /etc/cron/certbot
RUN chmod +x /etc/cron/certbot

# /var/www/certbot 디렉토리 생성
RUN mkdir -p /var/www/certbot
RUN chmod -R 755 /var/www/certbot

# entrypoint.sh 파일 복사
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x entrypoint.sh

# HTTPS 포트 열기
EXPOSE 80 443

# entrypoint.sh 스크립트를 엔트리포인트로 설정
ENTRYPOINT ["/entrypoint.sh"]
