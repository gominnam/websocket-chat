const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const userSignInButton = document.getElementById('userSignIn');
const userSingUpButton = document.getElementById('userSignUp');
const container = document.getElementById('container');

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});

signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

userSignInButton.addEventListener('click', async (event) => {
    event.preventDefault();
    await login();
});

async function login(){
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    if(!loginValidation(email, password)) return;

    const data = {
        email: email,
        password: password,
        name: ""
    };

    showLoadingSpinner();
    try {
        const response = await fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        });
        if (response.ok) {
            //로그인 성공 로직
            const data = await response.json();
            if(data.status !== 200){
                alert(data.message);
                return;
            }
            const accessToken = data.data.accessToken;
            const refreshToken = data.data.refreshToken;
            const headers = new Headers();
            //cookie 설정하는 util 함수 만들어서 사용하기
            headers.append("Authorization", `Bearer ${accessToken}`);
            headers.append("Authorization-refresh", `Bearer ${refreshToken}`);
            window.location.href = "/chat";
            console.log('로그인 성공', data);
        } else {
            // 오류 처리
            throw new Error('Network response was not ok');
        }
    } catch (error) {
        console.error('요청 오류', error);
    } finally {
        // 화면 잠금 해제
        hideLoadingSpinner();
    }



    // fetch('/api/user/login', {
    //     method: 'POST',
    //     body: JSON.stringify(data),
    //     headers: {
    //         'Content-Type': 'application/json'
    //     },
    // }).then(response => {
    //     if(!response.ok){
    //         throw new Error('Network response was not ok');
    //     }
    //     var accessToken  = response.headers.get('Authorization');
    //     var refreshToken  = response.headers.get('Authorization-refresh');
    //     const headers = new Headers();
    //     headers.append("Authorization", `Bearer ${accessToken}`);
    //     window.location.href = "/chat";
    // })
    //     .catch(error => {
    //         console.error('Error:', error);
    //     });
}

userSingUpButton.addEventListener('click', () => {
    const email = document.getElementById('save-email').value;
    const name = document.getElementById('save-name').value;
    const password = document.getElementById('save-password').value;
    const data = {
        email: email,
        password: password,
        name: name
    };

    fetch('/api/user/register', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
            Accept: 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
            if(data.status === 200){
                window.location.href = "/chat";
            }else{
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
});

function loginValidation(email, password){
    if(email == null || email === ""){
        alert("Email is required");
        return false;
    }
    if(password == null || password === ""){
        alert("Password is required");
        return false;
    }
    return true;
}

function showLoadingSpinner() {
    document.getElementById('spinner').style.display = 'block';
}

function hideLoadingSpinner() {
    document.getElementById('spinner').style.display = 'none';
}