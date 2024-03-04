let params = new URLSearchParams(window.location.search);
let accessToken = params.get("accessToken");
let refreshToken = params.get("refreshToken");

if(accessToken){
    saveAccessTokenToLocalStorage(accessToken);
    saveRefreshTokenToLocalStorage(refreshToken);
    navigateTo("/chat");
} else {
    navigateTo("/main");
}
