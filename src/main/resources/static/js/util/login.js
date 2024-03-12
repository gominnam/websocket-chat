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
        password: password
    };

    showLoadingSpinner();
    try {
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
            });

        if (response.ok) {
            saveAccessTokenToLocalStorage(response.headers.get('Authorization'));
            saveRefreshTokenToLocalStorage(response.headers.get('Authorization-refresh'));
            await navigateTo('/chat');
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
