FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive \
    DOMAIN_NAME=${DOMAIN_NAME} \
    EMAIL=${EMAIL}

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y \
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
COPY certbot.sh /etc/cron/certbot
RUN chmod +x /etc/cron/certbot


# HTTPS 포트 열기
EXPOSE 80 443

# nginx 시작 및 유지
CMD ["nginx", "-g", "daemon off;"]

# Certbot 갱신 스크립트 실행 및 Nginx 유지
CMD ["/bin/bash", "-c", "/etc/cron/certbot && nginx -g 'daemon off;'"]




