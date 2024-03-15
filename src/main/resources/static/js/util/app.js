const routes = [
    { path: "/", view: () => "<h1>Loading...</h1>", method: ["GET"] },
    { path: "/main", view: () => "<h1>메인 화면입니다.</h1>", method: ["GET"] },
    { path: "/chat", view: () => "<h1>채팅 페이지입니다.</h1>", method: ["GET"] },
    { path: "/signup", view: () => "<h1>oauth2 signup</h1>", method: ["GET"] },
    { path: "/signin", view: () => "<h1>oauth2 signin</h1>", method: ["GET"] },
    { path: "/not-found", view: () => "<h1>404 페이지를 찾을 수 없습니다.</h1>", method: ["GET"] }
];

const baseApiUrl = "/components";

const navigateTo = (url) => {
    history.pushState(null, null, url);
    App();
};

const App = async () => {
    let path = window.location.pathname === "/" ? "/main" : window.location.pathname;
    const route = routes.find(r => r.path === path) || routes.find(r => r.path === "/not-found");
    saveTokenFromURL(window.location.search);
    let view = await apiRequest(baseApiUrl + route.path, route.method.toString());
    const appContainer = document.querySelector("#app");
    appContainer.innerHTML = view;

    const scriptElements = appContainer.querySelectorAll('script');
    scriptElements.forEach((elem) => {
        const src = elem.getAttribute('src');
        if (src) {
            const script = document.createElement('script');
            script.src = src;
            script.async = true;
            document.body.appendChild(script);
        } else {
            eval(elem.textContent);
        }
    });
};

window.addEventListener("popstate", App);

document.addEventListener("DOMContentLoaded", () => {

    App();
});
