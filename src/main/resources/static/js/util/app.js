const routes = [
    { path: "/", view: () => "<h1>Loading...</h1>", method: ["GET"] },
    { path: "/main", view: () => "<h1>메인 화면입니다.</h1>", method: ["GET"] },
    { path: "/chat", view: () => "<h1>채팅 페이지입니다.</h1>", method: ["GET"] },
    { path: "/not-found", view: () => "<h1>404 페이지를 찾을 수 없습니다.</h1>", method: ["GET"] }
];

const navigateTo = (url) => {
    history.pushState(null, null, url);
    App();
};

const App = async () => {
    let path = window.location.pathname === "/" ? "/main" : window.location.pathname;
    const route = routes.find(r => r.path === path) || routes.find(r => r.path === "/not-found");
    let view = await apiRequest(route.path, route.method.toString());
    const appContainer = document.querySelector("#app");
    appContainer.innerHTML = view;

    // 받아온 HTML 내의 <script> 태그들을 찾아 실행
    const scriptElements = appContainer.querySelectorAll('script');
    scriptElements.forEach((elem) => {
        const src = elem.getAttribute('src');
        if (src) {
            const script = document.createElement('script');
            script.src = src;
            script.async = false; // 필요에 따라 설정
            document.body.appendChild(script);
        }
    });
};

window.addEventListener("popstate", App);

document.addEventListener("DOMContentLoaded", () => {
    document.body.addEventListener("click", e => {
        const target = e.target.closest("[data-link]");
        if (target) {
            e.preventDefault();
            navigateTo(target.href);
        }
    });

    App();
});
