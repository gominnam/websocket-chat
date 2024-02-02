#alpine은 linux를 기반으로 하는 가벼운 NginX 이미지
FROM nginx:alpine

# Default 설정을 지우고 커스텀 Nginx 설정 파일 복사
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80 443

# Nginx 서버 실행
CMD ["nginx", "-g", "daemon off;"]
