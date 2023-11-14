
function saveAccessTokenToLocalStorage(accessToken) {
    localStorage.setItem('accessToken', accessToken);
}

function saveRefreshTokenToLocalStorage(refreshToken) {
    localStorage.setItem('refreshToken', refreshToken);
}

function loginRequest(data) {
    fetch(url, {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json'
        },
    }).then(response => {
        if(!response.ok){
            throw new Error('Network response was not ok');
        }
        saveAccessTokenToLocalStorage(response.headers.get('Authorization'));
        saveRefreshTokenToLocalStorage(response.headers.get('Authorization-refresh'));

        return response.ok;
    })
        .catch(error => {
            console.error('Error:', error);
        });
}

//ex) apiRequest('/some/url', 'POST');
function apiRequest(url, method) {
    const accessToken = localStorage.getItem('accessToken');

    //todo: accessToken 만료시간 체킹하고 refreshToken으로 accessToken 재발급 받기
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
    }).then(response => {
        if(!response.ok){
            throw new Error('Network response was not ok');
        }
        saveAccessTokenToLocalStorage(response.headers.get('Authorization'));
        saveRefreshTokenToLocalStorage(response.headers.get('Authorization-refresh'));

        const headers = new Headers();
        headers.append("Authorization", `Bearer ${accessToken}`);
        window.location.href = "/chat";
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
