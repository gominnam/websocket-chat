
function saveAccessTokenToLocalStorage(accessToken) {
    localStorage.setItem('accessToken', accessToken);
}

function saveRefreshTokenToLocalStorage(refreshToken) {
    localStorage.setItem('refreshToken', refreshToken);
}

function isAccessTokenExpired(token) {
    const payloadBase64 = token.split('.')[1];
    const decodedPayload = JSON.parse(atob(payloadBase64));
    const expiration = decodedPayload.exp;

    const now = Math.floor(Date.now() / 1000); // 현재 시간을 Unix 시간으로 변환합니다.

    if (expiration < now) {
        console.log("액세스 토큰이 만료되었습니다.");
        return true;
    }  else {
        console.log("액세스 토큰이 유효합니다.");
        return false;
    }
}

function getAuthToken(){
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken || accessToken === 'null') {
        console.log("token is not exists.");
        return null;
    }

    if(isAccessTokenExpired(accessToken)) {
       const refreshToken = localStorage.getItem('refreshToken');
       return {
           key: 'Authorization-refresh',
           value: refreshToken
       }
   }
   return {
         key: 'Authorization',
         value: accessToken
   }
}

async function apiRequest(requestUrl, method) {
    let headers = {
        'Content-Type': 'application/json'
    };
    let authToken= getAuthToken();

    if(authToken){
       headers[authToken.key] = authToken.value;
    }
    try {
        const response = await fetch(requestUrl, {
            method: method,
            headers: headers
        });
        if (response.ok) {
            const token = response.headers.get('Authorization');
            if(token){
                saveAccessTokenToLocalStorage(token);
            }
            return await response.text();
        } else {
            console.error("Network response was not ok");
        }
    } catch (error) {
        console.error('Error validating token:', error);
    }
}

function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

function saveTokenFromURL(url){
    const accessToken = getQueryParam('accessToken');
    const refreshToken = getQueryParam('refreshToken');
    console.log(accessToken +", " + refreshToken);
    if(accessToken){
        saveAccessTokenToLocalStorage(accessToken);
    }
    if(refreshToken){
        saveRefreshTokenToLocalStorage(refreshToken);
    }
}

function getParsedToken(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}
