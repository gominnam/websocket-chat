
function saveAccessTokenToLocalStorage(accessToken) {
    localStorage.setItem('accessToken', accessToken);
}

function saveRefreshTokenToLocalStorage(refreshToken) {
    localStorage.setItem('refreshToken', refreshToken);
}

//todo: 만료시간 체킹
async function apiRequest(requestUrl, method) {
    const accessToken = localStorage.getItem('accessToken');
    let headers = {
        'Content-Type': 'application/json'
    };
    if (accessToken && accessToken !== 'null') {
        headers['Authorization'] = `Bearer ${accessToken}`;
    }
    try {
        const response = await fetch(requestUrl, {
            method: method,
            headers: headers
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