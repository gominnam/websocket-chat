
function saveAccessTokenToLocalStorage(accessToken) {
    localStorage.setItem('accessToken', accessToken);
}

function saveRefreshTokenToLocalStorage(refreshToken) {
    localStorage.setItem('refreshToken', refreshToken);
}

async function apiRequest(requestUrl, method) {
    const accessToken = localStorage.getItem('accessToken');
    try {
        const response = await fetch(requestUrl, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });
        if (response.ok) {
            const htmlSource = await response.text(); // 응답 본문을 문자열로 변환
            return htmlSource;
        } else {
            console.error("Network response was not ok");
        }
    } catch (error) {
        console.error('Error validating token:', error);
    }
}