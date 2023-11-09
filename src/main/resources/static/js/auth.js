
function saveAccessTokenToLocalStorage(accessToken) {
    localStorage.setItem('accessToken', accessToken);
}

function saveRefreshTokenToLocalStorage(refreshToken) {
    localStorage.setItem('refreshToken', refreshToken);
}

// 토큰을 가져와서 헤더에 추가하고 API 호출
function ApiRequest(url, method) {
    const accessToken = localStorage.getItem('accessToken');

    if (!accessToken) {
        console.error('Access Token not found');
        return;
    }

    fetch(url, {
        method: method, // 또는 다른 HTTP 메소드
        headers: {
            'Content-Type': 'application/json',
            'accessHeader': accessToken
        },
    })
        .then(response => {
            return response.json();
        })
        .then(data => {
            console.log('Server Data:', data);
            // 다음 작업 수행
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// 예시: 로그인 후 토큰 저장
const receivedAccessToken = 'your-received-access-token';
saveTokenToLocalStorage(receivedAccessToken);

// 예시: API 호출
makeApiRequest();
