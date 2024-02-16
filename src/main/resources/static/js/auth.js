
function saveAccessTokenToLocalStorage(accessToken) {
    localStorage.setItem('accessToken', accessToken);
}

function saveRefreshTokenToLocalStorage(refreshToken) {
    localStorage.setItem('refreshToken', refreshToken);
}

async function ApiRequest(requestUrl, method) {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
        window.location.href = "/";
        return;
    }

    try {
        const response = await fetch(requestUrl, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (response.ok) {
            // 토큰 유효, /chat 페이지로 리다이렉트
            window.location.href = requestUrl;
        } else {
            // 토큰 무효, 로그인 페이지로 리다이렉트
            window.location.href = "/";
        }
    } catch (error) {
        console.error('Error validating token:', error);
        window.location.href = "/";
    }
}