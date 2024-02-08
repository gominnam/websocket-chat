FROM ubuntu:22.04

# 환경변수 설정
ARG EMAIL
ENV DEBIAN_FRONTEND=noninteractive \
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
    && rm -rf /var/lib/apt/lists/*

# nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# certbot 설정 파일 복사
COPY certbot.sh /usr/local/bin/certbot.sh
RUN chmod +x /usr/local/bin/certbot.sh

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
