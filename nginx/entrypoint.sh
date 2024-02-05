#!/bin/bash

# Nginx를 백그라운드에서 시작
nginx &

# Certbot 스크립트 실행
/bin/bash /usr/local/bin/certbot.sh

# Nginx 프로세스를 foreground에서 계속 실행
wait $!